package model.context.spatial.objects;

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
 * oder den Beitritt in diesen anfragen kann.
 */
public class RoomReception extends Interactable {

    /** Menü-Option zum Erzeugen eines privaten Raums. */
    private static final int MENU_OPTION_CREATE = 1;

    /** Menü-Option zum Betreten eines privaten Raums. */
    private static final int MENU_OPTION_JOIN = 2;

    /** Menü-Option zum Anfragen zum Beitritt eines privaten Raums. */
    private static final int MENU_OPTION_REQUEST = 3;

    /** Regulärer Ausdruck der das Format eines Raumnamens festlegt. */
    private static final Pattern ROOMNAME_PATTERN = Pattern.compile("^\\w{2,16}");

    /** Regulärer Ausdruck der das Format eines Raumpassworts festlegt. */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{4,32}");

    /**
     * Erzeugt eine neue Instanz der RoomReception.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public RoomReception(@NotNull final String objectName, @NotNull final Area parent,
                         @NotNull final CommunicationRegion communicationRegion,
                         @NotNull final Set<CommunicationMedium> communicationMedia, @NotNull final Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, ContextMenu.ROOM_RECEPTION_MENU);
    }

    @Override
    public void interact(@NotNull final User user) {
        throwIfUserNotAvailable(user);

        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMovable(false);
        user.send(SendAction.OPEN_MENU, this);

        // Sende die Liste aller privaten Räume an den Benutzer.
        user.send(SendAction.CONTEXT_LIST, world);
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                  @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        super.executeMenuOption(user, menuOption, args);

        switch (menuOption) {
            case MENU_OPTION_CREATE: // Erzeuge einen privaten Raum.
                if (args.length < 3) {
                    throw new IllegalMenuActionException("", "object.arguments.to-few");
                }

                String createRoomName = args[0];
                // Prüfe, ob der übergebene Raumname das richtige Format hat.
                if (!ROOMNAME_PATTERN.matcher(createRoomName).matches()) {
                    throw new IllegalMenuActionException("", "object.room-reception.illegal-name");
                }
                String password = args[1];
                // Prüfe, ob das übergebene Passwort das richtige Format hat.
                if (!PASSWORD_PATTERN.matcher(password).matches()) {
                    throw new IllegalMenuActionException("", "object.room-reception.illegal-password");
                }
                // Ermittle die Karte, die der private Raum haben soll.
                ContextMap map;
                try {
                    map = ContextMap.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new IllegalMenuActionException("", ex, "object.room-reception.map-not-found", args[2]);
                }
                // Prüfe, ob bereits ein privater Raum mit diesem Namen existiert.
                World world = user.getWorld();
                if (world == null) {
                    throw new IllegalStateException("Users world is not available");
                }
                if (world.getPrivateRooms()
                        .values().stream().anyMatch(room -> room.getContextName().equals(createRoomName))) {
                    throw new IllegalMenuActionException("", "object.room-reception.already-created", createRoomName);
                }

                // Erzeuge den privaten Raum, füge ihn der Welt hinzu, gebe dem erzeugenden Benutzer die Rolle des
                // Rauminhabers und teleportiere ihn in den privaten Raum.

                Room privateRoom = new Room(createRoomName, world, map, password);
                privateRoom.build();
                world.addPrivateRoom(privateRoom);

                // Schließe das Menü des Benutzers.
                user.setCurrentInteractable(null);
                user.setMovable(true);
                user.send(SendAction.CLOSE_MENU, this);
                user.teleport(privateRoom.getSpawnLocation());
                user.addRole(privateRoom, Role.ROOM_OWNER);

                // Sende an alle Benutzer, die gerade das Menü einer Rezeption geöffnet haben, die Liste aller
                // privaten Räume.
                world.getUsers().values().stream()
                        .filter(receiver -> receiver.getCurrentMenu() == ContextMenu.ROOM_RECEPTION_MENU)
                        .forEach(receiver -> receiver.send(SendAction.CONTEXT_LIST, world));
                break;
            case MENU_OPTION_JOIN: // Betrete einen existierenden privaten Raum.
                if (args.length < 2) {
                    throw new IllegalMenuActionException("", "object.arguments.to-few");
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
                } catch (NoSuchElementException ex) {
                    throw new IllegalMenuActionException("", ex, "object.room-reception.room-not-found", joinRoomName);
                }

                // Prüfe, ob sich der Benutzer bereits in diesem Raum befindet.
                if (privateRoom.contains(user)) {
                    throw new IllegalMenuActionException("", "object.room-reception.already-joined", privateRoom.getContextName());
                }

                // Prüfe, ob der Benutzer die Berechtigung besitzt, den Raum ohne Passworteingabe zu betreten.
                if (!user.hasPermission(privateRoom, Permission.ENTER_PRIVATE_ROOM)) {
                    password = args[1];
                    // Prüfe, ob das Passwort korrekt übergeben wurde.
                    if (!privateRoom.checkPassword(password)) {
                        throw new IllegalMenuActionException("", "object.room-reception.invalid-password");
                    }
                }

                // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMovable(true);
                user.send(SendAction.CLOSE_MENU, this);
                user.teleport(privateRoom.getSpawnLocation());
                break;
            case MENU_OPTION_REQUEST: // Stelle eine Anfrage zum Beitritt eines privaten Raums.
                if (args.length < 2) {
                    throw new IllegalMenuActionException("", "object.arguments.to-few");
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
                } catch (NoSuchElementException ex) {
                    throw new IllegalMenuActionException("", ex, "object.room-reception.room-not-found", requestedRoomName);
                }

                // Prüfe, ob sich der Benutzer bereits in diesem Raum befindet.
                if (requestedPrivateRoom.contains(user)) {
                    throw new IllegalMenuActionException("", "object.room-reception.already-joined", requestedPrivateRoom.getContextName());
                }

                // Ermittle den Rauminhaber.
                Map<UUID, User> users = requestedPrivateRoom.getUsers();
                User roomOwner;
                try {
                    roomOwner = users.values().stream()
                            .filter(containedUser -> containedUser.hasPermission(requestedPrivateRoom, Permission.MANAGE_PRIVATE_ROOM))
                            .findFirst().orElseThrow();
                } catch (NoSuchElementException ex) {
                    throw new IllegalStateException("This private room does not have a room owner.", ex);
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