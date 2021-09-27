package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiWindow;

/**
 * Eine Klasse, welche das Fenster zum Anzeigen eines Bildes repräsentiert.
 */
public class ImageWindow extends ChatiWindow {

    private static final float SAVE_BUTTON_WIDTH = 180;
    private static final float EDGE_SPACE = 75;

    private final ScrollPane imageScrollPane;

    /**
     * Erzeugt eine neue Instanz des ImageWindow.
     * @param fileName Name der Bilddatei.
     * @param imagePixmap Anzuzeigendes Bild.
     */
    protected ImageWindow(String fileName, Pixmap imagePixmap) {
        super("", 1, 1);
        getTitleLabel().setText(fileName);

        setModal(true);
        setMovable(false);

        ChatiTextButton saveButton = new ChatiTextButton("window.image.save", true);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ImageFileChooserWindow(fileName, imagePixmap).open();
            }
        });

        Table imageTable = new Table();
        Image image = new Image(new TextureRegionDrawable(new Texture(imagePixmap)));

        float widthRatio = 1;
        float heightRatio = 1;
        if (imagePixmap.getWidth() > Gdx.graphics.getWidth()) {
            widthRatio = (float) Gdx.graphics.getWidth() / imagePixmap.getWidth();
        }
        if (imagePixmap.getHeight() > Gdx.graphics.getHeight()) {
            heightRatio = (float) Gdx.graphics.getHeight() / imagePixmap.getHeight();
        }
        float ratio = Math.min(widthRatio, heightRatio);
        float imageWidth = ratio * image.getWidth();
        float imageHeight = ratio * image.getHeight();
        imageTable.add(image).width(imageWidth).height(imageHeight);
        imageScrollPane = new ScrollPane(imageTable, Chati.CHATI.getSkin());
        imageScrollPane.setOverscroll(false, false);
        add(saveButton).width(SAVE_BUTTON_WIDTH).height(ROW_HEIGHT).row();
        add(imageScrollPane).grow();

        setWidth(Math.min(Gdx.graphics.getWidth(), Math.max(SAVE_BUTTON_WIDTH, imageWidth) + EDGE_SPACE));
        setHeight(Math.min(Gdx.graphics.getHeight(), Math.max(SAVE_BUTTON_WIDTH, imageHeight) + EDGE_SPACE + ROW_HEIGHT));
    }

    @Override
    public void focus() {
        super.focus();
        if (getStage() != null) {
            getStage().setScrollFocus(imageScrollPane);
        }
    }
}
