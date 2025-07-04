package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryDbClient {
    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public CategoryJson createCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
    }

    public CategoryJson findCategoryById(UUID id) {
        return CategoryJson.fromEntity(categoryDao.findCategoryById(id).get());
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return CategoryJson.fromEntity(categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName).get());
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return categoryDao.findAllCategoriesByUsername(username).stream()
                .map(CategoryJson::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        categoryDao.deleteCategory(categoryEntity);
    }
}
