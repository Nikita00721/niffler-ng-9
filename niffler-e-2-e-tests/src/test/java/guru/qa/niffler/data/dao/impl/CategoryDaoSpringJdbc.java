package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private final DataSource dataSource;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CategoryEntity create(CategoryEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (username, name, archived) " +
                            "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, spend.getUsername());
            ps.setString(2, spend.getName());
            ps.setBoolean(3, spend.isArchived());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID uuid) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE id = ?",
                        (rs, rowNum) -> {
                            CategoryEntity category = new CategoryEntity();
                            category.setId(rs.getObject("id", UUID.class));
                            category.setUsername(rs.getString("username"));
                            category.setName(rs.getString("name"));
                            category.setArchived(rs.getBoolean("archived"));
                            return category;
                        },
                        uuid
                )
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE username = ? AND name = ?",
                        (rs, rowNum) -> {
                            CategoryEntity category = new CategoryEntity();
                            category.setId(rs.getObject("id", UUID.class));
                            category.setUsername(rs.getString("username"));
                            category.setName(rs.getString("name"));
                            category.setArchived(rs.getBoolean("archived"));
                            return category;
                        },
                        username, categoryName
                )
        );
    }

    @Override
    public List<CategoryEntity> findAllCategoriesByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                "SELECT * FROM category WHERE username = ?",
                (rs, rowNum) -> {
                    CategoryEntity category = new CategoryEntity();
                    category.setId(rs.getObject("id", UUID.class));
                    category.setUsername(rs.getString("username"));
                    category.setName(rs.getString("name"));
                    category.setArchived(rs.getBoolean("archived"));
                    return category;
                },
                username
        );
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("DELETE FROM category WHERE id = ?", category.getId());
        category.setId(null);
    }
}
