package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import view2.Chati;

public class ChatiTextField extends TextField {

    private static final int TEXT_FIELD_MAX_LENGTH = 32;
    private static final int PASSWORD_FIELD_MAX_LENGTH = 48;

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

        setTextFieldFilter((textField, c) -> !getText().isBlank() || !Character.toString(c).matches("\\s"));
    }

    public boolean isBlank() {
        return getText().isBlank();
    }

    public void reset() {
        setText("");
        if (hasKeyboardFocus() && getStage() != null) {
            getStage().unfocus(this);
        }
    }
}
