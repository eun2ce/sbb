package com.mysite.sbb.category;

import com.mysite.sbb.DataNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public void create(Category category) {
    this.categoryRepository.save(category);
  }

  public Category getCategoryByName(String name) {
    return this.categoryRepository.findByName(name)
        .orElseThrow(() -> new DataNotFoundException("category not found"));
  }

  public List<Category> getAll() {
    return this.categoryRepository.findAll();
  }
}
