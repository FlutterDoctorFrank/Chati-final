package view.Screens;

import model.communication.message.MessageType;
import model.context.spatial.Menu;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ViewControllerInterface {

    void registrationResponse(boolean success, String messageKey);
    void loginResponse(boolean success, String messageKey);
    void passwordChangeResponse(boolean success, String messageKey);
    void deleteAccountResponse(boolean success, String messageKey);
    void avatarChangeResponse(boolean success, String messageKey);
    void createWorldResponse(boolean success, String messageKey);
    void deleteWorldResponse(boolean success, String messageKey);
    void joinWorldResponse(boolean success, String messageKey);
    void showChatMessage(UUID userID, LocalDateTime timestamp, MessageType messageType);
    void playVoiceData(UUID userID, LocalDateTime timestamp, byte[] voiceData);
    void openMenu(Menu menu);
    void closeMenu(Menu menu);
    void menuActionResponse(boolean success, String messageKey);
}
