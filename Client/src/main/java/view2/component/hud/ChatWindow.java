package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IUserManagerView;
import view2.Chati;
import view2.component.ChatiWindow;
import view2.component.KeyAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ChatWindow extends ChatiWindow {

    private static final float DEFAULT_WIDTH = 650;
    private static final float DEFAULT_HEIGHT = 400;
    private static final float MINIMUM_WIDTH = 300;
    private static final float MINIMUM_HEIGHT = 150;
    private static final float SPACE = 5;
    private static final float SEND_BUTTON_WIDTH = 120;
    private static final float SEND_BUTTON_HEIGHT = 60;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;
    private static final float MESSAGE_AREA_FONT_SCALE_FACTOR = 1.6f;
    private static final Label.LabelStyle STYLE = new Label.LabelStyle();
    private static final BitmapFont FONT = new BitmapFont();

    private static ChatWindow chatWindow;

    private Table messageLabelContainer;
    private ScrollPane historyScrollPane;
    private TextArea typeMessageArea;
    private TextButton sendButton;
    private TextButton minimizeButton;

    private ChatWindow() {
        super("Chat");
        STYLE.font = FONT;
        STYLE.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        create();
        setLayout();
    }

    @Override
    protected void create() {
        messageLabelContainer = new Table();

        historyScrollPane = new ScrollPane(messageLabelContainer, Chati.SKIN);

        Skin typeMessageAreaSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        typeMessageAreaSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(MESSAGE_AREA_FONT_SCALE_FACTOR);
        typeMessageArea = new TextArea("Nachricht", typeMessageAreaSkin);
        typeMessageArea.getStyle().fontColor = Color.GRAY;
        typeMessageArea.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (focused && typeMessageArea.getStyle().fontColor == Color.GRAY) {
                    typeMessageArea.setText("");
                    typeMessageArea.getStyle().fontColor = Color.BLACK;
                }
                else if (typeMessageArea.getText().isEmpty()) {
                    resetMessageArea();
                }
            }
        });
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
        });

        sendButton = new TextButton("Senden", Chati.SKIN);
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

        minimizeButton = new TextButton("X", Chati.SKIN);
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
        add(historyScrollPane).top().fillX().expandX().fillY().expandY().padBottom(SPACE).row();

        Table sendContainer = new Table();
        sendContainer.add(typeMessageArea).height(SEND_BUTTON_HEIGHT).expandX().fillX()
                .padLeft(SPACE).padRight(SPACE);
        sendContainer.add(sendButton).width(SEND_BUTTON_WIDTH).height(SEND_BUTTON_HEIGHT)
                .padLeft(SPACE).padRight(SPACE);
        add(sendContainer).height(SEND_BUTTON_HEIGHT).expandX().fillX();

        getTitleTable().add(minimizeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
        HeadUpDisplay.getInstance().getStage().addActor(this);
    }

    private void resetMessageArea() {
        typeMessageArea.setText("Nachricht");
        typeMessageArea.getStyle().fontColor = Color.GRAY;
    }

    private void sendMessage() {
        if (typeMessageArea.getStyle().fontColor == Color.GRAY || typeMessageArea.getText().isEmpty()) {
            return;
        }
        String sendMessage = typeMessageArea.getText();
        if (sendMessage.charAt(sendMessage.length() - 1) == '\n') {
            sendMessage = sendMessage.substring(0, sendMessage.length() - 1);
        }
        Chati.getInstance().getServerSender().send(ServerSender.SendAction.MESSAGE, sendMessage);
        typeMessageArea.setText("");
    }

    public void showMessage(UUID userId, String message, MessageType messageType, LocalDateTime timestamp) {
        String username;
        IUserManagerView userManager = Chati.getInstance().getUserManager();
        if (messageType == MessageType.INFO) {
            username = "Info";
        } else if (userManager.getInternUserView() != null && userManager.getInternUserView().getUserId().equals(userId)) {
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
            case INFO:
                messageColor = Color.RED;
                break;
            default:
                throw new IllegalArgumentException("No valid messagetype.");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = timestamp.format(formatter);

        String showMessage = "[" + timeString + "] " + username + ": " + message;
        Label showLabel = new Label(showMessage, STYLE);
        showLabel.setColor(messageColor);
        showLabel.setWrap(true);

        messageLabelContainer.add(showLabel).top().left().padLeft(SPACE).padBottom(SPACE).expandX().fillX().row();
        historyScrollPane.scrollTo(0, 0, 0, 0);
    }

    public void clearChat() {
        resetMessageArea();
        messageLabelContainer.clearChildren();
    }

    public void focus() {
        getStage().setScrollFocus(historyScrollPane);
        historyScrollPane.scrollTo(0, 0, 0, 0);
    }

    public static ChatWindow getInstance() {
        if (chatWindow == null) {
            chatWindow = new ChatWindow();
        }
        return chatWindow;
    }
}
