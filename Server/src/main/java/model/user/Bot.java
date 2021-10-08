package model.user;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import controller.network.ClientSender.SendAction;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.context.spatial.Area;
import model.context.spatial.Direction;
import model.exception.IllegalNotificationActionException;
import model.exception.IllegalPositionException;
import model.exception.UserNotFoundException;
import model.notification.Notification;
import model.role.Permission;
import model.role.Role;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioFormat;
import java.util.*;

/**
 * Eine Klasse, welche einen automatisierten Benutzer repräsentiert.
 */
public class Bot extends User {

    private static final float DEFAULT_VELOCITY = 100f / 30;
    private static final float SPRINT_SPEED_FACTOR = 1.67f;
    private static final float MINIMUM_DISTANCE = 2 * Area.VIEW_TILE_SIZE;
    private static final float FOLLOW_DISTANCE = 4 * Area.VIEW_TILE_SIZE;

    private static final Map<String, Bot> bots = new HashMap<>();

    private final Voice voice;
    private User followUser;
    private User forwardWhisperTo;
    private boolean sprint;
    private boolean follow;
    private boolean autoChat;

    /**
     * Erzeugt eine neue Instanz eines Bot.
     * @param botname Name des Bots.
     */
    private Bot(@NotNull final String botname) {
        super(botname, false);
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        this.voice = VoiceManager.getInstance().getVoice("kevin16");
        this.voice.setAudioPlayer(new BotAudioPlayer(this));
        this.voice.allocate();
        this.sprint = false;
        this.follow = true;
        this.autoChat = true;
    }

    /**
     * Erzeugt einen neuen Bot.
     * @param botname Name des Bots.
     * @param executor Erzeugender Benutzer.
     */
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

    /**
     * Entfernt einen Bot.
     * @param botname Name des Bots.
     * @param executor Entfernender Benutzer.
     */
    public static void destroy(@NotNull final String botname, @NotNull final User executor) {
        if (executor.getWorld() == null || executor.getLocation() == null) {
            // Evtl Loggen
            return;
        }

        if (destroy(botname)) {
            bots.remove(botname);
        } else {
            // Informiere den ausführenden Benutzer darüber, dass kein Bot mit diesem Namen existiert.
            TextMessage infoMessage = new TextMessage("chat.command.create-bot.not-exist");
            executor.send(SendAction.MESSAGE, infoMessage);
        }
    }

    /**
     * Entfernt alle Bots.
     * @param executor Entfernender Benutzer.
     */
    public static void destroyAll(@NotNull final User executor) {
        bots.values().forEach(bot -> destroy(bot.getUsername()));
        bots.clear();
    }

    /**
     * Löscht die Informationen über einen Bot.
     * @param botname Name des Bots, dessen Informationen gelöscht werden soll.
     * @return true, wenn ein Bot mit dem Namen gefunden wurde, sonst false.
     */
    private static boolean destroy(@NotNull final String botname) {
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
     * Gibt einen Bot zurück.
     * @param botId ID des Bots.
     * @return Bot mit der ID.
     */
    public static @Nullable Bot get(@NotNull final UUID botId) {
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
    public static @Nullable Bot get(@NotNull final String botname) {
        return bots.get(botname);
    }

    @Override
    public boolean isOnline() {
        return getStatus() != Status.OFFLINE;
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object object) {
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

            case MESSAGE:
                if (object instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) object;
                    User sender = textMessage.getSender();
                    String message = textMessage.getTextMessage();
                    if (sender != null && !sender.equals(this)) {
                        if (textMessage.getMessageType() == MessageType.WHISPER && message != null
                                && message.startsWith("\\") && getWorld() != null
                                && sender.hasPermission(getWorld(), Permission.MANAGE_BOTS)) {
                            for (WhisperCommand command : WhisperCommand.values()) {
                                if (message.substring(1).matches(command.commandPattern)) {
                                    command.handle(sender, this, message.substring(1));
                                    return;
                                }
                            }
                            if (forwardWhisperTo != null) {
                                forwardWhisperTo.send(SendAction.MESSAGE, textMessage);
                                return;
                            }
                        }
                        chat();
                    }
                }
                break;

            case NOTIFICATION:
                if (object instanceof Notification) {
                    Notification notification = (Notification) object;
                    try {
                        notification.accept();
                    } catch (IllegalNotificationActionException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
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

    /**
     * Lässt den Bot einen Benutzer zum Folgen suchen.
     */
    private void search() {
        followUser = null;
        if (!follow) {
            return;
        }

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

    /**
     * Folgt einem Benutzer.
     */
    private void follow() {
        if (followUser == null || !follow) {
            return;
        }

        Direction direction = currentLocation.getDirection();
        float moveX = 0;
        float moveY = 0;
        float movementSpeed = sprint ? DEFAULT_VELOCITY * SPRINT_SPEED_FACTOR : DEFAULT_VELOCITY;

        switch (followUser.currentLocation.getDirection()) {
            case UP:
            case DOWN:
                moveX = followUser.currentLocation.getPosX() - currentLocation.getPosX();

                if (moveX != 0) {
                    if (Math.abs(moveX) > movementSpeed) {
                        moveX = moveX > 0 ? movementSpeed : -movementSpeed;
                    }
                } else {
                    if (followUser.currentLocation.getDirection() == direction) {
                        moveY = followUser.currentLocation.getPosY() - currentLocation.getPosY();

                        if (Math.abs(moveY) < MINIMUM_DISTANCE) {
                            moveY = 0;
                        } else if (Math.abs(moveY) > movementSpeed) {
                            moveY = moveY > 0 ? movementSpeed : -movementSpeed;
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
                    if (Math.abs(moveY) > movementSpeed) {
                        moveY = moveY > 0 ? movementSpeed : -movementSpeed;
                    }
                } else {
                    if (followUser.currentLocation.getDirection() == direction) {
                        moveX = followUser.currentLocation.getPosX() - currentLocation.getPosX();

                        if (Math.abs(moveX) < MINIMUM_DISTANCE) {
                            moveX = 0;
                        } else if (Math.abs(moveX) > movementSpeed) {
                            moveX = moveX > 0 ? movementSpeed : -movementSpeed;
                        }
                    } else {
                        direction = followUser.currentLocation.getDirection();
                    }
                }
                break;
        }

        if (moveX != 0 || moveY != 0 || direction != currentLocation.getDirection()) {
            try {
                move(direction, currentLocation.getPosX() + moveX, currentLocation.getPosY() + moveY, sprint);
            } catch (IllegalPositionException ignored) {

            }
        }
    }

    /**
     * Lässt den Bot vorgefertigte Nachrichten schreiben oder sprechen.
     */
    private void chat() {
        if (!autoChat) {
            return;
        }
        // Sprüche ausm Lexikon klopfen
    }

    /**
     * Lässt den Bot einen Text vorlesen.
     * @param message Vorzulesender Text.
     */
    private void talk(@NotNull String message) {
        this.voice.speak(message);
    }

    /**
     * Ein Enum, welches die Kommandos repräsentiert, mit denen man den Bot konfigurieren kann.
     */
    private enum WhisperCommand {

        /**
         * Lässt den Bot einen Benutzer verfolgen.
         */
        FOLLOW("(?i)follow(\\s.*|$)") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                String[] commandParts = message.split("\\s", 2);

                if (commandParts.length == 2) {
                    try {
                        bot.followUser = UserAccountManager.getInstance().getUser(commandParts[1]);
                        if (bot.followUser.getLocation() != null) {
                            bot.teleport(bot.followUser.getLocation());
                        }
                    } catch (UserNotFoundException e) {
                        // Tu nichts.
                    }
                }

                bot.follow = true;
            }
        },

        /**
         * Beendet das Verfolgen von Benutzern.
         */
        UNFOLLOW("(?i)unfollow") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.follow = false;
                bot.followUser = null;
            }
        },

        /**
         * Lässt den Bot sprinten.
         */
        SPRINT_ON("(?i)sprint_on.*") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.sprint = true;
            }
        },

        /**
         * Lässt den Bot in normaler Geschwindigkeit fortbewegen.
         */
        SPRINT_OFF("(?i)sprint_off.*") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.sprint = false;
            }
        },

        /**
         * Lässt den Bot vorgefertigte Nachrichten schreiben und sprechen.
         */
        AUTOCHAT_ON("(?i)autochat_on.*") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.autoChat = true;
            }
        },

        /**
         * Stoppt das Schreiben und Sprechen von vorgefertigten Nachrichten.
         */
        AUTOCHAT_OFF("(?i)autochat_off.*") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.autoChat = false;
            }
        },

        /**
         * Veranlasst, dass der Bot eine erhaltene Flüsternachricht an den hinterlegten Benutzer weiterleitet, sofern
         * diese Flüsternachricht kein Kommando enthält.
         */
        FORWARD_ON("(?i)forward(\\s.*|$)") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                String[] commandParts = message.split("\\s", 2);

                if (commandParts.length < 2) {
                    bot.forwardWhisperTo = sender;
                } else {
                    try {
                        bot.forwardWhisperTo = UserAccountManager.getInstance().getUser(commandParts[1]);
                    } catch (UserNotFoundException e) {
                        // Tu nichts.
                    }
                }
            }
        },

        /**
         * Veranlasst, dass der Bot keine Flüsternachrichten mehr weiterleitet.
         */
        FORWARD_OFF("(?i)forward_off.*") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.forwardWhisperTo = null;
            }
        },

        /**
         * Lässt den Bot eine Nachricht schreiben.
         */
        WRITE("(?i)write(\\s.*|$)") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                String[] commandParts = message.split("\\s", 2);

                if (commandParts.length == 2) {
                    bot.chat(commandParts[1], new byte[0], null);
                }
            }
        },

        /**
         * Lässt den Bot eine Nachricht vorlesen.
         */
        TALK("(?i)talk(\\s.*|$)") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                String[] commandParts = message.split("\\s", 2);

                if (commandParts.length == 2) {
                    bot.talk(commandParts[1]);
                }
            }
        };

        /** Ein regulärer Ausdruck eines spezifischen Chatbefehls. */
        private final String commandPattern;

        /**
         * Verarbeitet die Nachricht gemäß dem Chatbefehl.
         * @param sender Sender der Nachricht.
         * @param message Erhaltene Nachricht.
         */
        protected abstract void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message);

        /**
         * Erzeugt eine Instanz eines Chatbefehls für einen Bot.
         * @param commandPattern Pattern der Nachricht zur Verwendung dieses Chatbefehls.
         */
        WhisperCommand(@NotNull final String commandPattern) {
            this.commandPattern = commandPattern;
        }
    }

    /**
     * Der AudioPlayer des Bots.
     */
    private static class BotAudioPlayer implements AudioPlayer {

        private static final int SAMPLING_RATE = 44100;
        private static final int SEND_RATE = 30;
        private static final int BLOCK_SIZE = SAMPLING_RATE / SEND_RATE;

        private final Bot bot;
        private AudioFormat currentFormat;

        /**
         * Erzeugt eine neue Instanz des BotAudioPlayer.
         * @param bot Zugehöriger Bot.
         */
        public BotAudioPlayer(@NotNull final Bot bot) {
            this.bot = bot;
            setAudioFormat(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLING_RATE, 16, 1, BLOCK_SIZE, SEND_RATE, true));
        }

        @Override
        public synchronized void setAudioFormat(AudioFormat format) {
            this.currentFormat = format;
        }

        @Override
        public AudioFormat getAudioFormat() {
            return currentFormat;
        }

        @Override
        public void pause() {
        }

        @Override
        public synchronized void resume() {
        }

        @Override
        public synchronized void cancel() {
        }

        @Override
        public synchronized void reset() {
        }

        @Override
        public void startFirstSampleTimer() {
        }

        @Override
        public synchronized void close() {
        }

        @Override
        public float getVolume() {
            return 1.0F;
        }

        @Override
        public void setVolume(float volume) {
        }

        @Override
        public void begin(int size) {
        }

        @Override
        public boolean end() {
            return true;
        }

        public boolean drain() {
            return true;
        }

        @Override
        public synchronized long getTime() {
            return -1L;
        }

        @Override
        public synchronized void resetTime() {
        }

        @Override
        public boolean write(byte[] audioData) {
            return write(audioData, 0, audioData.length);
        }

        @Override
        public boolean write(byte[] bytes, int offset, int size) {
            short[] shorts = toShort(bytes, true);
            short[] upSampledShorts = new short[2 * shorts.length];
            for (int i = 0; i < shorts.length - 1; i++) {
                upSampledShorts[2 * i] = shorts[i];
                upSampledShorts[2 * i + 1] = (short) (1f / 2 * shorts[i] + 1f / 2 * shorts[i + 1]);
            }
            byte[] upSampledBytes = toByte(upSampledShorts, true);
            for (int i = 0; i < upSampledBytes.length; i += BLOCK_SIZE) {
                bot.talk(Arrays.copyOfRange(upSampledBytes, i, i + BLOCK_SIZE));
            }
            return true;
        }

        @Override
        public void showMetrics() {
        }
    }

    /**
     * Kopiert Daten aus einem Short-Array in einen Byte-Array doppelter Größe.
     * @param shorts Short-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Byte-Array mit den kopierten Daten.
     */
    public static byte[] toByte(final short[] shorts, final boolean bigEndian) {
        byte[] bytes = new byte[shorts.length * 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) (shorts[i] >> 8);
                bytes[2 * i + 1] = (byte) shorts[i];
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) shorts[i];
                bytes[2 * i + 1] = (byte) (shorts[i] >> 8);
            }
        }
        return bytes;
    }

    /**
     * Kopiert Daten aus einem Byte-Array in einen Short-Array halber Größe.
     * @param bytes Byte-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Short-Array mit den kopierten Daten.
     */
    public static short[] toShort(final byte[] bytes, final boolean bigEndian) {
        short[] shorts = new short[bytes.length / 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] & 0xff) | (bytes[2 * i + 1] << 8));
            }
        }
        return shorts;
    }
}
