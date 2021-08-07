package view2.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class ChatiTable extends Table {

    protected static final Skin SKIN = new Skin(Gdx.files.internal("shadeui/uiskin.json"));

    protected ChatiTable(String name) {
        super(SKIN);
        setName(name);
        create();
        setLayout();
    }

    protected abstract void create();

    protected abstract void setLayout();
}
