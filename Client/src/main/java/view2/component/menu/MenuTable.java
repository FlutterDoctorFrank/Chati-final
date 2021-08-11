package view2.component.menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import view2.component.ChatiTable;

public abstract class MenuTable extends ChatiTable {

    protected static final float ROW_WIDTH = 600;
    protected static final float ROW_HEIGHT = 60;
    protected static final float VERTICAL_SPACING = 15;
    protected static final float HORIZONTAL_SPACING = 15;
    protected static final float LABEL_FONT_SCALE_FACTOR = 0.5f;
    protected static final float TEXTFIELD_FONT_SCALE_FACTOR = 1.6f;

    protected Label infoLabel;

    public abstract void clearTextFields();

    public void showMessage(String message) {
        infoLabel.setText(message);
    }

    @Override
    protected void create() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        this.infoLabel = new Label("", style);
    }
}
