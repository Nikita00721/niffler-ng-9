package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Isolated
public class LastTest {
    private final UsersClient usersClient = UsersClient.getInstance();

    @Test
    @User
    void lastTest(UserJson user) {
        List<UserJson> users = usersClient.allUsers(user.username(), null);
        assertFalse(users.isEmpty());
    }
}
