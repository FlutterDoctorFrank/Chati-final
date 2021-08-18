package view2.component.world.objectMenus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.Music;
import view2.Chati;
import view2.component.ChatiTable;
import view2.component.ChatiWindow;
import view2.component.menu.ContextEntry;
import view2.component.menu.MenuResponse;
import view2.component.menu.MenuScreen;

import java.util.EnumSet;

public class RoomReceptionWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_WIDTH = 650;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;

    private final ContextID roomReceptionId;

    private Label infoLabel;

    private Table roomSelectTable;

    private Table roomJoinTable;
    private TextField passwordField;
    private TextButton acceptButton;

    private Table roomCreateTable;

    protected RoomReceptionWindow(ContextID roomReceptionId) {
        super("Raumrezeption");
        this.roomReceptionId = roomReceptionId;
        create();
        setLayout();
    }

    @Override
    protected void create() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        infoLabel = new Label("", style);
    }

    @Override
    protected void setLayout() {
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        add(new RoomSelectTable());
    }

    private class RoomSelectTable extends ChatiTable {

        private Label roomSelectLabel;
        private SelectBox<ContextEntry> roomSelectBox;
        private TextButton joinButton;
        private TextButton requestButton;
        private TextButton createButton;
        private TextButton cancelButton;

        @Override
        protected void create() {
            infoLabel.setText("Wähle eine Aktion aus!");

            Label.LabelStyle style = new Label.LabelStyle();
            style.font = new BitmapFont();
            style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
            roomSelectLabel = new Label("Raum: ", style);

            roomSelectBox = new SelectBox<>(Chati.SKIN);
            // roomSelectBox.setItems...

            joinButton = new TextButton("Beitreten", Chati.SKIN);
            joinButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    remove();
                    RoomReceptionWindow.this.add(new RoomJoinTable());
                }
            });

            requestButton = new TextButton("Beitritt anfragen", Chati.SKIN);
        }

        @Override
        protected void setLayout() {
        }
    }

    private static class RoomJoinTable extends ChatiTable {

        private TextField passwordField;
        private TextButton acceptButton;
        private TextButton cancelButton;

        @Override
        protected void create() {
        }

        @Override
        protected void setLayout() {

        }
    }

    private static class RoomCreateTable extends ChatiTable {

        private TextField roomnameField;
        private TextField passwordField;
        private TextButton acceptButton;
        private TextButton cancelButton;

        @Override
        protected void create() {

        }

        @Override
        protected void setLayout() {

        }
    }
}
