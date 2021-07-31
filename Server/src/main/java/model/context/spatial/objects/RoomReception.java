package model.context.spatial.objects;

import controller.network.ClientSender;
import model.context.ContextID;
import model.context.spatial.SpatialMap;
import model.context.spatial.Menu;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.notification.RoomRequest;
import model.role.Permission;
import model.role.Role;
import model.user.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Eine Klasse, welche ein Objekt repräsentiert, durch welches ein Benutzer private Räume erstellen, diesen beitreten
 * oder den Beitritt in diesen anfragen kann. Ist immer vom Typ {@link model.context.spatial.SpatialContextType#OBJECT}.
 */
public class RoomReception extends SpatialContext {

    /** Regulärer Ausdruck der das Format eines Raumnamens festlegt. */
    private static final Pattern ROOMNAME_PATTERN = Pattern.compile("^\\w{2,16}");

    /** Regulärer Ausdruck der das Format eines Raumpassworts festlegt. */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{4,32}");

    /**
     * Erzeugt eines neue Instanz der RoomReception.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     */
    public RoomReception(String objectName, SpatialContext parent) {
        super(objectName, parent, Menu.ROOM_RECEPTION_MENU, null, new HashSet<>());
    }

    @Override
    public void interact(User user) {
        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMoveable(false);
        user.getClientSender().send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        switch (menuOption) {
            case 0: // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 1: // Erzeuge einen privaten Raum.
                String roomname = args[0];
                // Prüfe, ob der übergebene Raumname das richtige Format hat.
                if (!ROOMNAME_PATTERN.matcher(roomname).matches()) {
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
                    map = SpatialMap.valueOf(args[3]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalMenuActionException("", "Die anzuzeigende Karte existiert nicht.", e);
                }
                // Erzeuge den privaten Raum, füge ihn der Welt hinzu, gebe dem erzeugenden Benutzer die Rolle des
                // Rauminhabers und teleportiere ihn in den privaten Raum.
                SpatialContext world = user.getWorld();
                SpatialContext privateRoom = new SpatialContext(roomname, world, map, password, null, new HashSet<>());
                world.addChild(privateRoom);
                world.addPrivateRoom(privateRoom);
                user.addRole(privateRoom, Role.ROOM_OWNER);
                user.teleport(privateRoom.getSpawnLocation());

                // Schließe das Menü des Benutzers.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
                break;
            case 2: // Betrete einen existierenden privaten Raum.
                ContextID roomID = new ContextID(args[0]);
                privateRoom = user.getWorld().getPrivateRoom(roomID);
                // Prüfe, ob der zu betretende Raum existiert.
                if (privateRoom == null) {
                    throw new IllegalMenuActionException("", "Der zu betretende private Raum existiert nicht.");
                }
                password = args[2];
                // Prüfe, ob das Passwort korrekt übergeben wurde.
                if (!privateRoom.checkPassword(password)) {
                    throw new IllegalMenuActionException("", "Das eingegebene Passwort ist nicht korrekt.");
                }
                // Teleportiere den Benutzer in den privaten Raum.
                user.teleport(privateRoom.getSpawnLocation());
                // Schließe das Menü beim Benutzer.
                user.setCurrentInteractable(null);
                user.setMoveable(true);
                user.getClientSender().send(ClientSender.SendAction.CLOSE_MENU, this);
            case 3: // Stelle eine Anfrage zum Beitritt eines privaten Raums.
                roomID = new ContextID(args[0]);
                privateRoom = user.getWorld().getPrivateRoom(roomID);
                // Prüfe, ob der angefragte Raum existiert.
                if (privateRoom == null) {
                    throw new IllegalMenuActionException("", "Der angefragte private Raum existiert nicht.");
                }
                // Ermittle den Rauminhaber.
                Map<UUID, User> users = privateRoom.getUsers();
                SpatialContext finalPrivateRoom = privateRoom;
                User roomOwner = users.values().stream()
                        .filter(containedUser -> containedUser.hasPermission(finalPrivateRoom, Permission.MANAGE_PRIVATE_ROOM))
                        .findFirst().orElseThrow();
                // Sende dem Rauminhaber die Beitrittsanfrage.
                RoomRequest roomRequest = new RoomRequest(roomOwner, args[1], user, privateRoom);
                roomOwner.addNotification(roomRequest);
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}
