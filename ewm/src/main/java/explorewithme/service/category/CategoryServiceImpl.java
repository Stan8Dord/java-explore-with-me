package explorewithme.service.category;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.ConflictException;
import explorewithme.exceptions.NotFoundCategoryException;
import explorewithme.model.category.Category;
import explorewithme.model.category.dto.CategoryDto;
import explorewithme.model.category.CategoryMapper;
import explorewithme.model.category.dto.NewCategoryDto;
import explorewithme.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int fromNum, int size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Category> categories = categoryRepository.findAll(page);

        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId, HttpServletRequest request) {
        Category category = checkCategory(catId, request);
        return CategoryMapper.toCategoryDto(category);
    }

    public Category checkCategory(Long catId, HttpServletRequest request) {
        Optional<Category> categoryOptional = categoryRepository.findById(catId);

        if (categoryOptional.isPresent())
            return categoryOptional.get();
        else
            throw new NotFoundCategoryException(catId, request);
    }

    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategory, HttpServletRequest request) {
        checkCategoryName(newCategory.getName(), request);

        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategory)));
    }

    private void checkCategoryName(String catName, HttpServletRequest request) {
        if (catName == null)
            throw new BadRequestException(request.getParameterMap().toString());
        if (getCategoryNames().contains(catName))
            throw new ConflictException(String.format("Такая категория %s уже существует! Request path = %s",
                    catName, request.getRequestURI()));
    }

    @Override
    public CategoryDto modifyCategory(CategoryDto categoryDto, HttpServletRequest request) {
        if (categoryDto.getId() <= 0)
            throw new BadRequestException(request.getParameterMap().toString());
        checkCategoryName(categoryDto.getName(), request);

        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    public Set<String> getCategoryNames() {
        return categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toSet());
    }

    @Override
    public void deleteCategory(long catId) {
        categoryRepository.deleteById(catId);
    }
}
