package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PopupMenu extends Table {
    protected Skin skin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
    protected DropDownMenu dropDownMenu;

    public PopupMenu(DropDownMenu menu) {
        this.dropDownMenu = menu;
        setWidth(200);
        setHeight(300);
        setPosition(dropDownMenu.getHud().getWidth() - getWidth(), dropDownMenu.getHud().getHeight() - getHeight() - 2 * dropDownMenu.getHeight());
    }

    public DropDownMenu getDropDownMenu() {
        return dropDownMenu;
    }
}

