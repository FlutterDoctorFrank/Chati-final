package view2.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import view2.Assets;
import view2.Chati;

public class ChatiTextArea extends TextArea {

    private static final int TEXT_AREA_MAX_LENGTH = 512;

    private final String text;

    public ChatiTextArea(String text, boolean writeEnters) {
        super(text, Assets.getNewSkin());
        this.text = text;
        setMaxLength(TEXT_AREA_MAX_LENGTH);
        getStyle().fontColor = Color.GRAY;
        addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused && getStyle().fontColor == Color.GRAY) {
                    getStyle().fontColor = Color.BLACK;
                    setText("");
                }
                else if (getText().isBlank()) {
                    reset();
                }
            }
        });
        this.writeEnters = false;
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ChatiTextArea.this.writeEnters = writeEnters && !getText().isBlank();
            }
        });
        setTextFieldFilter((textField, c) -> !getText().isBlank() || !Character.toString(c).matches("\\s"));
    }

    public void reset() {
        getStyle().fontColor = Color.GRAY;
        setText(text);
        Chati.CHATI.getScreen().getStage().unfocus(this);
    }
}
