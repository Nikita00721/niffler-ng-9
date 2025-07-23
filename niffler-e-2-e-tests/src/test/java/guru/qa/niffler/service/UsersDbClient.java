package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.user.CurrencyValues;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    //ChainedTransactionManager
    private final TransactionTemplate chainedTxTemplate;

    public UsersDbClient() {
        var authDs = DataSources.dataSource(CFG.authJdbcUrl());
        var userdataDs = DataSources.dataSource(CFG.userdataJdbcUrl());
        var authTxManager = new DataSourceTransactionManager(authDs);
        var userdataTxManager = new DataSourceTransactionManager(userdataDs);
        var chainedTxManager = new ChainedTransactionManager(authTxManager, userdataTxManager);
        this.chainedTxTemplate = new TransactionTemplate(chainedTxManager);
    }

    public UserJson createUser(String username, String password) throws Exception {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            userdataUserRepository.create(userEntity(username)),
                            null
                    );
                }
        );
    }

    public void addIncomeInvitation(UserJson targetUser, int count) throws Exception {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id()).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = RandomDataUtils.randomUsername();

                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity addressee = userdataUserRepository.create(userEntity(username));

                            userdataUserRepository.addIncomeInvitation(targetEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }

    public void addOutcomeInvitation(UserJson targetUser, int count) throws Exception {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id()).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = RandomDataUtils.randomUsername();

                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity addressee = userdataUserRepository.create(userEntity(username));

                            userdataUserRepository.addOutcomeInvitation(targetEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }

    public void addFriendship(UserJson targetUser, int count) {

    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(e);
                    return ae;
                }
        ).toList());
        return authUser;
    }

    // Пример использования ChainedTransactionManager для распределённой транзакции
    public void createUserWithChainedTx(UserJson user) {
        chainedTxTemplate.execute(status -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);
            AuthUserEntity createdAuthUser = authUserRepository.create(authUser);

            AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                    e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.getUser().setId(createdAuthUser.getId());
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

    // JDBC без транзакции
    public UserJson createUserJdbcNoTx(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        AuthUserEntity createdAuthUser = authUserRepository.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.getUser().setId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);
        authAuthorityDao.create(authorityEntities);
        return user;
    }

    // JDBC с транзакцией
    public UserJson createUserJdbcWithTx(UserJson user) {
        return txTemplate.execute(status -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);
            AuthUserEntity createdAuthUser = authUserRepository.create(authUser);

            AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                    e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.getUser().setId(createdAuthUser.getId());
                        ae.setAuthority(e);
                        return ae;
                    }
            ).toArray(AuthorityEntity[]::new);
            authAuthorityDao.create(authorityEntities);
            return user;
        });
    }

    // Spring-JDBC без транзакции (обычный вызов)
    public UserJson createUserSpringJdbcNoTx(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        AuthUserEntity createdAuthUser = authUserRepository.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.getUser().setId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);
        authAuthorityDao.create(authorityEntities);
        return user;
    }

    // Spring-JDBC с транзакцией (TransactionTemplate)
    public UserJson createUserSpringJdbcWithTx(UserJson user) {
        return txTemplate.execute(status -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);
            AuthUserEntity createdAuthUser = authUserRepository.create(authUser);

            AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                    e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.getUser().setId(createdAuthUser.getId());
                        ae.setAuthority(e);
                        return ae;
                    }
            ).toArray(AuthorityEntity[]::new);
            authAuthorityDao.create(authorityEntities);
            return user;
        });
    }

    // Поиск всех пользователей (Spring-JDBC)
    public List<AuthUserEntity> findAllAuthUsers() {
        return authUserRepository.findAll();
    }
}