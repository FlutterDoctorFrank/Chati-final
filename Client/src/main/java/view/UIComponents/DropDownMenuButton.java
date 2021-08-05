package view.UIComponents;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Objects;

public class DropDownMenuButton extends Container {
    private PopupMenu table;
    private boolean isPressed = false;

    public DropDownMenuButton(Texture texture, String name, PopupMenu table) {
        this.table = table;

        setName(name);
        padRight(10);
        padTop(10);
        width(table.getDropDownMenu().getWidth());
        height(table.getDropDownMenu().getHeight());

        Image image = new Image(texture);
        setActor(image);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                handleInput();
            }
        });
    }

    private void changeBackgroundColor(float r, float g, float b, float a) {
        Pixmap buttonColor = new Pixmap((int) getWidth(), (int) getHeight(), Pixmap.Format.RGB888);
        buttonColor.setColor(r, g, b, a);
        buttonColor.fill();
        setBackground(new Image(new Texture(buttonColor)).getDrawable());

    }

    private void  handleInput() {
        if (Objects.isNull(table.getDropDownMenu().getCurrentButtonPressed())) {
            table.getDropDownMenu().setCurrentButtonPressed(this);
        } else if (table.getDropDownMenu().getCurrentButtonPressed().equals(this)){
            table.getDropDownMenu().setCurrentButtonPressed(null);
        } else {
            table.getDropDownMenu().getCurrentButtonPressed().pressed();
            table.getDropDownMenu().setCurrentButtonPressed(this);
        }
        pressed();
    }

    public void pressed() {
        if (isPressed) {
            isPressed = false;
            setBackground(null);
            table.getDropDownMenu().getHud().addPopupMenu(table, false);
            return;
        }
        isPressed = true;
        table.getDropDownMenu().getHud().addPopupMenu(table, true);
        changeBackgroundColor(0, 1, 0, 0);
    }
}
