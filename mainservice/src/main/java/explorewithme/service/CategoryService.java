package explorewithme.service;

import explorewithme.model.category.CategoryDto;
import explorewithme.model.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long catId);

    CategoryDto addNewCategory(NewCategoryDto newCategory);

    CategoryDto modifyCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);
}
