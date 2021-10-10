package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.ChatiEmojiManager;
import view2.ChatiLocalization.Translatable;
import view2.KeyCommand;
import view2.userInterface.*;
import view2.world.component.InternUserAvatar;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Eine Klasse, welche das Chatfenster der Anwendung repräsentiert.
 */
public class ChatWindow extends ResizableWindow {

    private static final int MAX_CHAT_MESSAGES = 256;
    private static final long SHOW_TYPING_DURATION = 1; // in Sekunden

    private static final float DEFAULT_WIDTH = 700;
    private static final float DEFAULT_HEIGHT = 400;
    private static final float MINIMUM_WIDTH = 450;
    private static final float MINIMUM_HEIGHT = 200;
    private static final float SPACE = 5;
    private static final float SEND_BUTTON_WIDTH = 120;
    private static final float SEND_BUTTON_HEIGHT = 60;
    private static final float MAX_IMAGE_SIZE = 180;
    private static final float IMAGE_SCALE_FACTOR = 0.025f;

    private final List<ChatMessage> chatMessages;
    private final Map<IUserView, Long> typingUsers;
    private final Stack chatStack;
    private final Table messageContainer;
    private final ScrollPane historyScrollPane;
    private final ScrollPane emojiScrollPane;
    private final Table emojiScrollPaneContainer;
    private final ChatiTextArea typeMessageArea;
    private final ChatiTextButton sendButton;
    private final Label typingUsersLabel;
    private final Table attachedImageLabelContainer;

    private long lastTimeTypingSent;
    private FileHandle attachedImage;
    private boolean isAtBottom;

    /**
     * Erzeugt eine neue Instanz des ChatWindow.
     */
    public ChatWindow() {
        super("window.title.chat");
        chatMessages = new LinkedList<>();
        typingUsers = new HashMap<>();

        messageContainer = new Table();
        historyScrollPane = new ScrollPane(messageContainer, Chati.CHATI.getSkin());
        historyScrollPane.setOverscroll(false, false);
        historyScrollPane.setScrollingDisabled(true, false);

        Table emojiContainer = new Table();
        Color weblinkColor = Color.ROYAL;
        Label creditsLabel = new Label("Emojis by [#" + weblinkColor + "]https://openmoji.org", Chati.CHATI.getSkin());
        creditsLabel.addListener(new WeblinkClickListener(creditsLabel, weblinkColor, 0));
        creditsLabel.setFontScale(0.67f);
        emojiContainer.add(creditsLabel).left().padLeft(SPACE).row();
        HorizontalGroup emojiGroup = new HorizontalGroup().wrap().rowAlign(Align.left).padLeft(SPACE);
        Chati.CHATI.getEmojiManager().getEmojis().values().forEach(emoji -> {
            ChatiImageButton emojiButton = new ChatiImageButton(new TextureRegionDrawable(emoji.getRegion()));
            emojiButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    typeMessageArea.appendText(emoji.toString());
                    InputEvent typingEvent = new InputEvent();
                    typingEvent.setType(InputEvent.Type.keyTyped);
                    typeMessageArea.fire(typingEvent);
                }
            });
            emojiGroup.addActor(emojiButton);
        });
        emojiContainer.add(emojiGroup).grow();
        emojiScrollPane = new ScrollPane(emojiContainer, Chati.CHATI.getSkin());
        emojiScrollPane.setFadeScrollBars(false);
        emojiScrollPane.setOverscroll(false, false);
        emojiScrollPane.setScrollingDisabled(true, false);

        ChatiImageButton emojiButton = new ChatiImageButton(new TextureRegionDrawable(Chati.CHATI.getRegions("emoji").get(1)));
        emojiButton.getImage().setScale(ChatiEmojiManager.EMOJI_SCALE);
        emojiButton.addListener(new ChatiTooltip("hud.tooltip.emoji"));
        emojiButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (emojiScrollPaneContainer.hasParent()) {
                    closeEmojiMenu();
                } else {
                    openEmojiMenu();
                }
            }
        });

        ChatiImageButton attachmentButton = new ChatiImageButton(Chati.CHATI.getDrawable("attachment"));
        attachmentButton.addListener(new ChatiTooltip("hud.tooltip.attachment"));
        attachmentButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ImageFileChooserWindow().open();
            }
        });

        typingUsersLabel = new Label("", Chati.CHATI.getSkin());
        typingUsersLabel.setFontScale(0.67f);

        attachedImageLabelContainer = new Table();

        typeMessageArea = new ChatiTextArea("menu.text-field.message", false);
        typeMessageArea.addListener(new InputListener() {
            @Override
            public boolean keyDown(@NotNull final InputEvent event, final int keycode) {
                return KeyCommand.SEND_CHAT_MESSAGE.matches(keycode);
            }
            @Override
            public boolean keyUp(@NotNull final InputEvent event, final int keycode) {
                if (KeyCommand.SEND_CHAT_MESSAGE.matches(keycode)) {
                    sendMessage();
                    return true;
                }
                return false;
            }
            @Override
            public boolean keyTyped(@NotNull final InputEvent event, final char c) {
                long now = System.currentTimeMillis();
                if (c != 0 && now - lastTimeTypingSent >= 1000 * SHOW_TYPING_DURATION / 2) {
                    lastTimeTypingSent = now;
                    Chati.CHATI.send(ServerSender.SendAction.TYPING);
                }
                setSendButtonState();
                return true;
            }
        });

        sendButton = new ChatiTextButton("menu.button.send", true);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                sendMessage();
            }
        });

        TextButton closeButton = new TextButton("X", Chati.CHATI.getSkin());
        closeButton.setDisabled(true);
        closeButton.addListener(new ChatiTooltip("hud.tooltip.close"));
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getHeadUpDisplay().hideChatWindow();
            }
        });

        // Layout
        setVisible(false);
        setPosition(Gdx.graphics.getWidth(), 0);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);

        messageContainer.top();
        messageContainer.defaults().top().left().padLeft(SPACE).padBottom(SPACE).growX();
        emojiScrollPaneContainer = new Table();
        emojiScrollPaneContainer.add(emojiScrollPane).height(MINIMUM_HEIGHT - 4 * SPACE).bottom().pad(2 * SPACE).growX();
        emojiScrollPaneContainer.bottom();
        chatStack = new Stack();
        chatStack.add(historyScrollPane);
        add(chatStack).top().minWidth(MINIMUM_WIDTH).minHeight(MINIMUM_HEIGHT).grow().row();
        Table labelContainer = new Table();
        typingUsersLabel.setAlignment(Align.left, Align.left);
        labelContainer.add(typingUsersLabel).left().padLeft(SPACE).growX();
        labelContainer.add(attachedImageLabelContainer).right().padRight(SPACE).growX();
        add(labelContainer).padTop(-1.5f * SPACE).growX().row();
        Table sendContainer = new Table();
        sendContainer.defaults().height(SEND_BUTTON_HEIGHT).padLeft(SPACE / 2).padRight(SPACE / 2);
        Table buttonContainer = new Table();
        buttonContainer.defaults().width(SEND_BUTTON_HEIGHT / 2).height(SEND_BUTTON_HEIGHT / 2);
        emojiButton.getImage().setOrigin(SEND_BUTTON_HEIGHT / 4, SEND_BUTTON_HEIGHT / 4);
        buttonContainer.add(emojiButton).row();
        attachmentButton.getImage().setOrigin(SEND_BUTTON_HEIGHT / 4, SEND_BUTTON_HEIGHT / 4);
        buttonContainer.add(attachmentButton);
        sendContainer.add(buttonContainer).width(SEND_BUTTON_HEIGHT / 2).padLeft(SPACE);
        sendContainer.add(typeMessageArea).growX();
        sendContainer.add(sendButton).width(SEND_BUTTON_WIDTH).padRight(SPACE);
        add(sendContainer).growX();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void act(final float delta) {
        long now = System.currentTimeMillis();
        typingUsers.values().removeIf(lastTypingTime -> now - lastTypingTime >= 1000 * SHOW_TYPING_DURATION);
        showTypingUsers();

        if (dragging && isAtBottom) {
            historyScrollPane.scrollTo(0, 0, 0, 0);
            layout();
        } else {
            isAtBottom = historyScrollPane.isBottomEdge();
        }
        super.act(delta);
    }

    /**
     * Fügt einen Benutzer zu der Menge an Benutzern hinzu, die gerade als tippend angezeigt werden sollen.
     * @param userId ID des hinzuzufügenden Benutzers.
     */
    public void addTypingUser(@NotNull final UUID userId) {
        try {
            IUserView user = Chati.CHATI.getUserManager().getExternUserView(userId);
            typingUsers.put(user, System.currentTimeMillis());
            showTypingUsers();
        } catch (UserNotFoundException e) {
            Chati.LOGGER.log(Level.WARNING, "Received typing information from an unknown user", e);
        }
    }

    /**
     * Fügt einen Bildanhang hinzu, der mit der nächsten Nachricht gesendet wird.
     * @param image Angehangenes Bild.
     */
    public void attachImage(@NotNull final FileHandle image) {
        Label attachedImageLabel = new Label(image.name(), Chati.CHATI.getSkin());
        attachedImageLabel.setAlignment(Align.right, Align.right);
        attachedImageLabel.setFontScale(0.67f);
        TextButton removeAttachedImageButton = new TextButton("X", Chati.CHATI.getSkin());
        removeAttachedImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                removeAttachedImage();
            }
        });
        removeAttachedImageButton.getLabel().setFontScale(0.67f);
        attachedImageLabelContainer.clear();
        attachedImageLabelContainer.add(attachedImageLabel).right().padRight(SPACE).growX();
        attachedImageLabelContainer.add(removeAttachedImageButton).size(4 * SPACE).right();
        this.attachedImage = image;
        setSendButtonState();
    }

    /**
     * Zeigt eine von einem anderen Benutzer erhalten Nachricht an.
     * @param senderId ID des Senders.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageType Typ der Nachricht.
     * @param userMessage Anzuzeigende Nachricht.
     * @param imageData Daten des Bildanhangs.
     * @param imageName Name des Bildanhangs.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void showUserMessage(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp,
                                @NotNull final MessageType messageType, @NotNull final String userMessage,
                                final byte[] imageData, @Nullable final String imageName) throws UserNotFoundException {
        String username;
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.getUserId().equals(senderId)) {
            username = internUser.getUsername();
        } else {
            username = Chati.CHATI.getUserManager().getExternUserView(senderId).getUsername();
        }
        Color messageColor;
        switch (messageType) {
            case STANDARD:
                messageColor = Color.WHITE;
                break;
            case WHISPER:
                messageColor = Color.GRAY;
                break;
            case AREA:
                messageColor = Color.LIME;
                break;
            case ROOM:
                messageColor = Color.CORAL;
                break;
            case WORLD:
                messageColor = Color.SKY;
                break;
            case GLOBAL:
                messageColor = Color.GOLD;
                break;
            case INFO:
                Chati.LOGGER.log(Level.WARNING, "Received info message from a user");
                return;
            default:
                Chati.LOGGER.log(Level.WARNING, "Received message with no valid type");
                return;
        }
        ChatMessage chatMessage = new ChatMessage(timestamp, Chati.CHATI.getLocalization().format("pattern.chat.message",
                username, userMessage), messageColor);
        if (imageData.length != 0 && imageName != null) {
            chatMessage.setImage(imageName, imageData);
        }
        showChatMessage(chatMessage);
    }

    /**
     * Zeigt eine Info-Nachricht an.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageBundle Übersetzbare Nachricht zusammen mit ihren benötigten Argumenten.
     */
    public void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle) {
        String message = Chati.CHATI.getLocalization().format(messageBundle.getMessageKey(), messageBundle.getArguments());
        ChatMessage chatMessage = new ChatMessage(timestamp, Chati.CHATI.getLocalization().format("pattern.chat.info",
                message), Color.RED);
        showChatMessage(chatMessage);
    }

    /**
     * Zeige das Chatfenster an und fokussiere es.
     */
    public void show() {
        setVisible(true);
        historyScrollPane.layout();
        historyScrollPane.scrollTo(0, 0, 0, 0);

        InternUserAvatar internUserAvatar = Chati.CHATI.getWorldScreen().getInternUserAvatar();
        if (getStage() != null && internUserAvatar != null && !internUserAvatar.isMoving()) {
            getStage().setKeyboardFocus(typeMessageArea);
            getStage().setScrollFocus(historyScrollPane);
        }
    }

    /**
     * Zeige das Chatfenster nicht an und entferne den Fokus.
     */
    public void hide() {
        closeEmojiMenu();
        setVisible(false);
        if (getStage() != null) {
            getStage().unfocus(this);
        }
    }

    /**
     * Leert den Nachrichtenverlauf und das Nachrichtenfeld.
     */
    public void clearChat() {
        chatMessages.clear();
        typingUsers.clear();
        typeMessageArea.reset();
        messageContainer.clearChildren();
        removeAttachedImage();
        setSendButtonState();
    }

    /**
     * Setzt den Text im Textfeld.
     * @param text Anzuzeigender Text.
     */
    public void setText(String text) {
        typeMessageArea.setText("");
        typeMessageArea.setText(text);
        if (text.length() > 0 && Character.isWhitespace(text.charAt(text.length() - 1))) {
            typeMessageArea.appendText(" ");
        }
        typeMessageArea.setCursorPosition(typeMessageArea.getText().length());
        if (getStage() != null) {
            getStage().setKeyboardFocus(typeMessageArea);
        }
    }

    /**
     * Sendet eine eingegebene Nachricht.
     */
    private void sendMessage() {
        if (typeMessageArea.isBlank() && attachedImage == null) {
            return;
        }
        if (attachedImage != null) {
            Chati.CHATI.send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText().trim(),
                    attachedImage.name(), attachedImage.readBytes());
            removeAttachedImage();
        } else {
            Chati.CHATI.send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText().trim());
        }
        typeMessageArea.setText("");
        typeMessageArea.translate();
        closeEmojiMenu();
        setSendButtonState();
    }

    /**
     * Zeigt eine erhaltene Chatnachricht an.
     * @param chatMessage Anzuzeigende Chatnachricht.
     */
    private void showChatMessage(@NotNull final ChatMessage chatMessage) {
        if (chatMessages.size() >= MAX_CHAT_MESSAGES) {
            chatMessages.remove(0);
            messageContainer.removeActorAt(0, false);
        }
        chatMessages.add(chatMessage);
        messageContainer.add(chatMessage).row();
        // Scrolle beim Erhalten einer neuen Nachricht nur bis nach unten, wenn die Scrollleiste bereits ganz unten war.
        // Ansonsten wird ein Durchscrollen des Verlaufs durch das Erhalten neuer Nachrichten gestört.
        if (isAtBottom) {
            historyScrollPane.layout();
            historyScrollPane.scrollTo(0, 0, 0, 0);
        }
    }

    /**
     * Zeige die momentan tippenden Benutzer an.
     */
    private void showTypingUsers() {
        if (typingUsers.isEmpty()) {
            typingUsersLabel.setText("");
        } else if (typingUsers.size() == 1) {
            try {
                typingUsersLabel.setText(Chati.CHATI.getLocalization().format("window.chat.typing-single",
                        typingUsers.keySet().iterator().next().getUsername()));
            } catch (NoSuchElementException e) {
                // Sollte niemals eintreffen.
                e.printStackTrace();
            }
        } else {
            typingUsersLabel.setText(Chati.CHATI.getLocalization().format("window.chat.typing-multiple", typingUsers.size()));
        }
    }

    /**
     * Entfernt den Anhang.
     */
    private void removeAttachedImage() {
        attachedImageLabelContainer.clear();
        if (attachedImage == null) {
            return;
        }
        attachedImage = null;
        setSendButtonState();
    }

    /**
     * Legt fest, ob die Sendetaste als aktiviert oder deaktiviert angezeigt werden soll.
     */
    private void setSendButtonState() {
        if (typeMessageArea.isBlank() && attachedImage == null && sendButton.isTouchable()) {
            sendButton.getLabel().setColor(Color.GRAY);
            sendButton.setTouchable(Touchable.disabled);
        } else if ((!typeMessageArea.isBlank() || attachedImage != null) && !sendButton.isTouchable()) {
            sendButton.getLabel().setColor(Color.WHITE);
            sendButton.setTouchable(Touchable.enabled);
        }
    }

    /**
     * Öffnet das Menü zur Auswahl eines Emojis.
     */
    private void openEmojiMenu() {
        chatStack.add(emojiScrollPaneContainer);
        if (emojiScrollPane.getStage() != null) {
            emojiScrollPane.getStage().setScrollFocus(emojiScrollPane);
        }
        emojiScrollPane.setScrollPercentY(0);
    }

    /**
     * Schließt das Menü zur Auswahl eines Emojis.
     */
    private void closeEmojiMenu() {
        emojiScrollPaneContainer.remove();
        if (historyScrollPane.getStage() != null) {
            historyScrollPane.getStage().setScrollFocus(historyScrollPane);
        }
    }

    @Override
    public void translate() {
        super.translate();
        chatMessages.forEach(ChatMessage::translate);
        typeMessageArea.translate();
        sendButton.translate();
    }

    /**
     * Eine Klasse, welche erhaltene Chatnachrichten repräsentiert.
     */
    private static class ChatMessage extends Table implements Translatable {

        private static final String URL_REGEX = "(((http|https)://)?(www.)|((http|https)://)((www.)?)|((http|https)://www.))" +
                "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        private static final Color WEB_LINK_COLOR = Color.ROYAL;

        private final LocalDateTime timestamp;
        private final String message;
        private final Color messageColor;
        private final Label messageLabel;

        /**
         * Erzeugt eine neue Instanz einer ChatMessage.
         * @param timestamp Zeitstempel der Nachricht.
         * @param message Anzuzeigende Textnachricht.
         * @param messageColor Farbe, in die der Text angezeigt werden soll.
         */
        public ChatMessage(@NotNull final LocalDateTime timestamp, @NotNull final String message,
                           @NotNull final Color messageColor) {
            this.timestamp = timestamp;
            this.message = message;
            this.messageColor = messageColor;

            defaults().left().growX();

            String datedMessage = Chati.CHATI.getLocalization().format("pattern.chat.time",
                    Timestamp.valueOf(timestamp), message);
            String markedUpMessage = getMarkedUpMessage(datedMessage, messageColor);
            messageLabel = new Label(markedUpMessage, Chati.CHATI.getSkin());
            messageLabel.setWrap(true);
            messageLabel.addListener(new WeblinkClickListener(messageLabel, WEB_LINK_COLOR, 0));

            add(messageLabel).row();
        }

        /**
         * Fügt der Textnachricht ein anzuzeigendes Bild hinzu.
         * @param imageName Name des anzuzeigenden Bildes.
         * @param imageData Daten des anzuzeigenden Bildes.
         */
        public void setImage(@NotNull final String imageName, final byte[] imageData) {
            Pixmap image;
            try {
                image = new Pixmap(imageData, 0, imageData.length);
            } catch (GdxRuntimeException e) {
                Chati.LOGGER.log(Level.WARNING, "Could not show received image", e);
                return;
            }

            Texture imageTexture = new Texture(image);
            Drawable imageDrawable = new TextureRegionDrawable(imageTexture);
            image.dispose();

            Label imageNameLabel = new Label(imageName, Chati.CHATI.getSkin());
            imageNameLabel.setAlignment(Align.center, Align.center);
            imageNameLabel.setFontScale(0.67f);
            imageNameLabel.setWrap(true);

            ChatiImageButton imageButton = new ChatiImageButton(imageDrawable, imageDrawable, imageDrawable,
                    IMAGE_SCALE_FACTOR);
            imageButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    new ImageWindow(imageName, imageData).open();
                }
            });

            Table imageContainer = new Table();
            imageContainer.left();
            float ratio = 1;
            float largerSide = Math.max(image.getWidth(), image.getHeight());
            if (largerSide > MAX_IMAGE_SIZE) {
                ratio = MAX_IMAGE_SIZE / largerSide;
            }
            float imageWidth = ratio * image.getWidth();
            float imageHeight = ratio * image.getHeight();
            imageButton.getImage().setOrigin(imageWidth / 2f, imageHeight / 2f);
            imageContainer.add(imageButton).width(imageWidth).height(imageHeight).row();
            imageContainer.add(imageNameLabel).width(MAX_IMAGE_SIZE);

            add(imageContainer);
        }

        @Override
        public void translate() {
            String datedMessage = Chati.CHATI.getLocalization().format("pattern.chat.time", Timestamp.valueOf(timestamp), message);
            String colorizedMessage = getMarkedUpMessage(datedMessage, messageColor);
            messageLabel.setText(colorizedMessage);
        }

        /**
         * Markiere die Nachricht mithilfe der Color-Markup-Language. Glyphen von Emojis bleiben hierbei weiß, um
         * korrekt dargestellt zu werden. Weblinks werden unabhängig von der verwendeten Farbe in die dafür vorgesehene
         * Farbe eingefärbt.
         * @param message Nachricht, die eingefärbt werden soll.
         * @param messageColor Farbe, in die die Nachricht eingefärbt werden soll.
         * @return Eingefärbte Nachricht.
         */
        private @NotNull String getMarkedUpMessage(@NotNull final String message, @NotNull final Color messageColor) {
            // Anmerkung: Die Verwendung der Color-Markup-Language führt dazu, dass die Breite des Textes nichtmehr
            // exakt berechnet wird und die Labels ab einer bestimmten Anzahl von Markierungen in einer Zeile nichtmehr
            // korrekt wrappen. Dies ist ein Fehler von LibGDX. Um das möglichst zu umgehen, müssen die verwendeten
            // Markierungen zum Umfärben minimiert werden.

            Matcher urlMatcher = Pattern.compile(URL_REGEX).matcher(message);
            boolean hasWeblink = urlMatcher.find();

            // Ist die Schriftfarbe weiß und enthält die Nachricht keinen Link, muss nicht umgefärbt werden.
            if (messageColor == Color.WHITE && !hasWeblink) {
                return message;
            }

            StringBuilder stringBuilder = new StringBuilder();
            Color currentColor = Color.WHITE;
            for (int i = 0; i < message.length(); i++) {
                Color previousColor = currentColor;
                char c = message.charAt(i);
                if (hasWeblink && i > urlMatcher.end()) {
                    hasWeblink = urlMatcher.find();
                }

                if (Chati.CHATI.getEmojiManager().isEmoji(c)) {
                    // Emojis müssen weiß eingefärbt sein um korrekt dargestellt zu werden.
                    currentColor = Color.WHITE;
                } else if (hasWeblink && i >= urlMatcher.start() && i < urlMatcher.end()) {
                    // Beginn eines Weblinks. Färbe auf die Farbe für Weblinks um.
                    currentColor = WEB_LINK_COLOR;
                } else if (hasWeblink && i == urlMatcher.end()) {
                    // Ende eines Weblinks. Damit zwei Weblinks, die durch Whitespace-Character getrennt werden,
                    // voneinander unterschieden werden können, muss am Ende eines Weblinks mindestens einmal umgefärbt
                    // werden. Vermeide im Fall von einem folgenden normalen Text oder Emoji ein zweites Umfärben, indem
                    // direkt auf die entsprechende Farbe des nächsten Nicht-Whitespace-Characters umgefärbt wird.
                    for (int j = i + 1; j < message.length(); j++) {
                        if (!Character.isWhitespace(message.charAt(j))) {
                            if (Chati.CHATI.getEmojiManager().isEmoji(message.charAt(j))) {
                                currentColor = Color.WHITE;
                            } else {
                                currentColor = messageColor;
                            }
                            break;
                        }
                    }
                } else if (!Character.isWhitespace(c)) {
                    // Färbe auf die Nachrichtenfarbe um. Im Falle von Whitespace-Character muss nicht umgefärbt werden.
                    currentColor = messageColor;
                }
                if (currentColor != previousColor) {
                    stringBuilder.append("[#").append(currentColor).append("]");
                }
                if (c == '[') {
                    // Escape den Anfang eines Color-Markups, sonst können Benutzer theoretisch durch die Eingabe der
                    // Codes in beliebigen Farben schreiben.
                    stringBuilder.append("[[");
                } else {
                    stringBuilder.append(c);
                }
            }
            return stringBuilder.toString();
        }
    }
}
