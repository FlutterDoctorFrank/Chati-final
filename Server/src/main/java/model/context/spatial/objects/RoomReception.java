package model.context.spatial.objects;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Map;
import model.context.spatial.SpatialContext;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.notification.RoomRequest;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;

import java.util.UUID;
import java.util.regex.Pattern;

public class RoomReception extends SpatialContext {

    private static final Pattern ROOMNAME_PATTERN = Pattern.compile("^\\w{2,16}");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{4,32}");

    protected RoomReception(String contextName, Context parent, java.util.Map<ContextID, SpatialContext> children) {
        super(contextName, parent, children);
    }

    @Override
    public void interact(User user) {
        user.setCurrentInteractable(this);
        // send menu open packet
    }

    @Override
    public void executeMenuOption(User user, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        switch (menuOption) {
            case 0:
                user.setCurrentInteractable(null);
                // Send packet for menu close
                break;
            case 1:
                String roomname = args[0];
                if (!ROOMNAME_PATTERN.matcher(roomname).matches()) {
                    throw new IllegalMenuActionException("", "Die eingegebene Raumbezeichnung hat nicht das richtige Format.");
                }
                String password = args[1];
                if (!PASSWORD_PATTERN.matcher(password).matches()) {
                    throw new IllegalMenuActionException("", "Das eingegebene Passwort hat nicht das richtige Format.");
                }
                Map map;
                try {
                    map = Map.valueOf(args[3]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalMenuActionException("", "Die anzuzeigende Karte existiert nicht.", e);
                }
                // create private room & add it to worlds private rooms
                // add role room owner
                // teleport to private room
                user.setCurrentInteractable(null);
                // send packet for menu close
                break;
            case 2:
                ContextID roomID = new ContextID(args[0]);
                SpatialContext privateRoom = user.getWorld().getPrivateRoom(roomID);
                if (privateRoom == null) {
                    throw new IllegalMenuActionException("", "Der zu betretende private Raum existiert nicht.");
                }
                password = args[2];
                if (!privateRoom.checkPassword(password)) {
                    throw new IllegalMenuActionException("", "Das eingegebene Passwort ist nicht korrekt.");
                }
                user.teleport(privateRoom.getSpawnLocation());
            case 3:
                roomID = new ContextID(args[0]);
                privateRoom = user.getWorld().getPrivateRoom(roomID);
                if (privateRoom == null) {
                    throw new IllegalMenuActionException("", "Der angefragte private Raum existiert nicht.");
                }
                java.util.Map<UUID, User> users = privateRoom.getContainedUsers();
                SpatialContext ownerRoom = privateRoom;
                User roomOwner = UserAccountManager.getInstance()
                        .getUsersWithPermission(privateRoom, Permission.MANAGE_PRIVATE_ROOM).entrySet().stream()
                        .findFirst().get().getValue();
                RoomRequest roomRequest = new RoomRequest(roomOwner, roomOwner.getWorld(), args[1], user, privateRoom);
                // send notification
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }
}
