package model.user.bot;

import controller.AudioUtils;
import controller.network.ClientSender;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.role.Role;
import model.user.Bot;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Eine Klasse, welche die Bots der Anwwendung verwaltet.
 */
public class BotManager implements Runnable {

    /** Singleton-Instanz der Klasse. */
    private static BotManager botManager;

    /** Menge aller Bots. */
    private final Map<String, Bot> bots;

    /** Information, ob der BotManager gerade aktiv ist. */
    private boolean isRunning;

    /**
     * Erzeugt eine neue Instanz des BotManager.
     */
    private BotManager() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        this.bots = new HashMap<>();
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            bots.values().forEach(bot -> {
                if (bot.hasData()) {
                    byte[] sendData = bot.getNextFrame();
                    if (sendData != null) {
                        bot.talk(sendData);
                    }
                } else if (bot.isAutoChat()) {
                    bot.chat();
                }
                bot.choose();
            });

            try {
                Thread.sleep(955 / AudioUtils.FRAME_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Erzeugt einen neuen Bot.
     * @param botname Name des Bots.
     * @param executor Erzeugender Benutzer.
     */
    public void createBot(@NotNull final String botname, @NotNull final User executor) {
        if (executor.getWorld() == null || executor.getLocation() == null) {
            return;
        }

        if (bots.containsKey(botname) || UserAccountManager.getInstance().isRegistered(botname)) {
            // Informiere den ausführenden Benutzer darüber, dass bereits ein Bot mit diesem Namen existiert.
            TextMessage infoMessage = new TextMessage("chat.command.create-bot.already-exist");
            executor.send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }

        Bot bot = new Bot(botname);
        bot.addRole(GlobalContext.getInstance(), Role.BOT);
        bot.login(null);
        GlobalContext.getInstance().addUser(bot);
        bot.teleport(executor.getLocation());
        bots.put(botname, bot);
    }

    /**
     * Entfernt einen Bot.
     * @param botname Name des Bots.
     * @param executor Entfernender Benutzer.
     */
    public void destroyBot(@NotNull final String botname, @NotNull final User executor) {
        if (executor.getWorld() == null || executor.getLocation() == null) {
            // Evtl Loggen
            return;
        }

        if (destroyBot(botname)) {
            bots.remove(botname);
        } else {
            // Informiere den ausführenden Benutzer darüber, dass kein Bot mit diesem Namen existiert.
            TextMessage infoMessage = new TextMessage("chat.command.create-bot.not-exist");
            executor.send(ClientSender.SendAction.MESSAGE, infoMessage);
        }
    }

    /**
     * Entfernt alle Bots.
     */
    public void destroyAllBots() {
        bots.values().forEach(bot -> destroyBot(bot.getUsername()));
        bots.clear();
    }

    /**
     * Gibt einen Bot zurück.
     * @param botId ID des Bots.
     * @return Bot mit der ID.
     */
    public @Nullable Bot getBot(@NotNull final UUID botId) {
        try {
            return bots.values().stream().filter(bot -> bot.getUserId().equals(botId)).findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Gibt einen Bot zurück.
     * @param botname Name des Bots.
     * @return Bot mit dem Namen.
     */
    public @Nullable Bot getBot(@NotNull final String botname) {
        return bots.get(botname);
    }

    /**
     * Löscht die Informationen über einen Bot.
     * @param botname Name des Bots, dessen Informationen gelöscht werden soll.
     * @return true, wenn ein Bot mit dem Namen gefunden wurde, sonst false.
     */
    private boolean destroyBot(@NotNull final String botname) {
        Bot bot = bots.get(botname);
        if (bot != null) {
            UserAccountManager.getInstance().getUsers().values().forEach(user -> user.removeFriend(bot));
            UserAccountManager.getInstance().getUsers().values().forEach(user -> user.unignoreUser(bot));
            bot.logout();
            return true;
        }
        return false;
    }

    /**
     * Startet den Thread des BotManager.
     */
    private void start() {
        this.isRunning = true;
        Thread botManagerThread = new Thread(this);
        botManagerThread.setDaemon(true);
        botManagerThread.start();
    }

    /**
     * Gibt die Singleton-Instanz des BotManager zurück.
     * @return Singleton-Instanz des BotManager.
     */
    public static BotManager getInstance() {
        if (botManager == null) {
            botManager = new BotManager();
        }
        if (!botManager.isRunning) {
            botManager.start();
        }
        return botManager;
    }
}
