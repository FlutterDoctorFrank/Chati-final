package view2.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import view2.Chati;
import view2.component.hud.ChatWindow;
import view2.component.hud.HeadUpDisplay;

public class MenuStage extends Stage {

    public MenuStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Chati.SPRITE_BATCH);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!(getKeyboardFocus() instanceof TextField)) {
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_CHAT && HeadUpDisplay.getInstance().isChatEnabled()
                    && !ChatWindow.getInstance().isVisible()) {
                HeadUpDisplay.getInstance().showChatWindow();
            }
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_USER_LIST) {
                if (!HeadUpDisplay.getInstance().isUserMenuOpen()) {
                    HeadUpDisplay.getInstance().openUserMenu();
                } else {
                    HeadUpDisplay.getInstance().closeMenus();
                }
            }
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_NOTIFICATION) {
                if (!HeadUpDisplay.getInstance().isNotificationMenuOpen()) {
                    HeadUpDisplay.getInstance().openNotificationMenu();
                } else {
                    HeadUpDisplay.getInstance().closeMenus();
                }
            }
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_SETTINGS) {
                if (!HeadUpDisplay.getInstance().isSettingsMenuOpen()) {
                    HeadUpDisplay.getInstance().openSettingsMenu();
                } else {
                    HeadUpDisplay.getInstance().closeMenus();
                }
            }

        }
        if (KeyAction.getAction(keycode) == KeyAction.CLOSE) {
            setKeyboardFocus(null);
            if (HeadUpDisplay.getInstance().isChatEnabled() && ChatWindow.getInstance().isVisible()) {
                HeadUpDisplay.getInstance().hideChatWindow();
            } else if (HeadUpDisplay.getInstance().isUserMenuOpen()
                    || HeadUpDisplay.getInstance().isNotificationMenuOpen()
                    || HeadUpDisplay.getInstance().isSettingsMenuOpen()) {
                HeadUpDisplay.getInstance().closeMenus();
            } else {
                HeadUpDisplay.getInstance().openSettingsMenu();
            }
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (HeadUpDisplay.getInstance().isChatEnabled()) {
            ChatWindow chatWindow = ChatWindow.getInstance();
            if (screenX < chatWindow.getX() || screenX > chatWindow.getX() + chatWindow.getWidth()
                    || Gdx.graphics.getHeight() - screenY < chatWindow.getY()
                    || Gdx.graphics.getHeight() - screenY > chatWindow.getY() + chatWindow.getHeight()) {
                if (chatWindow.getStage() != null) {
                    chatWindow.getStage().unfocus(chatWindow);
                }
            }
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
