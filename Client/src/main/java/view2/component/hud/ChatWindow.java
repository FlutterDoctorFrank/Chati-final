package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import model.communication.message.MessageType;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import view2.Chati;
import view2.Texture;
import view2.component.ChatiWindow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
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
    private static final float SPACING = 5;
    private static final Label.LabelStyle STYLE = new Label.LabelStyle();
    private static final BitmapFont FONT = new BitmapFont();

    private static ChatWindow chatWindow;

    private final List<Label> messageLabels;
    private Table messageLabelContainer;
    private ScrollPane historyScrollPane;
    private TextArea typeMessageArea;
    private TextButton sendButton;
    private TextButton minimizeButton;

    private ChatWindow() {
        super("Chat");
        STYLE.font = FONT;
        STYLE.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        this.messageLabels = new LinkedList<>();
        create();
        setLayout();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        /*
        if (Chati.getInstance().isUserInfoChanged()) {
            IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
            if (internUser == null || !internUser.isInCurrentWorld() || internUser.getCurrentWorld() == null) {
                remove();
                HeadUpDisplay.getInstance().chatButton.remove();
            }
        }
        */
        super.draw(batch, parentAlpha);
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
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    sendMessage();
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
                ChatWindow.this.setVisible(false);
                HeadUpDisplay.getInstance().chatButton.getStyle().imageUp = Texture.CHAT_ICON;
                HeadUpDisplay.getInstance().chatButton.setChecked(false);
            }
        });
    }

    @Override
    protected void setLayout() {
        setModal(false);
        setMovable(true);
        setResizable(true);
        setResizeBorder((int) SPACE * 2);
        defaults().minWidth(MINIMUM_WIDTH).minHeight(MINIMUM_HEIGHT);
        setKeepWithinStage(true);
        setPosition(Gdx.graphics.getWidth(), 0);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);

        add(historyScrollPane).top().expandX().fillX().expandY().fillY().padBottom(SPACE).row();

        Table sendContainer = new Table();
        sendContainer.add(typeMessageArea).height(SEND_BUTTON_HEIGHT).expandX().fillX()
                .padLeft(SPACE).padRight(SPACE);
        sendContainer.add(sendButton).width(SEND_BUTTON_WIDTH).height(SEND_BUTTON_HEIGHT)
                .padLeft(SPACE).padRight(SPACE);
        add(sendContainer).height(SEND_BUTTON_HEIGHT).expandX().fillX();

        getTitleTable().add(minimizeButton).right().width(getTitleLabel().getHeight() * (2f/3f)).height(getTitleLabel().getHeight() * (2f/3f));
    }

    private void resetMessageArea() {
        typeMessageArea.setText("Nachricht");
        typeMessageArea.getStyle().fontColor = Color.GRAY;
    }

    private void sendMessage() {
        if (typeMessageArea.getStyle().fontColor == Color.GRAY || typeMessageArea.getText().isEmpty()) {
            return;
        }
        Chati.getInstance().getServerSender().send(ServerSender.SendAction.MESSAGE, typeMessageArea.getText());
        typeMessageArea.setText("");
    }

    public void receiveMessage(UUID userId, String message, MessageType messageType, LocalDateTime timestamp) {
        String username;
        IUserManagerView userManager = Chati.getInstance().getUserManager();
        if (messageType == MessageType.INFO) {
            username = "";
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

        messageLabelContainer.add(showLabel).left().padLeft(SPACING).padBottom(SPACING).expandX().row();
    }

    public static ChatWindow getInstance() {
        if (chatWindow == null) {
            chatWindow = new ChatWindow();
        }
        return chatWindow;
    }
}
