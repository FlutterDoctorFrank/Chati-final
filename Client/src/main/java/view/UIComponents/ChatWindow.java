package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;

public class ChatWindow extends Window {
    private Label chatLable;
    private TextArea messageField;
    private ScrollPane chatScroll;
    private boolean minimized = false;
    private Hud hud;
    private TextButton minimizeButton;

    public ChatWindow(Hud hud) {
        super("Chati", new Skin(Gdx.files.internal("shadeui/uiskin.json")));
        this.hud = hud;
        setResizable(true);
        setSize(300, 350);
        setPosition(hud.getWidth() - getWidth(), 0);
        setName("chat-window");
        create();
    }

    private void create() {
        chatLable = new Label("", getSkin());
        chatLable.setWrap(true);
        chatLable.setAlignment(Align.topLeft);

        chatScroll = new ScrollPane(chatLable, getSkin());
        chatScroll.setFadeScrollBars(false);


        messageField = new TextArea("", getSkin());
        messageField.setPrefRows(3);
        //ScrollPane inputScroll = new ScrollPane(messageField, getSkin());
        //inputScroll.setFadeScrollBars(false);
        //inputScroll.layout();


        TextButton sendButton = new TextButton("Senden", getSkin());
        sendButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                String message = messageField.getText();
                if (message.length() > 0) {
                    messageField.setText("");
                    String[] messageToSend = {message};
                    hud.sendTextMessage(messageToSend);
                }
            }
        });

        Table inputTable = new Table();
        inputTable.defaults().space(10).height(40);
        inputTable.add(messageField).expand().fill();
        inputTable.add(sendButton).right();

        add(chatScroll).expand().fill();
        row();
        add(inputTable);

        minimizeButton = new TextButton("-", getSkin());
        minimizeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                minimize();
            }
        });

        getTitleTable().add(minimizeButton).width(20).height(20);
    }

    public void receiveMessage(@NotNull String message) {
        chatLable.setText(chatLable.getText() + message + "\n");
        chatScroll.scrollTo(0, 0, 0, 0);
    }

    private void minimize() {
        if (!minimized) {
            setSize(getWidth(), getTitleTable().getHeight());
            minimizeButton.setText("+");
            setResizable(false);
            minimized = true;
        } else {
            setSize(getWidth(), 350);
            minimizeButton.setText("-");
            setResizable(true);
            minimized = false;
        }
    }
}
