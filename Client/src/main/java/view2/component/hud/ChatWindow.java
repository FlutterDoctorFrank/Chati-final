package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IUserManagerView;
import model.user.UserManager;
import view2.Assets;
import view2.Chati;
import view2.component.AbstractWindow;
import view2.component.ChatiTextArea;
import view2.component.KeyAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class ChatWindow extends AbstractWindow {

    private static final float DEFAULT_WIDTH = 650;
    private static final float DEFAULT_HEIGHT = 400;
    private static final float MINIMUM_WIDTH = 300;
    private static final float MINIMUM_HEIGHT = 150;
    private static final float SPACE = 5;
    private static final float SEND_BUTTON_WIDTH = 120;
    private static final float SEND_BUTTON_HEIGHT = 60;

    private Table messageLabelContainer;
    private ScrollPane historyScrollPane;
    private ChatiTextArea typeMessageArea;
    private TextButton sendButton;
    private TextButton minimizeButton;

    public ChatWindow() {
        super("Chat");
        create();
        setLayout();
    }

    @Override
    protected void create() {
        messageLabelContainer = new Table();

        historyScrollPane = new ScrollPane(messageLabelContainer, Assets.SKIN);

        typeMessageArea = new ChatiTextArea("Nachricht", false);
        typeMessageArea.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return KeyAction.getAction(keycode) == KeyAction.SEND_CHAT_MESSAGE;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (KeyAction.getAction(keycode) == KeyAction.SEND_CHAT_MESSAGE) {
                    sendMessage();
                    return true;
                }
                return false;
            }
            //////////////////////////////////////////////////////////////
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if (UserManager.getInstance().getInternUser().getUsername().equals("Frank")) {
                    typeMessageArea.setText(typeMessageArea.getText().concat(String.valueOf((char) new Random().nextInt(25) + 'a')));
                }
                return true;
            }
            /////////////////////////////////////////////////////////////
        });

        sendButton = new TextButton("Senden", Assets.SKIN);
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

        minimizeButton = new TextButton("X", Assets.SKIN);
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
    }

    @Override
    protected void setLayout() {
        setVisible(false);
        setModal(false);
        setMovable(true);
        setResizable(true);
        setResizeBorder((int) getPadBottom());
        defaults().minWidth(MINIMUM_WIDTH).minHeight(MINIMUM_HEIGHT);
        setKeepWithinStage(true);
        setPosition(Gdx.graphics.getWidth(), 0);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);

        messageLabelContainer.top();
        add(historyScrollPane).top().grow().padBottom(SPACE).row();
        Table sendContainer = new Table();
        sendContainer.defaults().height(SEND_BUTTON_HEIGHT).padLeft(SPACE).padRight(SPACE);
        sendContainer.add(typeMessageArea).growX();
        sendContainer.add(sendButton).width(SEND_BUTTON_WIDTH);
        add(sendContainer).height(SEND_BUTTON_HEIGHT).growX();

        getTitleTable().add(minimizeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void open() {
        setVisible(true);
        // Das ist mit Absicht 2 mal hier, wenn man die Methode scrollTo nur 1 mal aufruft, scrollt er nicht bis ans
        // Ende falls die Nachricht aus mehreren Zeilen besteht! Es hängt warscheinlich mit der Art und Weise zusammen
        // wie in LibGDX Labels gehandhabt werden, bei denen setWrap auf true gesetzt ist.
        historyScrollPane.scrollTo(0, 0, 0, 0);
        historyScrollPane.scrollTo(0, 0, 0, 0);

        Chati.CHATI.getScreen().getStage().setKeyboardFocus(typeMessageArea);
        Chati.CHATI.getScreen().getStage().setScrollFocus(typeMessageArea);
    }

    @Override
    public void close() {
        setVisible(false);
        Chati.CHATI.getScreen().getStage().unfocus(this);
    }

    private void resetMessageArea() {
        typeMessageArea.reset();
    }

    private void sendMessage() {
        if (typeMessageArea.getStyle().fontColor == Color.GRAY || typeMessageArea.getText().isBlank()) {
            return;
        }
        //////////////////////////////////////////////////////////////////
        if (UserManager.getInstance().getInternUser().getUsername().equals("Frank")) {
            Chati.CHATI.getServerSender().send(ServerSender.SendAction.MESSAGE, "Ich bin schwul");
            typeMessageArea.setText("");
            return;
        }
        //////////////////////////////////////////////////////////////////

        Chati.CHATI.getServerSender().send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText().trim());
        typeMessageArea.setText("");
    }

    public void showUserMessage(UUID userId, LocalDateTime timestamp, MessageType messageType, String userMessage) {
        String username;
        IUserManagerView userManager = Chati.CHATI.getUserManager();
        if (userManager.getInternUserView() != null && userManager.getInternUserView().getUserId().equals(userId)) {
            username = userManager.getInternUserView().getUsername();
        } else {
            try {
                username = userManager.getExternUserView(userId).getUsername();
            } catch (UserNotFoundException e) {
                throw new IllegalArgumentException("Received message from an unknown user.", e);
            }
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
        resetMessageArea();
        messageLabelContainer.clearChildren();
    }

    private void showMessage(LocalDateTime timestamp, String message, Color messageColor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = timestamp.format(formatter);
        String showMessage = "[" + timeString + "] " + message;
        Label showLabel = new Label(showMessage, Assets.SKIN);
        showLabel.setColor(messageColor);
        showLabel.setWrap(true);
        messageLabelContainer.add(showLabel).top().left().padLeft(SPACE).padBottom(SPACE).growX().row();
        // Das ist mit Absicht 2 mal hier, wenn man die Methode scrollTo nur 1 mal aufruft, scrollt er nicht bis ans
        // Ende falls die Nachricht aus mehreren Zeilen besteht! Es hängt warscheinlich mit der Art und Weise zusammen
        // wie in LibGDX Labels gehandhabt werden, bei denen setWrap auf true gesetzt ist.
        historyScrollPane.scrollTo(0, 0, 0, 0);
        historyScrollPane.scrollTo(0, 0, 0, 0);
    }
}
