package view2.component.menu.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import model.context.spatial.SpatialMap;
import view2.Assets;
import view2.Chati;
import view2.component.menu.MenuResponse;
import view2.component.menu.MenuScreen;
import view2.component.menu.table.MenuTable;
import view2.component.menu.table.StartTable;

import java.util.EnumSet;

public class WorldCreateTable extends MenuTable {

    private TextField worldNameField;
    private Label mapSelectLabel;
    private SelectBox<SpatialMap> mapSelectBox;
    private TextButton confirmButton;
    private TextButton cancelButton;

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle einen Namen und eine Karte!");

        mapSelectLabel = new Label("Karte: ", Assets.SKIN);
        mapSelectBox = new SelectBox<>(Assets.SKIN);
        mapSelectBox.setItems(EnumSet.allOf(SpatialMap.class).toArray(new SpatialMap[0]));

        worldNameField = new TextField("Name der Welt", Assets.getNewSkin());
        worldNameField.getStyle().fontColor = Color.GRAY;
        worldNameField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if(focused && worldNameField.getStyle().fontColor == Color.GRAY) {
                    worldNameField.getStyle().fontColor = Color.BLACK;
                    worldNameField.setText("");
                }
                else if (worldNameField.getText().isBlank()) {
                    resetTextFields();
                }
            }
        });

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
                Chati.CHATI.getMenuScreen().setPendingResponse(MenuResponse.CREATE_WORLD);
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
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().fillX().expandX();
        container.add(infoLabel).row();
        container.add(worldNameField).row();
        Table mapSelectContainer = new Table();
        mapSelectContainer.add(mapSelectLabel).width(ROW_WIDTH / 8f);
        mapSelectContainer.add(mapSelectBox).fillX().expandX();
        container.add(mapSelectContainer).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).fillX().expandX();
        buttonContainer.add(confirmButton).spaceRight(SPACING);
        buttonContainer.add(cancelButton);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {
        worldNameField.getStyle().fontColor = Color.GRAY;
        worldNameField.setText("Name der Welt");
        getStage().unfocus(worldNameField);
    }
}
