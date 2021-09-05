package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.role.Permission;
import model.user.IInternUserView;
import view2.Chati;
import view2.component.ChatiTextButton;
import view2.component.hud.HudMenuWindow;
import view2.component.menu.table.LoginTable;
import view2.component.menu.table.StartTable;

public class SettingsWindow extends HudMenuWindow {

    private static final float TOP_SPACING = 7.5f;
    private static final float HORIZONTAL_SPACING = 20;
    private static final float BOTTOM_SPACING  = 7.5f;

    private final ChatiTextButton administratorManageMenuButton;
    private final ChatiTextButton leaveWorldButton;
    private final ChatiTextButton logoutButton;

    public SettingsWindow() {
        super("Einstellungen");

        IInternUserView internUser = Chati.CHATI.getInternUser();

        ChatiTextButton languageSelectMenuButton = new ChatiTextButton("Sprache wählen", true);
        languageSelectMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new LanguageSelectWindow().open();
            }
        });

        ChatiTextButton volumeChangeMenuButton = new ChatiTextButton("Lautstärke anpassen", true);
        volumeChangeMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new VolumeChangeWindow().open();
            }
        });

        ChatiTextButton worldSettingsButton = new ChatiTextButton("Welteinstellungen", true);
        worldSettingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                new WorldSettingsWindow().open();
            }
        });

        administratorManageMenuButton = new ChatiTextButton("Administratoren verwalten", true);
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
                new AdministratorManageWindow().open();
            }
        });

        leaveWorldButton = new ChatiTextButton("Welt verlassen", true);
        if (internUser == null || internUser.getCurrentWorld() == null) {
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
                IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
                if (internUser == null || internUser.getCurrentWorld() == null) {
                    return;
                }
                Chati.CHATI.send(ServerSender.SendAction.WORLD_ACTION, internUser.getCurrentWorld().getContextId(), false);
                Chati.CHATI.setScreen(Chati.CHATI.getMenuScreen());
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });

        logoutButton = new ChatiTextButton("Abmelden", true);
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
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGOUT, "", false);
                if (!Chati.CHATI.getScreen().equals(Chati.CHATI.getMenuScreen())) {
                    Chati.CHATI.setScreen(Chati.CHATI.getMenuScreen());
                }
                Chati.CHATI.getMenuScreen().setMenuTable(new LoginTable());
            }
        });

        ChatiTextButton quitButton = new ChatiTextButton("Beenden", true);
        quitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });

        // Layout
        defaults().pad(TOP_SPACING, HORIZONTAL_SPACING, BOTTOM_SPACING, HORIZONTAL_SPACING).grow();
        add(languageSelectMenuButton).row();
        add(volumeChangeMenuButton).row();
        add(worldSettingsButton).row();
        add(administratorManageMenuButton).row();
        add(leaveWorldButton).row();
        add(logoutButton).row();
        add(quitButton);
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
    protected void enableButton(ChatiTextButton button) {
        button.setTouchable(Touchable.enabled);
        button.getLabel().setColor(Color.WHITE);
    }

    @Override
    protected void disableButton(ChatiTextButton button) {
        button.setTouchable(Touchable.disabled);
        button.getLabel().setColor(Color.DARK_GRAY);
    }
}
