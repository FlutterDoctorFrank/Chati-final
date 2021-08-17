package view2.component.menu;

import com.badlogic.gdx.InputProcessor;
import view2.component.hud.ChatWindow;
import view2.component.hud.HeadUpDisplay;
import view2.component.KeyAction;

public class MenuInputProcessor implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
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
        if (KeyAction.getAction(keycode) == KeyAction.CLOSE) {
            HeadUpDisplay.getInstance().getStage().setKeyboardFocus(null);
            if (ChatWindow.getInstance().isVisible()) {
                HeadUpDisplay.getInstance().hideChatWindow();
            } else if (HeadUpDisplay.getInstance().isUserMenuOpen()
                    || HeadUpDisplay.getInstance().isNotificationMenuOpen()
                    || HeadUpDisplay.getInstance().isSettingsMenuOpen()) {
                HeadUpDisplay.getInstance().closeMenus();
            } else {
                HeadUpDisplay.getInstance().openSettingsMenu();
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
