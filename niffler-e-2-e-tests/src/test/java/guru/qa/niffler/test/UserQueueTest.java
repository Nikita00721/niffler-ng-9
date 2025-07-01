package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static org.hamcrest.MatcherAssert.assertThat;

@WebTest
public class UserQueueTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void friendsTableShouldBeEmptyForNewUsers(@UserType(UserType.Type.EMPTY) StaticUser user) throws InterruptedException {
        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit()
                .navigateToFriendsPage()
                .checkThatPageLoaded();

        int friendsListSize = friendsPage.checkFriendsTableSize();
        assertThat("'EMPTY' user must be contained 0 friend", friendsListSize, Matchers.equalTo(0));
    }

    @Test
    void friendsShuoldBePresentInFriendsTable(@UserType(UserType.Type.WITH_FRIEND) StaticUser user) throws InterruptedException {
        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit()
                .navigateToFriendsPage()
                .checkThatPageLoaded();

        int friendsListSize = friendsPage.checkFriendsTableSize();
        assertThat("'WITH_FRIEND' user must be contained 1 friend", friendsListSize, Matchers.equalTo(1));
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(UserType.Type.WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit()
                .navigateToFriendsPage()
                .checkThatPageLoaded()
                .checkThatFriendsRequestHasButtonAcceptAndDecline();
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(UserType.Type.WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit()
                .navigateToFriendsPage()
                .checkThatPageLoaded()
                .openAllPeoplesList()
                .checkOneElementHasWaitingStatus();
    }
}
