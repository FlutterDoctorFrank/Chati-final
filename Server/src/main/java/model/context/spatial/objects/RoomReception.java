package model.context.spatial.objects;

import controller.network.ClientSender;
import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.*;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.notification.RoomRequest;
import model.role.Permission;
import model.role.Role;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer private Räume erstellen, diesen beitreten
 * oder den Beitritt in diesen anfragen kann. Ist immer vom Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class RoomReception extends Interactable {

    /** Regulärer Ausdruck der das Format eines Raumnamens festlegt. */
    private static final Pattern ROOMNAME_PATTERN = Pattern.compile("^\\w{2,16}");

    /** Regulärer Ausdruck der das Format eines Raumpassworts festlegt. */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{4,32}");

    /**
     * Erzeugt eines neue Instanz der RoomReception.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public RoomReception(@NotNull final String objectName, @NotNull final Area parent,
                         @NotNull final CommunicationRegion communicationRegion,
                         @NotNull final Set<CommunicationMedium> communicationMedia, @NotNull final Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, Menu.ROOM_RECEPTION_MENU);
    }

    @Override
    public void interact(@NotNull final User user) {
        throwIfUserNotAvailable(user);

        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMoveable(false);
        user.send(SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                  @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        throwIfUserNotAvailable(user);

        switch (menuOption) {
            case 0: // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.send(SendAction.CLOSE_MENU, this);
                break;
            case 1: // Erzeuge einen privaten Raum.
                if (args.length < 3) {
                    throw new IllegalMenuActionException("", "Die angegeben Argument sind nicht ausreichend.");
                }

                String createRoomName = args[0];
                // Prüfe, ob der übergebene Raumname das richtige Format hat.
                if (!ROOMNAME_PATTERN.matcher(createRoomName).matches()) {
                    throw new IllegalMenuActionException("", "Die eingegebene Raumbezeichnung hat nicht das richtige Format.");
                }
                String password = args[1];
                // Prüfe, ob das übergebene Passwort das richtige Format hat.
                if (!PASSWORD_PATTERN.matcher(password).matches()) {
                    throw new IllegalMenuActionException("", "Das eingegebene Passwort hat nicht das richtige Format.");
                }
                // Ermittle die Karte, die der private Raum haben soll.
                SpatialMap map;
                try {
                    map = SpatialMap.valueOf(args[2]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalMenuActionException("", "Die anzuzeigende Karte existiert nicht.", e);
                }
                // Erzeuge den privaten Raum, füge ihn der Welt hinzu, gebe dem erzeugenden Benutzer die Rolle des
                // Rauminhabers und teleportiere ihn in den privaten Raum.
                World world = user.getWorld();

                if (world == null) {
                    throw new IllegalStateException("Users world is not available");
                }

                Room privateRoom = new Room(createRoomName, world, map, password);
                world.addPrivateRoom(privateRoom);

                // Schließe das Menü des Benutzers.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.send(ClientSender.SendAction.CLOSE_MENU, this);
                user.teleport(privateRoom.getSpawnLocation());
                user.addRole(privateRoom, Role.ROOM_OWNER);

                // Sende an alle Benutzer, die gerade das Menü einer Rezeption geöffnet haben, die Liste aller
                // privaten Räume.
                world.getUsers().values().stream()
                        .filter(receiver -> receiver.getCurrentMenu() == Menu.ROOM_RECEPTION_MENU)
                        .forEach(receiver -> receiver.send(SendAction.CONTEXT_LIST, world));
                break;
            case 2: // Betrete einen existierenden privaten Raum.
                if (args.length < 2) {
                    throw new IllegalMenuActionException("", "Die angegeben Argument sind nicht ausreichend.");
                }

                if (user.getWorld() == null) {
                    throw new IllegalStateException("Users world is not available");
                }

                // Prüfe, ob der zu betretende Raum existiert.
                String joinRoomName = args[0];
                try {
                    privateRoom = user.getWorld().getPrivateRooms().values().stream()
                            .filter(room -> room.getContextName().equals(joinRoomName))
                            .findFirst().orElseThrow();
                } catch (NoSuchElementException e) {
                    throw new IllegalMenuActionException("", "Der zu betretende private Raum existiert nicht.", e);
                }

                password = args[2];
                // Prüfe, ob das Passwort korrekt übergeben wurde.
                if (!privateRoom.checkPassword(password)) {
                    throw new IllegalMenuActionException("", "Das eingegebene Passwort ist nicht korrekt.");
                }
                // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.send(SendAction.CLOSE_MENU, this);
                user.teleport(privateRoom.getSpawnLocation());
                break;
            case 3: // Stelle eine Anfrage zum Beitritt eines privaten Raums.
                if (args.length < 2) {
                    throw new IllegalMenuActionException("", "Die angegeben Argument sind nicht ausreichend.");
                }

                if (user.getWorld() == null) {
                    throw new IllegalStateException("Users world is not available");
                }

                String requestedRoomName = args[0];
                Room requestedPrivateRoom;
                try {
                    requestedPrivateRoom = user.getWorld().getPrivateRooms().values().stream()
                            .filter(room -> room.getContextName().equals(requestedRoomName))
                            .findFirst().orElseThrow();
                } catch (NoSuchElementException e) {
                    throw new IllegalMenuActionException("", "Der angefragte private Raum existiert nicht.", e);
                }
                // Ermittle den Rauminhaber.
                Map<UUID, User> users = requestedPrivateRoom.getUsers();
                User roomOwner;
                try {
                    roomOwner = users.values().stream()
                            .filter(containedUser -> containedUser.hasPermission(requestedPrivateRoom, Permission.MANAGE_PRIVATE_ROOM))
                            .findFirst().orElseThrow();
                } catch (NoSuchElementException e) {
                    throw new IllegalStateException("This private room does not have a room owner.", e);
                }
                // Sende dem Rauminhaber die Beitrittsanfrage.
                RoomRequest roomRequest = new RoomRequest(roomOwner, args[1], user, requestedPrivateRoom);
                roomOwner.addNotification(roomRequest);
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}