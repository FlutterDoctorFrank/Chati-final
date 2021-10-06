package model.user;

import controller.network.ClientSender.SendAction;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.exception.IllegalNotificationActionException;
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

    private static final float FOLLOW_DISTANCE = 128;
    private static final float UNFOLLOW_DISTANCE = 1024;

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

        if (action == SendAction.AVATAR_SPAWN) {
            if (followUser != null) {
                return;
            }

            if (object instanceof User) {
                User user = (User) object;
                if (getLocation() != null && user.getLocation() != null
                        && getLocation().distance(user.getLocation()) <= FOLLOW_DISTANCE) {
                    followUser = user;
                }
            }
        }

        if (action == SendAction.AVATAR_MOVE) {
            if (object instanceof User) {
                User user = (User) object;
                if (followUser == null && getLocation() != null && user.getLocation() != null
                        && getLocation().distance(user.getLocation()) <= FOLLOW_DISTANCE) {
                    followUser = user;
                } else if (user.equals(followUser) && (getLocation() == null || user.getLocation() == null
                        || getLocation().distance(user.getLocation()) >= UNFOLLOW_DISTANCE)) {
                    followUser = null;
                }
            }
        }

        if (action == SendAction.AVATAR_REMOVE) {
            if (followUser == null) {
                return;
            }

            if (object instanceof User) {
                User user = (User) object;
                if (user.equals(followUser)) {
                    followUser = null;
                }
            }
        }
    }

    private void move() {
    }

    private void follow() {
    }

    private void chat() {
        chat("Ich liebe dich.", new byte[0], null);
    }
}
