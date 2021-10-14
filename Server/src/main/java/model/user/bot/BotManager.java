package model.user.bot;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import utils.AudioUtils;
import controller.network.ClientSender;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Eine Klasse, welche die Bots der Anwwendung verwaltet.
 */
public class BotManager implements Runnable {

    /** Weibliche deutsche Stimme für den Text-To-Speech Synthesizer. */
    public static final String GERMAN_FEMALE_VOICE = "bits1-hsmm";

    /** Männliche deutsche Stimme für den Text-To-Speech Synthesizer. */
    public static final String GERMAN_MALE_VOICE = "bits3-hsmm";

    /** Männliche englische Stimme für den Text-To-Speech Synthesizer. */
    public static final String ENGLISH_MALE_VOICE = "cmu-rms-hsmm";

    /** Singleton-Instanz der Klasse. */
    private static BotManager botManager;

    /** Menge aller Bots. */
    private final Map<String, Bot> bots;

    /** Stellt Methoden bereit, um Text in Sprachdaten umzuwandeln. */
    private MaryInterface textToSpeech;

    /** Information, ob der BotManager gerade aktiv ist. */
    private boolean isRunning;

    /**
     * Erzeugt eine neue Instanz des BotManager.
     */
    private BotManager() {
        this.bots = new HashMap<>();
        this.isRunning = false;
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

    @Override
    public void run() {
        try {
            textToSpeech = new LocalMaryInterface();
        } catch (MaryConfigurationException e) {
            e.printStackTrace();
        }

        long now = System.currentTimeMillis();
        long timer = 0;
        long deltaTime;

        while (isRunning) {
            deltaTime = System.currentTimeMillis() - now;
            now = System.currentTimeMillis();
            timer += deltaTime;

            for (Bot bot : bots.values()) {
                if (bot.hasVoiceData()) {
                    byte[] sendData = bot.getNextFrame();
                    if (sendData != null) {
                        bot.talk(sendData);
                    }
                } else {
                    if (timer >= 1000) {
                        bot.randomChat();
                    }
                }
                bot.choose();
            }
            if (timer >= 1000) {
                timer = 0;
            }

            try {
                Thread.sleep(955 / AudioUtils.FRAME_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generiert abspielbare Sprachdaten aus einem Text.
     * @param voice Stimme, in der die Sprachdaten generiert werden soll.
     * @param text Text, der in Sprachdaten umgewandelt werden soll.
     * @return Sprachdaten des Textes.
     */
    public byte[] generateSpeech(@NotNull final String voice, @NotNull final String text) {
        if (textToSpeech != null) {
            textToSpeech.setVoice(voice);
            try {
                AudioInputStream audioIn = textToSpeech.generateAudio(text);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                AudioSystem.write(audioIn, AudioFileFormat.Type.WAVE, out);
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                byte[] bytes = AudioSystem.getAudioInputStream(new BufferedInputStream(in)).readAllBytes();
                short[] shorts = AudioUtils.toShort(bytes, false);
                return AudioUtils.toByte(AudioUtils.sampleUp(shorts, 3), true);
            } catch (SynthesisException | IOException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
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
