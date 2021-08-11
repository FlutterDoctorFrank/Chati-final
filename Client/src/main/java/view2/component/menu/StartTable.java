package view2.component.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import controller.network.ServerSender;
import view2.Chati;

public class StartTable extends MenuTable {

    private Label worldSelectLabel;
    private SelectBox<ContextEntry> worldSelectBox;
    private TextButton joinWorldButton;
    private TextButton createWorldButton;
    private TextButton deleteWorldButton;
    private TextButton changeAvatarButton;
    private TextButton profileSettingsButton;
    private TextButton logoutButton;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateWorldList();
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void create() {
        super.create();
        infoLabel.setText("Bitte wähle eine Aktion aus!");

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);

        worldSelectLabel = new Label("Welt: ", style);
        worldSelectBox = new SelectBox<>(Chati.SKIN);

        joinWorldButton = new TextButton("Welt Beitreten", Chati.SKIN);
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

        createWorldButton = new TextButton("Welt erstellen", Chati.SKIN);
        createWorldButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new WorldCreateTable());
            }
        });

        deleteWorldButton = new TextButton("Welt löschen", Chati.SKIN);
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

        changeAvatarButton = new TextButton("Avatar ändern", Chati.SKIN);
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

        profileSettingsButton = new TextButton("Konto verwalten", Chati.SKIN);
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

        logoutButton = new TextButton("Abmelden", Chati.SKIN);
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
        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        Table worldSelectContainer = new Table();
        worldSelectContainer.setWidth(ROW_WIDTH);
        worldSelectContainer.defaults().space(VERTICAL_SPACING);
        add(worldSelectContainer).spaceBottom(HORIZONTAL_SPACING).row();
        worldSelectContainer.add(worldSelectLabel).width(ROW_WIDTH / 8f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        worldSelectContainer.add(worldSelectBox).width(7 * ROW_WIDTH / 8f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);

        Table worldButtonContainer = new Table();
        worldButtonContainer.setWidth(ROW_WIDTH);
        worldButtonContainer.defaults().space(VERTICAL_SPACING);
        add(worldButtonContainer).spaceBottom(HORIZONTAL_SPACING).row();
        worldButtonContainer.add(joinWorldButton).width(ROW_WIDTH / 3f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        worldButtonContainer.add(createWorldButton).width(ROW_WIDTH / 3f - (VERTICAL_SPACING)).height(ROW_HEIGHT);
        worldButtonContainer.add(deleteWorldButton).width(ROW_WIDTH / 3f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);

        Table settingsButtonContainer = new Table();
        settingsButtonContainer.setWidth(ROW_WIDTH);
        settingsButtonContainer.defaults().space(VERTICAL_SPACING);
        add(settingsButtonContainer).spaceBottom(HORIZONTAL_SPACING).row();
        settingsButtonContainer.add(changeAvatarButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        settingsButtonContainer.add(profileSettingsButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);

        add(logoutButton).width(ROW_WIDTH).center().height(ROW_HEIGHT);
    }

    @Override
    public void clearTextFields() {
    }

    private void updateWorldList() {
        worldSelectBox.setItems(Chati.getInstance().getMenuScreen().getWorlds().toArray(new ContextEntry[0]));
    }
}
