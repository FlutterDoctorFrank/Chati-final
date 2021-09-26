package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.jetbrains.annotations.NotNull;
import view2.userInterface.ChatiWindow;
import view2.userInterface.hud.ChatWindow;
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
        super(new ScreenViewport(), Chati.CHATI.getSpriteBatch());
        this.openWindows = new Stack<>();
    }

    @Override
    public boolean keyDown(final int keycode) {
        if (!(getKeyboardFocus() instanceof TextField)) {
            if (KeyCommand.OPEN_CHAT.matches(keycode)) {
                Chati.CHATI.getHeadUpDisplay().showChatWindow();
            }
            if (KeyCommand.OPEN_COMMUNICATION_MENU.matches(keycode)) {
                if (Chati.CHATI.getHeadUpDisplay().isCommunicationWindowOpen()) {
                    Chati.CHATI.getHeadUpDisplay().closeCommunicationWindow();
                } else {
                    Chati.CHATI.getHeadUpDisplay().openCommunicationWindow();
                }
            }
            if (KeyCommand.TOGGLE_MICROPHONE.matches(keycode)) {
                Chati.CHATI.getHeadUpDisplay().toggleMicrophone();
            }
            if (KeyCommand.TOGGLE_SOUND.matches(keycode)) {
                Chati.CHATI.getHeadUpDisplay().toggleSound();
            }
            if (KeyCommand.OPEN_USER_MENU.matches(keycode)) {
                if (Chati.CHATI.getHeadUpDisplay().isUserMenuOpen()) {
                    Chati.CHATI.getHeadUpDisplay().closeCurrentMenu();
                } else {
                    Chati.CHATI.getHeadUpDisplay().openUserMenu();
                }
            }
            if (KeyCommand.OPEN_NOTIFICATION_MENU.matches(keycode)) {
                if (Chati.CHATI.getHeadUpDisplay().isNotificationMenuOpen()) {
                    Chati.CHATI.getHeadUpDisplay().closeCurrentMenu();
                } else {
                    Chati.CHATI.getHeadUpDisplay().openNotificationMenu();
                }
            }
            if (KeyCommand.OPEN_SETTINGS_MENU.matches(keycode)) {
                if (Chati.CHATI.getHeadUpDisplay().isSettingsMenuOpen()) {
                    Chati.CHATI.getHeadUpDisplay().closeCurrentMenu();
                } else {
                    Chati.CHATI.getHeadUpDisplay().openSettingsMenu();
                }
            }
        }

        if (KeyCommand.CLOSE.matches(keycode)) {
            setKeyboardFocus(null);
            if (!openWindows.isEmpty()) {
                openWindows.peek().close();
            } else if (Chati.CHATI.getHeadUpDisplay().isChatOpen()) {
                Chati.CHATI.getHeadUpDisplay().hideChatWindow();
            } else {
                Chati.CHATI.getHeadUpDisplay().openSettingsMenu();
            }
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        if (Chati.CHATI.getHeadUpDisplay().isChatOpen()) {
            ChatWindow chatWindow = Chati.CHATI.getHeadUpDisplay().getChatWindow();
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
     * Gibt die Fenster zurück, die gerade geöffnet sind.
     * @return die geöffneten Fenster.
     */
    public @NotNull Stack<ChatiWindow> getOpenWindows() {
        return this.openWindows;
    }

    /**
     * Fügt ein geöffnetes Fenster in den Stack aller geöffneter Fenster ein und zeigt es an.
     * @param window Neu geöffnetes Fenster.
     */
    public void openWindow(@NotNull final ChatiWindow window) {
        openWindows.push(window);
        addActor(window);
        window.focus();
    }

    /**
     * Entfernt ein Fenster aus dem Stack aller geöffneter Fenster und lässt es nicht mehr anzeigen.
     * @param window Zu schließendes Fenster.
     */
    public void closeWindow(@NotNull final ChatiWindow window) {
        if (openWindows.remove(window)) {
            window.remove();
            if (!openWindows.isEmpty()) {
                openWindows.peek().focus();
            }
        }
    }
}
