package view.Screens;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import view.UIComponents.Hud;
import view.UIComponents.LoginTable;
import view.UIComponents.UIComponentTable;

public class MenuScreen extends ApplicationScreen {

    public MenuScreen (ScreenHandler screenHandler) {
        hud = new Hud(spriteBatch, screenHandler);
        hud.addTable(new LoginTable(hud, screenHandler));
    }

    public MenuScreen (Table table, ScreenHandler screenHandler) {
        hud = new Hud(spriteBatch, screenHandler);
        hud.addTable(table);
    }

}
