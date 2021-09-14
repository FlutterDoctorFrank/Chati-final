package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import view2.Chati;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextFields repräsentiert.
 */
public class ChatiTextField extends TextField {

    private static final int TEXT_FIELD_MAX_LENGTH = 32;
    private static final int PASSWORD_FIELD_MAX_LENGTH = 48;

    /**
     * Erzeugt eine neue Instanz der ChatiTextArea.
     * @param messageText Anzuzeigender Nachricht wenn der Text leer ist.
     * @param passwordField Information, ob dieses TextFields für Passwörter genutzt wird.
     */
    public ChatiTextField(String messageText, boolean passwordField) {
        super("", Chati.CHATI.getSkin());
        setMessageText(messageText);

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
     * Leert den Text und entfernt den Fokus von der TextArea.
     */
    public void reset() {
        setText("");
        if (hasKeyboardFocus() && getStage() != null) {
            getStage().unfocus(this);
        }
    }
}
