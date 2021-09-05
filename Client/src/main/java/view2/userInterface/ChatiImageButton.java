package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ChatiImageButton extends ImageButton {

    private static final float DEFAULT_IMAGE_SCALE_FACTOR = 0.1f;

    private float buttonScaleFactor;

    public ChatiImageButton(Drawable imageUnchecked) {
        this(imageUnchecked, imageUnchecked);
    }

    public ChatiImageButton(Drawable imageUnchecked, Drawable imageChecked, Drawable imageDisabled, float buttonScaleFactor) {
        this(imageUnchecked, imageChecked);
        getStyle().imageDisabled = imageDisabled;
        this.buttonScaleFactor = buttonScaleFactor;
    }

    public ChatiImageButton(Drawable imageUnchecked, Drawable imageChecked) {
        super(imageUnchecked, imageUnchecked, imageChecked);
        this.buttonScaleFactor = DEFAULT_IMAGE_SCALE_FACTOR;
        getImage().scaleBy(-buttonScaleFactor);

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getImage().scaleBy(-buttonScaleFactor);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getImage().scaleBy(buttonScaleFactor);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    getImage().scaleBy(buttonScaleFactor);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    getImage().scaleBy(-buttonScaleFactor);
                }
            }
        });
    }
}
