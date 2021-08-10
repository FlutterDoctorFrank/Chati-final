package view2.component.menu;

import view2.component.ChatiTable;

public abstract class MenuTable extends ChatiTable {

    protected MenuTable(String name) {
        super(name);
    }

    public abstract void clearTextFields();
}
