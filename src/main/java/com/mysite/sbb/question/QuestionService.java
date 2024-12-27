package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QuestionService {

  private final QuestionRepository questionRepository;

  private Specification<Question> search(String kw) {
    return new Specification<Question>() {
      @Override
      public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query,
          CriteriaBuilder criteriaBuilder) {
        query.distinct(true); // 중복 제거
        Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
        Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
        Join<Answer, SiteUser> u2 = q.join("author", JoinType.LEFT);
        return criteriaBuilder.or(criteriaBuilder.like(q.get("subject"), "%" + kw + "%"), // 제목
            criteriaBuilder.like(q.get("content"), "%" + kw + "%"),      // 내용
            criteriaBuilder.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
            criteriaBuilder.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
            criteriaBuilder.like(u2.get("username"), "%" + kw + "%"));
      }
    };
  }

  public Page<Question> getList(int page, String kw) {
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    Specification<Question> spec = search(kw);
    return this.questionRepository.findAll(spec, pageable);
  }

  public Question getQuestion(Integer id) {
    Optional<Question> question = this.questionRepository.findById(id);
    if (question.isPresent()) {
      return question.get();
    } else {
      throw new DataNotFoundException("question not found");
    }
  }

  public void create(String subject, String content, SiteUser author, Category category) {
    Question question = new Question();
    question.setSubject(subject);
    question.setContent(content);
    question.setAuthor(author);
    question.setCreateDate(LocalDateTime.now());
    question.setCategory(category);
    this.questionRepository.save(question);
  }

  public void modify(Question question, String subject, String content, Category category) {
    question.setSubject(subject);
    question.setContent(content);
    question.setModifyDate(LocalDateTime.now());
    question.setCategory(category);
    this.questionRepository.save(question);
  }

  public void delete(Question question) {
    this.questionRepository.delete(question);
  }

  public void vote(Question question, SiteUser siteUser) {
    question.getVoter().add(siteUser);
    this.questionRepository.save(question);
  }
}
