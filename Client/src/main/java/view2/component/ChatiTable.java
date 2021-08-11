package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import view2.Chati;

public abstract class ChatiTable extends Table {

    protected ChatiTable() {
        super(Chati.SKIN);
        setFillParent(true);
    }

    protected abstract void create();

    protected abstract void setLayout();
}
