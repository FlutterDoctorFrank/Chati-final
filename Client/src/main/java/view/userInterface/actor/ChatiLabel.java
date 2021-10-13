package view.userInterface.actor;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import model.MessageBundle;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.ChatiLocalization.Translatable;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten übersetzbaren Labels repräsentiert.
 */
public class ChatiLabel extends Label implements Translatable {

    private final MessageBundle message;

    /**
     * Erzeugt eine neue Instanz eines ChatiLabels
     * @param messageKey Kennung des anzuzeigenden Texts.
     */
    public ChatiLabel(@NotNull final String messageKey) {
        super("", Chati.CHATI.getSkin());
        this.message = new MessageBundle(messageKey);
        translate();
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiLabels
     * @param messageKey Kennung des anzuzeigenden Texts.
     * @param arguments Zum messageKey zugehörige Argumente.
     */
    public ChatiLabel(@NotNull final String messageKey, @NotNull final Object... arguments) {
        this(new MessageBundle(messageKey, arguments));
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiLabels
     * @param messageBundle Kennung des anzuzeigenden Texts.
     */
    public ChatiLabel(@NotNull final MessageBundle messageBundle) {
        super("", Chati.CHATI.getSkin());
        this.message = messageBundle;
        translate();
    }

    @Override
    public void translate() {
        setText(Chati.CHATI.getLocalization().format(message.getMessageKey(), message.getArguments()));
    }
}
