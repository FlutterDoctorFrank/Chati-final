package view2.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import view2.Chati;

/**
 * Eine abstrakte Klasse, welche ein Menü im Menübildschirm repräsentiert.
 */
public abstract class MenuTable extends Table {

    protected static final int MAX_LIST_COUNT = 10;
    protected static final float ROW_WIDTH = 600;
    protected static final float ROW_HEIGHT = 60;
    protected static final float SPACING = 15;

    protected final Label infoLabel;

    /**
     * Erzeugt eine neue Instanz des MenuTable.
     */
    public MenuTable() {
        this.infoLabel = new Label("", Chati.CHATI.getSkin());
        infoLabel.setAlignment(Align.center, Align.center);
        infoLabel.setWrap(true);

        setFillParent(true);
    }

    /**
     * Zeigt eine Nachricht auf dem Info-Label an.
     * @param message Anzuzeigende Nachricht.
     */
    public void showMessage(String message) {
        infoLabel.setText(message);
    }

    /**
     * Setzt alle Textfelder in diesem Menü zurück.
     */
    public abstract void resetTextFields();
}
