package view2.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import view2.Chati;
import view2.component.hud.ChatWindow;
import view2.component.hud.HeadUpDisplay;

import java.util.Stack;

public class MenuStage extends Stage {

    private final Stack<AbstractWindow> openWindows;

    public MenuStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Chati.CHATI.getSpriteBatch());
        this.openWindows = new Stack<>();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!(getKeyboardFocus() instanceof TextField ) && openWindows.isEmpty()) {
            if (KeyAction.OPEN_CHAT.matches(keycode) && !HeadUpDisplay.getInstance().isChatOpen()) {
                HeadUpDisplay.getInstance().showChatWindow();
            }
            if (KeyAction.OPEN_USER_LIST.matches(keycode)) {
                if (!HeadUpDisplay.getInstance().isUserMenuOpen()) {
                    HeadUpDisplay.getInstance().openUserMenu();
                } else {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                }
            }
            if (KeyAction.OPEN_NOTIFICATION.matches(keycode)) {
                if (!HeadUpDisplay.getInstance().isNotificationMenuOpen()) {
                    HeadUpDisplay.getInstance().openNotificationMenu();
                } else {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                }
            }
            if (KeyAction.OPEN_SETTINGS.matches(keycode)) {
                if (!HeadUpDisplay.getInstance().isSettingsMenuOpen()) {
                    HeadUpDisplay.getInstance().openSettingsMenu();
                } else {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                }
            }

        }

        if (KeyAction.CLOSE.matches(keycode)) {
            setKeyboardFocus(null);
            if (!openWindows.isEmpty()) {
                openWindows.peek().close();
            } else if (HeadUpDisplay.getInstance().isChatOpen()) {
                HeadUpDisplay.getInstance().hideChatWindow();
            } else if (HeadUpDisplay.getInstance().isCommunicationWindowOpen()) {
                HeadUpDisplay.getInstance().closeCommunicableUserWindow();
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

    public void openWindow(AbstractWindow window) {
        openWindows.push(window);
        addActor(window);
    }

    public void closeWindow(AbstractWindow window) {
        if (openWindows.remove(window)) {
            window.remove();
        }
    }
}
