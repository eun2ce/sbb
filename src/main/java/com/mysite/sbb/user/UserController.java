package com.mysite.sbb.user;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  private final MailSender mailSender;

  @GetMapping("/login")
  public String login() {
    return "pages/user/login";
  }

  @GetMapping("/signup")
  public String signup(UserCreateForm userCreateForm) {
    return "pages/user/signup";
  }

  @PostMapping("/signup")
  public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "pages/user/signup";
    }
    if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
      bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
      return "pages/user/signup";
    }
    try {
      userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(),
          userCreateForm.getPassword1());
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
      return "pages/user/signup";
    } catch (Exception e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", e.getMessage());
      return "pages/user/signup";
    }
    return "redirect:/";
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/update")
  public String update(Model model, Principal principal) {
    SiteUser siteUser = this.userService.getUser(principal.getName());
    model.addAttribute("userUpdateForm", new UserUpdateForm());
    model.addAttribute("username", siteUser.getUsername());
    model.addAttribute("email", siteUser.getEmail());
    model.addAttribute("passwordChanged", false);

    return "pages/user/update";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/update")
  public String update(@Valid UserUpdateForm userUpdateForm, BindingResult bindingResult,
      Model model, Principal principal) {
    SiteUser siteUser = this.userService.getUser(principal.getName());

    model.addAttribute("username", siteUser.getUsername());
    model.addAttribute("email", siteUser.getEmail());

    if (bindingResult.hasErrors()) { // 유효성 검증
      return "pages/user/update";
    }

    if (!this.userService.isMatch(userUpdateForm.getOriginPassword(), siteUser.getPassword())) {
      bindingResult.rejectValue("originPassword", "passwordInCorrect", "기존 비밀번호가 일치하지 않습니다.");
      return "pages/user/update";
    } else {
      model.addAttribute("originPassword", userUpdateForm.getOriginPassword());
    }

    if (!userUpdateForm.getNewPassword1().equals(userUpdateForm.getNewPassword2())) {
      model.addAttribute("newPassword1", userUpdateForm.getNewPassword1());
      model.addAttribute("newPassword2", userUpdateForm.getNewPassword2());
      bindingResult.rejectValue("newPassword2", "passwordInCorrect", "확인 비밀번호가 일치하지 않습니다.");
      return "pages/user/update";
    }

    try {
      this.userService.update(siteUser, userUpdateForm.getNewPassword1());
    } catch (Exception e) {
      bindingResult.reject("updateFailed", e.getMessage());
      e.printStackTrace();
      return "pages/user/update";
    }
    model.addAttribute("passwordChanged", true);
    return "pages/user/update";
  }

  @GetMapping("/reset")
  public String reset(Model model) {
    model.addAttribute("error", false);
    model.addAttribute("sendConfirm", false);
    model.addAttribute("email", false);
    return "pages/user/reset";
  }


  @PostMapping("/reset")
  public String reset(Model model, @RequestParam("email") String email) {
    boolean error = false;
    boolean sendConfirm = true;
    try {
      SiteUser siteUser = this.userService.getUserByEmail(email);
      SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
      simpleMailMessage.setTo(email);
      simpleMailMessage.setSubject("임시 비밀번호 발급 입니다.");
      StringBuilder sb = new StringBuilder();

      String newPassword = UUID.randomUUID().toString().replaceAll("-", "");
      sb.append(siteUser.getId()).append("계정의 임시 비밀번호는 [").append(newPassword).append("] 입니다.");
      simpleMailMessage.setText(sb.toString());
      this.userService.update(siteUser, newPassword);
      new Thread(() -> mailSender.send(simpleMailMessage)).start();
    } catch (Exception e) {
      error = true;
      sendConfirm = false;
    }
    model.addAttribute("error", error);
    model.addAttribute("sendConfirm", sendConfirm);
    model.addAttribute("email", email);

    return "pages/user/reset";
  }
}
