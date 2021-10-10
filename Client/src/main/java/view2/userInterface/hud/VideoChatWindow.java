package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import view2.Chati;
import view2.multimedia.VideoReceiver.VideoFrame;
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

    private final Map<IUserView, VideoChatUser> videoChatUsers;
    private final ByteBuffer frameBuffer;
    private final ScrollPane videoChatScrollPane;
    private final HorizontalGroup videoChatGroup;

    /**
     * Erzeugt eine neue Instanz des VideoChatWindow.
     */
    public VideoChatWindow() {
        super("window.title.video-chat");
        videoChatUsers = new HashMap<>();
        frameBuffer = BufferUtils.createByteBuffer(VideoRecorder.COLOR_BYTES * VideoRecorder.FRAME_WIDTH
                * VideoRecorder.FRAME_HEIGHT);

        videoChatGroup = new HorizontalGroup().wrap().pad(SPACE);
        videoChatGroup.top().left();
        videoChatScrollPane = new ScrollPane(videoChatGroup, Chati.CHATI.getSkin());
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
        float width = 2 * MIN_WIDTH + getPadX();
        float height = MIN_HEIGHT + getPadY();
        setWidth(width);
        setHeight(height);
        setPosition(Gdx.graphics.getWidth() + ChatWindow.DEFAULT_WIDTH - 2 * width, 0);

        add(videoChatScrollPane).minWidth(MIN_WIDTH).minHeight(MIN_HEIGHT).grow();
        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void act(final float delta) {
        while (Chati.CHATI.getMultimediaManager().hasFrame()) {
            VideoFrame frame = Chati.CHATI.getMultimediaManager().getNextFrame();
            IUserView sender = frame.getSender();

            if (!videoChatUsers.containsKey(sender)) {
                addVideoChatUser(sender);
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

    /**
     * Zeige das Videochatfenster an und fokussiere es.
     */
    public void show() {
        setVisible(true);
        if (getStage() != null) {
            getStage().setScrollFocus(videoChatScrollPane);
        }
    }

    /**
     * Zeige das Chatfenster nicht an und entferne den Fokus.
     */
    public void hide() {
        setVisible(false);
        if (getStage() != null) {
            getStage().unfocus(this);
        }
    }

    /**
     * Entfernt alle Daten über angezeigte Benutzer.
     */
    public void clearVideochat() {
        videoChatUsers.clear();
        videoChatGroup.clear();
    }

    /**
     * Fügt einen Benutzer in den Videochat hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    private void addVideoChatUser(@NotNull final IUserView user) {
        if (videoChatUsers.containsKey(user)) {
            return;
        }
        VideoChatUser videoChatUser = new VideoChatUser(user);
        videoChatUsers.put(user, videoChatUser);
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.equals(user)) {
            videoChatGroup.addActorAt(0, videoChatUser.getContainer());
        } else {
            videoChatGroup.addActor(videoChatUser.getContainer());
        }
    }

    /**
     * Eine Klasse, welche Benutzer des Videochats repräsentiert.
     */
    private static class VideoChatUser {

        private static final float MAX_INACTIVE_TIME = 1; // In Sekunden

        private final IUserView user;
        private final Texture videoTexture;
        private final Table container;
        private final Image talkingBorderBackground;
        private LocalDateTime lastFrameTime;

        /**
         * Erzeugt eine neue Instanz eines VideoChatUser.
         * @param user Zugehöriger Benutzer.
         */
        public VideoChatUser(@NotNull final IUserView user) {
            this.user = user;

            Stack videochatUserStack = new Stack();
            videochatUserStack.setSize(VIDEO_WIDTH + SPACE / 2, VIDEO_HEIGHT + SPACE / 2);

            Pixmap backgroundPixmap = new Pixmap((int) (VIDEO_WIDTH + SPACE / 2), (int) (VIDEO_HEIGHT + SPACE / 2), RGB888);
            backgroundPixmap.setColor(Color.GREEN);
            backgroundPixmap.fill();
            talkingBorderBackground = new Image(new Texture(backgroundPixmap));
            talkingBorderBackground.setVisible(false);
            videochatUserStack.add(talkingBorderBackground);

            videoTexture = new Texture(2 * VideoRecorder.FRAME_WIDTH, 2 * VideoRecorder.FRAME_HEIGHT, RGB888);
            Image videoImage = new Image(videoTexture);
            Table videoImageContainer = new Table();
            videoImageContainer.add(videoImage).size(VIDEO_WIDTH, VIDEO_HEIGHT);
            videochatUserStack.add(videoImageContainer);

            container = new Table();
            container.add(videochatUserStack).size(VIDEO_WIDTH + SPACE / 2, VIDEO_HEIGHT + SPACE / 2).row();
            container.add(new UserInfoContainer(user)).width(VIDEO_WIDTH + SPACE / 2);
        }

        /**
         * Bindet die zur Anzeige der Frames des Benutzers verwendete Textur, um das nächste Frame zu zeichnen.
         * @param timestamp Zeitstempel, an dem die Textur gebunden wurde.
         */
        public void bindVideoTexture(@NotNull final LocalDateTime timestamp) {
            videoTexture.bind();
            this.lastFrameTime = timestamp;
        }

        /**
         * Legt fest, ob der Benutzer durch einen Rahmen um das Video als sprechend angezeigt werden soll.
         * @param show true, wenn er als sprechend angezeigt werden soll, sonst false.
         */
        public void showTalkingBorder(final boolean show) {
            talkingBorderBackground.setVisible(show);
        }

        /**
         * Gibt zurück, ob in einer festgelegten Zeit ein Frame von diesem Benutzer eingegangen ist.
         * @return true, falls ein Frame eingegangen ist, sonst false.
         */
        public boolean isActive() {
            return LocalDateTime.now().minus((long) (1000 * MAX_INACTIVE_TIME), ChronoUnit.MILLIS).isBefore(lastFrameTime);
        }

        /**
         * Entfernt diesen Benutzer aus dem Videochat.
         */
        public void remove() {
            this.container.remove();
        }

        /**
         * Gibt den zugehörigen Benutzer zurück.
         * @return Zugehöriger Benutzer.
         */
        public @NotNull IUserView getUser() {
            return user;
        }

        /**
         * Gibt den Table zurück, in dem sich die Textur zur Anzeige des Benutzers befindet.
         * @return Container des Videochat-Benutzers.
         */
        public @NotNull Table getContainer() {
            return container;
        }
    }
}
