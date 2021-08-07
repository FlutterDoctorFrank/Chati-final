package view2.component.menu;

import model.context.ContextID;

import java.util.Map;

public interface IMenuScreen {

    void registrationResponse(boolean success, String messageKey);
    void loginResponse(boolean success, String messageKey);
    void passwordChangeResponse(boolean success, String messageKey);
    void deleteAccountResponse(boolean success, String messageKey);
    void avatarChangeResponse(boolean success, String messageKey);
    void createWorldResponse(boolean success, String messageKey);
    void deleteWorldResponse(boolean success, String messageKey);
    void joinWorldResponse(boolean success, String messageKey);
    void updateWorlds(Map<ContextID, String> worlds);
}
