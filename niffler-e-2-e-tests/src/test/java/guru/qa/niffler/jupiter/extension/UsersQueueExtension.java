package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome) {}

    private static final Queue<StaticUser> EMPTY_USER = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USER = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USER = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USER = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USER.add(new StaticUser("empty_user", "123", null, null, null));
        WITH_FRIEND_USER.add(new StaticUser("user_with_friend", "123", "duck", null, null));
        WITH_INCOME_REQUEST_USER.add(new StaticUser("income", "123", null, "outcome", null));
        WITH_OUTCOME_REQUEST_USER.add(new StaticUser("outcome", "123", null, null, "income"));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Map<UserType.Type, StaticUser> userMap = new EnumMap<>(UserType.Type.class);

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .forEach(parameter -> {
                    UserType.Type userType = parameter.getAnnotation(UserType.class).value();
                    StaticUser user = resolveUserFromQueue(userType);
                    userMap.put(userType, user);
                });

        context.getStore(NAMESPACE).put(context.getUniqueId(), userMap);

        Allure.getLifecycle().updateTestCase(testCase -> testCase.setStart(new Date().getTime()));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Map<UserType.Type, StaticUser> userMap =
                context.getStore(NAMESPACE).remove(context.getUniqueId(), Map.class);

        if (userMap != null) {
            for (Map.Entry<UserType.Type, StaticUser> entry : userMap.entrySet()) {
                returnUserToQueue(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        UserType.Type type = parameterContext.getParameter().getAnnotation(UserType.class).value();
        Map<UserType.Type, StaticUser> userMap =
                extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
        return userMap != null ? userMap.get(type) : null;
    }

    private StaticUser resolveUserFromQueue(UserType.Type type) {
        StopWatch sw = StopWatch.createStarted();
        Optional<StaticUser> user = Optional.empty();

        while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
            user = switch (type) {
                case EMPTY -> Optional.ofNullable(EMPTY_USER.poll());
                case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USER.poll());
                case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USER.poll());
                case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USER.poll());
            };
            if (user.isEmpty()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        }

        return user.orElseThrow(() ->
                new IllegalStateException("Can't get user of type: " + type + " within 30 seconds"));
    }

    private void returnUserToQueue(UserType.Type type, StaticUser user) {
        switch (type) {
            case EMPTY -> EMPTY_USER.add(user);
            case WITH_FRIEND -> WITH_FRIEND_USER.add(user);
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USER.add(user);
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USER.add(user);
        }
    }
}
