package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import view2.Chati;
import view2.multimedia.VideoFrame;
import view2.multimedia.VideoRecorder;
import view2.userInterface.ChatiTooltip;
import view2.userInterface.ResizableWindow;
import view2.userInterface.UserInfoContainer;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;

/**
 * Eine Klasse, welche das Videochat-Fenster der Anwendung repräsentiert.
 */
public class VideoChatWindow extends ResizableWindow {

    private static final float VIDEO_WIDTH = 320;
    private static final float VIDEO_HEIGHT = 240;
    private static final float SPACE = 10;
    private static final float MIN_WIDTH = VIDEO_WIDTH + 4 * SPACE;
    private static final float MIN_HEIGHT = VIDEO_HEIGHT + 4 * SPACE + 35;
    private static final float DEFAULT_WIDTH = 2 * MIN_WIDTH;
    private static final float DEFAULT_HEIGHT = 2 * MIN_HEIGHT;

    private final Map<IUserView, VideoChatUser> videoChatUsers;
    private final ByteBuffer frameBuffer;
    private final ScrollPane videoChatScrollPane;
    private final HorizontalGroup videoChatGroup;

    /**
     * Erzeugt eine neue Instanz des VideoChatWindow.
     */
    public VideoChatWindow() {
        super("window.title.video-chat");
        this.videoChatUsers = new HashMap<>();
        this.frameBuffer = BufferUtils.createByteBuffer(VideoRecorder.COLOR_BYTES * VideoRecorder.FRAME_WIDTH *
                VideoRecorder.FRAME_HEIGHT);

        this.videoChatGroup = new HorizontalGroup().wrap().pad(SPACE);
        videoChatGroup.top().left();
        this.videoChatScrollPane = new ScrollPane(videoChatGroup, Chati.CHATI.getSkin());
        videoChatScrollPane.setOverscroll(false, false);
        videoChatScrollPane.setFadeScrollBars(false);

        TextButton closeButton = new TextButton("X", Chati.CHATI.getSkin());
        closeButton.setDisabled(true);
        closeButton.addListener(new ChatiTooltip("hud.tooltip.close"));
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getHeadUpDisplay().hideVideoChatWindow();
            }
        });

        // Layout
        setVisible(false);
        setPosition(Gdx.graphics.getWidth(), 0);
        setWidth(DEFAULT_WIDTH);
        setHeight(DEFAULT_HEIGHT);

        add(videoChatScrollPane).minWidth(MIN_WIDTH).minHeight(MIN_HEIGHT).grow();
        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void act(final float delta) {
        Iterator<VideoChatUser> iterator = videoChatUsers.values().iterator();
        while (iterator.hasNext()) {
            VideoChatUser user = iterator.next();
            if (!user.isActive()) {
                user.remove();
                iterator.remove();
            }
        }
        super.act(delta);
    }

    @Override
    public void draw(@NotNull final Batch batch, final float parentAlpha) {
        while (Chati.CHATI.getMultimediaManager().hasFrame()) {
            VideoFrame frame = Chati.CHATI.getMultimediaManager().getNextFrame();
            IUserView sender = frame.getSender();

            if (!videoChatUsers.containsKey(sender)) {
                videoChatUsers.put(sender, new VideoChatUser(sender));
            }

            videoChatUsers.get(sender).showTalkingBorder(Chati.CHATI.getMultimediaManager().isTalking(sender));

            frameBuffer.position(0);
            frameBuffer.put(frame.getFrameData());
            Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
            videoChatUsers.get(sender).bindVideoTexture(frame.getTimestamp());
            frameBuffer.position(0);

            Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, VideoRecorder.FRAME_WIDTH,
                    VideoRecorder.FRAME_HEIGHT, 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, frameBuffer);
        }

        super.draw(batch, parentAlpha);
    }

    public void show() {
        setVisible(true);
        if (getStage() != null) {
            getStage().setScrollFocus(videoChatScrollPane);
        }
    }

    public void hide() {
        setVisible(false);
        if (getStage() != null) {
            getStage().unfocus(this);
        }
    }

    public void clearVideochat() {
        videoChatUsers.clear();
        videoChatGroup.clear();
    }

    private class VideoChatUser {

        private static final float MAX_INACTIVE_TIME = 0.4f; // In Sekunden

        private final Texture videoTexture;
        private final Table container;
        private final Image talkingBorderBackground;
        private LocalDateTime lastFrameTime;

        public VideoChatUser(@NotNull final IUserView user) {
            Stack stack = new Stack();
            stack.setSize(VIDEO_WIDTH + SPACE / 2, VIDEO_HEIGHT + SPACE / 2);

            Pixmap backgroundPixmap = new Pixmap((int) (VIDEO_WIDTH + SPACE / 2), (int) (VIDEO_HEIGHT + SPACE / 2), RGB888);
            backgroundPixmap.setColor(Color.GREEN);
            backgroundPixmap.fill();
            this.talkingBorderBackground = new Image(new Texture(backgroundPixmap));
            talkingBorderBackground.setVisible(false);
            stack.add(talkingBorderBackground);

            this.videoTexture = new Texture(VideoRecorder.FRAME_WIDTH, VideoRecorder.FRAME_HEIGHT, RGB888);
            Image videoImage = new Image(videoTexture);
            Table videoImageContainer = new Table();
            videoImageContainer.add(videoImage).size(VIDEO_WIDTH, VIDEO_HEIGHT);
            stack.add(videoImageContainer);

            this.container = new Table();
            container.add(stack).size(VIDEO_WIDTH + SPACE / 2, VIDEO_HEIGHT + SPACE / 2).row();
            container.add(new UserInfoContainer(user)).width(VIDEO_WIDTH + SPACE / 2);
            videoChatGroup.addActor(container);
        }

        public void remove() {
            this.container.remove();
        }

        public void bindVideoTexture(@NotNull final LocalDateTime timestamp) {
            videoTexture.bind();
            this.lastFrameTime = timestamp;
        }

        public void showTalkingBorder(final boolean show) {
            talkingBorderBackground.setVisible(show);
        }

        public boolean isActive() {
            return LocalDateTime.now().minus((long) (1000 * MAX_INACTIVE_TIME), ChronoUnit.MILLIS).isBefore(lastFrameTime);
        }
    }
}
