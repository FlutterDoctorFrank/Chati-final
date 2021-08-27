package view2.component.world.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.Menu;
import model.context.spatial.SpatialMap;
import view2.Assets;
import view2.Chati;
import view2.component.Response;
import view2.component.menu.ContextEntry;
import view2.component.menu.table.MenuTable;

import java.util.EnumSet;

public class RoomReceptionWindow extends InteractableWindow {

    private static final int MENU_OPTION_CREATE = 1;
    private static final int MENU_OPTION_JOIN = 2;
    private static final int MENU_OPTION_REQUEST = 3;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_WIDTH = 650;
    private static final float ROW_HEIGHT = 60;
    private static final float BUTTON_WIDTH = 180;
    private static final float BUTTON_HEIGHT = 60;
    private static final float SPACING = 15;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;

    private TextButton closeButton;

    private MenuTable currentTable;

    public RoomReceptionWindow(ContextID roomReceptionId) {
        super("Raumrezeption", roomReceptionId, Menu.ROOM_RECEPTION_MENU);
        create();
        setLayout();
    }

    @Override
    protected void create() {
        closeButton = new TextButton("X", Assets.SKIN);
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                close();
            }
        });
    }

    @Override
    protected void setLayout() {
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));

        setCurrentTable(new RoomSelectTable());
    }

    @Override
    public void showMessage(String messageKey) {
        currentTable.showMessage(messageKey);
    }

    public void setCurrentTable(MenuTable table) {
        clearChildren();
        currentTable = table;
        add(currentTable).width(WINDOW_WIDTH).height(WINDOW_HEIGHT - getPadTop());
    }

    private class RoomSelectTable extends MenuTable {

        private Label roomSelectLabel;
        private SelectBox<ContextEntry> roomSelectBox;
        private TextButton joinButton;
        private TextButton requestButton;
        private TextButton createButton;
        private TextButton cancelButton;

        @Override
        public void resetTextFields() {

        }

        @Override
        protected void create() {
            infoLabel.setText("Wähle eine Aktion aus!");

            roomSelectLabel = new Label("Raum: ", Assets.SKIN);
            roomSelectBox = new SelectBox<>(Assets.SKIN);
            updateRoomList();

            createButton = new TextButton("Raum erstellen", Assets.SKIN);
            createButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    setCurrentTable(new RoomCreateTable());
                }
            });

            joinButton = new TextButton("Beitreten", Assets.SKIN);
            joinButton.addListener(new ClickListener() {
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
                    setCurrentTable(new RoomJoinTable());
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
                    setCurrentTable(new RoomRequestTable());
                }
            });

            cancelButton = new TextButton("Abbrechen", Assets.SKIN);
            cancelButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    close();
                }
            });
        }

        @Override
        protected void setLayout() {
            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
            container.add(infoLabel).row();
            Table roomSelectContainer = new Table();
            roomSelectContainer.add(roomSelectLabel).width(ROW_WIDTH / 8f);
            roomSelectContainer.add(roomSelectBox).growX();
            container.add(roomSelectContainer).row();
            Table roomButtonContainer = new Table();
            roomButtonContainer.defaults().colspan(3).height(ROW_HEIGHT).growX();
            roomButtonContainer.add(joinButton).spaceRight(SPACING);
            roomButtonContainer.add(requestButton).spaceRight(SPACING);
            roomButtonContainer.add(createButton);
            container.add(roomButtonContainer).row();
            container.add(cancelButton);
            add(container).width(ROW_WIDTH);
        }

        private void updateRoomList() {
            roomSelectBox.setItems(Chati.CHATI.getWorldScreen().getPrivateRooms().toArray(new ContextEntry[0]));
        }
    }

    private class RoomCreateTable extends MenuTable {

        private TextField roomnameField;
        private TextField passwordField;
        private Label mapSelectLabel;
        private SelectBox<SpatialMap> mapSelectBox;
        private TextButton confirmButton;
        private TextButton cancelButton;

        @Override
        protected void create() {
            infoLabel.setText("Erstelle einen privaten Raum!");

            roomnameField = new TextField("Name des Raums", Assets.getNewSkin());
            roomnameField.getStyle().fontColor = Color.GRAY;
            roomnameField.addListener(new FocusListener() {
                @Override
                public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                    if(focused && roomnameField.getStyle().fontColor == Color.GRAY) {
                        roomnameField.getStyle().fontColor = Color.BLACK;
                        roomnameField.setText("");
                    }
                    else if (roomnameField.getText().isBlank()) {
                        resetRoomnameField();
                    }
                }
            });

            passwordField = new TextField("Passwort des Raums", Assets.getNewSkin());
            passwordField.getStyle().fontColor = Color.GRAY;
            passwordField.setPasswordCharacter('*');
            passwordField.addListener(new FocusListener() {
                @Override
                public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                    if(focused && passwordField.getStyle().fontColor == Color.GRAY) {
                        passwordField.getStyle().fontColor = Color.BLACK;
                        passwordField.setText("");
                        passwordField.setPasswordMode(true);
                    }
                    else if (passwordField.getText().isBlank()) {
                        resetPasswordField();
                    }
                }
            });

            mapSelectLabel = new Label("Karte: ", Assets.SKIN);
            mapSelectBox = new SelectBox<>(Assets.SKIN);
            mapSelectBox.setItems(EnumSet.allOf(SpatialMap.class).toArray(new SpatialMap[0]));

            confirmButton = new TextButton("Bestätigen", Assets.SKIN);
            confirmButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (roomnameField.getText().isBlank() || passwordField.getText().isBlank()
                            || roomnameField.getStyle().fontColor == Color.GRAY
                            || passwordField.getStyle().fontColor == Color.GRAY) {
                        infoLabel.setText("Bitte fülle alle Felder aus.");
                        return;
                    }
                    if (mapSelectBox.getSelected() == null) {
                        infoLabel.setText("Bitte wähle eine Karte aus!");
                        return;
                    }
                    Chati.CHATI.getWorldScreen().setPendingResponse(Response.MENU_ACTION_RESPONSE);
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomnameField.getText(), passwordField.getText(),
                                    String.valueOf(mapSelectBox.getSelected())}, MENU_OPTION_CREATE);
                }
            });

            cancelButton = new TextButton("Zurück", Assets.SKIN);
            cancelButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    setCurrentTable(new RoomSelectTable());
                }
            });
        }

        @Override
        protected void setLayout() {
            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
            container.add(infoLabel).row();
            container.add(roomnameField).row();
            container.add(passwordField).row();
            Table mapSelectContainer = new Table();
            mapSelectContainer.add(mapSelectLabel).width(ROW_WIDTH / 8f);
            mapSelectContainer.add(mapSelectBox).growX();
            container.add(mapSelectContainer).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).spaceRight(SPACING);
            buttonContainer.add(cancelButton);
            container.add(buttonContainer);
            add(container).width(ROW_WIDTH);
        }

        @Override
        public void resetTextFields() {
            resetRoomnameField();
            resetPasswordField();
        }

        private void resetRoomnameField() {
            roomnameField.getStyle().fontColor = Color.GRAY;
            roomnameField.setText("Name des Raums");
            getStage().unfocus(roomnameField);
        }

        private void resetPasswordField() {
            passwordField.getStyle().fontColor = Color.GRAY;
            passwordField.setText("Passwort des Raums");
            passwordField.setPasswordMode(false);
            getStage().unfocus(passwordField);
        }
    }

    private static class RoomJoinTable extends MenuTable {

        private TextField passwordField;
        private TextButton acceptButton;
        private TextButton cancelButton;

        @Override
        protected void create() {
        }

        @Override
        protected void setLayout() {

        }

        @Override
        public void resetTextFields() {

        }
    }

    private static class RoomRequestTable extends MenuTable {

        @Override
        protected void create() {

        }

        @Override
        protected void setLayout() {

        }

        @Override
        public void resetTextFields() {

        }
    }
}
