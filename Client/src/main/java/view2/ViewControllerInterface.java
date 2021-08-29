package view2;

import controller.network.ServerSender;
import model.MessageBundle;
import model.exception.ContextNotFoundException;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.exception.UserNotFoundException;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface ViewControllerInterface {

    /**
     * Setzt den ServerSender, über den die View den Controller über die zu sendenden Aktionen benachrichtigen kann.
     * @param sender Der sender für die view, oder null, falls keine Verbindung zum Server besteht.
     */
    void setSender(@Nullable final ServerSender sender);
    void updateWorlds(Map<ContextID, String> worlds);
    void updateRooms(ContextID worldId, Map<ContextID, String> privateRooms) throws ContextNotFoundException;
    void registrationResponse(boolean success, String messageKey);
    void loginResponse(boolean success, String messageKey);
    void passwordChangeResponse(boolean success, String messageKey);
    void deleteAccountResponse(boolean success, String messageKey);
    void avatarChangeResponse(boolean success, String messageKey);
    void createWorldResponse(boolean success, String messageKey);
    void deleteWorldResponse(boolean success, String messageKey);
    void joinWorldResponse(boolean success, String messageKey);
    void showChatMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String message, MessageBundle messageBundle) throws UserNotFoundException;
    void playVoiceData(UUID userId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException;
    void openMenu(ContextID contextId, Menu menu);
    void closeMenu(ContextID contextId, Menu menu);
    void menuActionResponse(boolean success, String messageKey);
    void logout();
    void leaveWorld();
}
