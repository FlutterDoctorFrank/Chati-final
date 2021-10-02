package view2.userInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.ChatiLocalization.Translatable;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextFields repräsentiert.
 */
public class ChatiTextField extends TextField implements Translatable {

    private final String messageTextKey;

    /**
     * Erzeugt eine neue Instanz der ChatiTextArea.
     * @param messageTextKey Kennung der anzuzeigenden Nachricht, wenn der Text leer ist.
     * @param textFieldType Typ des Textfeldes.
     */
    public ChatiTextField(@NotNull final String messageTextKey, @NotNull final TextFieldType textFieldType) {
        super("", Chati.CHATI.getSkin());
        this.messageTextKey = messageTextKey;
        this.translate();

        setMaxLength(textFieldType.getMaxFieldLength());
        if (textFieldType == TextFieldType.PASSWORD) {
            setPasswordMode(true);
            setPasswordCharacter('*');
        }
        setTextFieldFilter((textField, c) -> !isBlank() || !Character.toString(c).matches("\\s"));

        addListener(new InputListener() {
            @Override
            public void enter(@NotNull InputEvent event, final float x, final float y, final int pointer,
                             @Nullable final Actor fromActor) {
                if (pointer == -1) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Ibeam);
                }
            }
            @Override
            public void exit(@NotNull InputEvent event, final float x, final float y, final int pointer,
                             @Nullable final Actor fromActor) {
                if (pointer == -1) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }
        });
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
        setMessageText(Chati.CHATI.getLocalization().translate(messageTextKey));
    }

    /**
     * Ein Enum, welches die maximale Länge des Textes festlegt, die in ein Textfeld von diesem Typ eingegeben werden
     * kann.
     */
    public enum TextFieldType {

        /** Repräsentiert ein Standard-Textfeld. */
        STANDARD(32),

        /** Repräsentiert ein Passwort-Textfeld. */
        PASSWORD(48),

        /** Repräsentiert ein Dateinamen-Textfeld. */
        FILE(128);

        private final int maxFieldLength;

        /**
         * Erzeugt eine neue Instanz eines TextFieldType
         * @param maxFieldLength Maximale Länge des Textes, die in ein Textfeld von diesem Typ eingegeben werden kann.
         */
        TextFieldType(final int maxFieldLength) {
            this.maxFieldLength = maxFieldLength;
        }

        /**
         * Gibt die maximale Länge des Textes zurück, die in ein Textfeld von diesem Typ eingegeben werden kann.
         * @return Maximale Länge.
         */
        public int getMaxFieldLength() {
            return maxFieldLength;
        }
    }
}
