package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerOut;
import controller.network.protocol.PacketMenuOption;
import controller.network.protocol.PacketNotificationResponse;
import controller.network.protocol.PacketOutCommunicable;
import controller.network.protocol.PacketOutContextInfo;
import controller.network.protocol.PacketOutContextJoin;
import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextList.ContextInfo;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketOutMenuAction;
import controller.network.protocol.PacketOutNotification;
import controller.network.protocol.PacketOutNotification.Notification;
import controller.network.protocol.PacketOutUserInfo;
import controller.network.protocol.PacketOutUserInfo.UserInfo;
import controller.network.protocol.PacketOutUserInfo.UserInfo.Flag;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketProfileAction.Action;
import controller.network.protocol.PacketVoiceMessage;
import controller.network.protocol.PacketWorldAction;
import model.context.ContextID;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;
import model.exception.UserNotFoundException;
import model.user.IInternUserController;
import model.user.IUserController;
import model.user.Status;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Empfängt und verarbeitet alle eingehenden Pakete vom Server und sendet Pakete an den Server.
 */
public class ServerConnection extends Listener implements PacketListenerOut, ServerSender {

    private static final Logger LOGGER = Logger.getLogger("Chati-Network");

    private final ClientNetworkManager manager;

    private ContextID worldId;
    private UUID userId;

    public ServerConnection(@NotNull final ClientNetworkManager manager) {
        this.manager = manager;
    }

    public @NotNull IInternUserController getIntern() {
        return this.manager.getUserManager().getInternUserController();
    }

    public @NotNull IUserController getExtern(@NotNull final UUID userId) throws UserNotFoundException {
        return this.manager.getUserManager().getExternUserController(userId);
    }

    public @NotNull IUserController getUser(@NotNull final UUID userId) throws UserNotFoundException {
        if (userId.equals(this.userId)) {
            return this.manager.getUserManager().getInternUserController();
        }

        return this.manager.getUserManager().getExternUserController(userId);
    }

    public void send(@NotNull final Packet<?> packet) {
        if (this.manager.getEndPoint().isConnected()) {
            // Das Netzwerkpaket für die Sprachnachrichten wird über UDP anstatt TCP versendet.
            if (packet instanceof PacketVoiceMessage) {
                this.manager.getEndPoint().sendUDP(packet);
                return;
            }

            this.manager.getEndPoint().sendTCP(packet);

            if (!(packet instanceof PacketAvatarMove)) {
                LOGGER.info(String.format("Sent packet to server: %s", packet));
            }
        }
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object... objects) {
        this.send(action.getPacket(objects));
    }

    @Override
    public void received(@NotNull final Connection connection, @NotNull final Object object) {
        // Sollte niemals der Fall sein
        if (!this.manager.getEndPoint().equals(connection)) {
            connection.removeListener(this);
            return;
        }

        if (object instanceof Packet<?>) {
            if (!(object instanceof PacketAvatarMove)) {
                LOGGER.info(String.format("Received packet from server: %s", object));
            }

            try {
                call((Packet<?>) object, this);
            } catch (ClassCastException ex) {
                // Illegales Netzwerkpaket erhalten. Verbindung trennen.
                this.manager.getEndPoint().close();
            } catch (Exception ex) {
                // Unerwartete Exceptions abfangen, sodass ein Fehler nicht direkt zum Absturz der Anwendung führt.
                LOGGER.log(Level.WARNING, "Unhandled exception while processing network-packet " + object.getClass().getSimpleName(), ex);
            }
        }
    }

    @Override
    public void disconnected(@NotNull final Connection connection) {
        // Sollte niemals der Fall sein
        if (!this.manager.getEndPoint().equals(connection)) {
            connection.removeListener(this);
            return;
        }

        if (this.userId != null) {
            this.manager.getUserManager().logout();
            this.manager.getView().logout();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void call(@NotNull final Packet<T> packet,
                                                        @NotNull final PacketListener listener) {
        packet.call((T) listener);
    }

    @Override
    public void handle(@NotNull final PacketAvatarMove packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not move avatar while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            // Die Server-Anwendung muss die Benutzer-ID setzen.
            if (packet.getUserId() == null) {
                this.logInvalidPacket(packet, "User-ID can not be null");
                return;
            }

            try {
                final IUserController user = this.getUser(packet.getUserId());

                switch (packet.getAction()) {
                    case SPAWN_AVATAR:
                        if (packet.getDirection() == null) {
                            this.logInvalidPacket(packet, "Missing direction for avatar spawn");
                            return;
                        }

                        user.setInCurrentRoom(true);
                        user.setLocation(packet.getPosX(), packet.getPosY(), true, false, packet.getDirection());
                        break;

                    case MOVE_AVATAR:
                        if (packet.getDirection() == null) {
                            this.logInvalidPacket(packet, "Missing direction for avatar move");
                            return;
                        }

                        user.setLocation(packet.getPosX(), packet.getPosY(), false, packet.isSprinting(), packet.getDirection());
                        break;

                    case REMOVE_AVATAR:
                        user.setInCurrentRoom(false);
                        break;
                }
            } catch (UserNotFoundException ex) {
                LOGGER.warning("Server tried to move unknown user with id: " + packet.getUserId());
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not move avatar while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketChatMessage packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive chat message while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            if (packet.getTimestamp() == null) {
                this.logInvalidPacket(packet, "Timestamp can not be null");
                return;
            }

            if (packet.getMessageType() == null) {
                this.logInvalidPacket(packet, "MessageType can not be null");
                return;
            }

            try {
                switch (packet.getMessageType()) {
                    case WORLD:
                    case ROOM:
                    case WHISPER:
                    case STANDARD:
                        if (packet.getSenderId() == null) {
                            this.logInvalidPacket(packet, "Sender User-ID can not be null");
                            return;
                        }

                        if (packet.getMessage() == null) {
                            this.logInvalidPacket(packet, "Message can not be null");
                            return;
                        }
                        break;

                    case INFO:
                        if (packet.getBundle() == null) {
                            this.logInvalidPacket(packet, "MessageBundle can not be null");
                            return;
                        }

                        // TODO Vielleicht eine showInfoMessage(LocalDateTime timestamp, MessageBundle message) Methode?
                        // Befindet sich schon in der View. Übers Interface läuft alles über die gleiche Methode.
                        break;
                }

                this.manager.getView().showChatMessage(packet.getSenderId(), packet.getTimestamp(),
                        packet.getMessageType(), packet.getMessage(), packet.getBundle());
            } catch (UserNotFoundException ex) {
                // Unbekannter Sender.
                LOGGER.warning("Server tried to send message from unknown sender with id: " + ex.getUserID());
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not receive chat message while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketVoiceMessage packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive voice message while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            if (packet.getSenderId() == null) {
                this.logInvalidPacket(packet, "Sender User-ID can not be null");
                return;
            }

            if (packet.getTimestamp() == null) {
                this.logInvalidPacket(packet, "Timestamp can not be null");
                return;
            }

            try {
                this.manager.getView().playVoiceData(packet.getSenderId(), packet.getTimestamp(), packet.getVoiceData());
            } catch (UserNotFoundException ex) {
                // Unbekannter Sender.
                LOGGER.warning("Server tried to send voice message from unknown sender with id: " + ex.getUserID());
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not receive voice message while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketMenuOption packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive menu option while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            this.manager.getView().menuActionResponse(packet.isSuccess(),
                    packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
        } else {
            this.logUnexpectedPacket(packet, "Can not receive menu option while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketNotificationResponse packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive notification response while user is not logged in");
            return;
        }

        try {
            if (packet.getAction() != PacketNotificationResponse.Action.DELETE) {
                this.logInvalidPacket(packet, "Only Notification-Action DELETE is allowed");
                return;
            }

            this.getIntern().removeNotification(packet.getNotificationId());
        } catch (NotificationNotFoundException ex) {
            // Die angegebene Benachrichtigung wurde nicht gefunden.
            LOGGER.warning("Server tried to send notification delete for unknown notification with id: " + ex.getNotificationId());
        }
    }

    @Override
    public void handle(@NotNull final PacketWorldAction packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive world action while user is not logged in");
            return;
        }

        if (packet.getAction() == PacketWorldAction.Action.LEAVE) {
            if (this.worldId == null) {
                this.logUnexpectedPacket(packet, "Can not receive world action " + packet.getAction().name() + " while user is not in a world");
                return;
            }

            if (packet.getContextId() == null) {
                this.logInvalidPacket(packet, "Context-ID of the leaving world can not be null");
                return;
            }

            if (packet.getContextId().equals(this.worldId)) {
                this.getIntern().leaveWorld();
                this.manager.getView().leaveWorld();
                this.worldId = null;
            } else {
                this.logUnexpectedPacket(packet, "Can not leave a world that has not been entered");
            }
        } else {
            if (this.worldId != null) {
                this.logUnexpectedPacket(packet, "Can not receive world action " + packet.getAction().name() + " while user is already in a world");
                return;
            }

            switch (packet.getAction()) {
                case JOIN:
                    if (packet.getContextId() == null) {
                        this.logInvalidPacket(packet, "Context-ID of the joining world can not be null");
                        return;
                    }

                    if (packet.isSuccess()) {
                        if (packet.getName() == null) {
                            this.logInvalidPacket(packet, "Name of the joining world can not be null");
                            return;
                        }

                        this.getIntern().joinWorld(packet.getName());
                        this.worldId = packet.getContextId();
                    }

                    this.manager.getView().joinWorldResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;

                case CREATE:
                    this.manager.getView().createWorldResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;

                case DELETE:
                    this.manager.getView().deleteWorldResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketProfileAction packet) {
        if (packet.getAction() == Action.LOGIN || packet.getAction() == Action.REGISTER) {
            if (this.userId != null) {
                this.logUnexpectedPacket(packet, "Can not login/register user while user is already logged in");
                return;
            }

            switch (packet.getAction()) {
                case REGISTER:
                    this.manager.getView().registrationResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;

                case LOGIN:
                    if (packet.isSuccess()) {
                        if (packet.getUserId() == null) {
                            this.logInvalidPacket(packet, "User-ID of logged in user can not be null");
                            return;
                        }

                        if (packet.getName() == null) {
                            this.logInvalidPacket(packet, "Name of logged in user can not be null");
                            return;
                        }

                        if (packet.getAvatar() == null) {
                            this.logInvalidPacket(packet, "Avatar of logged in user can not be null");
                            return;
                        }

                        this.manager.getUserManager().login(this.userId = packet.getUserId(), packet.getName(),
                                Status.ONLINE, packet.getAvatar());
                    }

                    this.manager.getView().loginResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;
            }
        } else {
            if (this.userId == null) {
                this.logUnexpectedPacket(packet, "Can not change/logout/delete user while user is not logged in");
                return;
            }

            switch (packet.getAction()) {
                case CHANGE_AVATAR:
                    if (packet.isSuccess()) {
                        if (packet.getAvatar() == null) {
                            this.logInvalidPacket(packet, "New Avatar of changed user can not be null");
                            return;
                        }

                        this.getIntern().setAvatar(packet.getAvatar());
                    }

                    this.manager.getView().avatarChangeResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;

                case CHANGE_PASSWORD:
                    this.manager.getView().passwordChangeResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;

                case LOGOUT:
                    this.manager.getUserManager().logout();
                    this.manager.getView().logout();
                    this.worldId = null;
                    this.userId = null;
                    break;

                case DELETE:
                    this.manager.getUserManager().logout();
                    this.worldId = null;
                    this.userId = null;
                    this.manager.getView().deleteAccountResponse(packet.isSuccess(),
                            packet.getMessage() != null ? packet.getMessage().getMessageKey() : null);
                    break;
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketOutCommunicable packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive communicable users while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            for (final IUserController extern : this.manager.getUserManager().getExternUsers().values()) {
                extern.setCommunicable(false);
            }

            for (final UUID userId : packet.getCommunicables()) {
                try {
                    this.getExtern(userId).setCommunicable(true);
                } catch (UserNotFoundException ex) {
                    // Der Benutzer mit der momentanen ID ist dem Model nicht bekannt.
                    LOGGER.warning("Server tried to send unknown communicable user with id: " + userId);
                }
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not receive communicable users while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketOutContextList packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive context list while user is not logged in");
            return;
        }

        try {
            final Map<ContextID, String> contexts = new HashMap<>();

            for (final ContextInfo info : packet.getInfos()) {
                contexts.put(info.getContextId(), info.getName());
            }

            if (packet.getContextId() != null) {
                this.manager.getView().updateRooms(packet.getContextId(), contexts);
                return;
            }

            this.manager.getView().updateWorlds(contexts);
        } catch (ContextNotFoundException ex) {
            // Der übergeordnete Kontext wurde nicht gefunden.
            LOGGER.warning("Server tried to send rooms info for unknown world with id: " + ex.getContextID());
        }
    }

    @Override
    public void handle(@NotNull final PacketOutContextJoin packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not join context while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            if (packet.isJoin()) {
                if (packet.getName() == null) {
                    this.logInvalidPacket(packet, "Name of joined context can not be null");
                    return;
                }

                if (packet.getMap() == null) {
                    this.logInvalidPacket(packet, "Map of joined context can not be null");
                    return;
                }

                this.getIntern().joinRoom(packet.getContextId(), packet.getName(), packet.getMap());
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not join context while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketOutContextInfo packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive context info while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            try {
                final IInternUserController user = this.getIntern();

                user.setMusic(packet.getContextId(), packet.getMusic());
                user.setMute(packet.getContextId(), false);

                for (final IUserController extern : this.manager.getUserManager().getExternUsers().values()) {
                    extern.setMute(packet.getContextId(), false);
                }

                for (final UUID mute : packet.getMutes()) {
                    if (mute.equals(this.userId)) {
                        user.setMute(packet.getContextId(), true);
                        continue;
                    }

                    this.getExtern(mute).setMute(packet.getContextId(), true);
                }
            } catch (UserNotFoundException ex) {
                // Ein stumm zuschaltender Benutzer wurde nicht gefunden.
                LOGGER.warning("Server tried to send mute of unknown user with id: " + ex.getUserID());
            } catch (ContextNotFoundException ex) {
                // Context wurde nicht gefunden.
                LOGGER.warning("Server tried to send context info for unknown context with id: " + ex.getContextID());
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not receive context info while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketOutContextRole packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive context role while user is not logged in");
            return;
        }

        try {
            this.getUser(packet.getUserId()).setRoles(packet.getContextId(), new HashSet<>(Arrays.asList(packet.getRoles())));
        } catch (UserNotFoundException ex) {
            // Der Benutzer der Kontext-Rolle wurde nicht gefunden.
            LOGGER.warning("Server tried to send context-role of unknown user with id: " + ex.getUserID());
        } catch (ContextNotFoundException ex) {
            // Context wurde nicht gefunden.
            LOGGER.warning("Server tried to send context role for unknown context with id: " + ex.getContextID());
        }
    }

    @Override
    public void handle(@NotNull final PacketOutMenuAction packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive menu action while user is not logged in");
            return;
        }

        if (this.worldId != null) {
            if (packet.isOpen()) {
                this.manager.getView().openMenu(packet.getContextId(), packet.getMenu());
            } else {
                this.manager.getView().closeMenu(packet.getContextId(), packet.getMenu());
            }
        } else {
            this.logUnexpectedPacket(packet, "Can not receive menu action while user is not in a world");
        }
    }

    @Override
    public void handle(@NotNull final PacketOutNotification packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive notification while user is not logged in");
            return;
        }

        try {
            final Notification notification = packet.getNotification();

            this.getIntern().addNotification(notification.getContextId(), notification.getNotificationId(),
                    notification.getMessage(), notification.getTimestamp(), notification.getType());
        } catch (ContextNotFoundException ex) {
            // Context wurde nicht gefunden.
            LOGGER.warning("Server tried to send notification for unknown context with id: " + ex.getContextID());
        }
    }

    @Override
    public void handle(@NotNull final PacketOutUserInfo packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive user info while user is not logged in");
            return;
        }

        try {
            final UserInfo info = packet.getInfo();

            // Ist eine Kontext-ID im Paket gesetzt, so handelt es sich um einen Benutzer innerhalb der aktuellen Welt.
            if (packet.getContextId() != null) {
                if (!packet.getContextId().equals(this.worldId)) {
                    this.logUnexpectedPacket(packet, "Can not receive user info of a world that has not been entered");
                    return;
                }

                IUserController user;

                switch (packet.getAction()) {
                    case UPDATE_USER:
                        try {
                            user = this.getUser(info.getUserId());
                            user.setStatus(info.getStatus());

                            if (info.getAvatar() != null) {
                                user.setAvatar(info.getAvatar());
                            }
                        } catch (UserNotFoundException ex) {
                            // Externer Benutzer existiert noch nicht. Füge neuen hinzu.
                            this.manager.getUserManager().addExternUser(info.getUserId(), info.getName(),
                                    info.getStatus(), info.getAvatar());
                            user = this.getExtern(info.getUserId());
                        }

                        user.setFriend(info.getFlags().contains(Flag.FRIEND));

                        if (info.getFlags().contains(Flag.BANNED)) {
                            user.setInCurrentWorld(false);
                            user.setBan(packet.getContextId(), true);
                            return;
                        }

                        user.setIgnored(info.getFlags().contains(Flag.IGNORED));
                        user.setReport(packet.getContextId(), info.getFlags().contains(Flag.REPORTED));
                        user.setInPrivateRoom(info.getInPrivateRoom());
                        user.setInCurrentWorld(true);
                        break;

                    case REMOVE_USER:
                        this.manager.getUserManager().removeExternUser(info.getUserId());
                        break;
                }
            } else {
                switch (packet.getAction()) {
                    case UPDATE_USER:
                        IUserController user;

                        try {
                            user = this.getUser(info.getUserId());
                            user.setStatus(info.getStatus());
                        } catch (UserNotFoundException ex) {
                            // Externer Benutzer existiert noch nicht. Füge neuen hinzu.
                            this.manager.getUserManager().addExternUser(info.getUserId(), info.getName(),
                                    info.getStatus(), info.getAvatar());
                            user = this.getExtern(info.getUserId());
                        }

                        user.setFriend(info.getFlags().contains(Flag.FRIEND));
                        user.setIgnored(info.getFlags().contains(Flag.IGNORED));
                        user.setInPrivateRoom(info.getInPrivateRoom());
                        user.setInCurrentWorld(false);
                        break;

                    case REMOVE_USER:
                        this.manager.getUserManager().removeExternUser(info.getUserId());
                        break;
                }
            }
        } catch (UserNotFoundException ex) {
            // Benutzer wurde nicht gefunden.
            LOGGER.warning("Server tried to send user info of unknown user with id: " + ex.getUserID());
        } catch (ContextNotFoundException ex) {
            // Context wurde nicht gefunden.
            LOGGER.warning("Server tried to send user info for unknown context with id: " + ex.getContextID());
        }
    }

    private void logInvalidPacket(@NotNull final Packet<?> packet, @NotNull final String message) {
        LOGGER.warning(String.format("Received invalid packet %s from server: %s",
                packet.getClass().getSimpleName(), message));
    }

    private void logUnexpectedPacket(@NotNull final Packet<?> packet, @NotNull final String message) {
        LOGGER.warning(String.format("Received unexpected packet %s from server: %s",
                packet.getClass().getSimpleName(), message));
    }
}
