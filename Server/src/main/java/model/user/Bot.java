package model.user;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import controller.AudioUtils;
import controller.network.ClientSender.SendAction;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.context.spatial.Direction;
import model.context.spatial.Location;
import model.context.spatial.MapUtils;
import model.context.spatial.Room;
import model.exception.IllegalNotificationActionException;
import model.exception.IllegalPositionException;
import model.exception.UserNotFoundException;
import model.notification.Notification;
import model.role.Permission;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import javax.sound.sampled.AudioFormat;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welche einen automatisierten Benutzer repräsentiert.
 */
public class Bot extends User {

    private static final float DEFAULT_VELOCITY = 100f / 30;
    private static final float SPRINT_SPEED_FACTOR = 1.67f;
    private static final float MINIMUM_DISTANCE = 2 * MapUtils.INTERACTION_DISTANCE;
    private static final float FOLLOW_DISTANCE = 4 * MapUtils.INTERACTION_DISTANCE;
    private static final float MIN_AUTO_CHAT_PAUSE = 10; // In Sekunden.

    private final Queue<byte[]> audioDataBuffer;
    private final Voice voice;
    private User followUser;
    private User forwardWhisperTo;
    private boolean sprint;
    private boolean follow;
    private boolean teleport;
    private boolean autoChat;
    private long nextFollow;
    private long nextRotate;
    private long lastTimeAutoChat;
    private final Random random;

    /**
     * Erzeugt eine neue Instanz eines Bot.
     * @param botname Name des Bots.
     */
    public Bot(@NotNull final String botname) {
        super(botname, false);
        this.audioDataBuffer = new LinkedBlockingQueue<>();
        this.voice = VoiceManager.getInstance().getVoice("kevin16");
        this.voice.setAudioPlayer(new BotAudioPlayer(this));
        this.voice.allocate();
        this.sprint = false;
        this.follow = true;
        this.autoChat = true;
        this.random = new Random();
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
                        } else {
                            choose();
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

    /**
     * Lässt den Bot ein Ziel zum Verfolgen auswählen.
     */
    public void choose() {
        if (followUser != null || !follow || !teleport) {
            return;
        }

        if (System.currentTimeMillis() > nextFollow) {
            try {
                followUser = currentLocation.getRoom().getUsers().values().stream()
                        .filter(user -> !user.equals(this)).findAny().orElseThrow();

                Room room = currentLocation.getRoom();
                Direction direction = followUser.currentLocation.getDirection();
                float posX = followUser.currentLocation.getPosX();
                float posY = followUser.currentLocation.getPosY();

                switch (direction) {
                    case UP:
                        posY -= MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posY += 1;
                            if (posY > followUser.currentLocation.getPosY()) {
                                return;
                            }
                        }
                        break;

                    case RIGHT:
                        posX -= MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posX += 1;
                            if (posX > followUser.currentLocation.getPosX()) {
                                return;
                            }
                        }
                        break;

                    case DOWN:
                        posY += MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posY -= 1;
                            if (posY < followUser.currentLocation.getPosY()) {
                                return;
                            }
                        }
                        break;

                    case LEFT:
                        posX += MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posX -= 1;
                            if (posX > followUser.currentLocation.getPosX()) {
                                return;
                            }
                        }
                        break;
                }

                teleport(new Location(room, direction, posX, posY));
            } catch (NoSuchElementException ignored) {

            }
        } else if (System.currentTimeMillis() > nextRotate) {
            final int rotate = (currentLocation.getDirection().ordinal() + 1) % Direction.values().length;

            try {
                move(Direction.values()[rotate], currentLocation.getPosX(), currentLocation.getPosY(), false);
                nextRotate += 250;
            } catch (IllegalPositionException ignored) {

            }
        }
    }

    /**
     * Lässt den Bot vorgefertigte Nachrichten schreiben.
     */
    public void chat() {
        long now = System.currentTimeMillis();
        if (now - lastTimeAutoChat < 1000 * MIN_AUTO_CHAT_PAUSE) {
            return;
        }
        lastTimeAutoChat = now;
        int randomNumber = random.nextInt(128);
        switch (randomNumber) {
            case 0:
                chat("Wer immer tut was er schon kann, bleibt immer das was er schon war.", new byte[0], null);
                break;
            case 1:
                chat("Ein guter Gewinner muss auch ein guter Verlierer sein.", new byte[0], null);
                break;
            case 2:
                chat("Zu fallen ist nicht schlimm. Schlimm ist es, liegenzubleiben.", new byte[0], null);
                break;
            case 3:
                chat("Es gibt keinen Weg zum Glück. Glücklich sein ist der Weg.", new byte[0], null);
                break;
            case 4:
                chat("Denke nicht so oft an das was dir fehlt, sondern an das was du hast.", new byte[0], null);
                break;
            case 5:
                chat("Wer etwas will, der sucht nach Wegen. Wer etwas nicht will, sucht nach Gründen.", new byte[0], null);
                break;
            case 6:
                chat("Manchmal muss man am Ende stehen, um den Anfang zu sehen.", new byte[0], null);
                break;
            case 7:
                chat("Die Wahrheit erkennt man nicht an schönen Worten, sondern an leisen Taten.", new byte[0], null);
                break;
            default:
                // Tu nichts.
        }
    }

    /**
     * Gibt zurück, ob der Bot von selbst schreiben soll.
     * @return true, wenn der Bot von selbst schreiben soll, sonst false.
     */
    public boolean isAutoChat() {
        return autoChat;
    }

    /**
     * Gibt zurück, ob der Bot aktuell Daten zum Abspielen hat.
     * @return true, wenn er Daten zum Abspielen hat, sonst false.
     */
    public boolean hasData() {
        return !audioDataBuffer.isEmpty();
    }

    /**
     * Gibt die nächsten zu sendenden Sprachdaten zurück.
     * @return Zu sendende Sprachdaten des Bots.
     */
    public byte[] getNextFrame() {
        return audioDataBuffer.poll();
    }

    /**
     * Gibt zurück, ob der Bot vor einem Benutzer steht.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn er vor dem Benutzer steht, sonst false.
     */
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
        nextFollow = System.currentTimeMillis() + 3000;
        nextRotate = System.currentTimeMillis() + 250;

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
        float movementSpeed = sprint && followUser.isSprinting() ? DEFAULT_VELOCITY * SPRINT_SPEED_FACTOR : DEFAULT_VELOCITY;

        switch (followUser.currentLocation.getDirection()) {
            case UP:
            case DOWN:
                moveX = followUser.currentLocation.getPosX() - currentLocation.getPosX();

                if (moveX != 0) {
                    if (Math.abs(moveX) > movementSpeed) {
                        direction = moveX > 0 ? Direction.RIGHT : Direction.LEFT;
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
                        direction = moveY > 0 ? Direction.UP : Direction.DOWN;
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
     * Lässt den Bot einen Text vorlesen.
     * @param message Vorzulesender Text.
     */
    private void talk(@NotNull final String message) {
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
         * Lässt den Bot zu einem anderen Benutzer teleportieren, wenn er keinem Benutzer verfolgen kann.
         */
        TELEPORT_ON("(?i)teleport_on") {
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.teleport = true;
            }
        },

        /**
         * Beendet das Teleportieren zu anderen Benutzern.
         */
        TELEPORT_OFF("(?i)teleport_off") {
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.teleport = false;
            }
        },

        /**
         * Lässt den Bot sprinten.
         */
        SPRINT_ON("(?i)sprint_on") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.sprint = true;
            }
        },

        /**
         * Lässt den Bot in normaler Geschwindigkeit fortbewegen.
         */
        SPRINT_OFF("(?i)sprint_off") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.sprint = false;
            }
        },

        /**
         * Lässt den Bot vorgefertigte Nachrichten schreiben und sprechen.
         */
        AUTOCHAT_ON("(?i)autochat_on") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.autoChat = true;
            }
        },

        /**
         * Stoppt das Schreiben und Sprechen von vorgefertigten Nachrichten.
         */
        AUTOCHAT_OFF("(?i)autochat_off") {
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

        private final Bot bot;
        private AudioFormat currentFormat;

        /**
         * Erzeugt eine neue Instanz des BotAudioPlayer.
         * @param bot Zugehöriger Bot.
         */
        public BotAudioPlayer(@NotNull final Bot bot) {
            this.bot = bot;
            setAudioFormat(new AudioFormat(AudioUtils.ENCODING, AudioUtils.SAMPLING_RATE,
                    AudioUtils.SAMPLE_SIZE_IN_BITS, AudioUtils.CHANNELS, AudioUtils.FRAME_SIZE,
                    AudioUtils.FRAME_RATE, true));
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

        @Override
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
            short[] shorts = AudioUtils.toShort(bytes, true);
            short[] upSampledShorts = new short[2 * shorts.length];
            for (int i = 0; i < shorts.length - 1; i++) {
                upSampledShorts[2 * i] = shorts[i];
                upSampledShorts[2 * i + 1] = (short) (1f / 2 * shorts[i] + 1f / 2 * shorts[i + 1]);
            }
            byte[] upSampledBytes = AudioUtils.toByte(upSampledShorts, true);
            for (int i = 0; i < upSampledBytes.length; i += AudioUtils.FRAME_SIZE) {
                bot.audioDataBuffer.add(Arrays.copyOfRange(upSampledBytes, i, i + AudioUtils.FRAME_SIZE));
            }
            return true;
        }

        @Override
        public void showMetrics() {
        }
    }
}
