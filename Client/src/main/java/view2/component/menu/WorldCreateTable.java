package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import model.context.spatial.SpatialMap;
import view2.Chati;

import java.util.EnumSet;

public class WorldCreateTable extends MenuTable {

    private TextField worldNameField;
    private Label mapSelectLabel;
    private SelectBox<SpatialMap> mapSelectBox;
    private TextButton confirmButton;
    private TextButton cancelButton;

    @Override
    protected void create() {
        super.create();
        infoLabel.setText("Bitte wähle einen Namen und eine Karte!");

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);

        mapSelectLabel = new Label("Karte: ", style);
        mapSelectBox = new SelectBox<>(Chati.SKIN);
        mapSelectBox.setItems(EnumSet.allOf(SpatialMap.class).toArray(new SpatialMap[0]));

        Skin worldnameFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        worldnameFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(TEXTFIELD_FONT_SCALE_FACTOR);
        worldNameField = new TextField("Name der Welt", worldnameFieldSkin);
        worldNameField.getStyle().fontColor = Color.GRAY;
        worldNameField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if(focused && worldNameField.getStyle().fontColor == Color.GRAY) {
                    worldNameField.getStyle().fontColor = Color.BLACK;
                    worldNameField.setText("");
                }
                else if (worldNameField.getText().isEmpty()) {
                    resetTextFields();
                }
            }
        });

        confirmButton = new TextButton("Bestätigen", Chati.SKIN);
        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (worldNameField.getText().isEmpty()
                        || worldNameField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte gib den Namen der zu erstellenden Welt ein!");
                    return;
                }
                if (mapSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Karte aus!");
                    return;
                }
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.WORLD_CREATE, mapSelectBox.getSelected(), worldNameField.getText());
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.CREATE_WORLD);
            }
        });

        cancelButton = new TextButton("Zurück", Chati.SKIN);
        cancelButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new StartTable());
            }
        });
    }

    @Override
    protected void setLayout() {
        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        add(worldNameField).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        Table mapSelectContainer = new Table();
        mapSelectContainer.setWidth(ROW_WIDTH);
        mapSelectContainer.defaults().space(VERTICAL_SPACING);
        add(mapSelectContainer).spaceBottom(HORIZONTAL_SPACING).row();
        mapSelectContainer.add(mapSelectLabel).width(ROW_WIDTH / 8f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        mapSelectContainer.add(mapSelectBox).width(7 * ROW_WIDTH / 8f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);

        Table settingsButtonContainer = new Table();
        settingsButtonContainer.setWidth(ROW_WIDTH);
        settingsButtonContainer.defaults().space(VERTICAL_SPACING);
        add(settingsButtonContainer).spaceBottom(HORIZONTAL_SPACING);
        settingsButtonContainer.add(confirmButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        settingsButtonContainer.add(cancelButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
    }

    @Override
    public void resetTextFields() {
        worldNameField.getStyle().fontColor = Color.GRAY;
        worldNameField.setText("Name der Welt");
        getStage().unfocus(worldNameField);
    }
}
