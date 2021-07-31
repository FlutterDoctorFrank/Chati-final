package controller.network;

import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import controller.network.protocol.PacketOutContextInfo;
import controller.network.protocol.PacketOutContextJoin;
import controller.network.protocol.PacketOutContextMusic;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketOutMenuAction;
import controller.network.protocol.PacketOutUserInfo;
import controller.network.protocol.PacketOutUserInfo.Action;
import controller.network.protocol.PacketOutUserInfo.UserInfo;
import controller.network.protocol.PacketWorldAction;
import model.context.IContext;
import model.context.global.IGlobalContext;
import model.context.spatial.ISpatialContext;
import model.role.IContextRole;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Eine Schnittstelle, welche als Beobachter für das Model dient.
 */
public interface ClientSender {

    /**
     * Teilt dem Controller mit, welche Information sich am Model geändert hat. Sendet das zur Aktion zugehörige
     * Paket an den Client.
     * @param action Die Aktion die gesendet werden soll.
     * @param object Die Information die sich geändert hat.
     */
    void send(@NotNull final SendAction action, @NotNull final Object object);

    /**
     * Eine Enumeration über die verschiedenen Informationen, die vom Model aus an den Client gesendet werden können.
     */
    enum SendAction {

        /**
         * Information, dass neue Informationen über Benutzer versendet werden sollen.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link model.user.IUser}
         * </p>
         */
        USER_INFO {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IUser) {
                    final IUser other = (IUser) object;

                    if (user.getWorld() != null) {
                        final ISpatialContext world = user.getWorld();
                        final UserInfo info = new UserInfo(other.getUserId(), other.getUsername(), other.getStatus());

                        if (user.getFriends().containsKey(other.getUserId())) {
                            info.addFlag(UserInfo.Flag.FRIEND);
                        }

                        if (world.equals(other.getWorld())) {
                            info.setAvatar(other.getAvatar());

                            if (user.getIgnoredUsers().containsKey(other.getUserId())) {
                                info.addFlag(UserInfo.Flag.IGNORED);
                            }

                            if (world.getReportedUsers().containsKey(other.getUserId())) {
                                info.addFlag(UserInfo.Flag.REPORTED);
                            }

                            //TODO Kontexte in Benutzerinformation setzen, in denen der Benutzer stumm geschaltet ist

                            return new PacketOutUserInfo(world.getContextId(), Action.UPDATE_USER, info);
                        }

                        if (world.getBannedUsers().containsKey(other.getUserId())) {
                            info.addFlag(UserInfo.Flag.BANNED);

                            return new PacketOutUserInfo(world.getContextId(), Action.UPDATE_USER, info);
                        }

                        return new PacketOutUserInfo(world.getContextId(), Action.REMOVE_USER, info);
                    } else {
                        if (user.getFriends().containsKey(other.getUserId())) {
                            final UserInfo info = new UserInfo(other.getUserId(), other.getUsername());

                            info.addFlag(UserInfo.Flag.FRIEND);
                            info.setStatus(other.getStatus());

                            return new PacketOutUserInfo(null, Action.UPDATE_USER, info);
                        }

                        return new PacketOutUserInfo(null, Action.REMOVE_USER, new UserInfo(other.getUserId()));
                    }
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass die verfügbaren Welten oder private Räume an den Client versendet werden sollen.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IGlobalContext} oder {@link ISpatialContext} mit
         *     {@link model.context.spatial.SpatialContextType#WORLD}
         * </p>
         */
        CONTEXT_INFO {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IContext) {
                    final Set<PacketOutContextInfo.Info> infos = new HashSet<>();

                    if (object instanceof IGlobalContext) {
                        for (final ISpatialContext world : ((IGlobalContext) object).getWorlds().values()) {
                            infos.add(new PacketOutContextInfo.Info(world.getContextId(), world.getContextName()));
                        }
                    } else {
                        for (final ISpatialContext room : ((ISpatialContext) object).getPrivateRooms().values()) {
                            infos.add(new PacketOutContextInfo.Info(room.getContextId(), room.getContextName()));
                        }
                    }

                    return new PacketOutContextInfo(((IContext) object).getContextId(), infos);
                } else {
                    throw new IllegalArgumentException("Expected IGlobalContext or ISpatialContext, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine Welt beziehungsweise ein Raum betreten werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link ISpatialContext} mit
         *     {@link model.context.spatial.SpatialContextType#WORLD} oder
         *     {@link model.context.spatial.SpatialContextType#ROOM}
         * </p>
         */
        CONTEXT_JOIN {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof ISpatialContext) {
                    final ISpatialContext room = (ISpatialContext) object;

                    if (user.getWorld() != null) {
                        if (room.getMap() == null) {
                            throw new IllegalArgumentException("Context without a Map can not be joined");
                        }

                        return new PacketOutContextJoin(room.getContextId(), room.getMap());
                    }

                    return new PacketOutContextJoin(room.getContextId(), null);
                } else {
                    throw new IllegalArgumentException("Expected ISpatialContext, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass sich die Rolle innerhalb eines Kontextes geändert hat.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IContextRole}
         * </p>
         */
        CONTEXT_ROLE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IContextRole) {
                    final IContextRole role = (IContextRole) object;

                    //TODO Die korrekte Benutzer-ID setzen, für den die Rolle gilt. (IContextRole braucht einen Benutzer?)
                    return new PacketOutContextRole(role.getContext().getContextId(), user.getUserId(), role.getRoles());
                } else {
                    throw new IllegalArgumentException("Expected IContextRole, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass sich die Musik innerhalb eines Kontextes geändert hat.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link ISpatialContext}
         * </p>
         */
        CONTEXT_MUSIC {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof ISpatialContext) {
                    final ISpatialContext area = (ISpatialContext) object;

                    return new PacketOutContextMusic(area.getContextId(), area.getMusic());
                } else {
                    throw new IllegalArgumentException("Expected ISpatialContext, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass die Position eines Avatars im Raum aktualisiert werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link model.user.IUser}
         * </p>
         */
        AVATAR_MOVE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser self, @NotNull final Object object) {
                if (object instanceof IUser) {
                    final IUser other = (IUser) object;
                    int posX = other.getLocation().getPosX();
                    int posY = other.getLocation().getPosY();
                    AvatarAction action;

                    if (self.getLocation().getRoom().equals(other.getLocation().getRoom())) {
                        action = AvatarAction.UPDATE_AVATAR;
                    } else {
                        action = AvatarAction.SPAWN_AVATAR;
                    }

                    return new PacketAvatarMove(action, other.getUserId(), posX, posY);
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass ein Avatar aus dem aktuellen Raum entfernt werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link model.user.IUser}
         * </p>
         */
        AVATAR_REMOVE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser self, @NotNull final Object object) {
                if (object instanceof IUser) {
                    final IUser other = (IUser) object;
                    int posX = other.getLocation().getPosX();
                    int posY = other.getLocation().getPosY();

                    return new PacketAvatarMove(AvatarAction.REMOVE_AVATAR, other.getUserId(), posX, posY);
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass ein Menü geöffnet werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link ISpatialContext}
         * </p>
         */
        OPEN_MENU {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof ISpatialContext) {
                    final ISpatialContext interact = (ISpatialContext) object;

                    //TODO Überprüfung ob es sich bei dem räumlichen Kontext um ein Objekt handelt

                    return new PacketOutMenuAction(interact.getContextId(), null, true);
                } else {
                    throw new IllegalArgumentException("Expected ISpatialContext, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass ein geöffnetes Menü geschlossen werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link ISpatialContext}
         * </p>
         */
        CLOSE_MENU {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof ISpatialContext) {
                    final ISpatialContext interact = (ISpatialContext) object;

                    //TODO Überprüfung ob es sich bei dem räumlichen Kontext um ein Objekt handelt

                    return new PacketOutMenuAction(interact.getContextId(), null, false);
                } else {
                    throw new IllegalArgumentException("Expected ISpatialContext, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine Welt betreten oder verlassen werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link ISpatialContext}
         * </p>
         */
        WORLD_ACTION {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof ISpatialContext) {
                    final ISpatialContext world = (ISpatialContext) object;

                    //TODO: Check for Context-Type

                    final PacketWorldAction.Action action = world.getIUsers().containsKey(user.getUserId())
                            ? PacketWorldAction.Action.JOIN : PacketWorldAction.Action.LEAVE;

                    return new PacketWorldAction(action, world.getContextId(), null, true);
                } else {
                    throw new IllegalArgumentException("Expected ISpatialContext, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine neue Benachrichtigung gesendet werden soll.
         */
        NOTIFICATION {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                //TODO Verpackung des Notification-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass eine Chat-Nachricht gesendet werden soll.
         */
        MESSAGE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                //TODO Verpackung des ChatMessage-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        };

        /**
         * Verarbeitet das übergebene Objekt und verpackt die zu sendenden Informationen in ein Paket.
         * @param object Das Informationsobjekt, welches die benötigten Informationen beinhaltet.
         * @return Das erzeugte Paket.
         */
        protected abstract @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object);
    }
}
