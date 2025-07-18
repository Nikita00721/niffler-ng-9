package guru.qa.niffler.model;

import jaxb.userdata.FriendshipStatus;

import java.util.UUID;

public interface IUserJson {
  UUID id();

  String username();

  String firstname();

  String surname();

  CurrencyValues currency();

  String photo();

  String photoSmall();

  FriendshipStatus friendshipStatus();
}
