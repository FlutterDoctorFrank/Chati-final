package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import view2.Chati;
import view2.multimedia.VideoReceiver;
import view2.multimedia.VideoRecorder;
import view2.userInterface.ResizableWindow;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;

/**
 * Eine Klasse, welche das Videochat-Fenster der Anwendung repräsentiert.
 */
public class VideoChatWindow extends ResizableWindow {

    private static final float DEFAULT_WIDTH = 800;
    private static final float DEFAULT_HEIGHT = 600;
    private static final float MINIMUM_WIDTH = 600;
    private static final float MINIMUM_HEIGHT = 450;

    private final Map<IUserView, Texture> videoChatTextures;
    private final ByteBuffer frameBuffer;
    private final HorizontalGroup videoChatGroup;

    /**
     * Erzeugt eine neue Instanz des VideoChatWindow.
     */
    public VideoChatWindow() {
        super("window.title.video-chat");
        this.videoChatTextures = new HashMap<>();
        this.frameBuffer = BufferUtils.createByteBuffer(VideoRecorder.COLOR_BYTES * VideoRecorder.FRAME_WIDTH * VideoRecorder.FRAME_HEIGHT);

        this.videoChatGroup = new HorizontalGroup().wrap();
        add(videoChatGroup).grow();

        // Layout
        setVisible(false);
        setPosition(Gdx.graphics.getWidth(), 0);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);
    }

    @Override
    public void draw(@NotNull final Batch batch, final float parentAlpha) {
        while (Chati.CHATI.getMultimediaManager().hasFrame()) {
            VideoReceiver.VideoFrame frame = Chati.CHATI.getMultimediaManager().getNextFrame();

            if (!videoChatTextures.containsKey(frame.getSender())) {
                videoChatTextures.put(frame.getSender(), new Texture(VideoRecorder.FRAME_WIDTH, VideoRecorder.FRAME_HEIGHT, RGB888));
                Image image = new Image(videoChatTextures.get(frame.getSender()));
                Table imageContainer = new Table();
                imageContainer.add(image).size(1000, 650);
                videoChatGroup.addActor(imageContainer);
            }

            frameBuffer.position(0);
            frameBuffer.put(frame.getFrameData());
            Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
            videoChatTextures.get(frame.getSender()).bind();
            frameBuffer.position(0);
            Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, VideoRecorder.FRAME_WIDTH, VideoRecorder.FRAME_HEIGHT,
                    0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, frameBuffer);
        }

        super.draw(batch, parentAlpha);
    }

    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
        if (getStage() != null) {
            getStage().unfocus(this);
        }
    }
}
