package com.mysite.sbb.comment;

import com.mysite.sbb.question.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
  List<Comment> findByQuestion(Question question);
}
