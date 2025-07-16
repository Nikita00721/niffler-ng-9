package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public Optional<AuthUserEntity> findById(UUID uuid) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, uuid);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setUsername(rs.getString("username"));
                    authUserEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUserEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUserEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(authUserEntity);
                } else {
                    return Optional.empty();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend"
        )) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                List<AuthUserEntity> authUserEntities = new java.util.ArrayList<>();
                while (rs.next()) {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setId(rs.getObject("id", UUID.class));
                    authUserEntity.setUsername(rs.getString("username"));
                    authUserEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUserEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUserEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    authUserEntities.add(authUserEntity);
                }
                return authUserEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthUserEntity create(AuthUserEntity authUserEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, password, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, authUserEntity.getUsername());
            ps.setBoolean(2, authUserEntity.getAccountNonExpired());
            ps.setBoolean(3, authUserEntity.getAccountNonLocked());
            ps.setBoolean(4, authUserEntity.getCredentialsNonExpired());
            ps.setBoolean(5, authUserEntity.getEnabled());

            PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            ps.setString(6, passwordEncoder.encode(authUserEntity.getPassword()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    authUserEntity.setId(rs.getObject("id", UUID.class));
                }
            }
            return authUserEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
