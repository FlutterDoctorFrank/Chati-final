package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Chati;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten Windows repräsentiert.
 */
public abstract class ChatiWindow extends Window {

    protected static final float ROW_HEIGHT = 60;
    protected static final float SPACING = 15;

    /**
     * Erzeugt eine neue Instanz eines ChatiWindow.
     * @param title Anzuzeigender Titel.
     */
    protected ChatiWindow(String title) {
        super(title, Chati.CHATI.getSkin());

        ChatiTextButton closeButton = new ChatiTextButton("X", true);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    /**
     * Veranlasst das Öffnen des Fensters.
     */
    public void open() {
        Chati.CHATI.getScreen().getStage().openWindow(this);
    }

    /**
     * Veranlasst das Schließen des Fensters.
     */
    public void close() {
        Chati.CHATI.getScreen().getStage().closeWindow(this);
    }
}