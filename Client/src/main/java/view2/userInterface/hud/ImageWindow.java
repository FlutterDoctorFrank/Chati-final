package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.jetbrains.annotations.NotNull;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiWindow;

/**
 * Eine Klasse, welche das Fenster zum Anzeigen eines Bildes repräsentiert.
 */
public class ImageWindow extends ChatiWindow {

    private static final float SAVE_BUTTON_WIDTH = 180;

    private final Pixmap imagePixmap;

    /**
     * Erzeugt eine neue Instanz des ImageWindow.
     * @param imageName Name der Bilddatei.
     * @param imageData Anzuzeigendes Bild.
     */
    protected ImageWindow(@NotNull final String imageName, final byte[] imageData) {
        super("", 1, 1);
        getTitleLabel().setText(imageName);

        setModal(true);
        setMovable(false);

        this.imagePixmap = new Pixmap(imageData, 0, imageData.length);

        ChatiTextButton saveButton = new ChatiTextButton("menu.button.save", true);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ImageFileChooserWindow(imageName, imagePixmap).open();
            }
        });

        Table imageTable = new Table();
        Texture imageTexture = new Texture(imagePixmap);
        Image image = new Image(new TextureRegionDrawable(imageTexture));

        float widthRatio = 1;
        float heightRatio = 1;
        float maxImageWidth = Gdx.graphics.getWidth() - getPadX();
        float maxImageHeight = Gdx.graphics.getHeight() - ROW_HEIGHT - SPACE - getPadY();
        if (imagePixmap.getWidth() > maxImageWidth) {
            widthRatio = maxImageWidth / imagePixmap.getWidth();
        }
        if (imagePixmap.getHeight() > maxImageHeight) {
            heightRatio = maxImageHeight / imagePixmap.getHeight();
        }
        float ratio = Math.min(widthRatio, heightRatio);
        float imageWidth = ratio * imagePixmap.getWidth();
        float imageHeight = ratio * imagePixmap.getHeight();
        imageTable.add(image).size(imageWidth, imageHeight);

        add(saveButton).width(SAVE_BUTTON_WIDTH).height(ROW_HEIGHT).padTop(SPACE / 2).padBottom(SPACE / 2).row();
        add(imageTable).grow();

        setWidth(Math.max(SAVE_BUTTON_WIDTH, imageWidth) + getPadX());
        setHeight(Math.max(ROW_HEIGHT, imageHeight) + ROW_HEIGHT + SPACE + getPadY());

        // Translatable register
        translatables.add(saveButton);
        translatables.trimToSize();
    }

    @Override
    public void close() {
        imagePixmap.dispose();
        super.close();
    }
}
