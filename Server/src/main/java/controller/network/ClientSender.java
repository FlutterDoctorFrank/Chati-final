package controller.network;

import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketNotificationResponse;
import controller.network.protocol.PacketOutCommunicable;
import controller.network.protocol.PacketOutContextInfo;
import controller.network.protocol.PacketOutContextJoin;
import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketOutMenuAction;
import controller.network.protocol.PacketOutNotification;
import controller.network.protocol.PacketOutNotification.Notification;
import controller.network.protocol.PacketOutUserInfo;
import controller.network.protocol.PacketOutUserInfo.Action;
import controller.network.protocol.PacketOutUserInfo.UserInfo;
import controller.network.protocol.PacketOutUserInfo.UserInfo.Flag;
import controller.network.protocol.PacketUserTyping;
import controller.network.protocol.PacketAudioMessage;
import controller.network.protocol.PacketWorldAction;
import model.communication.message.ITextMessage;
import model.communication.message.IAudioMessage;
import model.communication.message.MessageType;
import model.context.IContext;
import model.context.global.IGlobalContext;
import model.context.spatial.Direction;
import model.context.spatial.IArea;
import model.context.spatial.IRoom;
import model.context.spatial.IWorld;
import model.context.spatial.objects.IInteractable;
import model.notification.INotification;
import model.notification.NotificationAction;
import model.role.IContextRole;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
         * Information, dass die Benutzer, mit denen kommuniziert werden kann, versendet werden sollen.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IUser} (Der eigene Benutzer)
         * </p>
         */
        COMMUNICABLES {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IUser) {
                    final IUser other = (IUser) object;

                    if (!other.equals(user)) {
                        throw new IllegalArgumentException("Can not send communicable users for an another user");
                    }

                    return new PacketOutCommunicable(other.getCommunicableIUsers().keySet().stream()
                            .filter(Predicate.not(user.getUserId()::equals)).collect(Collectors.toSet()));
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass neue Informationen über Benutzer versendet werden sollen.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IUser}
         * </p>
         */
        USER_INFO {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IUser) {
                    final IUser other = (IUser) object;

                    if (user.getWorld() != null) {
                        final IWorld world = user.getWorld();
                        final UserInfo info = new UserInfo(other.getUserId(), other.getUsername(), other.getStatus());

                        if (user.getFriends().containsKey(other.getUserId())) {
                            info.addFlag(Flag.FRIEND);
                        }

                        if (user.getIgnoredUsers().containsKey(other.getUserId())) {
                            info.addFlag(Flag.IGNORED);
                        }

                        if (world.equals(other.getWorld())) {
                            info.setAvatar(other.getAvatar());

                            if (other.getLocation() != null) {
                                info.setInPrivateRoom(other.getLocation().getRoom().isPrivate());
                            }

                            if (world.getReportedUsers().containsKey(other.getUserId())) {
                                info.addFlag(Flag.REPORTED);
                            }

                            return new PacketOutUserInfo(world.getContextId(), Action.UPDATE_USER, info);
                        }

                        if (world.getBannedUsers().containsKey(other.getUserId())) {
                            info.addFlag(Flag.BANNED);

                            return new PacketOutUserInfo(world.getContextId(), Action.UPDATE_USER, info);
                        }

                        return new PacketOutUserInfo(null, info.getFlags().contains(Flag.FRIEND) ?
                                Action.UPDATE_USER : Action.REMOVE_USER, info);
                    } else {
                        if (user.equals(other)) {
                            final UserInfo info = new UserInfo(user.getUserId(), user.getUsername(), other.getStatus());

                            return new PacketOutUserInfo(null, Action.UPDATE_USER, info);
                        }

                        if (user.getFriends().containsKey(other.getUserId())) {
                            final UserInfo info = new UserInfo(other.getUserId(), other.getUsername());

                            info.setStatus(other.getStatus());
                            info.addFlag(Flag.FRIEND);

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
         *     Erwartet als Objekt die Schnittstelle: {@link IGlobalContext} oder {@link IWorld}
         * </p>
         */
        CONTEXT_LIST {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IContext) {
                    final Set<PacketOutContextList.ContextInfo> infos = new HashSet<>();

                    if (object instanceof IGlobalContext) {
                        for (final IWorld world : ((IGlobalContext) object).getIWorlds().values()) {
                            infos.add(new PacketOutContextList.ContextInfo(world.getContextId(), world.getContextName()));
                        }

                        return new PacketOutContextList(null, infos);
                    } else {
                        for (final IRoom room : ((IWorld) object).getPrivateRooms().values()) {
                            infos.add(new PacketOutContextList.ContextInfo(room.getContextId(), room.getContextName()));
                        }
                    }

                    return new PacketOutContextList(((IContext) object).getContextId(), infos);
                } else {
                    throw new IllegalArgumentException("Expected IGlobalContext or IWorld, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine Welt beziehungsweise ein Raum betreten werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IRoom}
         * </p>
         */
        CONTEXT_JOIN {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IRoom) {
                    final IRoom room = (IRoom) object;

                    if (user.getWorld() != null) {
                        return new PacketOutContextJoin(room.getContextId(), room.getContextName(), room.getMap());
                    }

                    return new PacketOutContextJoin(room.getContextId(), null, null);
                } else {
                    throw new IllegalArgumentException("Expected IRoom, got " + object.getClass());
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

                    return new PacketOutContextRole(role.getContext().getContextId(), role.getUser().getUserId(), role.getRoles());
                } else {
                    throw new IllegalArgumentException("Expected IContextRole, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass sich die Musik innerhalb eines Kontextes geändert hat.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IArea}
         * </p>
         */
        CONTEXT_INFO {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IArea) {
                    final IArea area = (IArea) object;
                    final Set<UUID> mutes = new HashSet<>();

                    for (final IUser mute : area.getMutedUsers().values()) {
                        mutes.add(mute.getUserId());
                    }

                    return new PacketOutContextInfo(area.getContextId(), area.getMusic(), mutes);
                } else {
                    throw new IllegalArgumentException("Expected IArea, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass die Position eines Avatars im Raum gesetzt werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IUser}
         * </p>
         */
        AVATAR_SPAWN {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IUser) {
                    final IUser other = (IUser) object;

                    if (other.getLocation() == null) {
                        throw new IllegalStateException("User has no available location");
                    }

                    final Direction direction = other.getLocation().getDirection();
                    final float posX = other.getLocation().getPosX();
                    final float posY = other.getLocation().getPosY();

                    return new PacketAvatarMove(AvatarAction.SPAWN_AVATAR, other.getUserId(), direction,
                            posX, posY, false, other.isMovable());
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass die Position eines Avatars im Raum aktualisiert werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IUser}
         * </p>
         */
        AVATAR_MOVE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser self, @NotNull final Object object) {
                if (object instanceof IUser) {
                    final IUser other = (IUser) object;

                    if (self.getLocation() == null || other.getLocation() == null) {
                        throw new IllegalStateException("User has no available location");
                    }

                    final Direction direction = other.getLocation().getDirection();
                    final float posX = other.getLocation().getPosX();
                    final float posY = other.getLocation().getPosY();

                    return new PacketAvatarMove(AvatarAction.MOVE_AVATAR, other.getUserId(), direction,
                            posX, posY, other.isSprinting(), other.isMovable());
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass ein Avatar aus dem aktuellen Raum entfernt werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IUser}
         * </p>
         */
        AVATAR_REMOVE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser self, @NotNull final Object object) {
                if (object instanceof IUser) {
                    return new PacketAvatarMove(((IUser) object).getUserId());
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass ein Menü geöffnet werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IInteractable}
         * </p>
         */
        OPEN_MENU {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IInteractable) {
                    final IInteractable interactable = (IInteractable) object;

                    return new PacketOutMenuAction(interactable.getContextId(), interactable.getMenu(), true);
                } else {
                    throw new IllegalArgumentException("Expected IInteractable, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass ein geöffnetes Menü geschlossen werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IInteractable}
         * </p>
         */
        CLOSE_MENU {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IInteractable) {
                    final IInteractable interactable = (IInteractable) object;

                    return new PacketOutMenuAction(interactable.getContextId(), interactable.getMenu(), false);
                } else {
                    throw new IllegalArgumentException("Expected ISpatialContext, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine Welt betreten oder verlassen werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IWorld}
         * </p>
         */
        WORLD_ACTION {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IWorld) {
                    final IWorld world = (IWorld) object;
                    final PacketWorldAction.Action action = world.equals(user.getWorld())
                            ? PacketWorldAction.Action.JOIN : PacketWorldAction.Action.LEAVE;

                    return new PacketWorldAction(action, world.getContextId(), world.getContextName(), null, true);
                } else {
                    throw new IllegalArgumentException("Expected IWorld, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine neue Benachrichtigung gesendet werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link INotification}
         * </p>
         */
        NOTIFICATION {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof INotification) {
                    final INotification notification = (INotification) object;
                    final Notification info = new Notification(notification.getNotificationId(),
                            notification.getContext().getContextId(), notification.getMessageBundle(),
                            notification.getTimestamp(), notification.getNotificationType());

                    info.setAccepted(notification.isAccepted());
                    info.setDeclined(notification.isDeclined());
                    info.setRead(notification.isRead());

                    return new PacketOutNotification(info);
                } else {
                    throw new IllegalArgumentException("Expected INotification, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine Benachrichtigung gelöscht werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link INotification}
         * </p>
         */
        NOTIFICATION_DELETE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof INotification) {
                    final INotification notification = (INotification) object;

                    return new PacketNotificationResponse(notification.getNotificationId(), NotificationAction.DELETE);
                } else {
                    throw new IllegalArgumentException("Expected INotification, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass ein Benutzer gerade tippt.
         * <p>
         *     Erwartet als Object die Schnittstelle: {@link IUser}
         * </p>
         */
        TYPING {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IUser) {
                    return new PacketUserTyping(((IUser) object).getUserId());
                } else {
                    throw new IllegalArgumentException("Expected IUser, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine Chat-Nachricht gesendet werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link ITextMessage}
         * </p>
         */
        MESSAGE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof ITextMessage) {
                    final ITextMessage message = (ITextMessage) object;

                    if (message.getMessageType() != MessageType.INFO) {
                        if (message.getSender() == null || message.getTextMessage() == null) {
                            throw new IllegalArgumentException("Expected Sender and Message from ITextMessage, got null");
                        }

                        return new PacketChatMessage(message.getMessageType(), message.getSender().getUserId(),
                                message.getTextMessage(), message.getTimestamp());
                    }

                    if (message.getMessageBundle() == null) {
                        throw new IllegalArgumentException("Expected MessageBundle from ITextMessage, got null");
                    }

                    return new PacketChatMessage(message.getMessageBundle(), message.getTimestamp());
                } else {
                    throw new IllegalArgumentException("Expected ITextMessage, got " + object.getClass());
                }
            }
        },

        /**
         * Information, dass eine Audio-Nachricht gesendet werden soll.
         * <p>
         *     Erwartet als Objekt die Schnittstelle: {@link IAudioMessage}
         * </p>
         */
        AUDIO {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final IUser user, @NotNull final Object object) {
                if (object instanceof IAudioMessage) {
                    final IAudioMessage message = (IAudioMessage) object;

                    if (message.getSender() != null) {
                        return new PacketAudioMessage(message.getSender().getUserId(), message.getTimestamp(),
                                message.getAudioData());
                    }

                    return new PacketAudioMessage(message.getTimestamp(), message.getAudioData());
                } else {
                    throw new IllegalArgumentException("Expected IAudioMessage, got " + object.getClass());
                }
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
