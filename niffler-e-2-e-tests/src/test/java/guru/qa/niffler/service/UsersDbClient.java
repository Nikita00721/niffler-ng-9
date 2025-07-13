package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUserSpringJdbc(UserJson user) throws Exception {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUser = authUserDao
                    .create(authUser);

            AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                    e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setUserId(createdAuthUser.getId());
                        ae.setAuthority(e);
                        return ae;
                    }
            ).toArray(AuthorityEntity[]::new);

            authAuthorityDao.create(authorityEntities);
            return null;
        });
    }


//    public UserJson createUser(UserJson user) {
//        return UserJson.fromEntity(
//                xaTransaction(
//                        new Databases.XaFunction<>(
//                                con -> {
//                                    AuthUserEntity authUser = new AuthUserEntity();
//                                    authUser.setUsername(user.username());
//                                    authUser.setPassword(pe.encode("12345"));
//                                    authUser.setEnabled(true);
//                                    authUser.setAccountNonExpired(true);
//                                    authUser.setAccountNonLocked(true);
//                                    authUser.setCredentialsNonExpired(true);
//                                    new AuthUserDaoJdbc(con).create(authUser);
//                                    new AuthAuthorityDaoJdbc(con).create(
//                                            Arrays.stream(Authority.values())
//                                                    .map(a -> {
//                                                                AuthorityEntity ae = new AuthorityEntity();
//                                                                ae.setUserId(authUser.getId());
//                                                                ae.setAuthority(a);
//                                                                return ae;
//                                                            }
//                                                    ).toArray(AuthorityEntity[]::new));
//                                    return null;
//                                },
//                                CFG.authJdbcUrl()
//                        ),
//                        new Databases.XaFunction<>(
//                                con -> {
//                                    UserEntity ue = new UserEntity();
//                                    ue.setUsername(user.username());
//                                    ue.setFullname(user.fullname());
//                                    ue.setCurrency(user.currency());
//                                    new UdUserDaoJdbc(con).create(ue);
//                                    return ue;
//                                },
//                                CFG.userdataJdbcUrl()
//                        )
//                ),
//                null);
//    }
}