package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public abstract class UIComponentTable extends Table {
    protected Skin skin;
    protected Hud hud;
    protected Label infoLabel;

    public UIComponentTable(Hud hud) {
        setWidth(hud.getWidth());
        setHeight(hud.getHeight());
        this.hud = hud;
        skin =  new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        setFillParent(true);
        defaults().space(5);
    }

    public void displayMessage(String text) {
        infoLabel.setText(text);
    }
}
