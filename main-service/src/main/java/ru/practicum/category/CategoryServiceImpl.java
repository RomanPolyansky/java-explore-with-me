package ru.practicum.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.QCategory;
import ru.practicum.exception.ObjectNotFoundException;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Category addCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        log.info("CategoryRepository saved: {}", savedCategory);
        return savedCategory;
    }


    @Override
    public List<Category> getCategories(int from, int size) {
        List<Category> foundCategories = IterableUtils.toList(jpaQueryFactory.selectFrom(QCategory.category)
                .orderBy(QCategory.category.id.asc())
                .offset(from)
                .limit(size)
                .fetch());
        log.info("CategoryRepository returned: {}", foundCategories);
        return foundCategories;
    }

    @Override
    public Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Category with id %s does not exist", catId))
        );
    }

    @Override
    public Category changeCategory(long catId, Category category) {
        Category categoryInRepo = categoryRepository.findById(catId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Category with id %s does not exist", catId))
        );
        category.setId(catId);
        Category changedCategory = categoryRepository.save(category);
        log.info("CategoryRepository changed: {}; to {}", categoryInRepo, changedCategory);
        return changedCategory;
    }

    @Override
    public void deleteCategory(long catId) {
        Category categoryInRepo = categoryRepository.findById(catId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Category with id %s does not exist", catId))
        );
        log.info("CategoryRepository deletes: {}", categoryInRepo);
        categoryRepository.deleteById(catId);
    }
}
