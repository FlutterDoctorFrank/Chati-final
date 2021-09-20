package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.ChatiLocalization.Translatable;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextAreas repräsentiert.
 */
public class ChatiTextArea extends TextArea implements Translatable {

    private static final int TEXT_AREA_MAX_LENGTH = 512;

    private final String messageTextKey;

    /**
     * Erzeugt eine neue Instanz der ChatiTextArea.
     * @param messageTextKey Kennung der anzuzeigenden Nachricht, wenn der Text leer ist.
     * @param writeEnters Information, ob das Drücken der Enter-Taste bei nicht leerem Text in der TextArea einen
     * Zeilenumbruch verursacht.
     */
    public ChatiTextArea(@NotNull final String messageTextKey, final boolean writeEnters) {
        super("", Chati.CHATI.getSkin());
        this.messageTextKey = messageTextKey;
        this.writeEnters = false;
        translate();

        setMaxLength(TEXT_AREA_MAX_LENGTH);
        addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                ChatiTextArea.this.writeEnters = writeEnters && !getText().isBlank();
            }
        });

        setTextFieldFilter((textField, c) -> !isBlank() || !Character.toString(c).matches("\\s"));
    }

    /**
     * Gibt zurück, ob der Text leer ist oder nur Leerzeichen enthält.
     * @return true, wenn der Text leer ist oder nur Leerzeichen enthält, sonst false.
     */
    public boolean isBlank() {
        return getText().isBlank();
    }

    /**
     * Leert den Text und entfernt den Fokus von der TextArea.
     */
    public void reset() {
        setText("");
        if (hasKeyboardFocus() && getStage() != null) {
            getStage().unfocus(this);
        }
        translate();
    }

    @Override
    public void translate() {
        setMessageText(Chati.CHATI.getLocalization().translate(messageTextKey));
    }
}
