package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import view2.Chati;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextButtons repräsentiert.
 */
public class ChatiTextButton extends TextButton {

    /**
     * Erzeugt eine neue Instanz eines ChatiTextButton.
     * @param text Anzuzeigender Text.
     * @param disabled Information, ob dieser Button deaktiviert wird. Ein deaktivierter Button ändert nicht seinen
     * Zustand, wenn er gedrückt wird.
     */
    public ChatiTextButton(String text, boolean disabled) {
        super(text, Chati.CHATI.getSkin());
        setDisabled(disabled);
    }
}
