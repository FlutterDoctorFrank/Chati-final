package view2.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import view2.Assets;
import view2.Chati;

public class ChatiTextField extends TextField {

    private static final int TEXT_FIELD_MAX_LENGTH = 32;
    private static final int PASSWORD_FIELD_MAX_LENGTH = 48;

    private final String text;

    public ChatiTextField(String text, boolean passwordField) {
        super(text, Assets.getNewSkin());
        this.text = text;
        if (passwordField) {
            setMaxLength(PASSWORD_FIELD_MAX_LENGTH);
        } else {
            setMaxLength(TEXT_FIELD_MAX_LENGTH);
        }
        getStyle().fontColor = Color.GRAY;
        setPasswordCharacter('*');
        addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused && getStyle().fontColor == Color.GRAY) {
                    getStyle().fontColor = Color.BLACK;
                    setText("");
                    if (passwordField) {
                        setPasswordMode(true);
                    }
                }
                else if (getText().isBlank()) {
                    reset();
                }
            }
        });
        setTextFieldFilter((textField, c) -> !getText().isBlank() || !Character.toString(c).matches("\\s"));
    }

    public void reset() {
        getStyle().fontColor = Color.GRAY;
        setText(text);
        setPasswordMode(false);
        Chati.CHATI.getScreen().getStage().unfocus(this);
    }
}
