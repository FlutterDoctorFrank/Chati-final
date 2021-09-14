package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import view2.userInterface.ChatiWindow;
import view2.userInterface.hud.ChatWindow;
import view2.userInterface.hud.HeadUpDisplay;

import java.util.Stack;

/**
 * Eine Klasse, welche einen Container für alle auf einem Bildschirm anzuzeigenden Inhalte darstellt und das verarbeiten
 * von Input auf diesen koordiniert.
 */
public class ChatiStage extends Stage {

    private final Stack<ChatiWindow> openWindows;

    /**
     * Erzeugt eine neue Instanz der ChatiStage.
     */
    public ChatiStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Chati.CHATI.getSpriteBatch());
        this.openWindows = new Stack<>();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!(getKeyboardFocus() instanceof TextField)) {
            if (KeyCommand.OPEN_CHAT.matches(keycode)) {
                HeadUpDisplay.getInstance().showChatWindow();
            }
            if (KeyCommand.OPEN_COMMUNICATION_MENU.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isCommunicationWindowOpen()) {
                    HeadUpDisplay.getInstance().closeCommunicationWindow();
                } else {
                    HeadUpDisplay.getInstance().openCommunicationWindow();
                }
            }
            if (KeyCommand.TOGGLE_MICROPHONE.matches(keycode)) {
                HeadUpDisplay.getInstance().toggleMicrophone();
            }
            if (KeyCommand.TOGGLE_SOUND.matches(keycode)) {
                HeadUpDisplay.getInstance().toggleSound();
            }
            if (KeyCommand.OPEN_USER_MENU.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isUserMenuOpen()) {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                } else {
                    HeadUpDisplay.getInstance().openUserMenu();
                }
            }
            if (KeyCommand.OPEN_NOTIFICATION_MENU.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isNotificationMenuOpen()) {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                } else {
                    HeadUpDisplay.getInstance().openNotificationMenu();
                }
            }
            if (KeyCommand.OPEN_SETTINGS_MENU.matches(keycode)) {
                if (HeadUpDisplay.getInstance().isSettingsMenuOpen()) {
                    HeadUpDisplay.getInstance().closeCurrentMenu();
                } else {
                    HeadUpDisplay.getInstance().openSettingsMenu();
                }
            }
        }

        if (KeyCommand.CLOSE.matches(keycode)) {
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

    /**
     * Fügt ein geöffnetes Fenster in den Stack aller geöffneter Fenster ein und zeigt es an.
     * @param window Neu geöffnetes Fenster.
     */
    public void openWindow(ChatiWindow window) {
        openWindows.push(window);
        addActor(window);
    }

    /**
     * Entfernt ein Fenster aus dem Stack aller geöffneter Fenster und lässt es nicht mehr anzeigen.
     * @param window Zu schließendes Fenster.
     */
    public void closeWindow(ChatiWindow window) {
        if (openWindows.remove(window)) {
            window.remove();
        }
    }
}
