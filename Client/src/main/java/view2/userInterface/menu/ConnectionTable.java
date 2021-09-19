package view2.userInterface.menu;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.jetbrains.annotations.NotNull;
import view2.Chati;

/**
 * Eine Klasse, welche das Menü zum Verbinden repräsentiert.
 */
public class ConnectionTable extends MenuTable {

    private final Animation<TextureRegion> animation;
    private final Image animationImage;
    private float timer = 0;

    /**
     * Erzeugt eine neue Instanz des ConnectionTable.
     */
    public ConnectionTable(@NotNull final String labelKey) {
        super(labelKey);

        animation = new Animation<>(0.1f, Chati.CHATI.getRegions("loading"), PlayMode.LOOP);
        animationImage = new Image();

        Table container = new Table();
        container.add(animationImage).size(ROW_HEIGHT / 2).padBottom(SPACING).row();
        container.add(infoLabel);
        add(container);
    }

    @Override
    public void act(final float delta) {
        animationImage.setDrawable(new TextureRegionDrawable(animation.getKeyFrame(timer += delta, true)));
    }

    @Override
    public void resetTextFields() {

    }
}
