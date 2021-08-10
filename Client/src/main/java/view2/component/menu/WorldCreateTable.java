package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

    private static final String NAME = "world-create-table";

    private TextField worldNameField;
    private Label mapSelectLabel;
    private SelectBox<SpatialMap> mapSelectBox;
    private TextButton confirmButton;
    private TextButton cancelButton;

    protected WorldCreateTable() {
        super(NAME);
    }

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle einen Namen und eine Karte!");

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(0.5f);

        mapSelectLabel = new Label("Karte: ", style);
        mapSelectBox = new SelectBox<>(SKIN);
        mapSelectBox.setItems(EnumSet.allOf(SpatialMap.class).toArray(new SpatialMap[0]));

        Skin worldnameFieldSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        worldnameFieldSkin.get(TextField.TextFieldStyle.class).font.getData().setScale(1.6f);
        worldNameField = new TextField("Name der Welt", worldnameFieldSkin);
        worldNameField.getStyle().fontColor = Color.GRAY;
        worldNameField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event){
                if (event.toString().equals("mouseMoved")
                        || event.toString().equals("exit")
                        || event.toString().equals("enter")
                        || event.toString().equals("keyDown")
                        || event.toString().equals("touchUp")) {
                    return false;
                }
                if (worldNameField.getStyle().fontColor == Color.GRAY) {
                    worldNameField.setText("");
                    worldNameField.getStyle().fontColor = Color.BLACK;
                }
                return true;
            }
        });

        confirmButton = new TextButton("Bestätigen", SKIN);
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

        cancelButton = new TextButton("Zurück", SKIN);
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
        int width = 600;
        int height = 60;
        int verticalSpacing = 15;
        int horizontalSpacing = 15;

        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        add(worldNameField).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        Table mapSelectContainer = new Table();
        mapSelectContainer.setWidth(width);
        mapSelectContainer.defaults().space(verticalSpacing);
        add(mapSelectContainer).spaceBottom(horizontalSpacing).row();
        mapSelectContainer.add(mapSelectLabel).width(width / 8f - (verticalSpacing / 2f)).height(height);
        mapSelectContainer.add(mapSelectBox).width(7 * width / 8f - (verticalSpacing / 2f)).height(height);

        Table settingsButtonContainer = new Table();
        settingsButtonContainer.setWidth(width);
        settingsButtonContainer.defaults().space(verticalSpacing);
        add(settingsButtonContainer).spaceBottom(horizontalSpacing);
        settingsButtonContainer.add(confirmButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        settingsButtonContainer.add(cancelButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
    }

    @Override
    public void clearTextFields() {
        worldNameField.setText("");
    }
}
