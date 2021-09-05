package view2.component;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ChatiImageButton extends ImageButton {

    private static final float IMAGE_SCALE_FACTOR = 0.1f;

    public ChatiImageButton(Drawable imageUnchecked, Drawable imageChecked) {
        super(imageUnchecked, imageUnchecked, imageChecked);
        getImage().scaleBy(-IMAGE_SCALE_FACTOR);

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getImage().scaleBy(-IMAGE_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getImage().scaleBy(IMAGE_SCALE_FACTOR);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    getImage().scaleBy(IMAGE_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    getImage().scaleBy(-IMAGE_SCALE_FACTOR);
                }
            }
        });
    }
}
