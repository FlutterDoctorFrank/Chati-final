package view2.component.menu.table;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import view2.Assets;
import view2.component.AbstractTable;

public abstract class MenuTable extends AbstractTable {

    protected static final float ROW_WIDTH = 600;
    protected static final float ROW_HEIGHT = 60;
    protected static final float SPACING = 15;

    protected Label infoLabel;

    public MenuTable() {
        this.infoLabel = new Label("", Assets.getNewSkin());
        infoLabel.setAlignment(Align.center, Align.center);
        infoLabel.setWrap(true);
        create();
        setLayout();
    }

    public abstract void resetTextFields();

    public void showMessage(String message) {
        infoLabel.setText(message);
    }
}
