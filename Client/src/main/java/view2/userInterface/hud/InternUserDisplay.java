package view2.userInterface.hud;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import controller.network.ServerSender;
import model.user.IInternUserView;
import model.user.Status;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTooltip;
import view2.userInterface.UserInfoContainer;

/**
 * Eine Klasse, welche die Anzeige der Informationen des intern angemeldeten Benutzers repräsentiert.
 */
public class InternUserDisplay extends Table {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;

    private StatusSelectTable statusSelectTable;
    private final Image statusImage;

    /**
     * Erzeugt eine neue Instanz des InternUserDisplay.
     */
    public InternUserDisplay() {
        UserInfoContainer userInfoContainer = new UserInfoContainer(Chati.CHATI.getInternUser());

        TextButton statusButton = new TextButton("", Chati.CHATI.getSkin());
        statusButton.setDisabled(false);
        statusButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (statusSelectTable == null) {
                    statusSelectTable = new StatusSelectTable();
                    add(statusSelectTable).top().left().row();
                } else {
                    statusSelectTable.remove();
                    statusSelectTable = null;
                }
            }
        });

        statusImage = new Image();
        setStatusImage();

        // Layout
        setFillParent(true);
        top().left();
        Table container = new Table();

        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Chati.CHATI.getSkin().getRegion("panel1"), 10, 10, 10, 10));
        container.setBackground(controlsBackground);
        container.left().defaults().top().padTop(VERTICAL_SPACING);

        statusButton.add(statusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                .padLeft(USER_INFO_ICON_SIZE).padRight(USER_INFO_ICON_SIZE);
        container.add(statusButton).width(2 * USER_INFO_ICON_SIZE).height(2 * USER_INFO_ICON_SIZE)
                .space(HORIZONTAL_SPACING).padLeft(HORIZONTAL_SPACING);
        container.add(userInfoContainer).left().center().height(USER_INFO_ICON_SIZE).row();
        add(container).top().left().width(HudMenuWindow.HUD_WINDOW_WIDTH)
                .height(HeadUpDisplay.BUTTON_SIZE).grow().row();
    }

    @Override
    public void act(final float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            if (internUser != null) {
                setStatusImage();
            }
        }
        super.act(delta);
    }

    /**
     * Setzt das aktuell anzuzeigende Statussymbol.
     */
    private void setStatusImage() {
        IInternUserView internUser = Chati.CHATI.getInternUser();

        if (internUser == null) {
            return;
        }

        statusImage.getListeners().forEach(statusImage::removeListener);
        switch (internUser.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_online"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.online"));
                break;
            case AWAY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_away"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.away"));
                break;
            case BUSY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_busy"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.busy"));
                break;
            case INVISIBLE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_invisible"));
                statusImage.addListener(new ChatiTooltip("hud.tooltip.invisible"));
                break;
            case OFFLINE:
                throw new IllegalArgumentException("Intern user cannot be offline");
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }
    }

    /**
     * Eine Klasse, welche das Menü zur Auswahl des Status repräsentiert.
     */
    private static class StatusSelectTable extends Table {

        /**
         * Erzeugt eine neue Instanz des StatusSelectTable.
         */
        public StatusSelectTable() {
            IInternUserView internUser = Chati.CHATI.getInternUser();

            if (internUser == null) {
                return;
            }

            Image onlineStatusImage = new Image(Chati.CHATI.getDrawable("status_online"));
            Image busyStatusImage = new Image(Chati.CHATI.getDrawable("status_busy"));
            Image invisibleStatusImage = new Image(Chati.CHATI.getDrawable("status_invisible"));

            TextButton onlineStatusButton = new TextButton("", Chati.CHATI.getSkin());
            onlineStatusButton.setDisabled(false);
            onlineStatusButton.addListener(new ChatiTooltip("hud.tooltip.online"));
            if (internUser.getStatus() == Status.ONLINE || internUser.getStatus() == Status.AWAY) {
                onlineStatusButton.setChecked(true);
            }
            onlineStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                         final int pointer, final int button) {
                    return !onlineStatusButton.isChecked();
                }
                @Override
                public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                    final int pointer, final int button) {
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, Status.ONLINE);
                }
            });

            TextButton busyStatusButton = new TextButton("", Chati.CHATI.getSkin());
            busyStatusButton.setDisabled(false);
            busyStatusButton.addListener(new ChatiTooltip("hud.tooltip.busy"));
            if (internUser.getStatus() == Status.BUSY) {
                busyStatusButton.setChecked(true);
            }
            busyStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                         final int pointer, final int button) {
                    return !busyStatusButton.isChecked();
                }
                @Override
                public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                    final int pointer, final int button) {
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, Status.BUSY);
                }
            });

            TextButton invisibleStatusButton = new TextButton("", Chati.CHATI.getSkin());
            invisibleStatusButton.addListener(new ChatiTooltip("hud.tooltip.invisible"));
            if (internUser.getStatus() == Status.INVISIBLE) {
                invisibleStatusButton.setChecked(true);
            }
            invisibleStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                         final int pointer, final int button) {
                    return !invisibleStatusButton.isChecked();
                }
                @Override
                public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                    final int pointer, final int button) {
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, Status.INVISIBLE);
                }
            });

            ButtonGroup<TextButton> statusButtonGroup = new ButtonGroup<>();
            statusButtonGroup.setMinCheckCount(1);
            statusButtonGroup.setMaxCheckCount(1);
            statusButtonGroup.setUncheckLast(true);
            statusButtonGroup.add(onlineStatusButton);
            statusButtonGroup.add(busyStatusButton);
            statusButtonGroup.add(invisibleStatusButton);

            // Layout
            setFillParent(true);
            Table container = new Table();

            NinePatchDrawable controlsBackground =
                    new NinePatchDrawable(new NinePatch(Chati.CHATI.getSkin().getRegion("panel1"), 10, 10, 10, 10));
            container.setBackground(controlsBackground);

            onlineStatusButton.add(onlineStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);
            busyStatusButton.add(busyStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);
            invisibleStatusButton.add(invisibleStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);

            container.add(onlineStatusButton).size(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).padTop(VERTICAL_SPACING).row();
            container.add(busyStatusButton).size(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).row();
            container.add(invisibleStatusButton).size(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).padBottom(VERTICAL_SPACING);

            add(container).top().left().width(HeadUpDisplay.BUTTON_SIZE + HORIZONTAL_SPACING)
                    .padTop(HeadUpDisplay.BUTTON_SIZE - VERTICAL_SPACING).expand();
        }
    }
}