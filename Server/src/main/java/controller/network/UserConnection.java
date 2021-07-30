package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import controller.network.protocol.PacketInContextInteract;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerIn;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketProfileAction.Action;
import controller.network.protocol.PacketWorldAction;
import model.exception.ContextNotFoundException;
import model.exception.IllegalAccountActionException;
import model.exception.IllegalInteractionException;
import model.exception.IllegalPositionException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.UserNotFoundException;
import model.user.AdministrativeAction;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;
import java.util.logging.Logger;

/**
 * Empfängt und verarbeitet alle eingehenden Pakete eines einzelnen Clients und sendet Pakete an diesen Client.
 */
public class UserConnection extends Listener implements PacketListenerIn, ClientSender {

    private static final Logger LOGGER = Logger.getLogger("Chati-Network");

    private final ServerNetworkManager manager;
    private final Connection connection;

    private IUser user;

    public UserConnection(@NotNull final ServerNetworkManager manager, @NotNull final Connection connection) {
        this.manager = manager;
        this.connection = connection;

        connection.addListener(this);
    }

    public void send(@NotNull final Packet<?> packet) {
        if (this.connection.isConnected()) {
            this.connection.sendTCP(packet);
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
            try {
                call((Packet<?>) object, this);
            } catch (ClassCastException ex) {
                // Illegales Netzwerkpaket erhalten. Verbindung trennen.
                this.connection.close();
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
        if (this.user != null && this.user.getWorld() != null) {
            // Die Client-Anwendung darf nur die Aktion UPDATE_AVATAR versenden.
            if (packet.getAction() == AvatarAction.UPDATE_AVATAR) {
                // Überprüfung, ob gegebenenfalls eine falsche User-ID versendet wurde.
                if (packet.getUserId() != null && !packet.getUserId().equals(this.user.getUserId())) {
                    return;
                }

                try {
                    this.user.move(packet.getPosX(), packet.getPosY());
                } catch (IllegalPositionException ex) {
                    // Illegale Position. Sende vorherige Position.
                    LOGGER.warning("Received illegal movement from user " + this.user.getUsername() + ": " + ex.getMessage());

                    int posX = this.user.getLocation().getPosX();
                    int posY = this.user.getLocation().getPosY();

                    this.send(new PacketAvatarMove(AvatarAction.UPDATE_AVATAR, this.user.getUserId(), posX, posY));
                }
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketWorldAction packet) {
        if (this.user != null) {
            try {
                if (packet.getAction() == PacketWorldAction.Action.CREATE) {
                    if (packet.getMap() != null && packet.getName() != null) {
                        this.manager.getGlobal().createWorld(this.user.getUserId(), packet.getName(), packet.getMap());

                        // Erstellung erfolgreich. Sende Bestätigung.
                        this.send(new PacketWorldAction(packet, null, true));
                    } else {
                        LOGGER.warning("Received world-create action with missing map/name from user: " + this.user.getUsername());
                    }
                } else {
                    if (packet.getContextId() != null) {
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

                        //Aktion erfolgreich. Sende Bestätigung
                        this.send(new PacketWorldAction(packet, null, true));
                    } else {
                        LOGGER.warning("Received world action with missing context-id from user: " + this.user.getUsername());
                    }
                }
            } catch (IllegalWorldActionException ex) {
                // Illegale Welt-Aktion.
                LOGGER.warning("Received illegal world action from user " + this.user.getUsername() + ": " + ex.getMessage());
                this.send(new PacketWorldAction(packet, ex.getClientMessageKey(), false));
            } catch (NoPermissionException ex) {
                // Unzureichende Berechtigung. Sende zugehörige Fehlermeldung.
                LOGGER.fine("User " + this.user.getUsername() + " is missing permission " + ex.getPermission() + " for world action: " + packet.getAction().name());
                this.send(new PacketWorldAction(packet, "missing.permission", false));
            } catch (ContextNotFoundException ex) {
                // Ungültige Welt, auf die Zugegriffen wird.
                LOGGER.fine("User " + this.user.getUsername() + " tried to access non-existing world");
            } catch (UserNotFoundException ex) {
                // Sollte niemals der Fall sein.
                throw new IllegalStateException("Failed to provide corresponding user-id", ex);
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketProfileAction packet) {
        if (packet.getAction() == Action.LOGIN || packet.getAction() == Action.REGISTER) {
            if (this.user == null) {
                if (packet.getName() != null && packet.getPassword() != null) {
                    try {
                        switch (packet.getAction()) {
                            case REGISTER:
                                this.manager.getAccountManager().registerUser(packet.getName(), packet.getPassword());
                                break;

                            case LOGIN:
                                this.user = this.manager.getAccountManager().loginUser(packet.getName(), packet.getPassword(), this);
                                break;
                        }

                        this.send(new PacketProfileAction(packet, null, true));
                    } catch (IllegalAccountActionException ex) {
                        LOGGER.warning("Received illegal profile action from connection " + this.connection.getID() + ": " + ex.getMessage());
                        this.send(new PacketProfileAction(packet, ex.getClientMessageKey(), false));
                    }
                } else {
                    LOGGER.warning("Received login/register action with missing name/password from connection: " + this.connection.getID());
                }
            } else {
                LOGGER.fine("User " + this.user.getUsername() + " tried to login/register while already logged in");
            }
        } else {
            if (this.user != null) {
                try {
                    switch (packet.getAction()) {
                        case CHANGE_PASSWORD:
                            if (packet.getPassword() != null && packet.getNewPassword() != null) {
                                this.manager.getAccountManager().changePassword(this.user.getUserId(), packet.getPassword(), packet.getNewPassword());
                            } else {
                                LOGGER.warning("Received change-password action with missing passwords from user: " + this.user.getUsername());
                                return;
                            }
                            break;

                        case CHANGE_AVATAR:
                            if (packet.getAvatar() != null) {
                                this.user.setAvatar(packet.getAvatar());
                            } else {
                                LOGGER.warning("Received change-avatar action with missing avatar from user: " + this.user.getUsername());
                                return;
                            }
                            break;

                        case DELETE:
                            if (packet.getPassword() != null) {
                                this.manager.getAccountManager().deleteUser(this.user.getUserId(), packet.getPassword());
                            } else {
                                LOGGER.warning("Received change-password action with missing password from user: " + this.user.getUsername());
                                return;
                            }
                            break;

                        case LOGOUT:
                            this.manager.getAccountManager().logoutUser(this.user.getUserId());
                            break;
                    }

                    // Aktion erfolgreich. Sende Bestätigung.
                    this.send(new PacketProfileAction(packet, null, true));
                } catch (IllegalAccountActionException ex) {
                    LOGGER.warning("Received illegal profile action from user " + this.user.getUsername() + ": " + ex.getMessage());
                    this.send(new PacketProfileAction(packet, ex.getClientMessageKey(), false));
                } catch (UserNotFoundException ex) {
                    // Sollte niemals der Fall sein.
                    throw new IllegalStateException("Failed to provide corresponding user-id", ex);
                }
            } else {
                LOGGER.fine("Connection " + this.connection.getID() + " tried to logout/delete while not logged in");
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketInContextInteract packet) {
        if (this.user != null) {
            try {
                this.user.interact(packet.getContextId());
            } catch (IllegalInteractionException ex) {
                // Objekt wurde nicht gefunden oder der Benutzer interagiert bereits mit einem Objekt.
                // Verbindung trennen?
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketInUserManage packet) {
        if (this.user != null) {
            try {
                final AdministrativeAction action = AdministrativeAction.valueOf(packet.getAction().name());

                this.user.executeAdministrativeAction(packet.getUserId(), action, packet.getArguments());
            } catch (NoPermissionException ex) {
                // Unzureichende Berechtigung.
                LOGGER.fine("User " + this.user.getUsername() + " is missing permission " + ex.getPermission() + " for managing user: " + packet.getAction().name());
            } catch (UserNotFoundException ex) {
                // Ungültiger Benutzer, der verwaltet werden soll.
                LOGGER.fine("User " + this.user.getUsername() + " tried to manage non-existing user");
            } catch (IllegalArgumentException ex) {
                // Sollte niemals der Fall sein.
                throw new IllegalStateException("Failed to provide corresponding administrative-action", ex);
            }
        } else {
            LOGGER.fine("Connection " + this.connection.getID() + " tried to manage user while not logged in");
        }
    }
}
