package com.mysite.sbb.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

  @GetMapping("/find")
  public String find(Model model) {
    model.addAttribute("error", false);
    model.addAttribute("sendConfirm", false);
    return "pages/user/find";
  }

  @PostMapping("/find")
  public String find(Model model, @RequestParam(value = "email") String email) {
    SiteUser siteUser = this.userService.getUserByEmail(email);
    model.addAttribute("sendConfirm", true);
    model.addAttribute("error", false);
    model.addAttribute("userEmail", email);

    return "pages/user/find";
  }
}
