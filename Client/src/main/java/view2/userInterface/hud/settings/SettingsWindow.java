package view2.userInterface.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import model.role.Permission;
import model.user.IInternUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.hud.HudMenuWindow;
import view2.userInterface.menu.LoginTable;
import view2.userInterface.menu.StartTable;

/**
 * Eine Klasse, welche das Einstellungsmenü des HeadUpDisplay repräsentiert.
 */
public class SettingsWindow extends HudMenuWindow {

    private final ChatiTextButton administratorManageMenuButton;
    private final ChatiTextButton leaveWorldButton;
    private final ChatiTextButton logoutButton;

    /**
     * Erzeugt eine neue Instanz des SettingsWindow.
     */
    public SettingsWindow() {
        super("window.title.settings");

        IInternUserView internUser = Chati.CHATI.getInternUser();

        ChatiTextButton informationButton = new ChatiTextButton("menu.button.information", true);
        informationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new InformationWindow().open();
            }
        });

        ChatiTextButton languageSelectMenuButton = new ChatiTextButton("menu.button.select-language", true);
        languageSelectMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new LanguageSelectWindow().open();
            }
        });

        ChatiTextButton volumeChangeMenuButton = new ChatiTextButton("menu.button.sound-settings", true);
        volumeChangeMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new SoundConfigurationWindow().open();
            }
        });

        ChatiTextButton worldSettingsButton = new ChatiTextButton("menu.button.world-settings", true);
        worldSettingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new WorldConfigurationWindow().open();
            }
        });

        administratorManageMenuButton = new ChatiTextButton("menu.button.manage-administrator", true);
        if (internUser == null || !internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR)) {
            disableButton(administratorManageMenuButton);
        }
        administratorManageMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new AdministratorManageWindow().open();
            }
        });

        leaveWorldButton = new ChatiTextButton("menu.button.world-leave", true);
        if (internUser == null || internUser.getCurrentWorld() == null) {
            disableButton(leaveWorldButton);
        }
        leaveWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
                if (internUser == null || internUser.getCurrentWorld() == null) {
                    return;
                }
                Chati.CHATI.send(ServerSender.SendAction.WORLD_ACTION, internUser.getCurrentWorld().getContextId(), false);
                Chati.CHATI.setScreen(Chati.CHATI.getMenuScreen());
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });

        logoutButton = new ChatiTextButton("menu.button.logout", true);
        if (internUser == null) {
            disableButton(logoutButton);
        }
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGOUT, "", false);
                if (!Chati.CHATI.getScreen().equals(Chati.CHATI.getMenuScreen())) {
                    Chati.CHATI.setScreen(Chati.CHATI.getMenuScreen());
                }
                Chati.CHATI.getMenuScreen().setMenuTable(new LoginTable());
            }
        });

        ChatiTextButton quitButton = new ChatiTextButton("menu.button.exit", true);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Gdx.app.exit();
            }
        });

        // Layout
        defaults().pad(SPACING / 2, 3 * SPACING / 4, SPACING / 2, 3 * SPACING / 4).grow();
        add(informationButton).row();
        add(languageSelectMenuButton).row();
        add(volumeChangeMenuButton).row();
        add(worldSettingsButton).row();
        add(administratorManageMenuButton).row();
        add(leaveWorldButton).row();
        add(logoutButton).row();
        add(quitButton);

        // Translatable register
        translates.add(informationButton);
        translates.add(languageSelectMenuButton);
        translates.add(volumeChangeMenuButton);
        translates.add(worldSettingsButton);
        translates.add(administratorManageMenuButton);
        translates.add(leaveWorldButton);
        translates.add(logoutButton);
        translates.add(quitButton);
        translates.trimToSize();
    }

    @Override
    public void act(final float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            if (administratorManageMenuButton.isTouchable()
                    && (internUser == null || !internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR))) {
                disableButton(administratorManageMenuButton);
            } else if (!administratorManageMenuButton.isTouchable() && internUser != null
                    && internUser.hasPermission(Permission.ASSIGN_ADMINISTRATOR)) {
                enableButton(administratorManageMenuButton);
            }
            if (!leaveWorldButton.isTouchable() && internUser != null && internUser.isInCurrentWorld()) {
                enableButton(leaveWorldButton);
            } else if (leaveWorldButton.isTouchable() && (internUser == null || !internUser.isInCurrentWorld())) {
                disableButton(leaveWorldButton);
            }
            if (!logoutButton.isTouchable() && internUser != null) {
                enableButton(logoutButton);
            } else if (logoutButton.isTouchable() && internUser == null) {
                disableButton(logoutButton);
            }
        }
        super.act(delta);
    }

    @Override
    protected void enableButton(@NotNull final ChatiTextButton button) {
        button.setTouchable(Touchable.enabled);
        button.getLabel().setColor(Color.WHITE);
    }

    @Override
    protected void disableButton(@NotNull final ChatiTextButton button) {
        button.setTouchable(Touchable.disabled);
        button.getLabel().setColor(Color.DARK_GRAY);
    }
}
