package model.user;

import controller.network.ClientSender.SendAction;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.context.spatial.Direction;
import model.exception.IllegalNotificationActionException;
import model.exception.IllegalPositionException;
import model.notification.Notification;
import model.role.Role;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class Bot extends User implements Runnable {

    private static final float MAXIMUM_MOVEMENT = 3.3334f;
    private static final float MINIMUM_DISTANCE = 64;
    private static final float FOLLOW_DISTANCE = 128;

    private static final Map<String, Bot> bots = new HashMap<>();

    private User followUser;
    private boolean isRunning;

    private Bot(@NotNull final String botname) {
        super(botname, false);
    }

    public static void create(@NotNull final String botname, @NotNull final User executor) {
        if (executor.getWorld() == null || executor.getLocation() == null) {
            // Evtl Loggen
            return;
        }

        if (bots.containsKey(botname) || UserAccountManager.getInstance().isRegistered(botname)) {
            // Informiere den ausführenden Benutzer darüber, dass bereits ein Bot mit diesem Namen existiert.
            TextMessage infoMessage = new TextMessage("chat.command.create-bot.already-exist");
            executor.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        Bot bot = new Bot(botname);
        bot.addRole(GlobalContext.getInstance(), Role.BOT);
        bot.login(null);
        GlobalContext.getInstance().addUser(bot);
        bot.teleport(executor.getLocation());
        bots.put(botname, bot);
    }

    public static void destroy(@NotNull final String botname, @NotNull final User executor) {
        if (executor.getWorld() == null || executor.getLocation() == null) {
            // Evtl Loggen
            return;
        }

        Bot bot = bots.remove(botname);
        if (bot != null) {
            UserAccountManager.getInstance().getUsers().values().forEach(user -> user.removeFriend(bot));
            UserAccountManager.getInstance().getUsers().values().forEach(user -> user.unignoreUser(bot));
            bot.logout();
        } else {
            // Informiere den ausführenden Benutzer darüber, dass kein Bot mit diesem Namen existiert.
            TextMessage infoMessage = new TextMessage("chat.command.create-bot.not-exist");
            executor.send(SendAction.MESSAGE, infoMessage);
        }
    }

    public static void destroyAll(@NotNull final User executor) {
        bots.values().forEach(bot -> destroy(bot.getUsername(), executor));
        bots.clear();
    }

    public static @Nullable Bot get(@NotNull final UUID botId) {
        try {
            return bots.values().stream().filter(bot -> bot.getUserId().equals(botId)).findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public static @Nullable Bot get(@NotNull final String botname) {
        return bots.get(botname);
    }

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        Thread botThread = new Thread(this);
        botThread.setDaemon(true);
        botThread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            if (followUser != null) {
                follow();
                chat();
            } else {
                move();
            }
        }
    }

    @Override
    public void logout() {
        this.isRunning = false;
        super.logout();
    }

    @Override
    public boolean isOnline() {
        return getStatus() != Status.OFFLINE;
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object object) {
        if (action == SendAction.MESSAGE) {
            if (object instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) object;
                if (textMessage.getSender() != null && !textMessage.getSender().equals(this)) {
                    chat();
                }
            }
        }

        if (action == SendAction.NOTIFICATION) {
            if (object instanceof Notification) {
                Notification notification = (Notification) object;
                try {
                    notification.accept();
                } catch (IllegalNotificationActionException e) {
                    e.printStackTrace();
                }
            }
        }

        switch (action) {
            case AVATAR_SPAWN:
                if (object instanceof User && !object.equals(this)) {
                    if (followUser != null) {
                        if (followUser.equals(object)) {
                            if (currentLocation.distance(followUser.currentLocation) > FOLLOW_DISTANCE + MINIMUM_DISTANCE) {
                                search();
                            }
                        }
                        if (currentLocation.distance(followUser.currentLocation)
                                < currentLocation.distance(((User) object).currentLocation)) {
                            return;
                        }
                    }
                    if (currentLocation.distance(((User) object).currentLocation) <= FOLLOW_DISTANCE) {
                        followUser = (User) object;
                    }
                }
                break;

            case AVATAR_MOVE:
                if (object instanceof User && !object.equals(this)) {
                    if (followUser != null) {
                        if (followUser.equals(object)) {
                            if (currentLocation.distance(followUser.currentLocation) > FOLLOW_DISTANCE + MINIMUM_DISTANCE) {
                                search();
                                return;
                            }

                            for (final User user : currentLocation.getRoom().getUsers().values()) {
                                if (!user.equals(this) && !user.equals(followUser) && inFront(user)) {
                                    if (currentLocation.distance(user.currentLocation)
                                            < currentLocation.distance(followUser.currentLocation)) {
                                        followUser = (User) object;
                                        return;
                                    }
                                }
                            }

                            follow();
                        } else {
                            if (currentLocation.distance(((User) object).currentLocation)
                                    < currentLocation.distance(followUser.currentLocation)) {
                                followUser = (User) object;
                            }
                        }
                    } else {
                        if (currentLocation.distance(((User) object).currentLocation) <= FOLLOW_DISTANCE) {
                            followUser = (User) object;
                        }
                    }
                }
                break;

            case AVATAR_REMOVE:
                if (object instanceof User && !object.equals(this)) {
                    if (followUser != null && followUser.equals(object)) {
                        search();
                    }
                }
                break;
        }
    }

    private void move() {
    }

    private boolean inFront(@NotNull final User user) {
        switch (currentLocation.getDirection()) {
            case UP:
                return user.currentLocation.getPosY() > currentLocation.getPosY();

            case RIGHT:
                return user.currentLocation.getPosX() > currentLocation.getPosX();

            case DOWN:
                return user.currentLocation.getPosY() < currentLocation.getPosY();

            case LEFT:
                return user.currentLocation.getPosX() < currentLocation.getPosX();
        }

        return false;
    }

    private void search() {
        followUser = null;
        float minimumDistance = Float.MAX_VALUE;

        // Suche nach dem nächsten Benutzer innerhalb des Folgeradius.
        for (final User user : currentLocation.getRoom().getUsers().values()) {
            if (!user.equals(this) && inFront(user)) {
                float distance = currentLocation.distance(user.currentLocation);

                if (distance <= FOLLOW_DISTANCE && distance < minimumDistance) {
                    followUser = user;
                    minimumDistance = distance;
                }
            }
        }
    }

    private void follow() {
        if (followUser == null) {
            return;
        }

        Direction direction = currentLocation.getDirection();
        float moveX = 0;
        float moveY = 0;

        switch (followUser.currentLocation.getDirection()) {
            case UP:
            case DOWN:
                moveX = followUser.currentLocation.getPosX() - currentLocation.getPosX();

                if (moveX != 0) {
                    if (Math.abs(moveX) > MAXIMUM_MOVEMENT) {
                        moveX = moveX > 0 ? MAXIMUM_MOVEMENT : -MAXIMUM_MOVEMENT;
                    }
                } else {
                    if (followUser.currentLocation.getDirection() == direction) {
                        moveY = followUser.currentLocation.getPosY() - currentLocation.getPosY();

                        if (Math.abs(moveY) < MINIMUM_DISTANCE) {
                            moveY = 0;
                        } else if (Math.abs(moveY) > MAXIMUM_MOVEMENT) {
                            moveY = moveY > 0 ? MAXIMUM_MOVEMENT : -MAXIMUM_MOVEMENT;
                        }
                    } else {
                        direction = followUser.currentLocation.getDirection();
                    }
                }
                break;

            case RIGHT:
            case LEFT:
                moveY = followUser.currentLocation.getPosY() - currentLocation.getPosY();

                if (moveY != 0) {
                    if (Math.abs(moveY) > MAXIMUM_MOVEMENT) {
                        moveY = moveY > 0 ? MAXIMUM_MOVEMENT : -MAXIMUM_MOVEMENT;
                    }
                } else {
                    if (followUser.currentLocation.getDirection() == direction) {
                        moveX = followUser.currentLocation.getPosX() - currentLocation.getPosX();

                        if (Math.abs(moveX) < MINIMUM_DISTANCE) {
                            moveX = 0;
                        } else if (Math.abs(moveX) > MAXIMUM_MOVEMENT) {
                            moveX = moveX > 0 ? MAXIMUM_MOVEMENT : -MAXIMUM_MOVEMENT;
                        }
                    } else {
                        direction = followUser.currentLocation.getDirection();
                    }
                }
                break;
        }

        if (moveX != 0 || moveY != 0 || direction != currentLocation.getDirection()) {
            try {
                move(direction, currentLocation.getPosX() + moveX, currentLocation.getPosY() + moveY, false);
            } catch (IllegalPositionException ignored) {

            }
        }
    }

    private void chat() {
        chat("Ich liebe dich.", new byte[0], null);
    }
}
