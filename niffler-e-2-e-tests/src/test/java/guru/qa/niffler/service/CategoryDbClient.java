package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.Databases.transaction;

public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
            CategoryDao categoryDao = new CategoryDaoJdbc(connection);
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
        }, CFG.spendJdbcUrl());

    }

    public CategoryJson findCategoryById(UUID id) {
        return transaction(connection -> {
            CategoryDao categoryDao = new CategoryDaoJdbc(connection);
            return categoryDao.findCategoryById(id)
                    .map(CategoryJson::fromEntity)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        }, CFG.spendJdbcUrl());
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return transaction(connection -> {
            CategoryDao categoryDao = new CategoryDaoJdbc(connection);
            return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName)
                    .map(CategoryJson::fromEntity)
                    .orElseThrow(() -> new RuntimeException("Category not found for user: " + username + " with name: " + categoryName));
        }, CFG.spendJdbcUrl());
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return transaction(connection -> {
            CategoryDao categoryDao = new CategoryDaoJdbc(connection);
            List<CategoryEntity> categories = categoryDao.findAllCategoriesByUsername(username);
            return categories.stream()
                    .map(CategoryJson::fromEntity)
                    .collect(Collectors.toList());
        }, CFG.spendJdbcUrl());
    }

    public void deleteCategory(CategoryJson category) {
        transaction(connection -> {
            CategoryDao categoryDao = new CategoryDaoJdbc(connection);
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            categoryDao.deleteCategory(categoryEntity);
            return null;
        }, CFG.spendJdbcUrl());
    }
}
