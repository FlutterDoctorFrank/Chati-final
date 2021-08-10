package view2.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public abstract class ChatiTable extends Table {

    protected static final Skin SKIN = new Skin(Gdx.files.internal("shadeui/uiskin.json"));

    protected Label infoLabel;

    protected ChatiTable(String name) {
        super(SKIN);
        setFillParent(true);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(0.5f);
        this.infoLabel = new Label("", style);
        setName(name);
        create();
        setLayout();
    }

    protected abstract void create();

    protected abstract void setLayout();

    public abstract void clearTextFields();

    public void showMessage(String message) {
        infoLabel.setText(message);
    }
}
