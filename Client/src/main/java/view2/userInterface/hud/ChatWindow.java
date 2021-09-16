package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import view2.Localization;
import view2.Localization.Translatable;
import view2.userInterface.ChatiTextArea;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTooltip;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Eine Klasse, welche das Chatfenster der Anwendung repräsentiert.
 */
public class ChatWindow extends Window implements Translatable {

    private static final long SHOW_TYPING_DURATION = 1000; // in Millisekunden

    private static final float DEFAULT_WIDTH = 650;
    private static final float DEFAULT_HEIGHT = 400;
    private static final float MINIMUM_WIDTH = 400;
    private static final float MINIMUM_HEIGHT = 200;
    private static final float SPACE = 5;
    private static final float SEND_BUTTON_WIDTH = 120;
    private static final float SEND_BUTTON_HEIGHT = 60;

    private final Map<IUserView, Long> typingUsers;
    private long lastTimeTypingSent;

    private final Table messageLabelContainer;
    private final ScrollPane historyScrollPane;
    private final ChatiTextArea typeMessageArea;
    private final ChatiTextButton sendButton;
    private final Label typingUsersLabel;

    /**
     * Erzeugt eine neue Instanz des ChatWindow.
     */
    public ChatWindow() {
        super(Localization.translate("window.title.chat"), Chati.CHATI.getSkin());
        this.typingUsers = new HashMap<>();

        messageLabelContainer = new Table();
        historyScrollPane = new ScrollPane(messageLabelContainer, Chati.CHATI.getSkin());
        historyScrollPane.setOverscroll(false, false);

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
                if (c != 0 && now - lastTimeTypingSent >= SHOW_TYPING_DURATION / 2) {
                    lastTimeTypingSent = now;
                    Chati.CHATI.send(ServerSender.SendAction.TYPING);
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
        add(historyScrollPane).top().minWidth(MINIMUM_WIDTH).minHeight(MINIMUM_HEIGHT).grow().row();
        typingUsersLabel.setAlignment(Align.left, Align.left);
        add(typingUsersLabel).left().padLeft(SPACE).padTop(-1.5f * SPACE).row();
        Table sendContainer = new Table();
        sendContainer.defaults().height(SEND_BUTTON_HEIGHT).padLeft(SPACE).padRight(SPACE);
        sendContainer.add(typeMessageArea).growX();
        sendContainer.add(sendButton).width(SEND_BUTTON_WIDTH);
        add(sendContainer).growX();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void act(final float delta) {
        long now = System.currentTimeMillis();
        typingUsers.values().removeIf(lastTypingTime -> now - lastTypingTime >= SHOW_TYPING_DURATION);
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
            default:
                throw new IllegalArgumentException("No valid message type.");
        }
        showMessage(timestamp, Localization.format("pattern.chat.message", username, userMessage), messageColor);
    }

    /**
     * Zeigt eine Info-Nachricht an.
     * @param timestamp Zeitstempel der Nachricht.
     * @param messageBundle Übersetzbare Nachricht zusammen mit ihren benötigten Argumenten.
     */
    public void showInfoMessage(@NotNull final LocalDateTime timestamp, @NotNull final MessageBundle messageBundle) {
        String message = Localization.format(messageBundle.getMessageKey(), messageBundle.getArguments());
        showMessage(timestamp, Localization.format("pattern.chat.info", message), Color.RED);
    }

    /**
     * Leert den Nachrichtenverlauf und das Nachrichtenfeld.
     */
    public void clearChat() {
        typeMessageArea.reset();
        messageLabelContainer.clearChildren();
    }

    /**
     * Zeige das Chatfenster an und fokussiere es.
     */
    public void show() {
        setVisible(true);
        historyScrollPane.layout();
        historyScrollPane.scrollTo(0, 0, 0, 0);
        Chati.CHATI.getScreen().getStage().setKeyboardFocus(typeMessageArea);
        Chati.CHATI.getScreen().getStage().setScrollFocus(typeMessageArea);
    }

    /**
     * Zeige das Chatfenster nicht an und entferne den Fokus.
     */
    public void hide() {
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
    }

    /**
     * Zeigt eine Nachricht an.
     * @param timestamp Zeitstempel der anzuzeigenden Nachricht.
     * @param message Anzuzeigende Nachricht.
     * @param messageColor Farbe, in der die Nachricht angezeigt werden soll.
     */
    private void showMessage(@NotNull final LocalDateTime timestamp, @NotNull final String message,
                             @NotNull final Color messageColor) {
        String showMessage = Localization.format("pattern.chat.time", Timestamp.valueOf(timestamp), message);
        Label showLabel = new Label(showMessage, Chati.CHATI.getSkin());
        showLabel.setColor(messageColor);
        showLabel.setWrap(true);

        boolean isAtBottomBefore = historyScrollPane.isBottomEdge();
        messageLabelContainer.add(showLabel).top().left().padLeft(SPACE).padBottom(SPACE).growX().row();

        // Scrolle beim Erhalten einer neuen Nachricht nur bis nach unten, wenn die Scrollleiste bereits ganz unten war.
        // Ansonsten wird ein Durchscrollen des Verlaufs durch das Erhalten neuer Nachrichten gestört.
        if (isAtBottomBefore) {
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
                typingUsersLabel.setText(Localization.format("window.chat.typing-single", typingUsers.keySet().iterator().next().getUsername()));
            } catch (NoSuchElementException e) {
                // Sollte niemals eintreffen.
                e.printStackTrace();
            }
        } else {
            typingUsersLabel.setText(Localization.format("window.chat.typing-multiple", typingUsers.size()));
        }
    }

    @Override
    public void translate() {
        getTitleLabel().setText(Localization.translate("window.title.chat"));
        typeMessageArea.translate();
        sendButton.translate();
    }
}
