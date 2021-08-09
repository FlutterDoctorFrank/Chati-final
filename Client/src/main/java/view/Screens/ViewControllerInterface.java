package view.Screens;

import controller.network.ServerSender;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContextType;
import model.exception.UserNotFoundException;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface ViewControllerInterface {

    /**
     * Setzt den ServerSender, über den die View den Controller über die zu sendenden Aktionen benachrichtigen kann.
     * @param sender der sender für die view, oder null, falls keine Verbindung zum Server besteht.
     */
    void setSender(@Nullable final ServerSender sender);

    /**
     * Aktualisiert die Liste aller Welten.
     * @param worlds: Ein HashSet mit der Menge der IDs aller Welten, sowie dem
     * zugehörigen Namen der Welten.
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    void updateWorlds(Map<ContextID, String> worlds);

    /**
     * Aktualisiert die Liste aller privaten Räume in einer Welt.
     * @param worldId: ID der Welt, in der die Liste der privaten Räume aktualisiert
     * werden soll.
     * @param privateRooms: Ein HashSet mit der Menge der IDs aller privaten Räume, sowie dem zugehörigen Namen des Raums.
     * @throws ContextNotFoundException : wenn keine Welt mit der ID existiert.
     * @see SpatialContext
     * @see SpatialContextType#ROOM
     */
    void updateRooms(ContextID worldId, Map<ContextID,String> privateRooms) throws ContextNotFoundException;

    void registrationResponse(boolean success, String messageKey);
    void loginResponse(boolean success, String messageKey);
    void passwordChangeResponse(boolean success, String messageKey);
    void deleteAccountResponse(boolean success, String messageKey);
    void avatarChangeResponse(boolean success, String messageKey);
    void createWorldResponse(boolean success, String messageKey);
    void deleteWorldResponse(boolean success, String messageKey);
    void joinWorldResponse(boolean success, String messageKey);
    void showChatMessage(UUID userID, LocalDateTime timestamp, MessageType messageType, String message) throws UserNotFoundException;
    void playVoiceData(UUID userID, LocalDateTime timestamp, byte[] voiceData);
    void openMenu(Menu menu);
    void closeMenu(Menu menu);
    void menuActionResponse(boolean success, String messageKey);
    void logout();
}
