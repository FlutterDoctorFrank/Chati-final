package view2.component.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.spatial.SpatialMap;
import view2.Chati;

public class StartTable extends MenuTable {

    private static final String NAME = "start-table";
    private SelectBox<ContextEntry> worldSelectBox;
    private SelectBox<SpatialMap> mapSelectBox;
    private TextField worldNameField;
    private TextButton joinWorldButton;
    private TextButton createWorldButton;
    private TextButton deleteWorldButton;
    private TextButton changeAvatarButton;
    private TextButton profileSettingsButton;

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
        infoLabel = new Label("Bitte wählen Sie eine Welt aus.", SKIN);

        worldSelectBox = new SelectBox<>(SKIN);

        mapSelectBox = new SelectBox<>(SKIN);

        worldNameField = new TextField("Name der Welt", SKIN);
        worldNameField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                worldNameField.setText("");
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
                    infoLabel.setText("Bitte wähle eine Welt aus.");
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
                if (worldNameField.getText().isEmpty()) {
                    infoLabel.setText("Bitte gib den Namen der zu erstellenden Welt ein.");
                    return;
                }
                if (mapSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Karte aus.");
                    return;
                }
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.WORLD_CREATE, worldNameField.getText(), mapSelectBox.getSelected());
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
                    infoLabel.setText("Bitte wähle eine Welt aus.");
                    return;
                }
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.WORLD_DELETE, worldSelectBox.getSelected());
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

        profileSettingsButton = new TextButton("Konto verwalten.", SKIN);
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
    }

    @Override
    protected void setLayout() {

    }

    private void updateWorldList() {
        worldSelectBox.setItems(Chati.getInstance().getMenuScreen().getWorlds().toArray(new ContextEntry[0]));
    }
}
