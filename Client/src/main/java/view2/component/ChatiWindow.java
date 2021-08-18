package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import view2.Chati;

public abstract class ChatiWindow extends Window {

    protected ChatiWindow(String title) {
        super(title, Chati.SKIN);
    }

    protected abstract void create();

    protected abstract void setLayout();
}