package com.mysite.sbb.category;

import com.mysite.sbb.question.Question;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String name;

  @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
  private List<Question> categoryQuestionList;

  @Override
  public String toString() {
    return name;
  }
}
