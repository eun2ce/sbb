package com.mysite.sbb;

import com.mysite.sbb.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SbbApplicationTests {

  @Autowired
  private QuestionService questionService;

  @Test
  void testJpa() {
    int MAX_QUESTION_SIZE = 100;
    for (int i = 1; i <= 100; i++) {
      this.questionService.create(String.format("테스트 데이터입니다 : [%03d]", i), "내용 없음 ", null);
    }
  }
}
