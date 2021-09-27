package view2.userInterface.hud;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import org.jetbrains.annotations.NotNull;
import view2.Chati;

/**
 * Eine Klasse, welche ein Fenster zum Auswählen einer Bilddatei repräsentiert.
 */
public class ImageFileChooserWindow extends FileChooserWindow {

    private static final int MAX_FILE_SIZE = (int) Math.pow(2, 18);
    private static final String IMAGE_FILE_EXTENSIONS = ".*(?:jpg|jpeg|png|bmp)";

    private final Pixmap image;

    /**
     * Erzeugt eine neue Instanz des ImageFileChooserWindow zum Auswählen einer Bilddatei.
     */
    public ImageFileChooserWindow() {
        super("window.title.choose-image", pathname -> pathname.getPath().matches(IMAGE_FILE_EXTENSIONS), false, null);
        this.image = null;
    }

    /**
     * Erzeugt eine neue Instanz des ImageFileChooserWindow zum Speichern einer Bilddatei.
     * @param imageName Name des zu speichernden Bildes.
     * @param image Zu speicherndes Bild.
     */
    public ImageFileChooserWindow(@NotNull final String imageName, @NotNull final Pixmap image) {
        super("window.title.save-image", pathname -> pathname.getPath().matches(IMAGE_FILE_EXTENSIONS), true, imageName);
        this.image = image;
    }

    @Override
    protected boolean loadFile(@NotNull final FileHandle imageFileHandle) {
        if (!imageFileHandle.exists()) {
            showMessage("window.choose-image.file-not-exist");
            return false;
        }
        if (!imageFileHandle.extension().matches(IMAGE_FILE_EXTENSIONS)) {
            showMessage("window.choose-image.file-no-image");
            return false;
        }
        if (imageFileHandle.length() >= MAX_FILE_SIZE) {
            showMessage("window.choose-image.file-too-big", MAX_FILE_SIZE / 1024);
            return false;
        }

        Chati.CHATI.getHeadUpDisplay().getChatWindow().attachImage(imageFileHandle);
        return true;
    }

    @Override
    protected boolean saveFile(@NotNull final FileHandle imageFileHandle) {
        if (image == null) {
            return false;
        }
        PixmapIO.writePNG(imageFileHandle, image);
        return true;
    }
}
