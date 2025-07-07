package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.AuthUserJson;

import static guru.qa.niffler.data.Databases.transaction;

public class AuthDbClient {

    private static final Config CFG = Config.getInstance();

    public AuthUserJson getAuthUserByLogin(String login) {
        return transaction(connection -> {
            AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
            return authUserDao.getAuthUserByLogin(login)
                    .map(AuthUserJson::fromEntity)
                    .orElseThrow(() -> new RuntimeException("User not found with login: " + login));
        }, CFG.authJdbcUrl());
    }

    public AuthUserJson createUser(AuthUserJson user) {
        return transaction(connection -> {
            AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
            AuthUserEntity authUserEntity = AuthUserEntity.fromJson(user);
            return AuthUserJson.fromEntity(authUserDao.createUser(authUserEntity));
        }, CFG.authJdbcUrl());
    }

    public void deleteUserByLogin(String login) {
        transaction(connection -> {
            AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
            authUserDao.deleteUserByLogin(login);
            return null;
        }, CFG.authJdbcUrl());
    }
}
