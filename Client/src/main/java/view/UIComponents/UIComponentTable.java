package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class UIComponentTable extends Table {
    protected Skin skin;
    protected Hud hud;
    protected Label infoLabel;

    private static Skin staticSkin;

    public static Skin getStaticSkin() {
        if (staticSkin == null) {
            return staticSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        }

        return staticSkin;
    }

    public UIComponentTable(Hud hud) {
        setWidth(hud.getWidth());
        setHeight(hud.getHeight());
        this.hud = hud;
        skin =  getStaticSkin();
        setFillParent(true);
        defaults().space(5);
    }

    public void displayMessage(String text) {
        infoLabel.setText(text);
    }
}
