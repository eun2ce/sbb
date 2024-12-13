package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.comment.CommentForm;
import com.mysite.sbb.comment.CommentService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

  // @RequiredArgsConstructor 애너테이션 방식으로 (생성자 없이) questionRepository 객체 주입

  private final AnswerService answerService;
  private final CommentService commentService;
  private final QuestionService questionService;
  private final UserService userService;

  @GetMapping("/list")
  public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "kw", defaultValue = "") String kw) {
    Page<Question> paging = this.questionService.getList(page, kw);
    model.addAttribute("paging", paging);
    model.addAttribute("kw", kw);
    return "question_list";
  }

  @GetMapping(value = "/detail/{id}")
  public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm,
      @RequestParam(value = "answerPage", defaultValue = "0") int answerPage) {
    Question question = this.questionService.getQuestion(id);
    Page<Answer> answerPaging = this.answerService.getList(question, answerPage);
    List<Comment> commantList = this.commentService.getCommentList(question);
    model.addAttribute("question", question);
    model.addAttribute("answerPaging", answerPaging);
    model.addAttribute("commentList", commantList);
    model.addAttribute("commentForm", new CommentForm());  // 댓글 작성 폼
    return "question_detail";
  }

  @PreAuthorize("isAuthenticated()") // 로그인이 안된경우, 로그인 페이지로 강제 이동
  @GetMapping("/create")
  public String questionCreate(QuestionForm questionForm) {
    return "question_form";
  }

  @PreAuthorize("isAuthenticated()") // 로그인이 안된경우, 로그인 페이지로 강제 이동
  @PostMapping("/create")
  public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult,
      Principal principal) {
    if (bindingResult.hasErrors()) {
      return "question_form";
    }
    SiteUser siteUser = this.userService.getUser(principal.getName());
    this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
    return "redirect:/question/list";
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id,
      Principal principal) {
    Question question = this.questionService.getQuestion(id);
    if (!question.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
    }
    questionForm.setSubject(question.getSubject());
    questionForm.setContent(question.getContent());
    return "question_form";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
      Principal principal,
      @PathVariable("id") Integer id) {
    if (bindingResult.hasErrors()) {
      return "question_form";
    }
    Question question = this.questionService.getQuestion(id);
    if (!question.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
    }
    this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
    return String.format("redirect:/question/detail/%s", id);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String questionDelete(QuestionForm questionFrom, @PathVariable("id") Integer id,
      Principal principal) {
    Question question = this.questionService.getQuestion(id);
    if (!question.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
    }
    this.questionService.delete(question);

    return "redirect:/";
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/vote/{id}")
  public String questionVote(Principal principal, @PathVariable("id") Integer id) {
    Question question = this.questionService.getQuestion(id);
    SiteUser siteUser = this.userService.getUser(principal.getName());
    this.questionService.vote(question, siteUser);
    return String.format("redirect:/question/detail/%s", id);
  }
}
