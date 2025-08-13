package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.extension.ClientResolver;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

@ExtendWith(ClientResolver.class)
public class JdbcTest {

  private SpendClient spendClient;
  private UsersClient usersClient;

  @Test
  void txTest() throws Exception {

    SpendJson spend = spendClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "cat-name-tx-3",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-tx-3",
            "duck"
        )
    );

    System.out.println(spend);
  }


  static UsersDbClient usersDbClient = new UsersDbClient();

  @ValueSource(strings = {
      "valentin-11"
  })
  @ParameterizedTest
  void springJdbcTest(String uname) throws Exception {
    UserJson user = usersDbClient.createUser(
        uname,
        "12345"
    );

    usersDbClient.addIncomeInvitation(user, 1);
    usersDbClient.addOutcomeInvitation(user, 1);
    usersDbClient.addFriend(user, 1);
  }
}
