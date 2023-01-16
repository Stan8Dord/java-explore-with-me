package explorewithme.service;

import explorewithme.model.category.CategoryDto;
import explorewithme.model.category.NewCategoryDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long catId, HttpServletRequest request);

    CategoryDto addNewCategory(NewCategoryDto newCategory, HttpServletRequest request);

    CategoryDto modifyCategory(CategoryDto categoryDto, HttpServletRequest request);

    void deleteCategory(long catId);
}
