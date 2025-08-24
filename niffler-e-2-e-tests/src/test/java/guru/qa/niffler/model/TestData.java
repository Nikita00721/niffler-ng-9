package guru.qa.niffler.model;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public record TestData(
    @Nonnull String password,
    @Nonnull List<guru.qa.niffler.model.UserJson> friends,
    @Nonnull List<guru.qa.niffler.model.UserJson> incomeInvitations,
    @Nonnull List<guru.qa.niffler.model.UserJson> outcomeInvitations,
    @Nonnull List<guru.qa.niffler.model.CategoryJson> categories,
    @Nonnull List<guru.qa.niffler.model.SpendJson> spendings
) {

  public TestData(String password) {
    this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public TestData(String password, List<guru.qa.niffler.model.UserJson> friends, List<guru.qa.niffler.model.UserJson> incomeInvitations, List<guru.qa.niffler.model.UserJson> outcomeInvitations) {
    this(password, friends, incomeInvitations, outcomeInvitations, new ArrayList<>(), new ArrayList<>());
  }

  @Nonnull
  public TestData addCategories(List<guru.qa.niffler.model.CategoryJson> categories) {
    return new TestData(
        this.password,
        this.friends,
        this.incomeInvitations,
        this.outcomeInvitations,
        categories,
        this.spendings
    );
  }

  @Nonnull
  public TestData addSpendings(List<SpendJson> spendings) {
    return new TestData(
        this.password,
        this.friends,
        this.incomeInvitations,
        this.outcomeInvitations,
        this.categories,
        spendings
    );
  }

  @Nonnull
  public String[] friendsUsernames() {
    return extractUsernames(friends);
  }

  @Nonnull
  public String[] incomeInvitationsUsernames() {
    return extractUsernames(incomeInvitations);
  }

  @Nonnull
  public String[] outcomeInvitationsUsernames() {
    return extractUsernames(outcomeInvitations);
  }

  @Nonnull
  public String[] categoryDescriptions() {
    return categories.stream().map(CategoryJson::name).toArray(String[]::new);
  }

  @Nonnull
  private String[] extractUsernames(List<guru.qa.niffler.model.UserJson> users) {
    return users.stream().map(UserJson::username).toArray(String[]::new);
  }
}
