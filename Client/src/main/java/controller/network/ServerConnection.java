package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerOut;
import controller.network.protocol.PacketMenuOption;
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

    private UUID userId;

    public ServerConnection(@NotNull final ClientNetworkManager manager) {
        this.manager = manager;

        manager.getEndPoint().addListener(this);
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

        // Die Server-Anwendung muss die Benutzer-ID setzen.
        if (packet.getUserId() != null) {
            try {
                final IUserController user = this.getUser(packet.getUserId());

                switch (packet.getAction()) {
                    case SPAWN_AVATAR:
                    case UPDATE_AVATAR:
                        user.setInCurrentRoom(true);
                        user.setPosition(packet.getPosX(), packet.getPosY());
                        break;

                    case REMOVE_AVATAR:
                        user.setInCurrentRoom(false);
                }
            } catch (UserNotFoundException ex) {
                LOGGER.warning("Server tried to move unknown user with id: " + packet.getUserId());
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketChatMessage packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive chat message while user is not logged in");
            return;
        }

        try {
            this.manager.getView().showChatMessage(packet.getSenderId(), packet.getTimestamp(), packet.getMessageType(), packet.getMessage());
        } catch (UserNotFoundException ex) {
            // Unbekannter Sender.
            LOGGER.warning("Server tried to send message from unknown sender with id: " + ex.getUserID());
        }
    }

    @Override
    public void handle(@NotNull final PacketVoiceMessage packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive voice message while user is not logged in");
            return;
        }

        this.manager.getView().playVoiceData(packet.getSenderId(), packet.getTimestamp(), packet.getVoiceData());
    }

    @Override
    public void handle(@NotNull final PacketMenuOption packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive menu option while user is not logged in");
            return;
        }

        this.manager.getView().menuActionResponse(packet.isSuccess(), packet.getMessage());
    }

    @Override
    public void handle(@NotNull final PacketWorldAction packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive world action while user is not logged in");
            return;
        }

        switch (packet.getAction()) {
            case JOIN:
                this.manager.getView().joinWorldResponse(packet.isSuccess(), packet.getMessage());
                break;

            case LEAVE:
                this.getIntern().leaveWorld();
                break;

            case CREATE:
                this.manager.getView().createWorldResponse(packet.isSuccess(), packet.getMessage());
                break;

            case DELETE:
                this.manager.getView().deleteWorldResponse(packet.isSuccess(), packet.getMessage());
                break;
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
                    this.manager.getView().registrationResponse(packet.isSuccess(), packet.getMessage());
                    break;

                case LOGIN:
                    if (packet.isSuccess()) {
                        this.manager.getUserManager().login(this.userId = packet.getUserId(),
                                packet.getName(), Status.ONLINE, packet.getAvatar());
                    }

                    this.manager.getView().loginResponse(packet.isSuccess(), packet.getMessage());
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
                        this.getIntern().setAvatar(packet.getAvatar());
                    }

                    this.manager.getView().avatarChangeResponse(packet.isSuccess(), packet.getMessage());
                    break;

                case CHANGE_PASSWORD:
                    this.manager.getView().passwordChangeResponse(packet.isSuccess(), packet.getMessage());
                    break;

                case LOGOUT:
                    this.manager.getUserManager().logout();
                    this.manager.getView().logout();
                    break;

                case DELETE:
                    this.manager.getUserManager().logout();
                    this.manager.getView().deleteAccountResponse(packet.isSuccess(), packet.getMessage());
                    break;
            }
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

        if (packet.isJoin()) {
            this.getIntern().joinRoom(packet.getContextId(), packet.getName(), packet.getMap());
        }
    }

    @Override
    public void handle(@NotNull final PacketOutContextInfo packet) {
        if (this.userId == null) {
            this.logUnexpectedPacket(packet, "Can not receive context info while user is not logged in");
            return;
        }

        try {
            this.getIntern().setMusic(packet.getContextId(), packet.getMusic());

            for (final IUserController extern : this.manager.getUserManager().getExternUsers().values()) {
                extern.setMute(packet.getContextId(), false);
            }

            for (final UUID mute : packet.getMutes()) {
                this.getExtern(mute).setMute(packet.getContextId(), true);
            }
        } catch (UserNotFoundException ex) {
            // Ein stumm zuschaltender Benutzer wurde nicht gefunden.
            LOGGER.warning("Server tried to send mute of unknown user with id: " + ex.getUserID());
        } catch (ContextNotFoundException ex) {
            // Context wurde nicht gefunden.
            LOGGER.warning("Server tried to send context info for unknown context with id: " + ex.getContextID());
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

        //TODO Die View benötigt die Information zu welchem context das Menü gehört.

        if (packet.isOpen()) {
            this.manager.getView().openMenu(packet.getMenu());
        } else {
            this.manager.getView().closeMenu(packet.getMenu());
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
                    notification.getMessage(), notification.getTimestamp(), notification.isRequest());
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
                IUserController user;

                switch (packet.getAction()) {
                    case UPDATE_USER:
                        try {
                            user = this.getUser(info.getUserId());

                            if (info.getAvatar() != null) {
                                user.setAvatar(info.getAvatar());
                            }

                            if (info.getStatus() != null) {
                                user.setStatus(info.getStatus());
                            }
                        } catch (UserNotFoundException ex) {
                            // Externer Benutzer existiert noch nicht. Füge neuen hinzu.
                            this.manager.getUserManager().addExternUser(info.getUserId(), info.getName(),
                                    info.getStatus(), info.getAvatar());
                            user = this.getExtern(info.getUserId());
                        }

                        for (final Flag flag : info.getFlags()) {
                            switch (flag) {
                                case FRIEND:
                                    user.setFriend(true);
                                    break;

                                case IGNORED:
                                    user.setIgnored(true);
                                    break;

                                case REPORTED:
                                    user.setReport(packet.getContextId(), true);
                                    break;

                                case BANNED:
                                    user.setTeleportable(false);
                                    user.setInCurrentRoom(false);
                                    // Muss das trotzdem true sein, obwohl der Benutzer nicht in der Welt sein kann? Oder auf welchen Wert muss das gesetzt werden?
                                    user.setInCurrentWorld(false);
                                    user.setBan(packet.getContextId(), true);
                                    return;
                            }
                        }

                        user.setInCurrentWorld(true);
                        user.setTeleportable(info.getTeleportTo());
                        break;

                    case REMOVE_USER:
                        user = this.getExtern(info.getUserId());
                        user.setTeleportable(info.getTeleportTo());
                        user.setInCurrentRoom(false);
                        user.setInCurrentWorld(false);

                        if (!info.getFlags().contains(Flag.FRIEND)) {
                            this.manager.getUserManager().removeExternUser(info.getUserId());
                        }
                }
            } else {
                switch (packet.getAction()) {
                    case UPDATE_USER:
                        IUserController user;

                        try {
                            user = this.getExtern(info.getUserId());

                            if (info.getStatus() != null) {
                                user.setStatus(info.getStatus());
                            }
                        } catch (UserNotFoundException ex) {
                            // Externer Benutzer existiert noch nicht. Füge neuen hinzu.
                            this.manager.getUserManager().addExternUser(info.getUserId(), info.getName(),
                                    info.getStatus(), info.getAvatar());
                            user = this.getExtern(info.getUserId());
                        }

                        for (final Flag flag : info.getFlags()) {
                            switch (flag) {
                                case FRIEND:
                                    user.setFriend(true);
                                    break;

                                case IGNORED:
                                    user.setIgnored(true);
                                    break;
                            }
                        }

                        user.setTeleportable(info.getTeleportTo());
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

    private void logUnexpectedPacket(@NotNull final Packet<?> packet, @NotNull final String message) {
        LOGGER.warning(String.format("Received unexpected packet %s from server: %s",
                packet.getClass().getSimpleName(), message));
    }
}
