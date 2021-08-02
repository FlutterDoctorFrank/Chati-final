package view.UIComponents;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class UsersListTable extends Table {
    private Hud hud;

    public UsersListTable (Hud hud) {
        this.hud = hud;


    }

    public Hud getHud() {
        return hud;
    }
}
