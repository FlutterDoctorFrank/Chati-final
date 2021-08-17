package view2.component;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import view2.component.hud.ChatWindow;
import view2.component.hud.HeadUpDisplay;

public class MenuStage extends Stage {

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
        } else {
            if (KeyAction.getAction(keycode) == KeyAction.CLOSE) {
                setKeyboardFocus(null);
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
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return super.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return super.scrolled(amountX, amountY);
    }
}
