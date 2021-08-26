package view2.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.FitViewport;
import view2.Chati;
import view2.component.hud.ChatWindow;
import view2.component.hud.HeadUpDisplay;

import java.util.Stack;

public class MenuStage extends Stage {

    private final Stack<Window> openWindows;

    public MenuStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Chati.SPRITE_BATCH);
        this.openWindows = new Stack<>();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!(getKeyboardFocus() instanceof TextField)) {
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_CHAT && !HeadUpDisplay.getInstance().isChatOpen()) {
                HeadUpDisplay.getInstance().showChatWindow();
            }
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_USER_LIST) {
                if (!HeadUpDisplay.getInstance().isUserMenuOpen()) {
                    HeadUpDisplay.getInstance().openUserMenu();
                } else {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                }
            }
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_NOTIFICATION) {
                if (!HeadUpDisplay.getInstance().isNotificationMenuOpen()) {
                    HeadUpDisplay.getInstance().openNotificationMenu();
                } else {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                }
            }
            if (KeyAction.getAction(keycode) == KeyAction.OPEN_SETTINGS) {
                if (!HeadUpDisplay.getInstance().isSettingsMenuOpen()) {
                    HeadUpDisplay.getInstance().openSettingsMenu();
                } else {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                }
            }

        }

        if (KeyAction.getAction(keycode) == KeyAction.CLOSE) {
            setKeyboardFocus(null);
            if (!openWindows.isEmpty()) {
                closeWindow();
            } else if (HeadUpDisplay.getInstance().isChatOpen()) {
                HeadUpDisplay.getInstance().hideChatWindow();
            } else if (HeadUpDisplay.getInstance().isMenuOpen()) {
                HeadUpDisplay.getInstance().closeCurrentMenu();
            } else {
                HeadUpDisplay.getInstance().openSettingsMenu();
            }
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (HeadUpDisplay.getInstance().isChatOpen()) {
            ChatWindow chatWindow = HeadUpDisplay.getInstance().getChatWindow();
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

    public void openWindow(Window window) {
        openWindows.push(window);
        addActor(window);
    }

    public void closeWindow(Window window) {
        if (openWindows.remove(window)) {
            window.remove();
        }
    }

    public void closeWindow() {
        Window window = openWindows.pop();
        if (window != null) {
            window.remove();
        }
    }
}
