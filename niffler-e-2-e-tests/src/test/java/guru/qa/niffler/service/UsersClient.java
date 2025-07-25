package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {
    UserJson createUser(String username, String password) throws Exception;

    void addIncomeInvitation(UserJson targetUser, int count) throws Exception;

    void addOutcomeInvitation(UserJson targetUser, int count) throws Exception;

    void addFriend(UserJson targetUser, int count) throws Exception;
}