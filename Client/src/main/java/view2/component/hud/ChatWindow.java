package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import view2.Chati;
import view2.component.ChatiTextArea;
import view2.component.ChatiTextButton;
import view2.component.KeyAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatWindow extends Window {

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
    private final Label typingUsersLabel;

    public ChatWindow() {
        super("Chat", Chati.CHATI.getSkin());
        this.typingUsers = new HashMap<>();

        messageLabelContainer = new Table();
        historyScrollPane = new ScrollPane(messageLabelContainer, Chati.CHATI.getSkin());
        historyScrollPane.setOverscroll(false, false);

        typeMessageArea = new ChatiTextArea("Nachricht", false);
        typeMessageArea.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return KeyAction.SEND_CHAT_MESSAGE.matches(keycode);
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (KeyAction.SEND_CHAT_MESSAGE.matches(keycode)) {
                    sendMessage();
                    return true;
                }
                return false;
            }
            @Override
            public boolean keyTyped(InputEvent event, char c) {
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

        ChatiTextButton sendButton = new ChatiTextButton("Senden", true);
        sendButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                sendMessage();
            }
        });

        ChatiTextButton minimizeButton = new ChatiTextButton("X", true);
        minimizeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                HeadUpDisplay.getInstance().hideChatWindow();
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

        getTitleTable().add(minimizeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void act(float delta) {
        long now = System.currentTimeMillis();
        typingUsers.values().removeIf(lastTypingTime -> now - lastTypingTime >= SHOW_TYPING_DURATION);
        showTypingUsers();
        super.act(delta);
    }

    public void updateTypingUser(UUID userId) {
        IUserView user;
        try {
            user = Chati.CHATI.getUserManager().getExternUserView(userId);
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("Received typing information from an unknown user", e);
        }
        typingUsers.put(user, System.currentTimeMillis());
        showTypingUsers();
    }

    public void showUserMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String userMessage)
            throws UserNotFoundException {
        String username;
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.getUserId().equals(userId)) {
            username = internUser.getUsername();
        } else {
            username = Chati.CHATI.getUserManager().getExternUserView(userId).getUsername();
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
                throw new IllegalArgumentException("No valid messagetype.");
        }
        String message = username + ": " + userMessage;
        showMessage(timestamp, message, messageColor);
    }

    public void showInfoMessage(LocalDateTime timestamp, MessageBundle messageBundle) {
        // TODO get Message from messageBundle with Arguments.
        String message = "Info: " + messageBundle.getMessageKey();  // vorläufig
        showMessage(timestamp, message, Color.RED);
    }

    public void clearChat() {
        typeMessageArea.reset();
        messageLabelContainer.clearChildren();
    }

    private void sendMessage() {
        if (typeMessageArea.getText().isBlank()) {
            return;
        }
        Chati.CHATI.send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText().trim());
        typeMessageArea.setText("");
    }

    private void showMessage(LocalDateTime timestamp, String message, Color messageColor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = timestamp.format(formatter);
        String showMessage = "[" + timeString + "] " + message;
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

    private void showTypingUsers() {
        if (typingUsers.isEmpty()) {
            typingUsersLabel.setText("");
        } else if (typingUsers.size() == 1) {
            try {
                typingUsersLabel.setText(typingUsers.keySet().iterator().next().getUsername() + " tippt gerade...");
            } catch (NoSuchElementException e) {
                // Sollte niemals eintreffen.
                e.printStackTrace();
            }
        } else {
            typingUsersLabel.setText(typingUsers.size() + " Benutzer tippen gerade...");
        }
    }

    public void show() {
        setVisible(true);
        historyScrollPane.layout();
        historyScrollPane.scrollTo(0, 0, 0, 0);
        Chati.CHATI.getScreen().getStage().setKeyboardFocus(typeMessageArea);
        Chati.CHATI.getScreen().getStage().setScrollFocus(typeMessageArea);
    }

    public void hide() {
        setVisible(false);
        if (getStage() != null) {
            getStage().unfocus(this);
        }
    }
}
