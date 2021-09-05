package view2.component.menu.table;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import view2.Chati;

public abstract class MenuTable extends Table {

    protected static final float ROW_WIDTH = 600;
    protected static final float ROW_HEIGHT = 60;
    protected static final float SPACING = 15;

    protected final Label infoLabel;

    public MenuTable() {
        this.infoLabel = new Label("", Chati.CHATI.getSkin());
        infoLabel.setAlignment(Align.center, Align.center);
        infoLabel.setWrap(true);

        setFillParent(true);
    }

    public abstract void resetTextFields();

    public void showMessage(String message) {
        infoLabel.setText(message);
    }
}
