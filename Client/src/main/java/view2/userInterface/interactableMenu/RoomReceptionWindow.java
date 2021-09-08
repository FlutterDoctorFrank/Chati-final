package view2.userInterface.interactableMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.context.spatial.ContextMenu;
import model.context.spatial.ContextMap;
import model.role.Permission;
import view2.Chati;
import view2.userInterface.ChatiTextArea;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.ContextEntry;
import view2.userInterface.menu.table.MenuTable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class RoomReceptionWindow extends InteractableWindow {

    private static final int MENU_OPTION_CREATE = 1;
    private static final int MENU_OPTION_JOIN = 2;
    private static final int MENU_OPTION_REQUEST = 3;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 450;
    private static final float MESSAGE_AREA_HEIGHT = 120;

    private MenuTable currentTable;

    public RoomReceptionWindow(ContextID roomReceptionId) {
        super("Raumrezeption", roomReceptionId, ContextMenu.ROOM_RECEPTION_MENU);
        setCurrentTable(new RoomSelectTable());

        // Layout
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);
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

        private final SelectBox<ContextEntry> roomSelectBox;

        public RoomSelectTable() {
            infoLabel.setText("Wähle eine Aktion aus!");

            Label roomSelectLabel = new Label("Raum: ", Chati.CHATI.getSkin());
            roomSelectBox = new SelectBox<>(Chati.CHATI.getSkin());
            updateRoomList();

            ChatiTextButton createButton = new ChatiTextButton("Raum erstellen", true);
            createButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setCurrentTable(new RoomCreateTable());
                }
            });

            ChatiTextButton joinButton = new ChatiTextButton("Beitreten", true);
            joinButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (roomSelectBox.getSelected() == null) {
                        infoLabel.setText("Bitte wähle einen Raum aus.");
                        return;
                    }
                    if (Chati.CHATI.getInternUser().hasPermission(Permission.ENTER_PRIVATE_ROOM)) {
                        Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                                new String[]{roomSelectBox.getSelected().getName(), ""}, MENU_OPTION_JOIN);
                    } else {
                        setCurrentTable(new RoomJoinTable(roomSelectBox.getSelected()));
                    }
                }
            });

            ChatiTextButton requestButton = new ChatiTextButton("Beitritt anfragen", true);
            requestButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (roomSelectBox.getSelected() == null) {
                        infoLabel.setText("Bitte wähle einen Raum aus.");
                        return;
                    }
                    setCurrentTable(new RoomRequestTable(roomSelectBox.getSelected()));
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("Abbrechen", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    close();
                }
            });

            // Layout
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
        public void act(float delta) {
            if (Chati.CHATI.isRoomListChanged()) {
                updateRoomList();
            }
            super.act(delta);
        }

        @Override
        public void resetTextFields() {
        }

        private void updateRoomList() {
            roomSelectBox.setItems(Chati.CHATI.getPrivateRooms().toArray(new ContextEntry[0]));
        }
    }

    private class RoomCreateTable extends MenuTable {

        private final ChatiTextField roomnameField;
        private final ChatiTextField passwordField;

        public RoomCreateTable() {
            infoLabel.setText("Erstelle einen privaten Raum!");

            roomnameField = new ChatiTextField("Name des Raums", false);
            passwordField = new ChatiTextField("Passwort", true);

            Label mapSelectLabel = new Label("Karte: ", Chati.CHATI.getSkin());
            SelectBox<ContextMap> mapSelectBox = new SelectBox<>(Chati.CHATI.getSkin());
            mapSelectBox.setItems(EnumSet.allOf(ContextMap.class)
                    .stream().filter(Predicate.not(ContextMap::isPublicRoomMap)).toArray(ContextMap[]::new));

            ChatiTextButton confirmButton = new ChatiTextButton("Bestätigen", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (roomnameField.isBlank() || passwordField.isBlank()) {
                        infoLabel.setText("Bitte fülle alle Felder aus.");
                        return;
                    }
                    if (mapSelectBox.getSelected() == null) {
                        infoLabel.setText("Bitte wähle eine Karte aus!");
                        return;
                    }
                    Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomnameField.getText(), passwordField.getText(),
                                    String.valueOf(mapSelectBox.getSelected())}, MENU_OPTION_CREATE);
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("Zurück", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setCurrentTable(new RoomSelectTable());
                }
            });

            // Layout
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

        private final ChatiTextField passwordField;

        public RoomJoinTable(ContextEntry roomEntry) {

            infoLabel.setText("Bitte gib das Passwort ein!");
            passwordField = new ChatiTextField("Passwort", true);

            ChatiTextButton confirmButton = new ChatiTextButton("Bestätigen", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (passwordField.isBlank()) {
                        infoLabel.setText("Bitte gib das Passwort ein!");
                        return;
                    }
                    Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomEntry.getName(), passwordField.getText()}, MENU_OPTION_JOIN);
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("Zurück", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setCurrentTable(new RoomSelectTable());
                }
            });

            // Layout
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

        private final ChatiTextArea messageArea;

        public RoomRequestTable(ContextEntry roomEntry) {

            infoLabel.setText("Füge deiner Anfrage eine Nachricht hinzu!");

            messageArea = new ChatiTextArea("Nachricht", true);

            ChatiTextButton confirmButton = new ChatiTextButton("Bestätigen", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String message;
                    if (messageArea.isBlank()) {
                        message = "";
                    } else {
                        message = messageArea.getText().trim();
                    }

                    Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomEntry.getName(), message}, MENU_OPTION_REQUEST);

                    setCurrentTable(new RoomSelectTable());
                    currentTable.showMessage("Deine Anfrage wurde übermittelt!");
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("Zurück", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setCurrentTable(new RoomSelectTable());
                }
            });

            // Layout
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
