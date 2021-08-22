package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import view2.Assets;
import view2.component.ChatiTable;

public abstract class MenuTable extends ChatiTable {

    protected static final float ROW_WIDTH = 600;
    protected static final float ROW_HEIGHT = 60;
    protected static final float VERTICAL_SPACING = 15;
    protected static final float HORIZONTAL_SPACING = 15;

    protected Label infoLabel;

    public MenuTable() {
        this.infoLabel = new Label("", Assets.getNewSkin());
        create();
        setLayout();
    }

    public abstract void resetTextFields();

    public void showMessage(String message) {
        infoLabel.setText(message);
    }
}
