package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      username = "duck",
      spendings = {
          @Spending(
              description = "Advanced 9 поток!",
              amount = 777.00,
              category = "Обучение1"
          )
      }
  )
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(SpendJson spendJson) {
    final String newDescription = ":)";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .fillLoginPage("duck", "12345")
        .submit()
        .checkThatPageLoaded()
        .editSpending(spendJson.description())
        .setNewSpendingDescription(newDescription)
        .save()
        .checkThatTableContainsSpending(newDescription);
  }
}
