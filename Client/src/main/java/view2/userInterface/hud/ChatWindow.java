package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.KeyCommand;
import view2.ChatiLocalization.Translatable;
import view2.userInterface.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Eine Klasse, welche das Chatfenster der Anwendung repräsentiert.
 */
public class ChatWindow extends Window implements Translatable {

    private static final long SHOW_TYPING_DURATION = 1; // in Sekunden

    private static final float DEFAULT_WIDTH = 700;
    private static final float DEFAULT_HEIGHT = 400;
    private static final float MINIMUM_WIDTH = 450;
    private static final float MINIMUM_HEIGHT = 200;
    private static final float SPACE = 5;
    private static final float SEND_BUTTON_WIDTH = 120;
    private static final float SEND_BUTTON_HEIGHT = 60;

    private final Map<IUserView, Long> typingUsers;
    private long lastTimeTypingSent;

    private final Stack chatStack;
    private final Table messageLabelContainer;
    private final ScrollPane historyScrollPane;
    private final ScrollPane emojiScrollPane;
    private final Table emojiScrollPaneContainer;
    private final ChatiTextArea typeMessageArea;
    private final ChatiTextButton sendButton;
    private final Label typingUsersLabel;

    /**
     * Erzeugt eine neue Instanz des ChatWindow.
     */
    public ChatWindow() {
        super(Chati.CHATI.getLocalization().translate("window.title.chat"), Chati.CHATI.getSkin());
        this.typingUsers = new HashMap<>();

        messageLabelContainer = new Table();
        historyScrollPane = new ScrollPane(messageLabelContainer, Chati.CHATI.getSkin());
        historyScrollPane.setOverscroll(false, false);
        historyScrollPane.setScrollingDisabled(true, false);

        HorizontalGroup emojiContainer = new HorizontalGroup().wrap().rowAlign(Align.left).padLeft(SPACE);
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
            emojiContainer.addActor(emojiButton);
        });
        emojiScrollPane = new ScrollPane(emojiContainer, Chati.CHATI.getSkin());
        emojiScrollPane.setFadeScrollBars(false);
        emojiScrollPane.setOverscroll(false, false);
        emojiScrollPane.setScrollingDisabled(true, false);

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
                if (typeMessageArea.isBlank() && sendButton.isTouchable()) {
                    disableSendButton();
                } else if (!typeMessageArea.isBlank() && !sendButton.isTouchable()) {
                    enableSendButton();
                }
                return true;
            }
        });

        typingUsersLabel = new Label("", Chati.CHATI.getSkin());
        typingUsersLabel.setFontScale(0.67f);

        sendButton = new ChatiTextButton("menu.button.send", true);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                sendMessage();
            }
        });

        ChatiImageButton emojiButton = new ChatiImageButton(new TextureRegionDrawable(Chati.CHATI.getRegions("emoji").get(1)));
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

        TextButton placeHolderButton = new TextButton("", Chati.CHATI.getSkin());
        placeHolderButton.setDisabled(true);
        placeHolderButton.addListener(new ChatiTooltip("hud.tooltip.placeholder"));
        placeHolderButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                // TODO
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
        setModal(false);
        setMovable(true);
        setResizable(true);
        setResizeBorder((int) getPadBottom());
        setKeepWithinStage(true);
        setPosition(Gdx.graphics.getWidth(), 0);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);

        messageLabelContainer.top();
        emojiScrollPaneContainer = new Table();
        emojiScrollPaneContainer.add(emojiScrollPane).height(MINIMUM_HEIGHT - 4 * SPACE).bottom().pad(2 * SPACE).growX();
        emojiScrollPaneContainer.bottom();
        chatStack = new Stack();
        chatStack.add(historyScrollPane);
        add(chatStack).top().minWidth(MINIMUM_WIDTH).minHeight(MINIMUM_HEIGHT).grow().row();
        typingUsersLabel.setAlignment(Align.left, Align.left);
        add(typingUsersLabel).left().padLeft(SPACE).padTop(-1.5f * SPACE).row();
        Table sendContainer = new Table();
        sendContainer.defaults().height(SEND_BUTTON_HEIGHT).padLeft(SPACE / 2).padRight(SPACE / 2);
        Table buttonContainer = new Table();
        buttonContainer.defaults().width(SEND_BUTTON_HEIGHT / 2).height(SEND_BUTTON_HEIGHT / 2);
        emojiButton.getImage().setOrigin(SEND_BUTTON_HEIGHT / 4, SEND_BUTTON_HEIGHT / 4);
        buttonContainer.add(emojiButton).row();
        buttonContainer.add(placeHolderButton);
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
        super.act(delta);
    }

    /**
     * Fügt einen Benutzer zu der Menge an Benutzern hinzu, die gerade als tippend angezeigt werden sollen.
     * @param userId ID des hinzuzufügenden Benutzers.
     */
    public void addTypingUser(@NotNull final UUID userId) {
        IUserView user;
        try {
            user = Chati.CHATI.getUserManager().getExternUserView(userId);
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("Received typing information from an unknown user", e);
        }
        typingUsers.put(user, System.currentTimeMillis());
        showTypingUsers();
    }

    /**
     * Zeigt eine von einem anderen Benutzer erhalten Nachricht an.
     * @param senderId ID des Senders.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageType Typ der Nachricht.
     * @param userMessage Anzuzeigende Nachricht.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void showUserMessage(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp,
                                @NotNull final MessageType messageType, @NotNull final String userMessage) throws UserNotFoundException {
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
            case ROOM:
                messageColor = Color.ORANGE;
                break;
            case WORLD:
                messageColor = Color.SKY;
                break;
            case INFO:
                throw new IllegalArgumentException("Users cannot send info messages.");
            default:
                throw new IllegalArgumentException("No valid message type.");
        }
        showMessage(timestamp, Chati.CHATI.getLocalization().format("pattern.chat.message", username, userMessage), messageColor);
    }

    /**
     * Zeigt eine Info-Nachricht an.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageBundle Übersetzbare Nachricht zusammen mit ihren benötigten Argumenten.
     */
    public void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle) {
        String message = Chati.CHATI.getLocalization().format(messageBundle.getMessageKey(), messageBundle.getArguments());
        showMessage(timestamp, Chati.CHATI.getLocalization().format("pattern.chat.info", message), Color.RED);
    }

    /**
     * Leert den Nachrichtenverlauf und das Nachrichtenfeld.
     */
    public void clearChat() {
        typeMessageArea.reset();
        messageLabelContainer.clearChildren();
        disableSendButton();
    }

    /**
     * Zeige das Chatfenster an und fokussiere es.
     */
    public void show() {
        setVisible(true);
        historyScrollPane.layout();
        historyScrollPane.scrollTo(0, 0, 0, 0);
        Chati.CHATI.getScreen().getStage().setKeyboardFocus(typeMessageArea);
        Chati.CHATI.getScreen().getStage().setScrollFocus(historyScrollPane);
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
     * Sendet eine eingegebene Nachricht.
     */
    private void sendMessage() {
        if (typeMessageArea.isBlank()) {
            return;
        }
        Chati.CHATI.send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText().trim());
        typeMessageArea.setText("");
        typeMessageArea.translate();
        closeEmojiMenu();
        if (historyScrollPane.getStage() != null) {
            historyScrollPane.getStage().setScrollFocus(historyScrollPane);
        }
        disableSendButton();
    }

    /**
     * Zeigt eine Nachricht an.
     * @param timestamp Zeitstempel der anzuzeigenden Nachricht.
     * @param message Anzuzeigende Nachricht.
     * @param messageColor Farbe, in der die Nachricht angezeigt werden soll.
     */
    private void showMessage(@NotNull final LocalDateTime timestamp, @NotNull final String message,
                             @NotNull final Color messageColor) {
        String datedMessage = Chati.CHATI.getLocalization().format("pattern.chat.time", Timestamp.valueOf(timestamp), message);
        String colorizedMessage = getColorizedMessage(datedMessage, messageColor);
        Label messageLabel = new Label(colorizedMessage, Chati.CHATI.getSkin());
        messageLabel.setWrap(true);

        boolean isAtBottomBefore = historyScrollPane.isBottomEdge();
        messageLabelContainer.add(messageLabel).top().left().padLeft(SPACE).padBottom(SPACE).growX().row();

        // Scrolle beim Erhalten einer neuen Nachricht nur bis nach unten, wenn die Scrollleiste bereits ganz unten war.
        // Ansonsten wird ein Durchscrollen des Verlaufs durch das Erhalten neuer Nachrichten gestört.
        if (isAtBottomBefore) {
            historyScrollPane.layout();
            historyScrollPane.scrollTo(0, 0, 0, 0);
        }
    }

    /**
     * Färbe die Nachricht mithilfe der Color-Markup-Language ein. Glyphen von Emojis bleiben hierbei weiß, um korrekt
     * dargestellt zu werden.
     * @param message Nachricht, die eingefärbt werden soll.
     * @param color Farbe, in die die Nachricht eingefärbt werden soll.
     * @return Eingefärbte Nachricht.
     */
    private String getColorizedMessage(String message, Color color) {
        if (color == Color.WHITE) {
            return message;
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean white = true;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            boolean isEmoji = Chati.CHATI.getEmojiManager().isEmoji(c);
            if (isEmoji && !white) {
                stringBuilder.append("[WHITE]");
                white = true;
            } else if (!isEmoji && !Character.isWhitespace(c) && white) {
                stringBuilder.append("[#").append(color).append("]");
                white = false;
            }
            if (c == '[') {
                stringBuilder.append("[[");
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
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
     * Zeigt die Sendetaste als aktiviert an.
     */
    private void enableSendButton() {
        sendButton.getLabel().setColor(Color.WHITE);
        sendButton.setTouchable(Touchable.enabled);
    }

    /**
     * Zeigt die Sendetaste als deaktiviert an.
     */
    private void disableSendButton() {
        sendButton.getLabel().setColor(Color.GRAY);
        sendButton.setTouchable(Touchable.disabled);
    }

    private void openEmojiMenu() {
        chatStack.add(emojiScrollPaneContainer);
        if (emojiScrollPane.getStage() != null) {
            emojiScrollPane.getStage().setScrollFocus(emojiScrollPane);
        }
        emojiScrollPane.setScrollPercentY(0);
    }

    private void closeEmojiMenu() {
        emojiScrollPaneContainer.remove();
    }

    @Override
    public void translate() {
        getTitleLabel().setText(Chati.CHATI.getLocalization().translate("window.title.chat"));
        typeMessageArea.translate();
        sendButton.translate();
    }
}
