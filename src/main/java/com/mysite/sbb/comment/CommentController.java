package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

  private final AnswerService answerService;
  private final CommentService commentService;
  private final UserService userService;
  private final QuestionService questionService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/create/question/{id}")
  public String questionCommentCreate(Model model, @PathVariable("id") Integer id,
      @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
    Question question = this.questionService.getQuestion(id);
    SiteUser siteuser = this.userService.getUser(principal.getName());

    if (bindingResult.hasErrors()) {
      model.addAttribute("question", question);
      return "pages/question/detail";
    }

    this.commentService.create(question, null, commentForm.getContent(),
        siteuser);
    model.addAttribute("commentList", this.commentService.getCommentList(question));
    return String.format("redirect:/question/detail/%s", id);
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/create/answer/{id}")
  public String answerCommentCreate(Model model, @PathVariable("id") Integer id,
      @Valid CommentForm commentForm, BindingResult bindingResult) {
    Answer answer = this.answerService.getAnswer(id);
    Question question = answer.getQuestion();
    if (bindingResult.hasErrors()) {
      model.addAttribute("questiton", question);
      return "pages/question/detail";
    }
    this.commentService.create(question, answer, commentForm.getContent(), answer.getAuthor());
    model.addAttribute("commentList", this.commentService.getCommentList(question));
    return String.format("redirect:/question/detail/%s#answer_%s", question.getId(), id);
  }
}
