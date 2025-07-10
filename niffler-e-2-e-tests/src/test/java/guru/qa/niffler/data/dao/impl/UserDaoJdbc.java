package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UserDaoJdbc implements UserDao {

    private static final Config CFG = Config.getInstance();

    private final Connection connection;

    public UserDaoJdbc(Connection connection) {
        this.connection = connection;
    }


    @Override
    public UserEntity createUser(UserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO user (currency, firstname, full_name, photo, photo_small, surname, username) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getCurrency().name());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getFullName());
            ps.setString(4, Arrays.toString(user.getPhoto()));
            ps.setString(5, Arrays.toString(user.getPhotoSmall()));
            ps.setString(6, user.getSurname());
            ps.setString(7, user.getUsername());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Cant find id in result set");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findUserById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM user WHERE id = ?",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserEntity user = new UserEntity();
                    user.setId(rs.getObject("id", UUID.class));
                    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    user.setFirstName(rs.getString("firstname"));
                    user.setFullName(rs.getString("full_name"));
                    user.setPhoto(rs.getBytes("photo"));
                    user.setPhotoSmall(rs.getBytes("photo_small"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserEntity> findUserByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM user WHERE username = ?",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserEntity user = new UserEntity();
                    user.setId(rs.getObject("id", UUID.class));
                    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    user.setFirstName(rs.getString("firstname"));
                    user.setFullName(rs.getString("full_name"));
                    user.setPhoto(rs.getBytes("photo"));
                    user.setPhotoSmall(rs.getBytes("photo_small"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + username, e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteUser(UserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM user WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + user.getUsername(), e);
        }
    }
}
