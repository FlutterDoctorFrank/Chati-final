package view2.component.world.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.context.spatial.ContextMap;
import model.role.Permission;
import view2.Assets;
import view2.Chati;
import view2.component.ChatiTextArea;
import view2.component.ChatiTextField;
import view2.component.menu.ContextEntry;
import view2.component.menu.table.MenuTable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class RoomReceptionWindow extends InteractableWindow {

    private static final int MENU_OPTION_CREATE = 1;
    private static final int MENU_OPTION_JOIN = 2;
    private static final int MENU_OPTION_REQUEST = 3;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 450;
    private static final float MESSAGE_AREA_HEIGHT = 120;

    private TextButton closeButton;

    private MenuTable currentTable;

    public RoomReceptionWindow(ContextID roomReceptionId) {
        super("Raumrezeption", roomReceptionId, ContextMenu.ROOM_RECEPTION_MENU);
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

    public void setCurrentTable(MenuTable table) {
        removeActor(currentTable);
        currentTable = table;
        addActor(currentTable);
    }

    @Override
    public void receiveResponse(boolean success, String messageKey) {
        if (!success) {
            currentTable.showMessage(messageKey);
        }
    }

    private class RoomSelectTable extends MenuTable {

        private Label roomSelectLabel;
        private SelectBox<ContextEntry> roomSelectBox;
        private TextButton joinButton;
        private TextButton requestButton;
        private TextButton createButton;
        private TextButton cancelButton;

        public RoomSelectTable() {
            create();
            setLayout();
        }

        @Override
        public void act(float delta) {
            if (Chati.CHATI.isRoomListChanged()) {
                updateRoomList();
            }
            super.act(delta);
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
                    if (Chati.CHATI.getUserManager().getInternUserView().hasPermission(Permission.ENTER_PRIVATE_ROOM)) {
                        Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                                new String[]{roomSelectBox.getSelected().getName(), ""}, MENU_OPTION_JOIN);
                    } else {
                        setCurrentTable(new RoomJoinTable(roomSelectBox.getSelected()));
                    }
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
                    setCurrentTable(new RoomRequestTable(roomSelectBox.getSelected()));
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
            roomButtonContainer.add(joinButton).padRight(SPACING / 2);
            roomButtonContainer.add(requestButton).padLeft(SPACING / 2).padRight(SPACING / 2);
            roomButtonContainer.add(createButton).padLeft(SPACING / 2);
            container.add(roomButtonContainer).row();
            container.add(cancelButton);
            add(container).width(WINDOW_WIDTH - 2 * SPACING - RoomReceptionWindow.this.getPadX());
        }

        @Override
        public void resetTextFields() {
        }

        private void updateRoomList() {
            roomSelectBox.setItems(Chati.CHATI.getWorldScreen().getPrivateRooms().toArray(new ContextEntry[0]));
        }
    }

    private class RoomCreateTable extends MenuTable {

        private ChatiTextField roomnameField;
        private ChatiTextField passwordField;
        private Label mapSelectLabel;
        private SelectBox<ContextMap> mapSelectBox;
        private TextButton confirmButton;
        private TextButton cancelButton;

        public RoomCreateTable() {
            create();
            setLayout();
        }

        @Override
        protected void create() {
            infoLabel.setText("Erstelle einen privaten Raum!");

            roomnameField = new ChatiTextField("Name des Raums", false);
            passwordField = new ChatiTextField("Passwort", true);

            mapSelectLabel = new Label("Karte: ", Assets.SKIN);
            mapSelectBox = new SelectBox<>(Assets.SKIN);
            mapSelectBox.setItems(EnumSet.allOf(ContextMap.class)
                    .stream().filter(Predicate.not(ContextMap::isPublicRoomMap)).toArray(ContextMap[]::new));

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
            buttonContainer.add(confirmButton).padRight(SPACING / 2);
            buttonContainer.add(cancelButton).padLeft(SPACING / 2);
            container.add(buttonContainer);
            add(container).width(WINDOW_WIDTH - 2 * SPACING - RoomReceptionWindow.this.getPadX());
        }

        @Override
        public void resetTextFields() {
            roomnameField.reset();
            passwordField.reset();
        }
    }

    private class RoomJoinTable extends MenuTable {

        private final ContextEntry roomEntry;

        private ChatiTextField passwordField;
        private TextButton confirmButton;
        private TextButton cancelButton;

        public RoomJoinTable(ContextEntry roomEntry) {
            this.roomEntry = roomEntry;
            create();
            setLayout();
        }

        @Override
        protected void create() {
            infoLabel.setText("Bitte gib das Passwort ein!");

            passwordField = new ChatiTextField("Passwort", true);

            confirmButton = new TextButton("Bestätigen", Assets.SKIN);
            confirmButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (passwordField.getText().isBlank() || passwordField.getStyle().fontColor == Color.GRAY) {
                        infoLabel.setText("Bitte gib das Passwort ein!");
                        return;
                    }
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomEntry.getName(), passwordField.getText()}, MENU_OPTION_JOIN);
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
            container.add(passwordField).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACING / 2);
            buttonContainer.add(cancelButton).padLeft(SPACING / 2);
            container.add(buttonContainer);
            add(container).width(WINDOW_WIDTH - 2 * SPACING - RoomReceptionWindow.this.getPadX());
        }

        @Override
        public void resetTextFields() {
            passwordField.reset();
        }
    }

    private class RoomRequestTable extends MenuTable {

        private final ContextEntry roomEntry;

        private ChatiTextArea messageArea;
        private TextButton confirmButton;
        private TextButton cancelButton;

        public RoomRequestTable(ContextEntry roomEntry) {
            this.roomEntry = roomEntry;
            create();
            setLayout();
        }

        @Override
        protected void create() {
            infoLabel.setText("Füge deiner Anfrage eine Nachricht hinzu!");

            messageArea = new ChatiTextArea("Nachricht", true);

            confirmButton = new TextButton("Bestätigen", Assets.SKIN);
            confirmButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    String message;
                    if (messageArea.getText().isBlank() || messageArea.getStyle().fontColor == Color.GRAY) {
                        message = "";
                    } else {
                        message = messageArea.getText().trim();
                    }

                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomEntry.getName(), message}, MENU_OPTION_REQUEST);

                    setCurrentTable(new RoomSelectTable());
                    currentTable.showMessage("Deine Anfrage wurde übermittelt!");
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
            container.add(messageArea).height(MESSAGE_AREA_HEIGHT).spaceBottom(SPACING).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACING / 2);
            buttonContainer.add(cancelButton).padLeft(SPACING / 2);
            container.add(buttonContainer);
            add(container).width(WINDOW_WIDTH - 2 * SPACING - RoomReceptionWindow.this.getPadX());
        }

        @Override
        public void resetTextFields() {
            messageArea.reset();
        }
    }
}
