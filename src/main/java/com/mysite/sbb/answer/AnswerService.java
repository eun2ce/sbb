package com.mysite.sbb.answer;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

  private final AnswerRepository answerRepository;

  public Answer create(Question question, String content, SiteUser author) {
    Answer answer = new Answer();
    answer.setContent(content);
    answer.setAuthor(author);
    answer.setQuestion(question);
    answer.setCreateDate(LocalDateTime.now());
    this.answerRepository.save(answer);
    return answer;
  }

  public Answer getAnswer(Integer id) {
    Optional<Answer> answer = this.answerRepository.findById(id);

    if (answer.isPresent()) {
      return answer.get();
    } else {
      throw new DataNotFoundException("answer not found");
    }
  }

  public void modify(Answer answer, String content) {
    answer.setContent(content);
    answer.setModifyDate(LocalDateTime.now());
    this.answerRepository.save(answer);
  }

  public void delete(Answer answer) {
    this.answerRepository.delete(answer);
  }

  public void vote(Answer answer, SiteUser siteUser) {
    answer.getVoter().add(siteUser);
    this.answerRepository.save(answer);
  }

  public Page<Answer> getList(Question question, int page) {
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate")); // 정렬
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    return this.answerRepository.findAllByQuestion(question, pageable);
  }
}
