package view2.userInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import view2.Chati;
import view2.userInterface.hud.ChatWindow;
import view2.userInterface.hud.HeadUpDisplay;

import java.util.Stack;

public class ChatiStage extends Stage {

    private final Stack<ChatiWindow> openWindows;

    public ChatiStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Chati.CHATI.getSpriteBatch());
        this.openWindows = new Stack<>();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!(getKeyboardFocus() instanceof TextField)) {
            if (KeyAction.OPEN_CHAT.matches(keycode)) {
                HeadUpDisplay.getInstance().showChatWindow();
            }
            if (KeyAction.OPEN_COMMUNICATION_MENU.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isCommunicationWindowOpen()) {
                    HeadUpDisplay.getInstance().closeCommunicationWindow();
                } else {
                    HeadUpDisplay.getInstance().openCommunicationWindow();
                }
            }
            if (KeyAction.TOGGLE_MICROPHONE.matches(keycode)) {
                HeadUpDisplay.getInstance().toggleMicrophone();
            }
            if (KeyAction.TOGGLE_SOUND.matches(keycode)) {
                HeadUpDisplay.getInstance().toggleSound();
            }
            if (KeyAction.OPEN_USER_LIST.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isUserMenuOpen()) {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                } else {
                    HeadUpDisplay.getInstance().openUserMenu();
                }
            }
            if (KeyAction.OPEN_NOTIFICATION.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isNotificationMenuOpen()) {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                } else {
                    HeadUpDisplay.getInstance().openNotificationMenu();
                }
            }
            if (KeyAction.OPEN_SETTINGS.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isSettingsMenuOpen()) {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                } else {
                    HeadUpDisplay.getInstance().openSettingsMenu();
                }
            }
        }

        if (KeyAction.CLOSE.matches(keycode)) {
            setKeyboardFocus(null);
            if (!openWindows.isEmpty()) {
                openWindows.peek().close();
            } else if (HeadUpDisplay.getInstance().isChatOpen()) {
                HeadUpDisplay.getInstance().hideChatWindow();
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

    public void openWindow(ChatiWindow window) {
        openWindows.push(window);
        addActor(window);
    }

    public void closeWindow(ChatiWindow window) {
        if (openWindows.remove(window)) {
            window.remove();
        }
    }
}
