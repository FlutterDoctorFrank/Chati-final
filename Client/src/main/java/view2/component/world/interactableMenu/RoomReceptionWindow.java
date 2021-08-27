package view2.component.world.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.Menu;
import view2.Assets;
import view2.Chati;
import view2.component.AbstractTable;
import view2.component.menu.ContextEntry;

public class RoomReceptionWindow extends InteractableWindow {

    private static final int MENU_OPTION_CREATE = 1;
    private static final int MENU_OPTION_JOIN = 2;
    private static final int MENU_OPTION_REQUEST = 3;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_WIDTH = 650;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;

    private Label infoLabel;

    private Table roomSelectTable;
    private Table roomCreateTable;
    private Table roomJoinTable;
    private Table roomRequestTable;

    public RoomReceptionWindow(ContextID roomReceptionId) {
        super("Raumrezeption", roomReceptionId, Menu.ROOM_RECEPTION_MENU);
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

    @Override
    public void showResponse(boolean success, String messageKey) {

    }

    private class RoomSelectTable extends AbstractTable {

        private Label roomSelectLabel;
        private SelectBox<ContextEntry> roomSelectBox;
        private TextButton joinButton;
        private TextButton requestButton;
        private TextButton createButton;
        private TextButton cancelButton;

        @Override
        protected void create() {
            infoLabel.setText("Wähle eine Aktion aus!");

            roomSelectLabel = new Label("Raum: ", Assets.SKIN);
            roomSelectBox = new SelectBox<>(Assets.SKIN);
            updateRoomList();

            joinButton = new TextButton("Beitreten", Assets.SKIN);
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

            requestButton = new TextButton("Beitritt anfragen", Assets.SKIN);
            requestButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (roomSelectBox.getSelected() == null) {
                        infoLabel.setText("Bitte wähle einen Raum aus.");
                        return;
                    }
                    remove();
                    RoomReceptionWindow.this.add(new RoomRequestTable());
                }
            });
        }

        @Override
        protected void setLayout() {
        }

        private void updateRoomList() {
            roomSelectBox.setItems(Chati.CHATI.getWorldScreen().getPrivateRooms().toArray(new ContextEntry[0]));
        }
    }

    private static class RoomCreateTable extends AbstractTable {

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

    private static class RoomJoinTable extends AbstractTable {

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

    private static class RoomRequestTable extends AbstractTable {

        @Override
        protected void create() {

        }

        @Override
        protected void setLayout() {

        }
    }
}
