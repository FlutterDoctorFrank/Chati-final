package view2;

import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface ViewControllerInterface {
    void updateWorlds(Map<ContextID, String> worlds);
    void updateRooms(ContextID worldId, Map<ContextID,String> privateRooms);
    void registrationResponse(boolean success, String messageKey);
    void loginResponse(boolean success, String messageKey);
    void passwordChangeResponse(boolean success, String messageKey);
    void deleteAccountResponse(boolean success, String messageKey);
    void avatarChangeResponse(boolean success, String messageKey);
    void createWorldResponse(boolean success, String messageKey);
    void deleteWorldResponse(boolean success, String messageKey);
    void joinWorldResponse(boolean success, String messageKey);
    void showChatMessage(UUID userID, String message, MessageType messageType, LocalDateTime timestamp);
    void playVoiceData(UUID userID, LocalDateTime timestamp, byte[] voiceData);
    void openMenu(Menu menu);
    void closeMenu(Menu menu);
    void menuActionResponse(boolean success, String messageKey);
    void logout();
}
