package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class AbstractTable extends Table {

    protected AbstractTable() {
        setFillParent(true);
    }

    protected abstract void create();

    protected abstract void setLayout();
}
