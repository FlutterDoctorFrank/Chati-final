package view.UIComponents;


import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class SettingsTable extends PopupMenu {

    public SettingsTable (DropDownMenu menu) {
        super(menu);
        setName("settings-table");
        top();
        TextButton manageAdministrators = new TextButton("Administratoren verwalten", skin);
        TextButton worldLeave = new TextButton("Welt verlassen", skin);
        TextButton exit = new TextButton("Beenden", skin);

        add(manageAdministrators).width(getWidth());
        row();
        add(worldLeave).width(getWidth());
        row();
        add(exit).width(getWidth());

    }
}