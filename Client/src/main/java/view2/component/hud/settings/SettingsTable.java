package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.role.Permission;
import model.user.IInternUserView;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;
import view2.component.hud.HudMenuTable;
import view2.component.menu.LoginTable;
import view2.component.menu.StartTable;

public class SettingsTable extends HudMenuTable {

    private static final float TOP_SPACING = 20;
    private static final float VERTICAL_SPACING = 20;
    private static final float BOTTOM_SPACING  = 10;

    private TextButton languageSelectMenuButton;
    private TextButton volumeChangeMenuButton;
    private TextButton administratorManageMenuButton;
    private TextButton leaveWorldButton;
    private TextButton logoutButton;
    private TextButton closeApplicationButton;

    public SettingsTable() {
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.getInstance().isUserInfoChanged()) {
            IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
            if (!administratorManageMenuButton.isDisabled()
                    && (internUser == null || !internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR))) {
                disableButton(administratorManageMenuButton);
            } else if (administratorManageMenuButton.isDisabled() && internUser != null
                    && internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR)) {
                enableButton(administratorManageMenuButton);
            }
            if (leaveWorldButton.isDisabled()
                    && internUser != null && internUser.isInCurrentWorld() && internUser.getCurrentWorld() != null) {
                enableButton(leaveWorldButton);
            }
            if (logoutButton.isDisabled() && internUser != null) {
                enableButton(logoutButton);
            }
        }
        super.act(delta);
    }


    @Override
    protected void create() {
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();

        languageSelectMenuButton = new TextButton("Sprache wählen", Chati.SKIN);
        languageSelectMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getStage().addActor(new LanguageSelectWindow());
            }
        });

        volumeChangeMenuButton = new TextButton("Lautstärke anpassen", Chati.SKIN);
        volumeChangeMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getStage().addActor(new VolumeChangeWindow());
            }
        });

        Skin administratorManageMenuButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        administratorManageMenuButton = new TextButton("Administratoren verwalten", administratorManageMenuButtonSkin);
        if (internUser == null || !internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR)) {
            disableButton(administratorManageMenuButton);
        }
        administratorManageMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getStage().addActor(new AdministratorManageWindow());
            }
        });

        Skin leaveWorldButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        leaveWorldButton = new TextButton("Welt verlassen", leaveWorldButtonSkin);
        if (internUser == null || !internUser.isInCurrentWorld() || internUser.getCurrentWorld() == null) {
            disableButton(leaveWorldButton);
        }
        leaveWorldButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                disableButton(leaveWorldButton);
                ContextID currentWorldID = Chati.getInstance().getUserManager()
                        .getInternUserView().getCurrentWorld().getContextId();
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.WORLD_ACTION, currentWorldID, false);
                Chati.getInstance().setScreen(Chati.getInstance().getMenuScreen());
                Chati.getInstance().getMenuScreen().setTable(new StartTable());
            }
        });

        Skin logoutButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        logoutButton = new TextButton("Abmelden", logoutButtonSkin);
        if (internUser == null) {
            disableButton(logoutButton);
        }
        logoutButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                disableButton(logoutButton);
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGOUT, "", false);
                if (!Chati.getInstance().getScreen().equals(Chati.getInstance().getMenuScreen())) {
                    Chati.getInstance().setScreen(Chati.getInstance().getMenuScreen());
                }
                Chati.getInstance().getMenuScreen().setTable(new LoginTable());
            }
        });

        closeApplicationButton = new TextButton("Beenden", Chati.SKIN);
        closeApplicationButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    protected void setLayout() {
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE);
        Window window = new Window("Einstellungen", Chati.SKIN);
        window.setMovable(false);
        window.top();

        window.defaults().fill().expand().pad(TOP_SPACING, VERTICAL_SPACING, BOTTOM_SPACING, VERTICAL_SPACING);
        window.add(languageSelectMenuButton).row();
        window.add(volumeChangeMenuButton).row();
        window.add(administratorManageMenuButton).row();
        window.add(leaveWorldButton).row();
        window.add(logoutButton).row();
        window.add(closeApplicationButton);

        add(window).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT);
    }
}
