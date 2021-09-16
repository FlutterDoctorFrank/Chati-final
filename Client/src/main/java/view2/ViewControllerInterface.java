package view2;

import controller.network.ServerSender;
import model.MessageBundle;
import model.exception.ContextNotFoundException;
import model.communication.message.MessageType;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Ein Interface, welche dem Controller Methoden zum Benachrichtigen der View bereitstellt.
 */
public interface ViewControllerInterface {

    /**
     * Setzt den ServerSender, über den die View den Controller über die zu sendenden Aktionen benachrichtigen kann.
     * @param sender Der Sender für die View oder null, falls keine Verbindung zum Server besteht.
     */
    void setSender(@Nullable final ServerSender sender);

    /**
     * Setzt die verfügbaren Welten, die der Benutzer betreten kann.
     * @param worlds Verfügbare Welten.
     */
    void updateWorlds(@NotNull final Map<ContextID, String> worlds);

    /**
     * Setzt die innerhalb einer Welt verfügbaren privaten Räume, die der Benutzer betreten kann.
     * @param worldId ID der übergeordneten Welt der privaten Räume.
     * @param privateRooms Verfügbare private Räume.
     * @throws ContextNotFoundException falls der Benutzer nicht in einer Welt mit der ID ist.
     */
    void updateRooms(@NotNull final ContextID worldId, @NotNull final Map<ContextID, String> privateRooms) throws ContextNotFoundException;

    /**
     * Benachrichtigt die View, ob eine Registrierung von einem Benutzer erfolgreich beim Server durchgeführt wurde.
     * @param success true, falls die Registrierung erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void registrationResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, ob eine Anmeldung von einem Benutzer erfolgreich beim Server durchgeführt wurde oder nicht.
     * @param success true, falls die Anmeldung erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void loginResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, ob das Zurücksetzen des Passworts von einem Benutzer erfolgreich beim Server durchgeführt
     * wurde.
     * @param success true, falls das Zurücksetzen erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void passwordChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, ob das Löschen des Accounts von einem Benutzer erfolgreich beim Server durchgeführt wurde.
     * @param success true, falls das Löschen erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void deleteAccountResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, ob der Wechsel des Avatars eines Benutzers erfolgreich beim Server durchgeführt wurde.
     * @param success true, falls der Wechsel erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void avatarChangeResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, ob die Erstellung der Welt von einem Benutzer erfolgreich beim Server durchgeführt wurde.
     * @param success true, falls die Erstellung erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void createWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, ob das Löschen der Welt von einem Benutzer erfolgreich beim Server durchgeführt wurde.
     * @param success true, falls das Löschen erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void deleteWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, ob der Beitritt einer Welt von einem Benutzer erfolgreich beim Server durchgeführt wurde.
     * @param success true gesetzt, falls der Beitritt erfolgreich war, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void joinWorldResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, dass ein Benutzer im Chat als tippend angezeigt werden soll.
     * @param userId Die ID des als tippend anzuzeigenden Benutzers.
     */
    void showTypingUser(@NotNull final UUID userId);

    /**
     * Benachrichtigt die View, dass eine neue Chatnachricht erhalten wurde.
     * @param userId ID des Benutzers, der diese Nachricht gesendet hat.
     * @param timestamp Zeitpunkt, an dem diese Nachricht gesendet wurde.
     * @param messageType Typ der Nachricht. Spezifiziert, wie die Nachricht angezeigt werden soll.
     * @param message Nachricht, die angezeigt werden soll.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID gefunden werden konnte.
     */
    void showChatMessage(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                         @NotNull final MessageType messageType, @NotNull final String message) throws UserNotFoundException;

    /**
     * Benachrichtigt die View, dass eine neue Informationsnachricht erhalten wurde.
     * @param timestamp Zeitpunkt, an dem diese Nachricht gesendet wurde.
     * @param messageBundle Übersetzbare Nachricht mit ihren benötigten Argument.
     */
    void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle);

    /**
     * Benachrichtigt die View, dass ein neues Voice-Paket erhalten wurde.
     * @param userId ID des Benutzers, der dieses Paket gesendet hat.
     * @param timestamp Zeitpunkt, an dem dieses Paket gesendet wurde.
     * @param voiceData Sprachdaten, die abgespielt werden sollen.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID gefunden werden konnte.
     */
    void playVoiceData(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp, final byte[] voiceData) throws UserNotFoundException;

    /**
     * Benachrichtigt die View, dass ein neues Musikdaten-Paket erhalten wurde.
     * @param timestamp Zeitpunkt, an dem dieses Paket gesendet wurde.
     * @param musicData Musikdaten, die abgespielt werden sollen.
     */
    void playMusicData(@NotNull final LocalDateTime timestamp, final byte[] musicData);

    /**
     * Benachrichtigt die View, dass das Menü eines Interaktionsobjekts geöffnet werden soll.
     * @param contextId ID des Interaktionsobjekts, dessen Menü geöffnet werden soll.
     * @param menu Menü, dass geöffnet werden soll.
     */
    void openMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu menu);

    /**
     * Benachrichtigt die View, dass das aktuell offene Menü eines Interaktionsobjekts geschlossen werden soll.
     * @param contextId ID des Interaktionsobjekts, dessen Menü geschlossen werden soll.
     * @param menu Menü, dass geschlossen werden soll.
     */
    void closeMenu(@NotNull final ContextID contextId, @NotNull final ContextMenu menu);

    /**
     * Benachrichtigt die View über den Erfolg einer durchgeführten Menü-Aktion und die anzuzeigende Nachricht.
     * @param success true, falls die Menü-Aktion erfolgreich durchgeführt wurde, sonst false.
     * @param messageBundle Die Kennung der Nachricht, die dem Benutzer angezeigt werden soll.
     */
    void menuActionResponse(final boolean success, @Nullable final MessageBundle messageBundle);

    /**
     * Veranlasst das Abmelden des aktuell angemeldeten Benutzerkontos in der View.
     */
    void logout();

    /**
     * Veranlasst das Verlassen der Welt, in der sich der aktuell angemeldete Benutzer befindet.
     */
    void leaveWorld();
}
