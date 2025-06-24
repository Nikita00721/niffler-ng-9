package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @Category(
            name = "CatyArc_",
            username = "duck",
            isArchive = false
    )
    @Test
    public void archiveCategory() {
        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage("duck", "12345")
                .submit()
                .navigateToProfilePage()
                .checkThatPageLoaded();

        int initialSize = profilePage.getCategorySize();
        profilePage.archiveFirstCat()
                .assertCategoriesSize(initialSize - 1);
    }

    @Category(
            name = "CatUnarch_",
            username = "duck",
            isArchive = false
    )
    @Test
    public void unarchiveCategory() {
        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage("duck", "12345")
                .submit()
                .navigateToProfilePage()
                .checkThatPageLoaded();

        int initialSize = profilePage.getCategorySize();
        profilePage.switchArchiveVision()
                .unarchiveCat()
                .switchArchiveVision()
                .assertCategoriesSize(initialSize + 1);
    }
}
