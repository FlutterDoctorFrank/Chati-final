package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import view.Screens.ScreenHandler;

public abstract class UIComponentTable extends Table {
    protected Skin skin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));

    public UIComponentTable() {
        setFillParent(true);
        defaults().space(5);
    }

    protected Table dropDownMenus(Hud hud) {
        Table dropDownMenus = new Table();
        dropDownMenus.defaults().width(35).height(35).space(5);
        Image settings = new Image(new Texture("icons/settings_img.png"));
        Image users = new Image(new Texture("icons/user_img.png"));
        Image notifications = new Image(new Texture("icons/speech_bubble_img.png"));
        dropDownMenus.add(notifications);
        dropDownMenus.add(users);
        dropDownMenus.add(settings);
        return dropDownMenus;
    }

    protected float maxComponentHight() {
        float result = 0;
        return result;
    }

    protected float maxComponentWidth() {
        float result = 0;
        return result;
    }
}
