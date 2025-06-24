package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;

@ExtendWith(UsersQueueExtension.class)
public class UserQueueTest {

    @Test
    void testWithEmptyUser0(@UserType(empty = true) StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user);
    }

    @Test
    void testWithEmptyUser1(@UserType(empty = false  ) StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user);
    }
}
