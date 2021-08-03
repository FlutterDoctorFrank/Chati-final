package view.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import java.util.Objects;

public class DropDownMenuButton extends Container {
    private Table table;
    private boolean isPressed = false;
    private DropDownMenus buttonGroup;
    private Hud hud;

    public DropDownMenuButton(Texture texture, String name, Table table, Hud hud, DropDownMenus buttonGroup) {
        this.table = table;
        this.buttonGroup = buttonGroup;
        this.hud = hud;
        setName(name);
        padRight(10);
        padTop(10);
        width(35);
        height(35);

        Image image = new Image(texture);
        setActor(image);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Objects.isNull(buttonGroup.getCurrentButtonPressed())) {
                    buttonGroup.setCurrentButtonPressed(getName());
                } else if (buttonGroup.getCurrentButtonPressed().equals(getName())){
                    buttonGroup.setCurrentButtonPressed(null);
                } else {
                    DropDownMenuButton old = (DropDownMenuButton) buttonGroup.findActor(buttonGroup.getCurrentButtonPressed());
                    old.pressed();
                    buttonGroup.setCurrentButtonPressed(getName());
                }
                pressed();
            }
        });
    }

    private void changeBackgroundColor(float r, float g, float b, float a) {
        Pixmap buttonColor = new Pixmap((int) getWidth(), (int) getHeight(), Pixmap.Format.RGB888);
        buttonColor.setColor(r, g, b, a);
        buttonColor.fill();
        setBackground(new Image(new Texture(buttonColor)).getDrawable());

    }

    public void pressed() {
        if (isPressed) {
            isPressed = false;
            setBackground(null);
            hud.getGroup().removeActor(hud.getGroup().findActor(table.getName()));
            return;
        }
        isPressed = true;
        hud.getGroup().addActor(table);
        changeBackgroundColor(0, 1, 0, 0);
    }
}
