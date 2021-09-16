package view2.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import model.MessageBundle;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.ChatiLocalization;
import view2.ChatiLocalization.Translatable;
import view2.userInterface.ChatiLabel;
import java.util.ArrayList;

/**
 * Eine abstrakte Klasse, welche ein Menü im Menübildschirm repräsentiert.
 */
public abstract class MenuTable extends Table implements Translatable {

    protected static final int MAX_LIST_COUNT = 10;
    protected static final float ROW_WIDTH = 600;
    protected static final float ROW_HEIGHT = 60;
    protected static final float SPACING = 15;

    protected final ArrayList<Translatable> translates;
    protected final ChatiLabel infoLabel;

    /**
     * Erzeugt eine neue Instanz des MenuTable.
     */
    public MenuTable(@NotNull final String infoKey) {
        this.translates = new ArrayList<>();
        this.infoLabel = new ChatiLabel(infoKey);
        infoLabel.setAlignment(Align.center, Align.center);
        infoLabel.setWrap(true);

        setFillParent(true);
    }

    /**
     * Zeigt eine Nachricht auf dem Info-Label an.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
    public void showMessage(@NotNull final String messageKey) {
        infoLabel.setText(Chati.CHATI.getLocalization().translate(messageKey));
    }

    /**
     * Zeigt eine Nachricht auf dem Info-Label an.
     * @param messageBundle Kennung der anzuzeigenden Nachricht.
     */
    public void showMessage(@NotNull final MessageBundle messageBundle) {
        infoLabel.setText(Chati.CHATI.getLocalization().format(messageBundle.getMessageKey(), messageBundle.getArguments()));
    }

    @Override
    public void translate() {
        infoLabel.translate();
        translates.forEach(Translatable::translate);
    }

    /**
     * Setzt alle Textfelder in diesem Menü zurück.
     */
    public abstract void resetTextFields();
}
