package view2.userInterface.menu;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTable;

/**
 * Eine Klasse, welche das Menü zum Verbinden repräsentiert.
 */
public class ConnectionTable extends ChatiTable {

    private static final float FRAME_DURATION = 0.1f;

    private final Animation<TextureRegion> loadingAnimation;
    private final Image keyFrameImage;
    private float stateTime = 0;

    /**
     * Erzeugt eine neue Instanz des ConnectionTable.
     */
    public ConnectionTable(@NotNull final String labelKey) {
        super(labelKey);

        loadingAnimation = new Animation<>(FRAME_DURATION, Chati.CHATI.getRegions("loading"), PlayMode.LOOP);
        keyFrameImage = new Image();

        Table container = new Table();
        container.add(keyFrameImage).size(ROW_HEIGHT / 2).padBottom(SPACING).row();
        container.add(infoLabel);
        add(container);
    }

    @Override
    public void act(final float delta) {
        keyFrameImage.setDrawable(new TextureRegionDrawable(loadingAnimation.getKeyFrame(stateTime += delta, true)));
    }

    @Override
    public void resetTextFields() {
    }
}
