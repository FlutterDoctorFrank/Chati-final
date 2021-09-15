package view2.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.spatial.ContextMap;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiSelectBox;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.Response;
import java.util.EnumSet;

/**
 * Eine Klasse, welche das Menü zum Erstellen eine Welt repräsentiert.
 */
public class WorldCreateTable extends MenuTable {

    private final ChatiTextField worldNameField;

    /**
     * Erzeugt eine neue Instanz des WorldCreateTable.
     */
    public WorldCreateTable() {
        super("table.entry.create-world");

        ChatiLabel mapSelectLabel = new ChatiLabel("menu.label.map");
        ChatiSelectBox<ContextMap> mapSelectBox = new ChatiSelectBox<>(ContextMap::getName);
        mapSelectBox.setMaxListCount(MAX_LIST_COUNT);
        mapSelectBox.setItems(EnumSet.allOf(ContextMap.class)
                .stream().filter(ContextMap::isPublicRoomMap).toArray(ContextMap[]::new));

        worldNameField = new ChatiTextField("menu.text-field.world-name", false);

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (worldNameField.isBlank()) {
                    showMessage("table.create-world.no-name");
                    return;
                }
                if (mapSelectBox.getSelected() == null) {
                    showMessage("table.create-world.no-map");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.CREATE_WORLD);
                Chati.CHATI.send(ServerSender.SendAction.WORLD_CREATE, mapSelectBox.getSelected(), worldNameField.getText());
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });

        // Layout
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        container.add(worldNameField).row();
        Table mapSelectContainer = new Table();
        mapSelectContainer.add(mapSelectLabel).width(ROW_WIDTH / 8f);
        mapSelectContainer.add(mapSelectBox).growX();
        container.add(mapSelectContainer).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);

        // Translatable register
        translates.add(mapSelectLabel);
        translates.add(worldNameField);
        translates.add(confirmButton);
        translates.add(cancelButton);
        translates.trimToSize();
    }

    @Override
    public void resetTextFields() {
        worldNameField.reset();
    }
}
