package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class ChatiTable extends Table {

    protected ChatiTable() {
        setFillParent(true);
    }

    protected abstract void create();

    protected abstract void setLayout();
}
