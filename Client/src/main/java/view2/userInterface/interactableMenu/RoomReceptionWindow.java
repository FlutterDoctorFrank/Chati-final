package view2.userInterface.interactableMenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.MessageBundle;
import model.context.ContextID;
import model.context.spatial.ContextMap;
import model.context.spatial.ContextMenu;
import model.role.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiSelectBox;
import view2.userInterface.ChatiTextArea;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.userInterface.ContextEntry;
import view2.userInterface.menu.MenuTable;
import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * Eine Klasse, welche das Menü der RoomReception repräsentiert.
 */
public class RoomReceptionWindow extends InteractableWindow {

    private static final int MENU_OPTION_CREATE = 1;
    private static final int MENU_OPTION_JOIN = 2;
    private static final int MENU_OPTION_REQUEST = 3;

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 450;
    private static final float MESSAGE_AREA_HEIGHT = 120;

    private MenuTable currentTable;

    /**
     * Erzeugt eine neue Instanz des RoomReceptionWindow.
     * @param roomReceptionId ID des zugehörigen RoomReceptionWindow.
     */
    public RoomReceptionWindow(@NotNull final ContextID roomReceptionId) {
        super("window.title.room-reception", roomReceptionId, ContextMenu.ROOM_RECEPTION_MENU, WINDOW_WIDTH, WINDOW_HEIGHT);
        setCurrentTable(new RoomSelectTable());

        // Layout
        setModal(true);
        setMovable(false);
    }

    /**
     * Setzt den momentan anzuzeigenden Inhalt.
     * @param table Container des anzuzeigenden Inhalts.
     */
    public void setCurrentTable(@NotNull final MenuTable table) {
        removeActor(currentTable);
        currentTable = table;
        addActor(currentTable);
    }

    @Override
    public void receiveResponse(final boolean success, @Nullable final MessageBundle messageBundle) {
        if (!success && messageBundle != null) {
            currentTable.showMessage(messageBundle);
        }
    }

    @Override
    public void translate() {
        super.translate();
        currentTable.translate();
    }

    /**
     * Eine Klasse, welche das Menü zur Auswahl eines Raums repräsentiert.
     */
    private class RoomSelectTable extends MenuTable {

        private final ChatiSelectBox<ContextEntry> roomSelectBox;

        /**
         * Erzeugt eine neue Instanz des RoomSelectTable.
         */
        public RoomSelectTable() {
            super("table.entry.select-room");

            ChatiLabel roomSelectLabel = new ChatiLabel("menu.label.room");
            roomSelectBox = new ChatiSelectBox<>(ContextEntry::getName);
            roomSelectBox.setMaxListCount(MAX_LIST_COUNT);
            updateRoomList();

            ChatiTextButton createButton = new ChatiTextButton("menu.button.room-create", true);
            createButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    setCurrentTable(new RoomCreateTable());
                }
            });

            ChatiTextButton joinButton = new ChatiTextButton("menu.button.room-join", true);
            joinButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    if (Chati.CHATI.getInternUser() == null) {
                        return;
                    }
                    if (roomSelectBox.getSelected() == null) {
                        showMessage("table.entry.choose-room");
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

            ChatiTextButton requestButton = new ChatiTextButton("menu.button.room-request", true);
            requestButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    if (roomSelectBox.getSelected() == null) {
                        showMessage("table.entry.choose-room");
                        return;
                    }
                    setCurrentTable(new RoomRequestTable(roomSelectBox.getSelected()));
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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

            // Translatable register
            translates.add(roomSelectLabel);
            translates.add(createButton);
            translates.add(joinButton);
            translates.add(requestButton);
            translates.add(cancelButton);
            translates.trimToSize();
        }

        @Override
        public void act(final float delta) {
            if (Chati.CHATI.isRoomListChanged()) {
                updateRoomList();
            }
            super.act(delta);
        }

        @Override
        public void resetTextFields() {
        }

        /**
         * Aktualisiert die Liste der angezeigten privaten Räume.
         */
        private void updateRoomList() {
            roomSelectBox.setItems(Chati.CHATI.getUserManager().getPrivateRooms().values().stream()
                    .map(room -> new ContextEntry(room.getContextId(), room.getContextName()))
                    .distinct().toArray(ContextEntry[]::new));
        }
    }

    /**
     * Eine Klasse, welche das Menü zur Erstellung eines Raums repräsentiert.
     */
    private class RoomCreateTable extends MenuTable {

        private final ChatiTextField roomnameField;
        private final ChatiTextField passwordField;

        /**
         * Erzeugt eine neue Instanz des RoomCreateTable.
         */
        public RoomCreateTable() {
            super("table.entry.create-room");

            roomnameField = new ChatiTextField("menu.text-field.room-name", false);
            passwordField = new ChatiTextField("menu.text-field.password", true);

            ChatiLabel mapSelectLabel = new ChatiLabel("menu.label.map");
            ChatiSelectBox<ContextMap> mapSelectBox = new ChatiSelectBox<>(ContextMap::getName);
            mapSelectBox.setMaxListCount(MAX_LIST_COUNT);
            mapSelectBox.setItems(EnumSet.allOf(ContextMap.class)
                    .stream().filter(Predicate.not(ContextMap::isPublicRoomMap)).toArray(ContextMap[]::new));

            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    if (roomnameField.isBlank() || passwordField.isBlank()) {
                        showMessage("table.general.fill-fields");
                        return;
                    }
                    if (mapSelectBox.getSelected() == null) {
                        showMessage("table.create-room.no-map");
                        return;
                    }
                    Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomnameField.getText(), passwordField.getText(),
                                    String.valueOf(mapSelectBox.getSelected())}, MENU_OPTION_CREATE);
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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

            // Translatable register
            translates.add(roomnameField);
            translates.add(passwordField);
            translates.add(mapSelectLabel);
            translates.add(confirmButton);
            translates.add(cancelButton);
            translates.trimToSize();
        }

        @Override
        public void resetTextFields() {
            roomnameField.reset();
            passwordField.reset();
        }
    }

    /**
     * Eine Klasse, welche das Menü zum Betreten eines Raums repräsentiert.
     */
    private class RoomJoinTable extends MenuTable {

        private final ChatiTextField passwordField;

        /**
         * Erzeugt eine neue Instanz des RoomJoinTable.
         * @param roomEntry Eintrag des zu betretenden Raums.
         */
        public RoomJoinTable(@NotNull final ContextEntry roomEntry) {
            super("table.entry.join-room");

            passwordField = new ChatiTextField("menu.text-field.password", true);

            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    if (passwordField.isBlank()) {
                        return;
                    }
                    Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomEntry.getName(), passwordField.getText()}, MENU_OPTION_JOIN);
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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

            // Translatable register
            translates.add(passwordField);
            translates.add(confirmButton);
            translates.add(cancelButton);
            translates.trimToSize();
        }

        @Override
        public void resetTextFields() {
            passwordField.reset();
        }
    }

    /**
     * Eine Klasse, welche das Menü zur Anfrage zum Beitritt eines Raums repräsentiert.
     */
    private class RoomRequestTable extends MenuTable {

        private final ChatiTextArea messageArea;

        /**
         * Erzeugt eine neue Instanz des RoomRequestTable.
         * @param roomEntry Eintrag des anzufragenden Raums.
         */
        public RoomRequestTable(@NotNull final ContextEntry roomEntry) {
            super("table.entry.request-room");

            messageArea = new ChatiTextArea("menu.text-field.message", true);

            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    String message;
                    if (messageArea.isBlank()) {
                        message = "";
                    } else {
                        message = messageArea.getText().trim();
                    }

                    Chati.CHATI.send(ServerSender.SendAction.MENU_OPTION, interactableId,
                            new String[]{roomEntry.getName(), message}, MENU_OPTION_REQUEST);

                    setCurrentTable(new RoomSelectTable());
                    currentTable.showMessage("table.general.request-sent");
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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

            // Translatable register
            translates.add(messageArea);
            translates.add(confirmButton);
            translates.add(cancelButton);
            translates.trimToSize();
        }

        @Override
        public void resetTextFields() {
            messageArea.reset();
        }
    }
}
