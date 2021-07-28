package model.user;

import model.MessageBundle;
import model.context.Context;
import model.context.global.GlobalContext;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import java.lang.IllegalStateException;
import model.exception.NoPermissionException;
import model.notification.FriendRequest;
import model.notification.Notification;
import model.notification.RoomInvitation;
import model.role.Permission;
import model.role.Role;
import model.user.account.UserAccountManager;

import java.util.Map;
import java.util.UUID;

public enum AdministrativeAction {
    INVITE_FRIEND {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            if (performer.isFriend(target) || target.isFriend(performer)) {
                throw new IllegalStateException("Users are already friends.");
            }
            if (target.isIgnoring(performer)) {
                return;
            }
            if (performer.isIgnoring(target)) {
                performer.unignoreUser(target);
            }
            FriendRequest friendRequest = new FriendRequest(target, args[0], performer);
            target.addNotification(friendRequest);
        }
    },
    REMOVE_FRIEND {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            if (!performer.isFriend(target) || !target.isFriend(performer)) {
                throw new IllegalStateException("Users are not friends.");
            }
            performer.removeFriend(target);
            target.removeFriend(performer);
        }
    },
    IGNORE_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            if (performer.isIgnoring(target)) {
                throw new IllegalStateException("User is already ignored.");
            }
            if (performer.isFriend(target) || target.isFriend(performer)) {
                performer.removeFriend(target);
                target.removeFriend(performer);
            }
            performer.ignoreUser(target);
        }
    },
    UNIGNORE_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            if (!performer.isIgnoring(target)) {
                throw new IllegalStateException("User is not ignored.");
            }
            performer.unignoreUser(target);
        }
    },
    ROOM_INVITE {
        @Override
        protected void execute(User performer, User target, String[] args) throws NoPermissionException, IllegalStateException {
            SpatialContext invitedRoom = performer.getLocation().getRoom();
            if (!performer.hasPermission(invitedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
                throw new NoPermissionException("Performer has not the required permission.", performer, Permission.MANAGE_PRIVATE_ROOM);
            }
            if (invitedRoom.contains(target)) {
                throw new IllegalStateException("Invited user is already in the private room.");
            }
            RoomInvitation roomInvitation = new RoomInvitation(target, args[0], performer, invitedRoom);
            target.addNotification(roomInvitation);
        }
    },
    ROOM_KICK {
        @Override
        protected void execute(User performer, User target, String[] args) throws NoPermissionException, IllegalStateException {
            SpatialContext kickedRoom = performer.getLocation().getRoom();
            if (!performer.hasPermission(kickedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
                throw new NoPermissionException("Performer has not the required permission.", performer, Permission.MANAGE_PRIVATE_ROOM);
            }
            if (!kickedRoom.contains(target)) {
                throw new IllegalStateException("Target is not in the private room.");
            }
            target.teleport(target.getWorld().getSpawnLocation());
        }
    },
    TELEPORT_TO_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws NoPermissionException {
            Location performerLocation = performer.getLocation();
            Location targetLocation = target.getLocation();
            Context commonContext = performerLocation.getArea().lastCommonAncestor(targetLocation.getArea());
            if (commonContext == null || !(performer.hasPermission(commonContext, Permission.TELEPORT_TO_USER) || !performer.isFriend(target))) {
                throw new NoPermissionException("Performer has not the required permission.", performer, Permission.TELEPORT_TO_USER);
            }
            if (targetLocation.getRoom().isPrivate() && !performer.hasPermission(targetLocation.getRoom(), Permission.ENTER_PRIVATE_ROOM)) {
                throw new NoPermissionException("Performer has not the required permission.", performer, Permission.ENTER_PRIVATE_ROOM);
            }
            performer.teleport(targetLocation);
        }
    },
    REPORT_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            Context performerContext = performer.getWorld();
            if (target.hasPermission(performerContext, Permission.BAN_MODERATOR)) {
                throw new IllegalStateException("Users with permission to ban moderators cannot be reported.");
            }
            Map<UUID, User> receivers = UserAccountManager.getInstance().getUsersWithPermission(performerContext, Permission.BAN_MODERATOR);
            if (!target.hasPermission(performerContext, Permission.BAN_USER)) {
                receivers.putAll(UserAccountManager.getInstance().getUsersWithPermission(performerContext, Permission.BAN_USER));
            }
            performerContext.addReportedUser(target);

            Object[] notificationArguments = new Object[]{performer, target, args[0]};
            MessageBundle messageBundle = new MessageBundle("messageKey", notificationArguments);
            receivers.forEach((userID, user) -> {
                Notification reportNotification = new Notification(user, performer.getWorld(), messageBundle);
                user.addNotification(reportNotification);
            });
        }
    },
    MUTE_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws NoPermissionException, IllegalStateException {
            Context performerContext = performer.getLocation().getArea();
            Context targetContext = target.getLocation().getArea();
            Context commonContext = performerContext.lastCommonAncestor(targetContext);
            if (!performer.hasPermission(commonContext, Permission.MUTE)) {
                throw new NoPermissionException("Performer has not the required permission.", performer, Permission.MUTE);
            }
            if (commonContext.isMuted(target)) {
                throw new IllegalStateException("Target is already muted in this context.");
            }
            commonContext.addMutedUser(target);
        }
    },
    UNMUTE_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws NoPermissionException, IllegalStateException {
            Context performerContext = performer.getLocation().getArea();
            Context targetContext = target.getLocation().getArea();
            Context commonContext = performerContext.lastCommonAncestor(targetContext);
            if (!performer.hasPermission(commonContext, Permission.MUTE)) {
                throw new NoPermissionException("Performer has not the required permission.", performer, Permission.MUTE);
            }
            if (!commonContext.isMuted(target)) {
                throw new IllegalStateException("Target is not muted in this context.");
            }
            do {
                targetContext.removeMutedUser(target);
                targetContext = targetContext.getParent();
            } while (!targetContext.equals(commonContext));
        }
    },
    BAN_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException, NoPermissionException {
            Context performerContext = performer.getWorld();
            if (target.hasPermission(performerContext, Permission.BAN_MODERATOR)) {
                throw new IllegalStateException("Users with permission to ban moderators cannot be banned.");
            }
            Map<UUID, User> receivers = UserAccountManager.getInstance().getUsersWithPermission(performerContext, Permission.BAN_MODERATOR);
            if (target.hasPermission(performerContext, Permission.BAN_USER)) {
                if (!performer.hasPermission(performerContext, Permission.BAN_MODERATOR)) {
                    throw new NoPermissionException("Performer has not the required permission.", performer, Permission.BAN_MODERATOR);
                }
            } else {
                if (!performer.hasPermission(performerContext, Permission.BAN_USER)) {
                    throw new NoPermissionException("Performer has not the required permission.", performer, Permission.BAN_USER);
                }
                receivers.putAll(UserAccountManager.getInstance().getUsersWithPermission(performerContext, Permission.BAN_USER));
            }
            performerContext.removeReportedUser(target);
            performerContext.addBannedUser(target);

            Object[] notificationArguments = new Object[]{performer, target, args[0]};
            MessageBundle messageBundle = new MessageBundle("messageKey", notificationArguments);
            receivers.forEach((userID, user) -> {
                Notification banNotification = new Notification(user, performer.getWorld(), messageBundle);
                user.addNotification(banNotification);
            });
        }
    },
    UNBAN_USER {
        @Override
        protected void execute(User performer, User target, String[] args) throws NoPermissionException {
            Context performerContext = performer.getWorld();
            Map<UUID, User> receivers = UserAccountManager.getInstance().getUsersWithPermission(performerContext, Permission.BAN_MODERATOR);
            if (target.hasPermission(performerContext, Permission.BAN_USER)) {
                if (!performer.hasPermission(performerContext, Permission.BAN_MODERATOR)) {
                    throw new NoPermissionException("Performer has not the required permission.", performer, Permission.BAN_MODERATOR);
                }
            } else {
                if (!performer.hasPermission(performerContext, Permission.BAN_USER)) {
                    throw new NoPermissionException("Performer has not the required permission.", performer, Permission.BAN_USER);
                }
                receivers.putAll(UserAccountManager.getInstance().getUsersWithPermission(performerContext, Permission.BAN_USER));
            }
            performerContext.removeBannedUser(target);

            Object[] notificationArguments = new Object[]{performer, target, args[0]};
            MessageBundle messageBundle = new MessageBundle("messageKey", notificationArguments);
            receivers.forEach((userID, user) -> {
                Notification unbanNotification = new Notification(user, performer.getWorld(), messageBundle);
                user.addNotification(unbanNotification);
            });
        }
    },
    ASSIGN_MODERATOR {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            Context performerContext = performer.getWorld();
            if (target.hasRole(performerContext, Role.MODERATOR)) {
                throw new IllegalStateException("Target is already moderator in this world.");
            }
            target.addRole(performerContext, Role.MODERATOR);

            Object[] notificationArguments = new Object[]{performer, Role.MODERATOR, args[0]};
            MessageBundle messageBundle = new MessageBundle("messageKey", notificationArguments);
            Notification roleReceiveNotification = new Notification(target, performer.getWorld(), messageBundle);
            target.addNotification(roleReceiveNotification);
        }
    },
    WITHDRAW_MODERATOR {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            Context performerContext = performer.getWorld();
            if (!target.hasRole(performerContext, Role.MODERATOR)) {
                throw new IllegalStateException("Target is not moderator in this world.");
            }
            target.removeRole(performerContext, Role.MODERATOR);

            Object[] notificationArguments = new Object[]{performer, Role.MODERATOR, args[0]};
            MessageBundle messageBundle = new MessageBundle("messageKey", notificationArguments);
            Notification roleLoseNotification = new Notification(target, performer.getWorld(), messageBundle);
            target.addNotification(roleLoseNotification);
        }
    },
    ASSIGN_ADMINISTRATOR {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            Context performerContext = GlobalContext.getInstance();
            if (target.hasRole(performerContext, Role.ADMINISTRATOR)) {
                throw new IllegalStateException("Target is already administrator.");
            }
            target.addRole(performerContext, Role.ADMINISTRATOR);

            Object[] notificationArguments = new Object[]{performer, Role.ADMINISTRATOR, args[0]};
            MessageBundle messageBundle = new MessageBundle("messageKey", notificationArguments);
            Notification roleReceiveNotification = new Notification(target, GlobalContext.getInstance(), messageBundle);
            target.addNotification(roleReceiveNotification);
        }
    },
    WITHDRAW_ADMINISTRATOR {
        @Override
        protected void execute(User performer, User target, String[] args) throws IllegalStateException {
            Context performerContext = GlobalContext.getInstance();
            if (!target.hasRole(performerContext, Role.ADMINISTRATOR)) {
                throw new IllegalStateException("Target is not administrator");
            }
            target.removeRole(performerContext, Role.ADMINISTRATOR);

            Object[] notificationArguments = new Object[]{performer, Role.ADMINISTRATOR, args[0]};
            MessageBundle messageBundle = new MessageBundle("messageKey", args);
            Notification roleLoseNotification = new Notification(target, GlobalContext.getInstance(), messageBundle);
            target.addNotification(roleLoseNotification);
        }
    };

    protected abstract void execute(User performer, User target, String[] args) throws IllegalStateException, NoPermissionException;
}
