package view.userInterface.actor;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import model.Resource;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.ChatiLocalization.Translatable;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextButtons repräsentiert.
 */
public class ChatiTextButton extends TextButton implements Translatable {

    private final String messageKey;

    /**
     * Erzeugt eine neue Instanz eines ChatiTextButton.
     * @param resource Die Ressource, die für den Namen des Buttons verwendet wird.
     * @param disabled Information, ob dieser Button deaktiviert wird. Ein deaktivierter Button ändert nicht seinen
     * Zustand, wenn er gedrückt wird.
     */
    public ChatiTextButton(@NotNull final Resource resource, final boolean disabled) {
        super(resource.getName(), Chati.CHATI.getSkin());
        this.messageKey = null;
        this.setDisabled(disabled);
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiTextButton.
     * @param messageKey Kennung des anzuzeigenden Texts.
     * @param disabled Information, ob dieser Button deaktiviert wird. Ein deaktivierter Button ändert nicht seinen
     * Zustand, wenn er gedrückt wird.
     */
    public ChatiTextButton(@NotNull final String messageKey, final boolean disabled) {
        super(messageKey, Chati.CHATI.getSkin());
        this.messageKey = messageKey;
        setDisabled(disabled);
        translate();
    }

    @Override
    public void translate() {
        if (messageKey != null && !messageKey.isBlank()) {
            setText(Chati.CHATI.getLocalization().translate(messageKey));
        }
    }
}
