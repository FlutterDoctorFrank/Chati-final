package view.userInterface.hud.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import view.Chati;
import view.ChatiCursor;
import view.ChatiLocalization.Translatable;
import view.multimedia.video.ScreenRecorder;
import view.multimedia.video.VideoReceiver;
import view.multimedia.video.CameraRecorder;
import view.multimedia.video.VideoRecorder;
import view.userInterface.*;
import view.userInterface.actor.ChatiImageButton;
import view.userInterface.actor.ChatiTooltip;
import view.userInterface.actor.ResizableWindow;

import java.awt.*;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;

/**
 * Eine Klasse, welche das Videochat-Fenster der Anwendung repräsentiert.
 */
public class VideoChatWindow extends ResizableWindow implements Translatable {

    private static final float VIDEO_WIDTH = 425;
    private static final float VIDEO_HEIGHT = 240;
    private static final float ICON_SIZE = 30;
    private static final float SPACE = 10;
    private static final float MIN_WIDTH = VIDEO_WIDTH + 5 * SPACE;
    private static final float MIN_HEIGHT = VIDEO_HEIGHT + 4 * SPACE + 30;

    private final Map<IUserView, Monitor> userCameras;
    private final Map<IUserView, Monitor> userScreens;
    private final ByteBuffer frameBuffer;
    private final HorizontalGroup videoChatGroup;
    private final ScrollPane videoChatScrollPane;
    private final ChatiImageButton cameraButton;
    private final ChatiImageButton shareButton;
    private VideoWindow videoWindow;
    private boolean cameraOn;
    private boolean shareScreen;

    /**
     * Erzeugt eine neue Instanz des VideoChatWindow.
     */
    public VideoChatWindow() {
        super(Chati.CHATI.getLocalization().translate("window.title.video-chat"));
        userCameras = new HashMap<>();
        userScreens = new HashMap<>();
        frameBuffer = BufferUtils.createByteBuffer(VideoRecorder.COLOR_BYTES * ScreenRecorder.FRAME_WIDTH
                * ScreenRecorder.FRAME_HEIGHT);

        cameraButton = new ChatiImageButton(Chati.CHATI.getDrawable("camera_on"), Chati.CHATI.getDrawable("camera_off"),
                Chati.CHATI.getDrawable("camera_disabled"));
        cameraButton.addListener(new ChatiTooltip("hud.tooltip.toggle-camera"));
        cameraButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                setCameraOn(cameraButton.isChecked());
            }
        });

        shareButton = new ChatiImageButton(Chati.CHATI.getDrawable("share_screen_on"),
                Chati.CHATI.getDrawable("share_screen_off"), Chati.CHATI.getDrawable("share_screen_disabled"));
        shareButton.addListener(new ChatiTooltip("hud.tooltip.share-screen"));
        shareButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                shareScreen(shareButton.isChecked());
            }
        });

        videoChatGroup = new HorizontalGroup().wrap().space(SPACE).wrapSpace(SPACE).pad(SPACE);
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
        float width = 2 * MIN_WIDTH;
        float height = MIN_HEIGHT + ICON_SIZE + getPadY();
        setSize(width, height);
        setPosition(Gdx.graphics.getWidth(), 0);

        Table shareButtonContainer = new Table();
        shareButtonContainer.left().defaults().size(ICON_SIZE).left().padLeft(SPACE);
        shareButtonContainer.add(cameraButton, shareButton);
        add(shareButtonContainer).left().growX().row();
        add(videoChatScrollPane).minWidth(MIN_WIDTH).minHeight(MIN_HEIGHT).grow();
        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    @Override
    public void act(final float delta) {
        while (Chati.CHATI.getMultimediaManager().hasVideoFrame()) {
            VideoReceiver.VideoFrame frame = Chati.CHATI.getMultimediaManager().getNextVideoFrame();
            if (frame == null) {
                break;
            }
            IUserView sender = frame.getSender();
            int width;
            int height;

            Monitor monitor;
            if (frame.isScreenshot()) {
                if (!userScreens.containsKey(sender)) {
                    addUserScreen(sender);
                }
                monitor = userScreens.get(sender);
                width = ScreenRecorder.FRAME_WIDTH;
                height = ScreenRecorder.FRAME_HEIGHT;
            } else {
                if (!userCameras.containsKey(sender)) {
                    addUserCamera(sender);
                }
                monitor = userCameras.get(sender);
                monitor.showTalkingBorder(Chati.CHATI.getMultimediaManager().isTalking(sender));
                width = CameraRecorder.FRAME_WIDTH;
                height = CameraRecorder.FRAME_HEIGHT;
            }

            frameBuffer.position(0);
            frameBuffer.put(frame.getFrameData());
            Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
            monitor.bindVideoTexture(frame.getTimestamp());
            frameBuffer.position(0);

            Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, width, height, 0, GL20.GL_RGB,
                    GL20.GL_UNSIGNED_BYTE, frameBuffer);

            if (videoWindow != null && videoWindow.getUser().getUser().equals(sender)
                    && videoWindow.getUser().isScreen() == frame.isScreenshot()) {
                frameBuffer.put(frame.getFrameData());
                videoWindow.getUser().bindVideoTexture(frame.getTimestamp());
                frameBuffer.position(0);

                Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, width, height, 0, GL20.GL_RGB,
                        GL20.GL_UNSIGNED_BYTE, frameBuffer);
            }
        }

        Iterator<Monitor> iterator = userCameras.values().iterator();
        while (iterator.hasNext()) {
            Monitor monitor = iterator.next();
            if (!monitor.isActive()) {
                monitor.remove();
                iterator.remove();
            }
        }

        iterator = userScreens.values().iterator();
        while (iterator.hasNext()) {
            Monitor monitor = iterator.next();
            if (!monitor.isActive()) {
                monitor.remove();
                iterator.remove();
            }
        }

        if (videoWindow != null && !videoWindow.getUser().isActive()) {
            videoWindow.remove();
        }

        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null) {
            if (Chati.CHATI.getMultimediaManager().hasCamera() && internUser.canShow() && cameraButton.isDisabled()) {
                cameraButton.setDisabled(false);
                cameraButton.setTouchable(Touchable.enabled);
            } else if ((!Chati.CHATI.getMultimediaManager().hasCamera() || !internUser.canShow())
                    && !cameraButton.isDisabled()) {
                cameraButton.setDisabled(true);
                cameraButton.setTouchable(Touchable.disabled);
            }
            if (internUser.canShare() && shareButton.isDisabled()) {
                shareButton.setDisabled(false);
                shareButton.setTouchable(Touchable.enabled);
            } else if (!internUser.canShare() && !shareButton.isDisabled()) {
                shareButton.setDisabled(true);
                shareButton.setTouchable(Touchable.disabled);
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
    public void clearVideoChat() {
        userCameras.clear();
        userScreens.clear();
        closeVideoWindow();
    }

    /**
     * Öffnet ein Fenster zur Anzeige eines einzelnen Videos.
     * @param user Anzuzeigender Teilnehmer des Videochats.
     */
    public void openVideoWindow(@NotNull final VideoChatWindow.Monitor user) {
        if (getStage() != null && VideoChatWindow.this.videoWindow == null) {
            if (user.isScreen()) {
                videoWindow = new VideoWindow("window.title.video-chat.single-screen",
                        new Monitor(user.getUser(), user.isScreen(), true));
            } else {
                videoWindow = new VideoWindow("window.title.video-chat.single-camera",
                        new Monitor(user.getUser(), user.isScreen(), true));
            }
            getStage().addActor(videoWindow);
        }
    }

    /**
     * Schließt und entfernt das angezeigte Fenster mit einem Video.
     */
    public void closeVideoWindow() {
        if (videoWindow != null) {
            videoWindow.remove();
            videoWindow = null;
        }
    }

    /**
     * Gibt zurück, ob gerade ein Fenster mit einem Video geöffnet ist.
     * @return true, wenn ein Fenster mit einem Video geöffnet ist, sonst false.
     */
    public boolean isVideoWindowOpen() {
        return videoWindow != null;
    }

    /**
     * Setzt die Information, ob die Kamera eingeschaltet ist.
     * @param cameraOn true, wenn die Kamera eingeschaltet ist, sonst false.
     */
    public void setCameraOn(final boolean cameraOn) {
        this.cameraOn = cameraOn;
    }

    /**
     * Setzt die Information, ob der aktuelle Bildschirm geteilt werden soll.
     * @param shareScreen true, wenn der Bildschirm geteilt werden soll, sonst false.
     */
    public void shareScreen(final boolean shareScreen) {
        this.shareScreen = shareScreen;
    }

    /**
     * Gibt zurück, ob die Kamera eingeschaltet ist.
     * @return true, wenn die Kamera eingeschaltet ist, sonst false.
     */
    public boolean isCameraOn() {
        return cameraOn;
    }

    /**
     * Gibt zurück, ob der aktuelle Bildschirm geteilt werden soll.
     * @return true, wenn der Bildschirm geteilt werden soll, sonst false.
     */
    public boolean shareScreen() {
        return shareScreen;
    }

    /**
     * Fügt einen Benutzer zum Videochat hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    private void addUserCamera(@NotNull final IUserView user) {
        if (userCameras.containsKey(user)) {
            return;
        }
        Monitor monitor = new Monitor(user, false, false);
        userCameras.put(user, monitor);
        Table videoChatUserContainer = new Table();
        videoChatUserContainer.add(monitor).size(VIDEO_WIDTH, VIDEO_HEIGHT + 30);
        Monitor findUser = null;
        try {
            findUser = userScreens.values().stream().filter(screenSharingUser ->
                    screenSharingUser.getUser().equals(monitor.getUser())).findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            // Ignoriere
        }
        if (findUser != null && findUser.getParent() != null) {
            videoChatGroup.addActorAfter(findUser.getParent(), videoChatUserContainer);
        } else {
            IInternUserView internUser = Chati.CHATI.getInternUser();
            if (internUser != null && internUser.equals(monitor.getUser())) {
                videoChatGroup.addActorAt(0, videoChatUserContainer);
            } else {
                videoChatGroup.addActor(videoChatUserContainer);
            }
        }
    }

    /**
     * Fügt den geteilten Bildschirm eines Benutzers zum Videochat hinzu.
     * @param user Hinzuzufügender Benutzer
     */
    private void addUserScreen(@NotNull final IUserView user) {
        if (userScreens.containsKey(user)) {
            return;
        }
        Monitor screenSharingUser = new Monitor(user, true, false);
        userScreens.put(user, screenSharingUser);
        Table screenSharingUserContainer = new Table();
        screenSharingUserContainer.add(screenSharingUser).size(VIDEO_WIDTH, VIDEO_HEIGHT + 30);
        Monitor findUser = null;
        try {
            findUser = userCameras.values().stream().filter(monitor ->
                    monitor.getUser().equals(screenSharingUser.getUser())).findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            // Ignoriere
        }
        if (findUser != null && findUser.getParent() != null) {
            videoChatGroup.addActorBefore(findUser.getParent(), screenSharingUserContainer);
        } else {
            IInternUserView internUser = Chati.CHATI.getInternUser();
            if (internUser != null && internUser.equals(screenSharingUser.getUser())) {
                videoChatGroup.addActorAt(0, screenSharingUserContainer);
            } else {
                videoChatGroup.addActor(screenSharingUserContainer);
            }
        }
    }

    @Override
    public void translate() {
        getTitleLabel().setText(Chati.CHATI.getLocalization().translate("window.title.video-chat"));

        if (videoWindow != null) {
            videoWindow.translate();
        }
    }

    /**
     * Eine Klasse, welche anzuzeigende Videostreams des Videochats repräsentiert.
     */
    public class Monitor extends Table {

        private static final float MAX_INACTIVE_TIME = 3; // In Sekunden

        private final IUserView user;
        private final boolean screen;
        private final Texture videoTexture;
        private final Image talkingBackgroundImage;
        private LocalDateTime lastFrameTime;
        private boolean underCursor;

        /**
         * Erzeugt eine neue Instanz eines Monitor.
         * @param user Zugehöriger Benutzer.
         * @param screen falls true, repräsentiert diese Instanz einen geteilten Bildschirm, sonst einen mit der Kamera
         * am Videochat teilnehmenden Benutzer.
         * @param window falls true, wird dieser Monitor in einem separaten Fenster angezeigt, sonst im Fenster des
         * VideoChat.
         */
        public Monitor(@NotNull final IUserView user, final boolean screen, final boolean window) {
            this.user = user;
            this.screen = screen;
            Stack videoChatUserStack = new Stack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = (int) screenSize.getWidth();
            int screenHeight = (int) screenSize.getHeight();

            Pixmap talkingBackground = new Pixmap(screenWidth, screenHeight, RGB888);
            talkingBackground.setColor(Color.WHITE);
            talkingBackground.fill();
            talkingBackgroundImage = new Image(new Texture(talkingBackground));
            talkingBackgroundImage.setColor(Color.BLACK);
            Table talkingBackgroundContainer = new Table();
            talkingBackgroundContainer.add(talkingBackgroundImage).grow();
            videoChatUserStack.add(talkingBackgroundContainer);

            videoTexture = new Texture(screenWidth, screenHeight, RGB888);
            Image videoImage = new Image(videoTexture);
            Table videoImageContainer = new Table();
            videoImageContainer.add(videoImage).center().pad(SPACE / 4).grow();
            videoChatUserStack.add(videoImageContainer);

            videoChatUserStack.addListener(new ClickListener() {
                @Override
                public void enter(@NotNull final InputEvent event, final float x, final float y,
                                  final int pointer, @Nullable final Actor fromActor) {
                    if (pointer == -1 && !window) {
                        underCursor = true;
                    }
                }
                @Override
                public void exit(@NotNull final InputEvent event, final float x, final float y,
                                 final int pointer, @Nullable final Actor toActor) {
                    if (pointer == -1 && !window) {
                        underCursor = false;
                        Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
                    }
                }
                @Override
                public boolean mouseMoved(@NotNull final InputEvent event, final float x, final float y) {
                    if (underCursor && !window) {
                        Gdx.graphics.setCursor(ChatiCursor.HAND.getCursor());
                    }
                    return true;
                }
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    openVideoWindow(Monitor.this);
                }
            });

            add(videoChatUserStack).grow().row();
            add(new UserInfoContainer(user)).growX();
        }

        @Override
        public boolean remove() {
            if (getParent() != null) {
                return getParent().remove();
            }
            return super.remove();
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
            if (show && !screen) {
                talkingBackgroundImage.setColor(Color.GREEN);
            } else {
                talkingBackgroundImage.setColor(Color.BLACK);
            }
        }

        /**
         * Gibt zurück, ob in einer festgelegten Zeit ein Frame von diesem Benutzer eingegangen ist.
         * @return true, falls ein Frame eingegangen ist, sonst false.
         */
        public boolean isActive() {
            if (lastFrameTime == null) {
                lastFrameTime = LocalDateTime.now();
                return screen ? user.canShare() : user.canShow();
            }
            IInternUserView internUser = Chati.CHATI.getInternUser();
            if (user.equals(internUser)) {
                if (screen && !shareScreen || !screen && !cameraOn) {
                    return false;
                }
            }
            return LocalDateTime.now().minus((long) (1000 * MAX_INACTIVE_TIME), ChronoUnit.MILLIS)
                    .isBefore(lastFrameTime) && (screen ? user.canShare() : user.canShow());
        }

        /**
         * Gibt den zugehörigen Benutzer zurück.
         * @return Zugehöriger Benutzer.
         */
        public @NotNull IUserView getUser() {
            return user;
        }

        /**
         * Gibt zurück, ob diese Instanz einen geteilten Bildschirm, oder einen mit der Kamera am Videochat teilnehmenden
         * Benutzer repräsentiert.
         * @return true, falls diese Instanz einen geteilten Bildschirm repräsentiert, sonst false.
         */
        public boolean isScreen() {
            return screen;
        }
    }

    /**
     * Eine Klasse, welche ein Fenster zur Anzeige eines einzelnen Videos repräsentiert.
     */
    private class VideoWindow extends ResizableWindow implements Translatable {

        private final String titleKey;
        private final Monitor user;

        /**
         * Erzeugt eine neue Instanz des VideoWindow.
         * @param titleKey Kennung des anzuzeigenden Titels.
         * @param monitor Anzuzeigender Benutzer.
         */
        public VideoWindow(@NotNull final String titleKey, @NotNull final Monitor monitor) {
            super(Chati.CHATI.getLocalization().format(titleKey, monitor.getUser().getUsername()));
            this.titleKey = titleKey;
            this.user = monitor;

            add(monitor).minSize(ScreenRecorder.FRAME_WIDTH, ScreenRecorder.FRAME_HEIGHT + 35).grow();

            TextButton closeButton = new TextButton("X", Chati.CHATI.getSkin());
            closeButton.setDisabled(true);
            closeButton.addListener(new ChatiTooltip("hud.tooltip.close"));
            closeButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    remove();
                }
            });

            setModal(false);
            setMovable(true);
            setResizable(true);
            setKeepWithinStage(true);
            float width = 2 * VIDEO_WIDTH + getPadX();
            float height = 2 * VIDEO_HEIGHT + 35 + getPadY();
            setSize(width, height);
            setPosition((Gdx.graphics.getWidth() - width) / 2, (Gdx.graphics.getHeight() - height) / 2);

            getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
        }

        @Override
        public boolean remove() {
            VideoChatWindow.this.videoWindow = null;
            return super.remove();
        }

        public @NotNull VideoChatWindow.Monitor getUser() {
            return user;
        }

        @Override
        public void translate() {
            getTitleLabel().setText(Chati.CHATI.getLocalization().format(titleKey, user.getUser().getUsername()));
        }
    }
}
