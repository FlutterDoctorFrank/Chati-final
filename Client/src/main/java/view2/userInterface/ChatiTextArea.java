package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import view2.Chati;

public class ChatiTextArea extends TextArea {

    private static final int TEXT_AREA_MAX_LENGTH = 512;

    public ChatiTextArea(String messageText, boolean writeEnters) {
        super("", Chati.CHATI.getSkin());
        setMessageText(messageText);

        setMaxLength(TEXT_AREA_MAX_LENGTH);
        this.writeEnters = false;

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ChatiTextArea.this.writeEnters = writeEnters && !getText().isBlank();
            }
        });

        setTextFieldFilter((textField, c) -> !isBlank() || !Character.toString(c).matches("\\s"));
    }

    public boolean isBlank() {
        return getText().isBlank();
    }

    public void reset() {
        setText("");
        if (hasKeyboardFocus() && getStage() != null) {
            getStage().unfocus(this);
        }
    }
}
