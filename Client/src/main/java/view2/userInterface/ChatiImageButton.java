package view2.userInterface;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ChatiImageButton extends ImageButton {

    private static final float DEFAULT_IMAGE_SCALE_FACTOR = 0.1f;

    public ChatiImageButton(Drawable imageUnchecked, Drawable imageChecked, Drawable imageDisabled, float imageScaleFactor) {
        super(imageUnchecked, imageUnchecked, imageChecked);
        getStyle().imageDisabled = imageDisabled;
        getImage().scaleBy(-imageScaleFactor);

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getImage().scaleBy(-imageScaleFactor);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getImage().scaleBy(imageScaleFactor);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    getImage().scaleBy(imageScaleFactor);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    getImage().scaleBy(-imageScaleFactor);
                }
            }
        });
    }

    public ChatiImageButton(Drawable imageUnchecked, Drawable imageChecked) {
        this(imageUnchecked, imageChecked, imageUnchecked, DEFAULT_IMAGE_SCALE_FACTOR);
    }

    public ChatiImageButton(Drawable imageUnchecked) {
        this(imageUnchecked, imageUnchecked, imageUnchecked, DEFAULT_IMAGE_SCALE_FACTOR);
    }
}
