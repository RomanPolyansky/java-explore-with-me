package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.constraint.Create;
import ru.practicum.constraint.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@RestControllerAdvice
public class CategoryController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/categories")
    public CategoryDto addCategory(@RequestBody @Validated(Create.class) CategoryDto categoryDto) {
        Category category = CategoryMapper.convertToEntity(categoryDto);
        log.info("POST /admin/categories of: {}", category);
        return CategoryMapper.convertToDto(categoryService.addCategory(category));
    }

    
    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto addCategory(@RequestBody @Validated(Update.class) CategoryDto categoryDto,
                               @PathVariable(value = "catId") long catId) {
        Category category = CategoryMapper.convertToEntity(categoryDto);
        log.info("PATCH /admin/categories of: {}; to {}", catId, category);
        return CategoryMapper.convertToDto(categoryService.changeCategory(catId, category));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategory(@PathVariable(value = "catId") long catId) {
        log.info("DELETE of: {}", catId);
        categoryService.deleteCategory(catId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam (value = "from", defaultValue = "0") int from,
                                       @Positive @RequestParam (value = "size", defaultValue = "10") int size) {
        log.info("GET /admin/categories from: {}; size: {}", from, size);
        List<Category> categorysList = categoryService.getCategories(from, size);
        return categorysList.stream()
                .map(CategoryMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable (value = "catId") Long id) {
        log.info("GET /categories id: {}", id);
        return CategoryMapper.convertToDto(categoryService.getCategoryById(id));
    }
}

























































