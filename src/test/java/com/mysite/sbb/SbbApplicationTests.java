package com.mysite.sbb;

import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SbbApplicationTests {

  @Autowired
  private QuestionService questionService;

  @Autowired
  private AnswerService answerService;

  @Test
  void testJpa() {
    for (int i = 1; i <= 300; i++) {
      String content = String.format("테스트 답변입니다:[%03d]", i);
      Question question = this.questionService.getQuestion(300);
      this.answerService.create(question, content, null);
    }
  }
}
