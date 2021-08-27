package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import view2.Assets;
import view2.Chati;

public abstract class AbstractWindow extends Window {

    protected AbstractWindow(String title) {
        super(title, Assets.SKIN);
    }

    protected abstract void create();

    protected abstract void setLayout();

    public void open() {
        Chati.CHATI.getScreen().getStage().openWindow(this);
    }

    public void close() {
        Chati.CHATI.getScreen().getStage().closeWindow(this);
    }
}