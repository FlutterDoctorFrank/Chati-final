package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import view2.component.ChatiTable;

public abstract class MenuTable extends ChatiTable {

    protected Label infoLabel;

    protected MenuTable(String name) {
        super(name);
    }
}
