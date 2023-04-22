package ru.practicum.category;

import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {

    void deleteCategory(long catId);

    Category changeCategory(long catId, Category category);

    Category addCategory(Category category);

    List<Category> getCategories(int from, int size);

    Category getCategoryById(Long id);
}
