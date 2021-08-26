package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.context.ContextID;
import model.role.Permission;
import model.user.IInternUserView;
import view2.Assets;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;
import view2.component.hud.HudMenuWindow;
import view2.component.menu.table.LoginTable;
import view2.component.menu.table.StartTable;

public class SettingsWindow extends HudMenuWindow {

    private static final float TOP_SPACING = 7.5f;
    private static final float HORIZONTAL_SPACING = 20;
    private static final float BOTTOM_SPACING  = 7.5f;

    private TextButton languageSelectMenuButton;
    private TextButton volumeChangeMenuButton;
    private TextButton worldSettingsButton;
    private TextButton administratorManageMenuButton;
    private TextButton leaveWorldButton;
    private TextButton logoutButton;
    private TextButton closeApplicationButton;

    public SettingsWindow() {
        super("Einstellungen");
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            if (!administratorManageMenuButton.isDisabled()
                    && (internUser == null || !internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR))) {
                disableButton(administratorManageMenuButton);
            } else if (administratorManageMenuButton.isDisabled() && internUser != null
                    && internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR)) {
                enableButton(administratorManageMenuButton);
            }
            if (leaveWorldButton.isDisabled() && internUser != null && internUser.isInCurrentWorld()) {
                enableButton(leaveWorldButton);
            } else if (!leaveWorldButton.isDisabled() && (internUser == null || !internUser.isInCurrentWorld())) {
                disableButton(leaveWorldButton);
            }
            if (logoutButton.isDisabled() && internUser != null) {
                enableButton(logoutButton);
            } else if (!logoutButton.isDisabled() && internUser == null) {
                disableButton(logoutButton);
            }
        }
        super.act(delta);
    }

    @Override
    protected void create() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();

        languageSelectMenuButton = new TextButton("Sprache wählen", Assets.SKIN);
        languageSelectMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getScreen().getStage().openWindow(new LanguageSelectWindow());
            }
        });

        volumeChangeMenuButton = new TextButton("Lautstärke anpassen", Assets.SKIN);
        volumeChangeMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getScreen().getStage().openWindow(new VolumeChangeWindow());
            }
        });

        worldSettingsButton = new TextButton("Welteinstellungen", Assets.SKIN);
        worldSettingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getScreen().getStage().openWindow(new WorldSettingsTable());
            }
        });

        administratorManageMenuButton = new TextButton("Administratoren verwalten", Assets.getNewSkin());
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
                Chati.CHATI.getScreen().getStage().openWindow(new AdministratorManageWindow());
            }
        });

        leaveWorldButton = new TextButton("Welt verlassen", Assets.getNewSkin());
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
                ContextID currentWorldID = Chati.CHATI.getUserManager()
                        .getInternUserView().getCurrentWorld().getContextId();
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.WORLD_ACTION, currentWorldID, false);
                Chati.CHATI.setScreen(Chati.CHATI.getMenuScreen());
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });

        logoutButton = new TextButton("Abmelden", Assets.getNewSkin());
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
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.PROFILE_LOGOUT, "", false);
                if (!Chati.CHATI.getScreen().equals(Chati.CHATI.getMenuScreen())) {
                    Chati.CHATI.setScreen(Chati.CHATI.getMenuScreen());
                }
                Chati.CHATI.getMenuScreen().setMenuTable(new LoginTable());
            }
        });

        closeApplicationButton = new TextButton("Beenden", Assets.SKIN);
        closeApplicationButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                System.exit(0);
            }
        });
    }

    @Override
    protected void setLayout() {
        defaults().pad(TOP_SPACING, HORIZONTAL_SPACING, BOTTOM_SPACING, HORIZONTAL_SPACING).grow();
        add(languageSelectMenuButton).row();
        add(volumeChangeMenuButton).row();
        add(worldSettingsButton).row();
        add(administratorManageMenuButton).row();
        add(leaveWorldButton).row();
        add(logoutButton).row();
        add(closeApplicationButton);
    }
}
