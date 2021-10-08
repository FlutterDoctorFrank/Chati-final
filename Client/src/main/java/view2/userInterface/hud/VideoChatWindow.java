package view2.userInterface.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import view2.Chati;
import view2.ChatiLocalization.Translatable;

/**
 * Eine Klasse, welche das Videochat-Fenster der Anwendung repräsentiert.
 */
public class VideoChatWindow extends Window implements Translatable {

    /**
     * Erzeugt eine neue Instanz des VideoChatWindow.
     */
    public VideoChatWindow() {
        super("window.title.video-chat", Chati.CHATI.getSkin());
    }

    @Override
    public void translate() {
        getTitleLabel().setText(Chati.CHATI.getLocalization().translate("window.title.video-chat"));
    }
}
