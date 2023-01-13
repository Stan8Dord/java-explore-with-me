package explorewithme.service;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.ConflictException;
import explorewithme.exceptions.NotFoundException;
import explorewithme.model.category.Category;
import explorewithme.model.category.CategoryDto;
import explorewithme.model.category.CategoryMapper;
import explorewithme.model.category.NewCategoryDto;
import explorewithme.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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
    public CategoryDto getCategory(Long catId) {
        Category category = checkCategory(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    public Category checkCategory(Long catId) {
        Optional<Category> categoryOptional = categoryRepository.findById(catId);

        if (categoryOptional.isPresent())
            return categoryOptional.get();
        else
            throw new NotFoundException("Не найдено!");
    }

    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategory) {
        if (newCategory.getName() == null)
            throw new BadRequestException("Некорректный запрос");
        if (getCategoryNames().contains(newCategory.getName())) {
            throw new ConflictException("Такая категория уже существует!");
        } else {
            return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategory)));
        }
    }

    @Override
    public CategoryDto modifyCategory(CategoryDto categoryDto) {
        if (categoryDto.getId() <= 0)
            throw new BadRequestException("Некорректный запрос");
        if (getCategoryNames().contains(categoryDto.getName())) {
            throw new ConflictException("Такая категория уже существует!");
        } else {
            return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
        }
    }

    public Set<String> getCategoryNames() {
        return categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toSet());
    }

    @Override
    public void deleteCategory(long catId) {
        categoryRepository.deleteById(catId);
    }
}
