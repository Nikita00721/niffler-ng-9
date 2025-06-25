package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    private final SelenideElement friendsTable = $("#simple-tabpanel-friends");
    private final ElementsCollection friendsList = $$("#friends tr");
    private final SelenideElement buttonAcceptFriend = $(byText("Accept"));
    private final SelenideElement buttonDeclineFriend = $(byText("Decline"));
    private final SelenideElement waitingLabel = $(byText("Waiting..."));
    private final SelenideElement allPeopleLink = $("a.link.nav-link[href='/people/all']");

    public FriendsPage checkThatPageLoaded() {
        friendsTable.shouldBe(visible);
        return this;
    }

    public int checkFriendsTableSize () {
        return friendsList.size();
    }

    public FriendsPage checkThatFriendsRequestHasButtonAcceptAndDecline() {
        buttonAcceptFriend.shouldBe(visible);
        buttonDeclineFriend.shouldBe(visible);
        return this;
    }

    public FriendsPage openAllPeoplesList() {
        allPeopleLink.click();
        return this;
    }

    public FriendsPage checkOneElementHasWaitingStatus() {
        waitingLabel.shouldBe(visible);
        return this;
    }
}
