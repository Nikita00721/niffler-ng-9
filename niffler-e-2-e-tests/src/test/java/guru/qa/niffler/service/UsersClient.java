package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.List;

public interface UsersClient {
    UserJson createUser(String username, String password) throws Exception;

    List<UserJson> addIncomeInvitation(UserJson targetUser, int count) throws Exception;

    List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) throws Exception;

    List<UserJson> addFriend(UserJson targetUser, int count) throws Exception;
}