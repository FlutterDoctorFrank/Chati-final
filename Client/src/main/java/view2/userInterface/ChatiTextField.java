package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.Localization;
import view2.Localization.Translatable;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextFields repräsentiert.
 */
public class ChatiTextField extends TextField implements Translatable {

    private static final int TEXT_FIELD_MAX_LENGTH = 32;
    private static final int PASSWORD_FIELD_MAX_LENGTH = 48;

    private final String messageTextKey;

    /**
     * Erzeugt eine neue Instanz der ChatiTextArea.
     * @param messageTextKey Kennung der anzuzeigenden Nachricht, wenn der Text leer ist.
     * @param passwordField Information, ob dieses TextFields für Passwörter genutzt wird.
     */
    public ChatiTextField(@NotNull final String messageTextKey, final boolean passwordField) {
        super("", Chati.CHATI.getSkin());
        this.messageTextKey = messageTextKey;
        this.translate();

        if (passwordField) {
            setMaxLength(PASSWORD_FIELD_MAX_LENGTH);
            setPasswordMode(true);
            setPasswordCharacter('*');
        } else {
            setMaxLength(TEXT_FIELD_MAX_LENGTH);
        }

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
     * Leert den Text und entfernt den Fokus von des TextField.
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
        setMessageText(Localization.translate(messageTextKey));
    }
}
