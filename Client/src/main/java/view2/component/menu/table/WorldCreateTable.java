package view2.component.menu.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.spatial.ContextMap;
import view2.Assets;
import view2.Chati;
import view2.component.ChatiTextField;
import view2.component.Response;

import java.util.EnumSet;

public class WorldCreateTable extends MenuTable {

    private ChatiTextField worldNameField;
    private Label mapSelectLabel;
    private SelectBox<ContextMap> mapSelectBox;
    private TextButton confirmButton;
    private TextButton cancelButton;

    public WorldCreateTable() {
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle einen Namen und eine Karte!");

        mapSelectLabel = new Label("Karte: ", Assets.SKIN);
        mapSelectBox = new SelectBox<>(Assets.SKIN);
        mapSelectBox.setItems(EnumSet.allOf(ContextMap.class)
                .stream().filter(ContextMap::isPublicRoomMap).toArray(ContextMap[]::new));

        worldNameField = new ChatiTextField("Name der Welt", false);

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (worldNameField.getText().isBlank()
                        || worldNameField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte gib den Namen der zu erstellenden Welt ein!");
                    return;
                }
                if (mapSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Karte aus!");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.CREATE_WORLD);
                Chati.CHATI.getServerSender()
                        .send(ServerSender.SendAction.WORLD_CREATE, mapSelectBox.getSelected(), worldNameField.getText());
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
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });
    }

    @Override
    protected void setLayout() {
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
    }

    @Override
    public void resetTextFields() {
        worldNameField.reset();
    }
}
