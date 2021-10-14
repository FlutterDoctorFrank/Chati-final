package model.user.bot;

import model.user.Status;
import model.user.User;
import utils.AudioUtils;
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
    private static final float MINIMUM_DISTANCE = 2 * MapUtils.TILE_SIZE;
    private static final float FOLLOW_DISTANCE = 4 * MapUtils.TILE_SIZE;
    private static final float AUTO_CHAT_CHANCE = 1f / 128; // Pro Sekunde.
    private static final float MIN_AUTO_CHAT_PAUSE = 10; // In Sekunden.

    private final Queue<byte[]> voiceDataBuffer;
    private String voice;
    private User followUser;
    private User forwardWhisperTo;
    private boolean sprint;
    private boolean follow;
    private boolean teleport;
    private boolean autoChat;
    private boolean autoTalk;
    private long nextFollow;
    private long nextRotate;
    private long lastTimeChat;

    /**
     * Erzeugt eine neue Instanz eines Bot.
     * @param botname Name des Bots.
     */
    public Bot(@NotNull final String botname) {
        super(botname, false);
        this.voiceDataBuffer = new LinkedBlockingQueue<>();
        this.voice = getAvatar().isMale() ? BotManager.GERMAN_MALE_VOICE : BotManager.GERMAN_FEMALE_VOICE;
    }

    @Override
    public boolean isOnline() {
        return getStatus() != Status.OFFLINE;
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object object) {
        switch (action) {
            case CONTEXT_JOIN:
                if (object instanceof User && object.equals(this)) {
                    search();
                }
                break;

            case AVATAR_SPAWN:
                if (object instanceof User && !(object instanceof Bot)) {
                    User user = (User) object;
                    if (followUser != null) {
                        if (followUser.equals(object)) {
                            if (followUser.getLocation() != null &&
                                    currentLocation.distance(followUser.getLocation()) > FOLLOW_DISTANCE + MINIMUM_DISTANCE) {
                                search();
                            }
                        }
                        if (followUser.getLocation() != null && user.getLocation() != null &&
                                currentLocation.distance(followUser.getLocation()) < currentLocation.distance(user.getLocation())) {
                            return;
                        }
                    }
                    if (user.getLocation() != null && currentLocation.distance(user.getLocation()) <= FOLLOW_DISTANCE) {
                        followUser = user;
                    }
                }
                break;

            case AVATAR_MOVE:
                if (object instanceof User && !(object instanceof Bot)) {
                    User user = (User) object;
                    if (followUser != null) {
                        if (followUser.equals(object)) {
                            if (followUser.getLocation() != null
                                    && currentLocation.distance(followUser.getLocation()) > FOLLOW_DISTANCE + MINIMUM_DISTANCE) {
                                search();
                                return;
                            }

                            for (final User roomUser : currentLocation.getRoom().getUsers().values()) {
                                if (!roomUser.equals(this) && !roomUser.equals(followUser) && inFront(roomUser)) {
                                    if (followUser.getLocation() != null && roomUser.getLocation() != null
                                            &&currentLocation.distance(roomUser.getLocation())
                                            < currentLocation.distance(followUser.getLocation())) {
                                        followUser = user;
                                        return;
                                    }
                                }
                            }

                            follow();
                        } else {
                            if (followUser.getLocation() != null && user.getLocation() != null
                                    &&currentLocation.distance(user.getLocation())
                                    < currentLocation.distance(followUser.getLocation())) {
                                followUser = user;
                            }
                        }
                    } else {
                        if (user.getLocation() != null && currentLocation.distance(user.getLocation()) <= FOLLOW_DISTANCE) {
                            followUser = user;
                        }
                    }
                }
                break;

            case AVATAR_REMOVE:
                if (object instanceof User && !(object instanceof Bot)) {
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
                        if (textMessage.getMessageType() == MessageType.WHISPER && message != null) {
                            if (message.startsWith("\\") && getWorld() != null
                                    && sender.hasPermission(getWorld(), Permission.MANAGE_BOTS)) {
                                for (WhisperCommand command : WhisperCommand.values()) {
                                    if (message.substring(1).matches(command.commandPattern)) {
                                        command.handle(sender, this, message.substring(1));
                                        return;
                                    }
                                }
                            } else {
                                if (forwardWhisperTo != null) {
                                    whisper(forwardWhisperTo, "Nachricht weitergeleitet von " + sender.getUsername()
                                            + ": \n" + textMessage);
                                    return;
                                }
                                if (textMessage.getImageName() != null) {
                                    whisper(sender, "Danke für das nette Bild.");
                                }
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
                        // Ignoriere
                    }
                    removeNotification(notification);
                }
                break;
        }
    }

    /**
     * Lässt den Bot ein Ziel zum Verfolgen auswählen.
     */
    public void choose() {
        if (!teleport || !follow || followUser != null) {
            return;
        }

        if (System.currentTimeMillis() > nextFollow) {
            try {
                User follow = currentLocation.getRoom().getUsers().values().stream()
                        .filter(user -> !user.equals(this)).findAny().orElseThrow();
                if (follow.getLocation() == null) {
                    return;
                }

                Room room = currentLocation.getRoom();
                Direction direction = follow.getLocation().getDirection();
                float posX = follow.getLocation().getPosX();
                float posY = follow.getLocation().getPosY();

                switch (direction) {
                    case UP:
                        posY -= MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posY += 1;
                            if (posY > follow.getLocation().getPosY()) {
                                return;
                            }
                        }
                        break;

                    case RIGHT:
                        posX -= MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posX += 1;
                            if (posX > follow.getLocation().getPosX()) {
                                return;
                            }
                        }
                        break;

                    case DOWN:
                        posY += MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posY -= 1;
                            if (posY < follow.getLocation().getPosY()) {
                                return;
                            }
                        }
                        break;

                    case LEFT:
                        posX += MINIMUM_DISTANCE;

                        while (!room.isLegal(posX, posY)) {
                            posX -= 1;
                            if (posX > follow.getLocation().getPosX()) {
                                return;
                            }
                        }
                        break;
                }

                if (followUser == null) {
                    followUser = follow;
                    teleport(new Location(room, direction, posX, posY));
                }
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
    public void chat(@NotNull final String message) {
        chat(message, new byte[0], null);
        lastTimeChat = System.currentTimeMillis();
    }

    /**
     * Lässt den Bot einen Text vorlesen.
     * @param message Vorzulesender Text.
     */
    public void talk(@NotNull final String message) {
        byte[] voiceData = BotManager.getInstance().generateSpeech(voice, message);
        for (int i = 0; i < voiceData.length; i += AudioUtils.FRAME_SIZE) {
            voiceDataBuffer.add(Arrays.copyOfRange(voiceData, i, i + AudioUtils.FRAME_SIZE));
        }
        lastTimeChat = System.currentTimeMillis();
    }

    /**
     * Lässt den Bot zufallsabhängig Nachrichten versenden oder sprechen.
     */
    public void randomChat() {
        if (new Random().nextInt((int) (1 / AUTO_CHAT_CHANCE)) == 0) {
            String message = getRandomMessage();
            if (new Random().nextBoolean()) {
                if (canAutoChat()) {
                    chat(message);
                } else if (canAutoTalk()) {
                    talk(message);
                }
            } else {
                if (canAutoTalk()) {
                    talk(message);
                } else if (canAutoChat()) {
                    chat(message);
                }
            }
        }
    }

    /**
     * Gibt zurück, ob der Bot aktuell Daten zum Abspielen hat.
     * @return true, wenn er Daten zum Abspielen hat, sonst false.
     */
    public boolean hasVoiceData() {
        return !voiceDataBuffer.isEmpty();
    }

    /**
     * Gibt die nächsten zu sendenden Sprachdaten zurück.
     * @return Zu sendende Sprachdaten des Bots.
     */
    public byte[] getNextFrame() {
        return voiceDataBuffer.poll();
    }

    /**
     * Gibt zurück, ob der Bot vor einem Benutzer steht.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn er vor dem Benutzer steht, sonst false.
     */
    private boolean inFront(@NotNull final User user) {
        if (user.getLocation() == null) {
            return false;
        }

        switch (currentLocation.getDirection()) {
            case UP:
                return user.getLocation().getPosY() > currentLocation.getPosY();

            case RIGHT:
                return user.getLocation().getPosX() > currentLocation.getPosX();

            case DOWN:
                return user.getLocation().getPosY() < currentLocation.getPosY();

            case LEFT:
                return user.getLocation().getPosX() < currentLocation.getPosX();
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
            if (!user.equals(this) && user.getLocation() != null && inFront(user)) {
                float distance = currentLocation.distance(user.getLocation());

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
        if (!follow || followUser == null || followUser.getLocation() == null) {
            return;
        }

        Direction direction = currentLocation.getDirection();
        float moveX = 0;
        float moveY = 0;
        float movementSpeed = sprint && followUser.isSprinting() ? DEFAULT_VELOCITY * SPRINT_SPEED_FACTOR : DEFAULT_VELOCITY;

        switch (followUser.getLocation().getDirection()) {
            case UP:
            case DOWN:
                moveX = followUser.getLocation().getPosX() - currentLocation.getPosX();

                if (moveX != 0) {
                    if (Math.abs(moveX) > movementSpeed) {
                        direction = moveX > 0 ? Direction.RIGHT : Direction.LEFT;
                        moveX = moveX > 0 ? movementSpeed : -movementSpeed;
                    }
                } else {
                    if (followUser.getLocation().getDirection() == direction) {
                        moveY = followUser.getLocation().getPosY() - currentLocation.getPosY();

                        if (Math.abs(moveY) < MINIMUM_DISTANCE) {
                            moveY = 0;
                        } else if (Math.abs(moveY) > movementSpeed) {
                            moveY = moveY > 0 ? movementSpeed : -movementSpeed;
                        }
                    } else {
                        direction = followUser.getLocation().getDirection();
                    }
                }
                break;

            case RIGHT:
            case LEFT:
                moveY = followUser.getLocation().getPosY() - currentLocation.getPosY();

                if (moveY != 0) {
                    if (Math.abs(moveY) > movementSpeed) {
                        direction = moveY > 0 ? Direction.UP : Direction.DOWN;
                        moveY = moveY > 0 ? movementSpeed : -movementSpeed;
                    }
                } else {
                    if (followUser.getLocation().getDirection() == direction) {
                        moveX = followUser.getLocation().getPosX() - currentLocation.getPosX();

                        if (Math.abs(moveX) < MINIMUM_DISTANCE) {
                            moveX = 0;
                        } else if (Math.abs(moveX) > movementSpeed) {
                            moveX = moveX > 0 ? movementSpeed : -movementSpeed;
                        }
                    } else {
                        direction = followUser.getLocation().getDirection();
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
     * Flüstert einem Benutzer eine Nachricht zu.
     * @param user Benutzer, dem eine Nachricht zugeflüstert werden soll.
     * @param message Nachricht, die dem Benutzer zugeflüstert werden soll.
     */
    private void whisper(@NotNull final User user, @NotNull final String message) {
        TextMessage whisperMessage = new TextMessage(this, message, new byte[0], null, MessageType.WHISPER);
        user.send(SendAction.MESSAGE, whisperMessage);
    }

    /**
     * Gibt zurück, ob der Bot von selbst schreiben soll.
     * @return true, wenn der Bot von selbst schreiben soll, sonst false.
     */
    private boolean canAutoChat() {
        return autoChat && System.currentTimeMillis() - lastTimeChat >= 1000 * MIN_AUTO_CHAT_PAUSE;
    }

    /**
     * Gibt zurück, ob der Bot von selbst sprechen soll.
     * @return true, wenn der Bot von selbst sprechen soll, sonst false.
     */
    private boolean canAutoTalk() {
        return autoTalk && System.currentTimeMillis() - lastTimeChat >= 1000 * MIN_AUTO_CHAT_PAUSE;
    }

    /**
     * Gibt eine zufällige vorgefertigte Nachricht zurück.
     * @return Zufällige vorgefertigte Nachricht.
     */
    private @NotNull String getRandomMessage() {
        switch (new Random().nextInt(12)) {
            case 0: return "Wer immer tut was er schon kann, bleibt immer das was er schon war.";
            case 1: return "Ein guter Gewinner muss auch ein guter Verlierer sein.";
            case 2: return "Zu fallen ist nicht schlimm. Schlimm ist es, liegenzubleiben.";
            case 3: return "Es gibt keinen Weg zum Glück. Glücklich sein ist der Weg.";
            case 4: return "Denke nicht so oft an das was dir fehlt, sondern an das was du hast.";
            case 5: return "Wer etwas will, der sucht nach Wegen. Wer etwas nicht will, sucht nach Gründen.";
            case 6: return "Manchmal muss man am Ende stehen, um den Anfang zu sehen.";
            case 7: return "Die Wahrheit erkennt man nicht an schönen Worten, sondern an leisen Taten.";
            case 8: return "Man kann dir deinen Weg weisen, gehen musst du ihn selbst...";
            case 9: return "Auch ein langer Weg beginnt mit einem ersten Schritt.";
            case 10: return "Das Leben ist wie eine Achterbahnfahrt.";
            case 11: return "Gib niemals die Hoffnung auf.";
            default: return "";
        }
    }

    /**
     * Ein Enum, welches die Kommandos repräsentiert, mit denen man den Bot konfigurieren kann.
     */
    private enum WhisperCommand {

        /**
         * Sendet eine Auflistung aller verfügbaren Befehle als Flüsternachricht.
         */
        HELP("(?i)help") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.whisper(sender, "Verfügbare Befehle: \n"
                        + "\\follow <Benutzername> : Teleportiere zum Benutzer und verfolge ihn. \n"
                        + "\\follow : Verfolge Benutzer in der Nähe. \n"
                        + "\\unfollow : Verfolge keinen Benutzer mehr. \n"
                        + "\\teleport_on : Teleportiere zu einem Benutzer, wenn kein Benutzer zum Verfolgen gefunden wird. \n"
                        + "\\teleport_off : Teleportiere nicht mehr zu einem Benutzer, wenn kein Benutzer zum Verfolgen gefunden wird. \n"
                        + "\\sprint_on : Laufe mit Sprintgeschwindigkeit. \n"
                        + "\\sprint_off : Laufe mit normaler Geschwindigkeit. \n"
                        + "\\autochat_on : Schreibe automatisiert Nachrichten per Zufall. \n"
                        + "\\autochat_off : Schreibe nicht mehr automatisiert Nachrichten per Zufall. \n"
                        + "\\autotalk_on : Spreche automatisiert Nachrichten per Zufall. \n"
                        + "\\autotalk_off : Spreche nicht mehr automatisiert Nachrichten per Zufall. \n"
                        + "\\forward <Benutzername> : Leite erhaltene Flüsternachrichten an den Benutzer weiter. \n"
                        + "\\forward : Leite erhaltene Flüsternachrichten an dich weiter. \n"
                        + "\\forward_off : Leite Flüsternachrichten nicht mehr weiter. \n"
                        + "\\write <Nachricht> : Schreibe eine Textnachricht. \n"
                        + "\\talk <Nachricht> : Lese eine Nachricht vor."
                        + "\\set_voice <Name> : Lege meine Stimme fest. Wahl aus: de-w, de-m und en-m. \n"
                        + "\\destroy : Zerstöre mich.");
            }
        },

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
                        bot.whisper(sender, "Ich konnte diesen Benutzer nicht finden.");
                        return;
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
         * Lässt den Bot vorgefertigte Nachrichten schreiben.
         */
        AUTOCHAT_ON("(?i)autochat_on") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.autoChat = true;
            }
        },

        /**
         * Stoppt das Schreiben von vorgefertigten Nachrichten.
         */
        AUTOCHAT_OFF("(?i)autochat_off") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.autoChat = false;
            }
        },

        /**
         * Lässt den Bot vorgefertigte Nachrichten sprechen.
         */
        AUTOTALK_ON("(?i)autotalk_on") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.autoTalk = true;
            }
        },

        /**
         * Stoppt das Sprechen von vorgefertigten Nachrichten.
         */
        AUTOTALK_OFF("(?i)autotalk_off") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                bot.autoTalk = false;
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

                if (commandParts.length == 2) {
                    try {
                        bot.forwardWhisperTo = UserAccountManager.getInstance().getUser(commandParts[1]);
                    } catch (UserNotFoundException e) {
                        bot.whisper(sender, "Ich konnte diesen Benutzer nicht finden.");
                    }
                } else {
                    bot.forwardWhisperTo = sender;
                }
            }
        },

        /**
         * Veranlasst, dass der Bot keine Flüsternachrichten mehr weiterleitet.
         */
        FORWARD_OFF("(?i)forward_off") {
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
                    bot.chat(commandParts[1]);
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
        },

        SET_VOICE("(?i)set_voice(\\s.*|$)") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                String[] commandParts = message.split("\\s", 2);

                if (commandParts.length == 2) {
                    switch (commandParts[1]) {
                        case "de-w":
                            bot.voice = BotManager.GERMAN_FEMALE_VOICE;
                            break;
                        case "de-m":
                            bot.voice = BotManager.GERMAN_MALE_VOICE;
                            break;
                        case "en-m":
                            bot.voice = BotManager.ENGLISH_MALE_VOICE;
                            break;
                        default:
                            bot.whisper(sender, "Ungültige Stimme. Wahl aus: de-w, de-m, en-m.");
                    }
                } else {
                    bot.whisper(sender, "Bitte gib eine Stimme ein. Wahl aus: de-w, de-m, en-m");
                }
            }
        },

        DESTROY("(?i)destroy") {
            @Override
            public void handle(@NotNull final User sender, @NotNull final Bot bot, @NotNull final String message) {
                BotManager.getInstance().destroyBot(bot.getUsername(), sender);
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
}
