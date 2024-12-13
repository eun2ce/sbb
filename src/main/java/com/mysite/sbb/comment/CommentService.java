package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

  private final CommentRepository commentRepository;

  public Comment create(Question question, Answer answer, String content, SiteUser author) {
    Comment comment = new Comment();
    comment.setContent(content);
    comment.setAuthor(author);
    comment.setQuestion(question);
    comment.setAnswer(answer);
    this.commentRepository.save(comment);
    return comment;
  }

  public List<Comment> getCommentList(Question question) {
    return this.commentRepository.findByQuestion(question);
  }
}
