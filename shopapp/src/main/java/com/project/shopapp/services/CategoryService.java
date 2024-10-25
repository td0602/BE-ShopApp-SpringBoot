package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // tu dong tao constructer cho cac thuoc tinh final
public class CategoryService implements ICategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
//                .orElse(null);
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Category categoryOld = getCategoryById(categoryId);
        categoryOld.setName(categoryDTO.getName());
        categoryRepository.save(categoryOld);
        return categoryOld;
    }

    @Override
    public void deleteCategory(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
//    public CategoryService(CategoryRepository categoryRepository) {
//        this.categoryRepository = categoryRepository;
//    }
}
