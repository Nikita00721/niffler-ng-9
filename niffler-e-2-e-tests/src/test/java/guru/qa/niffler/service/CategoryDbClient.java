package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.spendJdbcUrl())
            )
    );

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public CategoryJson createCategory(CategoryJson category) throws Exception {
        return jdbcTxTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
        });

    }

    public CategoryJson findCategoryById(UUID id) throws Exception {
        return jdbcTxTemplate.execute(() -> {
            return categoryDao.findCategoryById(id)
                    .map(CategoryJson::fromEntity)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        });
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) throws Exception {
        return jdbcTxTemplate.execute(() -> {
            return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName)
                    .map(CategoryJson::fromEntity)
                    .orElseThrow(() -> new RuntimeException("Category not found for user: " + username + " with name: " + categoryName));
        });
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) throws Exception {
        return jdbcTxTemplate.execute(() -> {
            List<CategoryEntity> categories = categoryDao.findAllCategoriesByUsername(username);
            return categories.stream()
                    .map(CategoryJson::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    public void deleteCategory(CategoryJson category) throws Exception {
        jdbcTxTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            if (categoryEntity.getId() == null) {
                throw new RuntimeException("Category ID cannot be null for deletion");
            }
            categoryDao.deleteCategory(categoryEntity);
            return null;
        });
    }
}
