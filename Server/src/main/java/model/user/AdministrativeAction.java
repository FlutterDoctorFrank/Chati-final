package model.user;

import model.MessageBundle;
import model.context.Context;
import model.context.global.GlobalContext;
import model.context.spatial.Area;
import model.context.spatial.Room;
import model.context.spatial.World;
import model.exception.IllegalAdministrativeActionException;
import model.exception.NoPermissionException;
import model.notification.FriendRequest;
import model.notification.Notification;
import model.notification.RoomInvitation;
import model.role.Permission;
import model.role.Role;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Eine Enumeration, die der Durchführung von administrativen Aktionen von Benutzern an anderen Benutzern dient.
 */
public enum AdministrativeAction {

    /**
     * Sendet einem Benutzer eine Freundschaftsanfrage, sofern dieser Benutzer nicht bereits in der Freundesliste vom
     * anfragenden Benutzer enthalten ist und der angefragte Benutzer den anfragenden Benutzer nicht ignoriert.
     * Ignoriert der anfragende Benutzer den angefragten, wird dieser durch das Senden der Freundschaftsanfrage
     * automatisch nicht mehr ignoriert.
     */
    INVITE_FRIEND {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException {
            // Überprüfe, ob die Benutzer bereits befreundet sind.
            if (performer.isFriend(target) && target.isFriend(performer)) {
                throw new IllegalAdministrativeActionException("Users are already friends.",
                        performer, target, INVITE_FRIEND);
            }
            // Überprüfe, ob der angefragte Benutzer den anfragenden ignoriert.
            if (target.isIgnoring(performer)) {
                throw new IllegalAdministrativeActionException("Cannot send friend request to ignored users.",
                        performer, target, INVITE_FRIEND);
            }
            // Hebe die Ignorierung auf, wenn der anfragende Benutzer den angefragten ignoriert.
            if (performer.isIgnoring(target)) {
                performer.unignoreUser(target);
            }
            // Versende die Freundschaftsanfrage.
            FriendRequest friendRequest = new FriendRequest(target, args[0], performer);
            target.addNotification(friendRequest);
        }
    },

    /**
     * Löscht einen Benutzer aus der Freundesliste. Verursacht auch das Löschen des ausführenden Benutzers in der
     * Freundesliste des zu löschenden Benutzers.
     */
    REMOVE_FRIEND {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException {
            // Überprüfe, ob die Benutzer befreundet sind.
            if (!performer.isFriend(target) || !target.isFriend(performer)) {
                throw new IllegalAdministrativeActionException("Users are not friends.",
                        performer, target, REMOVE_FRIEND);
            }
            // Entferne die Benutzer gegenseitig aus der Freundesliste.
            performer.removeFriend(target);
            target.removeFriend(performer);
        }
    },

    /**
     * Ignoriert einen Benutzer. Die Kommunikation zwischen einem ignorierenden und einem ignorierten Benutzer ist nicht
     * möglich. Das Ignorieren eines in der Freundesliste enthaltenen Benutzer zieht die automatische Löschung der
     * Freundschaft in den Freundeslisten beider Benutzer mit sich.
     */
    IGNORE_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException {
            // Überprüfe, ob der anfragende Benutzer den angefragten bereits ignoriert.
            if (performer.isIgnoring(target)) {
                throw new IllegalAdministrativeActionException("User is already ignored.",
                        performer, target, IGNORE_USER);
            }
            // Entferne die Benutzer gegenseitig aus ihrer Freundesliste, falls vorhanden.
            if (performer.isFriend(target) || target.isFriend(performer)) {
                performer.removeFriend(target);
                target.removeFriend(performer);
            }
            // Ignoriere Benutzer.
            performer.ignoreUser(target);
        }
    },

    /**
     * Ignoriert einen Benutzer nicht mehr.
     */
    UNIGNORE_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException {
            // Überprüfe, ob Benutzer ignoriert wird.
            if (!performer.isIgnoring(target)) {
                throw new IllegalAdministrativeActionException("User is not ignored.",
                        performer, target, UNIGNORE_USER);
            }
            // Hebe die Ignorierung auf.
            performer.unignoreUser(target);
        }
    },

    /**
     * Lädt einen Benutzer in einen privaten Raum ein, in dem sich der ausführende Benutzer befindet und für den er die
     * nötige Berechtigung besitzt.
     */
    ROOM_INVITE {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            if (performer.getLocation() == null) {
                throw new IllegalStateException("Performers location is not available");
            }

            if (args.length < 1) {
                throw new IllegalArgumentException("Too few arguments to perform this action");
            }

            Room invitedRoom = performer.getLocation().getRoom();
            // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung für den Raum hat.
            if (!performer.hasPermission(invitedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
                throw new NoPermissionException("Performer has not the required permission.",
                        "action.room-invite.not-permitted", performer, Permission.MANAGE_PRIVATE_ROOM);
            }
            // Überprüfe, ob der eingeladene Benutzer bereits in diesem Raum ist.
            if (invitedRoom.contains(target)) {
                throw new IllegalAdministrativeActionException("Invited user is already in the private room.",
                        performer, target, ROOM_INVITE);
            }
            // Überprüfe, ob der eingeladene Benutzer gerade beschäftigt ist.
            if (target.getStatus() == Status.BUSY) {
                throw new IllegalAdministrativeActionException("Busy users can't be invited in a room.",
                        performer, target, ROOM_INVITE);
            }
            // Schicke die Raumeinladung.
            RoomInvitation roomInvitation = new RoomInvitation(target, args[0], performer, invitedRoom);
            target.addNotification(roomInvitation);
        }
    },

    /**
     * Entfernt einen Benutzer aus einem privaten Raum, in dem er sich befindet, und für den der ausführende Benutzer
     * die nötige Berechtigung besitzt.
     */
    ROOM_KICK {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            if (performer.getLocation() == null || target.getWorld() == null) {
                throw new IllegalStateException("Performers/Targets location/world is not available");
            }

            Room kickedRoom = performer.getLocation().getRoom();
            // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung für den Raum hat.
            if (!performer.hasPermission(kickedRoom, Permission.MANAGE_PRIVATE_ROOM)) {
                throw new NoPermissionException("Performer has not the required permission.",
                        "action.room-kick.not-permitted", performer, Permission.MANAGE_PRIVATE_ROOM);
            }
            // Überprüfe, ob sich der zu entfernende Benutzer in diesem Raum befindet.
            if (!kickedRoom.contains(target)) {
                throw new IllegalAdministrativeActionException("Target is not in the private room.",
                        performer, target, ROOM_KICK);
            }
            // Entferne den Benutzer aus dem Raum.
            target.teleport(target.getWorld().getPublicRoom().getSpawnLocation());
        }
    },

    /**
     * Teleportiert einen Benutzer zu einem anderen Benutzer. Die Aktion ist durchführbar, wenn sich der Benutzer, zu
     * dem sich teleportiert werden soll, in der Freundesliste befindet, oder beide Benutzer sich innerhalb eines
     * Kontextes befinden, für den der ausführende Benutzer die Berechtigung zur Teleportation besitzt. Befindet sich
     * der Benutzer, zu dem sich teleportiert werden soll, in einem privaten Raum, ist für die Durchführung der Aktion
     * die Berechtigung zum Beitritt privater Räume ohne Zugangskontrolle erforderlich.
     */
    TELEPORT_TO_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws NoPermissionException {
            if (performer.getLocation() == null || target.getLocation() == null) {
                throw new IllegalStateException("Performers/Targets location is not available");
            }

            Area performerArea = performer.getLocation().getArea();
            Area targetArea = target.getLocation().getArea();

            // Überprüfe, ob beide Benutzer sich innerhalb eines Kontextes befinden, in dem der ausführende Benutzer
            // die Berechtigung zum Teleportieren besitzt, oder ob die Benutzer befreundet sind.
            Context commonContext = performerArea.lastCommonAncestor(targetArea);

            if (!performer.hasPermission(commonContext, Permission.TELEPORT_TO_USER)
                    && (!performer.isFriend(target) || target.getStatus() == Status.BUSY)) {
                throw new NoPermissionException("Performer has not the required permission.",
                        "action.user-teleport.not-permitted", performer, Permission.TELEPORT_TO_USER);
            }

            // Überprüfe, ob sich der Benutzer, zu dem sich teleportiert werden soll, in einem privaten Raum befindet
            // und ob der ausführende Benutzer die Berechtigung zum Betreten privater Räume ohne Zugangskontrolle
            // besitzt.
            Room targetRoom = target.getLocation().getRoom();
            if (targetRoom.isPrivate() && !performer.hasPermission(targetRoom, Permission.ENTER_PRIVATE_ROOM)) {
                throw new NoPermissionException("Performer has not the required permission.",
                        "action.room-join.not-permitted", performer, Permission.ENTER_PRIVATE_ROOM);
            }

            // Teleportiere zu dem Benutzer.
            performer.teleport(target.getLocation());
        }
    },

    /**
     * Meldet einen Benutzer. Benutzer mit der Berechtigung Benutzer sperren zu können. erhalten eine Benachrichtigung
     * mit der Information über gemeldete Benutzer. Wird ein Benutzer mit dieser Berechtigung gemeldet, erhalten nur
     * Benutzer mit der Berechtigung Moderatoren zu sperren eine Benachrichtigung. Benutzer mit der Berechtigung
     * Moderatoren zu sperren können nicht gemeldet werden.
     */
    REPORT_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException {
            World performerWorld = performer.getWorld();

            if (performerWorld == null) {
                throw new IllegalStateException("Performers world is not available");
            }

            // Überprüfe, ob zu meldender Benutzer ein Administrator oder Besitzer ist.
            if (target.hasPermission(performerWorld, Permission.BAN_MODERATOR)) {
                throw new IllegalAdministrativeActionException("Users with permission to ban moderators cannot be reported.",
                        performer, target, REPORT_USER);
            }

            // Füge den Benutzer, zu den gemeldeten Benutzern in dem Kontext hinzu.
            performerWorld.addReportedUser(target);
            // Ermittle die Benutzer, die die Benachrichtigung über den gemeldeten Benutzer erhalten sollen.
            Map<UUID, User> receivers = UserAccountManager.getInstance()
                    .getUsersWithPermission(performerWorld, Permission.BAN_MODERATOR);
            if (!target.hasPermission(performerWorld, Permission.BAN_USER)) {
                receivers.putAll(UserAccountManager.getInstance().getUsersWithPermission(performerWorld,
                        Permission.BAN_USER));
            }

            // Sende die Benachrichtigung an alle Benutzer.
            MessageBundle messageBundle = new MessageBundle("action.user-report.info-moderator",
                    performer.getUsername(), target.getUsername(), args[0]);
            receivers.values().forEach(user -> {
                Notification reportNotification = new Notification(user, performer.getWorld(), messageBundle);
                user.addNotification(reportNotification);
            });
        }
    },

    /**
     * Stellt einen Benutzer im innersten räumlichen Kontext stumm, in dem sich sowohl der ausführende, als auch der
     * stummzuschaltende Benutzer befinden, sofern der ausführende Benutzer die Berechtigung für diesen Kontext besitzt.
     * Ein stummgeschalteter Benutzer kann im entsprechenden Kontext nicht mehr aktiv kommunizieren, außer per
     * Flüsternachricht.
     */
    MUTE_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            if (performer.getLocation() == null || target.getLocation() == null) {
                throw new IllegalStateException("Performers/Targets location is not available");
            }

            Area performerArea = performer.getLocation().getArea();
            Area targetArea = target.getLocation().getArea();
            // Überprüfe, ob beide Benutzer sich in einem Kontext befinden, in dem der ausführende Benutzer die
            // Berechtigung zum Stummschalten besitzt.
            Context commonContext = performerArea.lastCommonAncestor(targetArea);

            if (!performer.hasPermission(commonContext, Permission.MUTE)) {
                throw new NoPermissionException("Performer has not the required permission.",
                        "action.user-mute.not-permitted", performer, Permission.MUTE);
            }
            // Überprüfe, ob der stummzuschaltende Benutzer in dem Kontext bereits stummgeschaltet ist.
            if (commonContext.isMuted(target)) {
                throw new IllegalAdministrativeActionException("Target is already muted in this context.",
                        performer, target, MUTE_USER);
            }
            // Schalte den Benutzer stumm.
            commonContext.addMutedUser(target);
        }
    },

    /**
     * Hebt die Stummschaltung eines Benutzers auf. Es werden alle Stummschaltungen innerhalb des größten Kontexts
     * aufgehoben, für den der ausführende Benutzer die Berechtigung besitzt.
     */
    UNMUTE_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            if (performer.getLocation() == null || target.getLocation() == null) {
                throw new IllegalStateException("Performers/Targets location is not available");
            }

            Area performerArea = performer.getLocation().getArea();
            Area targetArea = target.getLocation().getArea();
            // Überprüfe, ob beide Benutzer sich in einem Kontext befinden, in dem der ausführende Benutzer die
            // Berechtigung zum Stummschalten besitzt.
            Context commonContext = performerArea.lastCommonAncestor(targetArea);

            if (!performer.hasPermission(commonContext, Permission.MUTE)) {
                throw new NoPermissionException("Performer has not the required permission.",
                        "action.user-mute.not-permitted", performer, Permission.MUTE);
            }

            // Überprüfe, ob der Benutzer, dessen Stummschaltung aufgehoben werden soll, in dem Kontext stummgeschaltet
            // ist.
            if (!commonContext.isMuted(target)) {
                throw new IllegalAdministrativeActionException("Target is not muted in this context.",
                        performer, target, UNMUTE_USER);
            }
            // Hebe die Stummschaltung in dem Kontext und allen übergeordneten Kontexten, bis zu dem größten Kontext,
            // in dem sich der ausführende Benutzer befindet und die Berechtigung besitzt, auf.
            commonContext.removeMutedUser(target);
        }
    },

    /**
     * Sperrt einen Benutzer in der aktuellenWelt des ausführenden Benutzers, wenn er die Berechtigung dafür besitzt.
     * Befindet sich der gesperrte Benutzer gerade in dieser Welt, wird er dadurch aus dieser entfernt. Er kann dieser
     * Welt nicht mehr beitreten. Benutzer mit der Berechtigung Benutzer zu sperren, können nur von Benutzern mit der
     * Berechtigung Moderatoren zu sperren, gesperrt werden. Andere Benutzer mit der entsprechenden Berechtigung, sowie
     * der gesperrte Benutzer werden durch eine Benachrichtigung über die Aktion informiert. Benutzer mit der
     * Berechtigung Moderatoren zu sperren können nicht gesperrt werden.
     */
    BAN_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            World performerWorld = performer.getWorld();

            if (performerWorld == null) {
                throw new IllegalStateException("Performers world is not available");
            }

            // Überprüfe, ob der Benutzer die Berechtigung zum Sperren des Benutzers hat.
            if (!performer.hasPermission(performerWorld, Permission.BAN_USER)
                || (!performer.hasPermission(performerWorld, Permission.BAN_MODERATOR)
                && target.hasPermission(performerWorld, Permission.BAN_USER))) {
                throw new NoPermissionException("Performer has not the required permission to ban.",
                        "action.user-ban.not-permitted", performer, Permission.BAN_USER);
            }

            // Überprüfe, ob der zu sperrende Benutzer ein Administrator oder Besitzer ist.
            if (target.hasPermission(performerWorld, Permission.BAN_MODERATOR)) {
                throw new IllegalAdministrativeActionException("Users with permission to ban moderators cannot be banned.",
                        performer, target, BAN_USER);
            }

            // Überprüfe, ob der Benutzer in dieser Welt bereits gesperrt ist.
            if (performerWorld.isBanned(target)) {
                throw new IllegalAdministrativeActionException("User is already banned in this world.",
                        "action.user-ban.not-permitted", performer, target, BAN_USER);
            }

            // Sperre den Benutzer.
            performerWorld.removeReportedUser(target);
            performerWorld.addBannedUser(target);

            // Entferne die Rolle des Moderators, falls der Benutzer diese in der Welt besitzt hat.
            if (target.hasRole(performerWorld, Role.MODERATOR)) {
                target.removeRole(performerWorld, Role.MODERATOR);
            }

            // Ermittle die Benutzer, die über das Sperren informiert werden sollen.
            Map<UUID, User> receivers = UserAccountManager.getInstance().getUsersWithPermission(performerWorld,
                    Permission.BAN_MODERATOR);
            if (!target.hasPermission(performerWorld, Permission.BAN_USER)) {
                receivers.putAll(UserAccountManager.getInstance().getUsersWithPermission(performerWorld,
                        Permission.BAN_USER));
            }

            // Sende eine Benachrichtigung mit der Information an alle relevanten Benutzer.
            MessageBundle messageBundle = new MessageBundle("action.user-ban.info-moderator",
                    performer.getUsername(), target.getUsername(), args[0]);
            receivers.values().forEach(user -> {
                Notification banNotification = new Notification(user, performerWorld, messageBundle);
                user.addNotification(banNotification);
            });

            // Sende eine Benachrichtigung an den gesperrten Benutzer.
            MessageBundle targetMessageBundle =
                    new MessageBundle("action.user-ban.info-user", performer.getUsername(),
                            performerWorld.getContextName(), args[0]);
            // Die Benachrichtigung muss zum globalen Kontext gehören, da sonst der gesperrte Benutzer niemals diese
            // Benachrichtigung lesen wird, weil er der Welt nicht mehr beitreten kann oder kurz nach Erhalt von der
            // Welt entfernt wird.
            Notification banNotification = new Notification(target, GlobalContext.getInstance(), targetMessageBundle);
            target.addNotification(banNotification);

            if (performerWorld.equals(target.getWorld())) {
                target.leaveWorld();
            }
        }
    },

    /**
     * Entsperrt einen Benutzer in der aktuellen Welt des ausführenden Benutzers, wenn er die Berechtigung dafür
     * besitzt. Benutzer mit der Berechtigung Benutzer zu sperren, können nur von Benutzern mit der Berechtigung
     * Moderatoren zu sperren, entsperrt werden. Andere Benutzer mit der entsprechenden Berechtigung, sowie der
     * entsperrte Benutzer werden durch eine Benachrichtigung über die Aktion informiert.
     */
    UNBAN_USER {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            World performerWorld = performer.getWorld();

            if (performerWorld == null) {
                throw new IllegalStateException("Performers world is not available");
            }

            // Überprüfe, ob der ausführende Benutzer die Berechtigung zum Entsperren des Benutzers hat.
            if (!performer.hasPermission(performerWorld, Permission.BAN_USER)) {
                throw new NoPermissionException("Performer has not the required permission to ban.",
                        "action.user-ban.not-permitted", performer, Permission.BAN_USER);
            }

            // Überprüfe, ob der Benutzer in dieser Welt gesperrt ist.
            if (!performerWorld.isBanned(target)) {
                throw new IllegalAdministrativeActionException("User is not banned from this world.",
                        performer, target, UNBAN_USER);
            }

            // Entsperre den Benutzer.
            performerWorld.removeBannedUser(target);

            // Ermittle die Benutzer, die über das Entsperren informiert werden sollen und überprüfe ob der ausführende
            // Benutzer die entsprechende Berechtigung besitzt.
            Map<UUID, User> receivers = UserAccountManager.getInstance().getUsersWithPermission(performerWorld,
                    Permission.BAN_USER);

            // Sende eine Benachrichtigung mit der Information an alle relevanten Benutzer.
            MessageBundle messageBundle = new MessageBundle("action.user-unban.info-moderator",
                    performer.getUsername(), target.getUsername(), args[0]);
            receivers.values().forEach(user -> {
                Notification unbanNotification = new Notification(user, performer.getWorld(), messageBundle);
                user.addNotification(unbanNotification);
            });

            // Sende eine Benachrichtigung an den entsperrten Benutzer.
            MessageBundle targetMessageBundle =
                    new MessageBundle("action.user-unban.info-user", performer.getUsername(),
                            performerWorld.getContextName(), args[0]);
            // Auch hier muss die Benachrichtigung im globalen Kontext gelten, da der Benutzer diese Benachrichtigung
            // nicht erhalten kann, bevor er versucht hat einer Welt beizutreten, in der er vorher gebannt war.
            Notification unbanNotification = new Notification(target, GlobalContext.getInstance(), targetMessageBundle);
            target.addNotification(unbanNotification);
        }
    },

    /**
     * Teilt einem Benutzer die Rolle des Moderators in der Welt des ausführenden Benutzers zu. Der Benutzer erhält eine
     * Benachrichtigung, in der ihm mitgeteilt wird, dass er diese Rolle erhalten hat.
     */
    ASSIGN_MODERATOR {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            World performerWorld = performer.getWorld();

            if (performerWorld == null) {
                throw new IllegalStateException("Performers world is not available");
            }

            // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung in dem Kontext besitzt.
            if (!performer.hasPermission(performerWorld, Permission.ASSIGN_MODERATOR)) {
                throw new NoPermissionException("no permission", "action.assign-moderator.not-permitted",
                        performer, Permission.ASSIGN_MODERATOR);
            }
            // Überprüfe, ob der Benutzer eine übergeordnete Rolle besitzt.
            if (target.hasRole(GlobalContext.getInstance(), Role.ADMINISTRATOR)
                    || target.hasRole(GlobalContext.getInstance(), Role.OWNER)) {
                throw new IllegalAdministrativeActionException("Cannot assign the moderator role to an administrator or owner.",
                        performer, target, ASSIGN_MODERATOR);
            }
            // Überprüfe, ob der Benutzer bereits die Rolle des Moderators in dem Kontext besitzt.
            if (target.hasRole(performerWorld, Role.MODERATOR)) {
                throw new IllegalAdministrativeActionException("Target is already moderator in this world.",
                        performer, target, ASSIGN_MODERATOR);
            }
            // Überprüfe, ob der Benutzer in der Welt gesperrt ist.
            if (performerWorld.isBanned(target)) {
                throw new IllegalAdministrativeActionException("Banned users cannot be moderators",
                        performer, target, ASSIGN_MODERATOR);
            }

            // Füge die Rolle hinzu.
            target.addRole(performerWorld, Role.MODERATOR);
            // Informiere den Benutzer über den Erhalt der Rolle.
            MessageBundle messageBundle = new MessageBundle("role.moderator.assignment",
                    performer.getUsername(), performerWorld.getContextName());
            Notification roleReceiveNotification = new Notification(target, performerWorld, messageBundle);
            target.addNotification(roleReceiveNotification);
        }
    },

    /**
     * Entzieht einem Benutzer die Rolle des Moderators in der Welt des ausführenden Benutzers. Der Benutzer erhält eine
     * Benachrichtigung, in der ihm mitgeteilt wird, dass ihm diese Rolle entzogen wurde.
     */
    WITHDRAW_MODERATOR {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            World performerWorld = performer.getWorld();

            if (performerWorld == null) {
                throw new IllegalStateException("Performers world is not available");
            }

            // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung in dem Kontext besitzt.
            if (!performer.hasPermission(performerWorld, Permission.ASSIGN_MODERATOR)) {
                throw new NoPermissionException("no permission", "action.assign-moderator.not-permitted",
                        performer, Permission.ASSIGN_MODERATOR);
            }
            // Überprüfe, ob der Benutzer eine übergeordnete Rolle besitzt.
            if (target.hasRole(GlobalContext.getInstance(), Role.ADMINISTRATOR)
                    || target.hasRole(GlobalContext.getInstance(), Role.OWNER)) {
                throw new IllegalAdministrativeActionException("Cannot withdraw the moderator role from an administrator or owner.",
                        performer, target, ASSIGN_MODERATOR);
            }
            // Überprüfe, ob der Benutzer die Rolle des Moderators in dem Kontext besitzt.
            if (!target.hasRole(performerWorld, Role.MODERATOR)) {
                throw new IllegalAdministrativeActionException("Target is not moderator in this world.",
                        performer, target, WITHDRAW_MODERATOR);
            }

            // Entferne die Rolle.
            target.removeRole(performerWorld, Role.MODERATOR);
            // Informiere den Benutzer über den Entzug der Rolle.
            MessageBundle messageBundle = new MessageBundle("role.moderator.withdrawal",
                    performer.getUsername(), performerWorld.getContextName());
            Notification roleLoseNotification = new Notification(target, performerWorld, messageBundle);
            target.addNotification(roleLoseNotification);
        }
    },

    /**
     * Teilt einem Benutzer die Rolle des Administrators zu. Der Benutzer erhält eine Benachrichtigung, in der ihm
     * mitgeteilt wird, dass er diese Rolle erhalten hat.
     */
    ASSIGN_ADMINISTRATOR {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            GlobalContext global = GlobalContext.getInstance();
            // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung in dem Kontext besitzt.
            if (!performer.hasPermission(global, Permission.ASSIGN_ADMINISTRATOR)) {
                throw new NoPermissionException("no permission", "action.assign-administrator.not-permitted",
                        performer, Permission.ASSIGN_ADMINISTRATOR);
            }
            // Überprüfe, ob der Benutzer eine übergeordnete Rolle besitzt.
            if (target.hasRole(GlobalContext.getInstance(), Role.OWNER)) {
                throw new IllegalAdministrativeActionException("Cannot assign the administrator role to an owner.",
                        performer, target, ASSIGN_ADMINISTRATOR);
            }
            // Überprüfe, ob der Benutzer bereits die Rolle des Administrators in dem Kontext besitzt.
            if (target.hasRole(global, Role.ADMINISTRATOR)) {
                throw new IllegalAdministrativeActionException("Target is already administrator.",
                        performer, target, ASSIGN_ADMINISTRATOR);
            }
            // Entferne die Rollen des Moderators in jeder Welt von diesem Benutzer.
            GlobalContext.getInstance().getWorlds().values().forEach(world -> target.removeRole(world, Role.MODERATOR));
            // Entferne Benutzer aus allen Kontexten als gemeldeter Benutzer.
            GlobalContext.getInstance().getWorlds().values().forEach(world -> world.removeReportedUser(target));
            // Füge die Rolle hinzu.
            target.addRole(global, Role.ADMINISTRATOR);
            // Informiere den Benutzer über den Erhalt der Rolle.
            MessageBundle messageBundle = new MessageBundle("role.administrator.assignment", performer.getUsername());
            Notification roleReceiveNotification = new Notification(target, global, messageBundle);
            target.addNotification(roleReceiveNotification);
        }
    },

    /**
     * Entzieht einem Benutzer die Rolle des Administrators, wenn der ausführende Benutzer die nötige Berechtigung dafür
     * besitzt. Der Benutzer erhält eine Benachrichtigung, in der ihm mitgeteilt wird, dass ihm diese Rolle entzogen
     * wurde.
     */
    WITHDRAW_ADMINISTRATOR {
        @Override
        protected void execute(@NotNull final User performer, @NotNull final User target,
                               @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException {
            GlobalContext global = GlobalContext.getInstance();
            // Überprüfe, ob der ausführende Benutzer die nötige Berechtigung in dem Kontext besitzt.
            if (!performer.hasPermission(global, Permission.ASSIGN_ADMINISTRATOR)) {
                throw new NoPermissionException("no permission", "action.assign-administrator.not-permitted",
                        performer, Permission.ASSIGN_ADMINISTRATOR);
            }
            // Überprüfe, ob der Benutzer eine übergeordnete Rolle besitzt.
            if (target.hasRole(GlobalContext.getInstance(), Role.OWNER)) {
                throw new IllegalAdministrativeActionException("Cannot withdraw the administrator role from an owner.",
                        performer, target, ASSIGN_ADMINISTRATOR);
            }
            // Überprüfe, ob der Benutzer die Rolle des Administrators in dem Kontext besitzt.
            if (!target.hasRole(global, Role.ADMINISTRATOR)) {
                throw new IllegalAdministrativeActionException("Target is not administrator.",
                        performer, target, WITHDRAW_ADMINISTRATOR);
            }
            // Entferne die Rolle.
            target.removeRole(global, Role.ADMINISTRATOR);
            // Informiere den Benutzer über den Verlust der Rolle.
            MessageBundle messageBundle = new MessageBundle("role.administrator.withdrawal", performer.getUsername());
            Notification roleLoseNotification = new Notification(target, global, messageBundle);
            target.addNotification(roleLoseNotification);
        }
    };

    /**
     * Führt die entsprechende administrative Aktion durch.
     * @param performer Ausführender Benutzer.
     * @param target Benutzer, auf den die Aktion ausgeführt wird.
     * @param args Argumente, mit denen die Aktion ausgeführt wird.
     * @throws IllegalAdministrativeActionException wenn die auszuführende Aktion nicht möglich ist.
     * @throws NoPermissionException wenn der ausführende Benutzer nicht die nötige Berechtigung besitzt.
     */
    protected abstract void execute(@NotNull final User performer, @NotNull final User target,
                                    @NotNull final String[] args) throws IllegalAdministrativeActionException, NoPermissionException;
}