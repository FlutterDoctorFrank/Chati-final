package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketInContextInteract;
import controller.network.protocol.PacketInNotificationReply;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerIn;
import controller.network.protocol.PacketMenuOption;
import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketOutNotification;
import controller.network.protocol.PacketOutNotification.Notification;
import controller.network.protocol.PacketOutUserInfo;
import controller.network.protocol.PacketOutUserInfo.UserInfo;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketProfileAction.Action;
import controller.network.protocol.PacketVoiceMessage;
import controller.network.protocol.PacketWorldAction;
import model.context.spatial.IWorld;
import model.exception.ContextNotFoundException;
import model.exception.IllegalAccountActionException;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.exception.IllegalNotificationActionException;
import model.exception.IllegalPositionException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.NotificationNotFoundException;
import model.exception.UserNotFoundException;
import model.notification.INotification;
import model.role.IContextRole;
import model.user.AdministrativeAction;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Empfängt und verarbeitet alle eingehenden Pakete eines einzelnen Clients und sendet Pakete an diesen Client.
 */
public class UserConnection extends Listener implements PacketListenerIn, ClientSender {

    private static final Logger LOGGER = Logger.getLogger("chati.network");

    private final ServerNetworkManager manager;
    private final Connection connection;

    private IUser user;

    public UserConnection(@NotNull final ServerNetworkManager manager, @NotNull final Connection connection) {
        this.manager = manager;
        this.connection = connection;
    }

    public void send(@NotNull final Packet<?> packet) {
        if (this.connection.isConnected()) {
            // Das Netzwerkpaket für die Sprachnachrichten wird über UDP anstatt TCP versendet.
            if (packet instanceof PacketVoiceMessage) {
                this.connection.sendUDP(packet);
                return;
            }

            this.connection.sendTCP(packet);

            LOGGER.info(String.format("Sent packet %s to connection: %s", packet.getClass().getSimpleName(), this.connection.getID()));
        }
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object object) {
        this.send(action.getPacket(this.user, object));
    }

    @Override
    public void received(@NotNull final Connection connection, @NotNull final Object object) {
        // Sollte niemals der Fall sein
        if (!this.connection.equals(connection)) {
            connection.removeListener(this);
            return;
        }

        if (object instanceof Packet<?>) {
            LOGGER.info(String.format("Received packet %s from connection: %s", object.getClass().getSimpleName(), connection.getID()));

            try {
                call((Packet<?>) object, this);
            } catch (ClassCastException ex) {
                // Illegales Netzwerkpaket erhalten. Verbindung trennen.
                this.connection.close();
            } catch (Exception ex) {
                // Unerwartete Exceptions abfangen, sodass ein Fehler nicht direkt zum Absturz der Anwendung führt.
                LOGGER.log(Level.WARNING, "Unhandled exception while processing network-packet " + object.getClass().getSimpleName(), ex);
            }
        }
    }

    @Override
    public void disconnected(@NotNull final Connection connection) {
        // Sollte niemals der Fall sein
        if (!this.connection.equals(connection)) {
            connection.removeListener(this);
            return;
        }

        if (this.user != null) {
            try {
                this.manager.getAccountManager().logoutUser(this.user.getUserId());
                this.user = null;
            } catch (UserNotFoundException ex) {
                // Sollte niemals der Fall sein.
                throw new IllegalStateException("Failed to provide corresponding user-id", ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void call(@NotNull final Packet<T> packet,
                                                        @NotNull final PacketListener listener) {
        packet.call((T) listener);
    }

    @Override
    public void handle(@NotNull final PacketAvatarMove packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not move while not logged in");
            return;
        }

        if (this.user.getWorld() != null) {
            if (packet.getAction() != AvatarAction.UPDATE_AVATAR) {
                // Die Client-Anwendung darf nur die Aktion UPDATE_AVATAR versenden.
                this.logInvalidPacket(packet, "Only Avatar-Action UPDATE_AVATAR is allowed");
                return;
            }

            if (packet.getUserId() != null && !packet.getUserId().equals(this.user.getUserId())) {
                this.logInvalidPacket(packet, "User-ID must be the own or null");
                return;
            }

            try {
                this.user.move(packet.getPosX(), packet.getPosY());
            } catch (IllegalPositionException ex) {
                // Illegale Position erhalten. Sende vorherige Position.
                LOGGER.warning("User " + this.user.getUsername() + " tried illegal movement: " + ex.getMessage());

                int posX = this.user.getLocation().getPosX();
                int posY = this.user.getLocation().getPosY();

                this.send(new PacketAvatarMove(AvatarAction.UPDATE_AVATAR, this.user.getUserId(), posX, posY));
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not move while not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketChatMessage packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not chat while not logged in");
            return;
        }

        if (this.user.getWorld() != null) {
            // Das Paket muss eine Nachricht enthalten.
            if (packet.getMessage() == null) {
                this.logInvalidPacket(packet, "Message can not be null");
                return;
            }

            if (packet.getSenderId() != null && !packet.getSenderId().equals(this.user.getUserId())) {
                this.logInvalidPacket(packet, "User-ID must be the own or null");
                return;
            }

            this.user.chat(packet.getMessage());
        } else {
            this.logUnexpectedPacket(packet, "Can not chat while not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketVoiceMessage packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not talk while not logged in");
            return;
        }

        if (this.user.getWorld() != null) {
            // Überprüfung, ob gegebenenfalls eine falsche User-ID versendet wurde.
            if (packet.getSenderId() != null && !packet.getSenderId().equals(this.user.getUserId())) {
                this.logInvalidPacket(packet, "User-ID must be the own or null");
                return;
            }

            this.user.talk(packet.getVoiceData());
        } else {
            this.logUnexpectedPacket(packet, "Can not talk while not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketMenuOption packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not interact with menu while not logged in");
            return;
        }

        if (this.user.getWorld() != null) {
            try {
                this.user.executeOption(packet.getContextId(), packet.getOption(), packet.getArguments());

                // Die Menü-Option war erfolgreich. Sende Bestätigung.
                this.send(new PacketMenuOption(packet, null, true));
            }catch (IllegalMenuActionException ex) {
                // Die erhaltenen Argumente enthalten ungültige Daten. Sende Fehlernachricht.
                this.send(new PacketMenuOption(packet, ex.getClientMessageKey(), false));
            } catch (IllegalInteractionException ex) {
                // Mit dem Objekt kann nicht interagiert werden oder das Objekt existiert nicht.
                LOGGER.warning("User " + this.user.getUsername() + " tried illegal context-interaction: " + ex.getMessage());
            } catch (ContextNotFoundException e) {
                // Unbekanntes Objekt, über dem eine Menü-Aktion ausgeführt werden soll.
                LOGGER.warning("User " + this.user.getUsername() + " tried to interact with an unknown object");
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not interact with menu while not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketWorldAction packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not perform world action while not logged in");
            return;
        }

        try {
            if (packet.getAction() == PacketWorldAction.Action.CREATE) {
                if (packet.getMap() == null || packet.getName() == null) {
                    this.logInvalidPacket(packet, "Missing world-name or world-map for create action");
                    return;
                }

                this.manager.getGlobal().createWorld(this.user.getUserId(), packet.getName(), packet.getMap());
            } else {
                if (packet.getContextId() == null) {
                    this.logInvalidPacket(packet, "Missing context-id for world action");
                    return;
                }

                switch (packet.getAction()) {
                    case JOIN:
                        this.user.joinWorld(packet.getContextId());
                        return; // #joinWorld() verschickt über den ClientSender die Bestätigung.

                    case LEAVE:
                        this.user.leaveWorld();
                        return; // #leaveWorld() verschickt über den ClientSender die Bestätigung.

                    case DELETE:
                        this.manager.getGlobal().removeWorld(this.user.getUserId(), packet.getContextId());
                        break;
                }
            }

            // Aktion erfolgreich. Sende Bestätigung
            this.send(new PacketWorldAction(packet, null, true));
        } catch (IllegalWorldActionException ex) {
            // Illegale Welt-Aktion erhalten. Sende Fehlermeldung
            this.send(new PacketWorldAction(packet, ex.getClientMessageKey(), false));
        } catch (NoPermissionException ex) {
            LOGGER.info("User " + this.user.getUsername() + " is missing permission " + ex.getPermission()
                    + " for world action: " + packet.getAction().name());

            // Unzureichende Berechtigung des Benutzers. Sende zugehörige Fehlermeldung.
            this.send(new PacketWorldAction(packet, "missing.permission", false));
        } catch (ContextNotFoundException ex) {
            // Unbekannte Welt, auf die zugegriffen werden soll.
            LOGGER.warning("User " + this.user.getUsername() + " tried to access unknown world");
        } catch (UserNotFoundException ex) {
            // Sollte niemals der Fall sein.
            throw new IllegalStateException("Failed to provide corresponding user-id", ex);
        }
    }

    @Override
    public void handle(@NotNull final PacketProfileAction packet) {
        if (packet.getAction() == Action.LOGIN || packet.getAction() == Action.REGISTER) {
            if (this.user != null) {
                this.logUnexpectedPacket(packet, "Can not login/register user while already logged in");
                return;
            }

            if (packet.getName() == null || packet.getPassword() == null) {
                this.logInvalidPacket(packet, "Missing username or password for user login/register");
                return;
            }

            try {
                switch (packet.getAction()) {
                    case REGISTER:
                        this.manager.getAccountManager().registerUser(packet.getName(), packet.getPassword());

                        this.send(new PacketProfileAction(packet, null, true));
                        break;

                    case LOGIN:
                        this.user = this.manager.getAccountManager().loginUser(packet.getName(), packet.getPassword(), this);

                        LOGGER.info("Connection " + this.connection.getID() + " logged in as: " + this.user.getUsername());

                        // Login erfolgreich. Sende benötigte Informationen.
                        this.send(new PacketProfileAction(packet, this.user.getUserId(), this.user.getAvatar(), null, true));

                        // Informationen über die verfügbaren Welten.
                        final Set<PacketOutContextList.ContextInfo> worlds = new HashSet<>();
                        for (final IWorld world : this.manager.getGlobal().getWorlds().values()) {
                            worlds.add(new PacketOutContextList.ContextInfo(world.getContextId(), world.getContextName()));
                        }
                        this.send(new PacketOutContextList(this.manager.getGlobal().getContextId(), worlds));

                        // Informationen über die globalen Rollen.
                        final IContextRole role = this.user.getGlobalRoles();
                        this.send(new PacketOutContextRole(role.getContext().getContextId(), this.user.getUserId(), role.getRoles()));

                        // Informationen über die befreundeten Benutzer.
                        for (final IUser friend : this.user.getFriends().values()) {
                            this.send(new PacketOutUserInfo(null, PacketOutUserInfo.Action.UPDATE_USER,
                                    new UserInfo(friend.getUserId(), friend.getUsername(), friend.getStatus())));
                        }

                        // Informationen über die erhaltenen globalen Benachrichtigungen.
                        for (final INotification notification : this.user.getGlobalNotifications().values()) {
                            this.send(new PacketOutNotification(new Notification(notification.getNotificationId(),
                                    notification.getContext().getContextId(), notification.getMessageBundle(),
                                    notification.getTimestamp(), notification.isRequest())));
                        }
                        break;
                }
            } catch (IllegalAccountActionException ex) {
                // Illegale Profilaktion erhalten. Sende Fehlermeldung.
                this.send(new PacketProfileAction(packet, ex.getClientMessageKey(), false));
            }
        } else {
            if (this.user == null) {
                this.logUnexpectedPacket(packet, "Can not change/logout/delete user while not logged in");
                return;
            }

            try {
                switch (packet.getAction()) {
                    case CHANGE_PASSWORD:
                        if (packet.getPassword() == null || packet.getNewPassword() == null) {
                            this.logInvalidPacket(packet, "Missing passwords for change-password action");
                            return;
                        }

                        this.manager.getAccountManager().changePassword(this.user.getUserId(), packet.getPassword(), packet.getNewPassword());
                        break;

                    case CHANGE_AVATAR:
                        if (packet.getAvatar() == null) {
                            this.logInvalidPacket(packet, "Missing avatar for change-avatar action");
                            return;
                        }

                        this.user.setAvatar(packet.getAvatar());
                        break;

                    case DELETE:
                        if (packet.getPassword() == null) {
                            this.logInvalidPacket(packet, "Missing password for delete action");
                            return;
                        }

                        this.manager.getAccountManager().deleteUser(this.user.getUserId(), packet.getPassword());
                        this.user = null;
                        break;

                    case LOGOUT:
                        this.manager.getAccountManager().logoutUser(this.user.getUserId());
                        this.user = null;
                        break;
                }

                // Aktion erfolgreich. Sende Bestätigung.
                this.send(new PacketProfileAction(packet, null, true));
            } catch (IllegalAccountActionException ex) {
                // Illegale Profilaktion erhalten. Sende Fehlermeldung
                this.send(new PacketProfileAction(packet, ex.getClientMessageKey(), false));
            } catch (UserNotFoundException ex) {
                // Sollte niemals der Fall sein.
                throw new IllegalStateException("Failed to provide corresponding user-id", ex);
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketInContextInteract packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not interact with context while not logged in");
            return;
        }

        if (this.user.getWorld() != null) {
            try {
                this.user.interact(packet.getContextId());
            } catch (IllegalInteractionException ex) {
                // Mit dem Objekt kann nicht interagiert werden.
                LOGGER.warning("User " + this.user.getUsername() + " tried illegal interaction: " + ex.getMessage());
            } catch (ContextNotFoundException ex) {
                // Unbekannter Kontext, mit dem interagiert werden soll.
                LOGGER.warning("User " + this.user.getUsername() + " tried to interact with an unknown context");
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not interact with context while not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketInNotificationReply packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not interact with notification while not logged in");
            return;
        }

        try {
            switch (packet.getAction()) {
                case ACCEPT:
                    this.user.manageNotification(packet.getNotificationId(), true);
                    break;

                case DECLINE:
                    this.user.manageNotification(packet.getNotificationId(), false);
                    break;

                case DELETE:
                    this.user.deleteNotification(packet.getNotificationId());
                    break;
            }
        } catch (IllegalNotificationActionException ex) {
            // Auf die Benachrichtigung wurde eine ungültige Aktion ausgeführt.
            LOGGER.warning("User " + this.user.getUsername() + " tried illegal notification action: " + ex.getMessage());
        } catch (NotificationNotFoundException ex) {
            // Unbekannte Benachrichtigung, auf die zugegriffen werden soll.
            LOGGER.warning("User " + this.user.getUsername() + " tried to access unknown notification");
        }
    }

    @Override
    public void handle(@NotNull final PacketInUserManage packet) {
        if (this.user == null) {
            this.logUnexpectedPacket(packet, "Can not manage user while not logged in");
            return;
        }

        try {
            final AdministrativeAction action = AdministrativeAction.valueOf(packet.getAction().name());

            this.user.executeAdministrativeAction(packet.getUserId(), action, packet.getArguments());
        } catch (NoPermissionException ex) {
            // Unzureichende Berechtigung des Benutzers.
            LOGGER.info("User " + this.user.getUsername() + " is missing permission " + ex.getPermission()
                    + " for managing user: " + packet.getAction().name());
        } catch (UserNotFoundException ex) {
            // Unbekannter Benutzer, der verwaltet werden soll.
            LOGGER.warning("User " + this.user.getUsername() + " tried to manage unknown user");
        } catch (IllegalArgumentException ex) {
            // Sollte niemals der Fall sein.
            throw new IllegalStateException("Failed to provide corresponding administrative-action", ex);
        }
    }

    private void logInvalidPacket(@NotNull final Packet<?> packet, @NotNull final String message) {
        LOGGER.warning(String.format("Received invalid packet %s from connection %s: %s",
                packet.getClass().getSimpleName(), this.connection.getID(), message));
    }

    private void logUnexpectedPacket(@NotNull final Packet<?> packet, @NotNull final String message) {
        LOGGER.warning(String.format("Received unexpected packet %s from connection %s: %s",
                packet.getClass().getSimpleName(), this.connection.getID(), message));
    }
}
