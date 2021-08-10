package view2.component.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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

public class StartTable extends MenuTable {

    private static final String NAME = "start-table";
    private Label worldSelectLabel;
    private Label mapSelectLabel;
    private SelectBox<ContextEntry> worldSelectBox;
    private SelectBox<SpatialMap> mapSelectBox;
    private TextField worldNameField;
    private TextButton joinWorldButton;
    private TextButton createWorldButton;
    private TextButton deleteWorldButton;
    private TextButton changeAvatarButton;
    private TextButton profileSettingsButton;
    private TextButton logoutButton;

    public StartTable() {
        super(NAME);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateWorldList();
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle eine Welt aus!");

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(0.5f);

        worldSelectLabel = new Label("Welt: ", style);
        worldSelectBox = new SelectBox<>(SKIN);

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

        joinWorldButton = new TextButton("Welt Beitreten", SKIN);
        joinWorldButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (worldSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Welt aus!");
                    return;
                }
                Chati.getInstance().getServerSender()
                    .send(ServerSender.SendAction.WORLD_ACTION, worldSelectBox.getSelected().getContextId(), true);
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.JOIN_WORLD);
            }
        });

        createWorldButton = new TextButton("Welt erstellen", SKIN);
        createWorldButton.addListener(new InputListener() {
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

        deleteWorldButton = new TextButton("Welt löschen", SKIN);
        deleteWorldButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (worldSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Welt aus!");
                    return;
                }
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.WORLD_DELETE, worldSelectBox.getSelected().getContextId());
                Chati.getInstance().getMenuScreen().setPendingResponse(Response.DELETE_WORLD);
            }
        });

        changeAvatarButton = new TextButton("Avatar ändern", SKIN);
        changeAvatarButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new AvatarSelectTable());
            }
        });

        profileSettingsButton = new TextButton("Konto verwalten", SKIN);
        profileSettingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new ProfileSettingsTable());
            }
        });

        logoutButton = new TextButton("Abmelden", SKIN);
        logoutButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGOUT, "", false);
                Chati.getInstance().getMenuScreen().setTable(new LoginTable());
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

        Table worldSelectContainer = new Table();
        worldSelectContainer.setWidth(width);
        worldSelectContainer.defaults().space(verticalSpacing);
        add(worldSelectContainer).spaceBottom(horizontalSpacing).row();
        worldSelectContainer.add(worldSelectLabel).width(width / 8f - (verticalSpacing / 2f)).height(height);
        worldSelectContainer.add(worldSelectBox).width(7 * width / 8f - (verticalSpacing / 2f)).height(height);

        add(createWorldButton).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        Table mapSelectContainer = new Table();
        mapSelectContainer.setWidth(width);
        mapSelectContainer.defaults().space(verticalSpacing);
        add(mapSelectContainer).spaceBottom(horizontalSpacing).row();
        mapSelectContainer.add(mapSelectLabel).width(width / 8f - (verticalSpacing / 2f)).height(height);
        mapSelectContainer.add(mapSelectBox).width(7 * width / 8f - (verticalSpacing / 2f)).height(height);

        Table worldButtonContainer = new Table();
        worldButtonContainer.setWidth(width);
        worldButtonContainer.defaults().space(verticalSpacing);
        add(worldButtonContainer).spaceBottom(horizontalSpacing).row();
        worldButtonContainer.add(joinWorldButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        worldButtonContainer.add(deleteWorldButton).width(width / 2f - (verticalSpacing / 2f)).height(height);

        Table settingsButtonContainer = new Table();
        settingsButtonContainer.setWidth(width);
        settingsButtonContainer.defaults().space(verticalSpacing);
        add(settingsButtonContainer).spaceBottom(horizontalSpacing).row();
        settingsButtonContainer.add(changeAvatarButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        settingsButtonContainer.add(profileSettingsButton).width(width / 2f - (verticalSpacing / 2f)).height(height);

        add(logoutButton).width(width).center().height(height);
    }

    @Override
    public void clearTextFields() {
        worldNameField.setText("");
    }

    private void updateWorldList() {
        worldSelectBox.setItems(Chati.getInstance().getMenuScreen().getWorlds().toArray(new ContextEntry[0]));
    }
}
