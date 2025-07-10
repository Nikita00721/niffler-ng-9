package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.Optional;

public interface AuthUserDao {
    Optional<AuthUserEntity> getAuthUserByLogin(String login);
    AuthUserEntity createUser(AuthUserEntity authUserEntity);
    void deleteUserByLogin(String login);
}
