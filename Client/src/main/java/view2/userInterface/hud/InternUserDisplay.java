package view2.userInterface.hud;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import controller.network.ServerSender;
import model.user.IInternUserView;
import model.user.Status;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiToolTip;
import view2.userInterface.UserInfoContainer;

public class InternUserDisplay extends Table {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;

    private StatusSelectTable statusSelectTable;
    private final Image statusImage;

    public InternUserDisplay() {
        UserInfoContainer userInfoContainer = new UserInfoContainer(Chati.CHATI.getUserManager().getInternUserView());

        ChatiTextButton statusButton = new ChatiTextButton("", false);
        statusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
    public void act(float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
            if (internUser != null) {
                setStatusImage();
            }
        }
        super.act(delta);
    }

    private void setStatusImage() {
        IInternUserView internUser = Chati.CHATI.getInternUser();

        statusImage.getListeners().forEach(statusImage::removeListener);
        switch (internUser.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_online"));
                statusImage.addListener(new ChatiToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_away"));
                statusImage.addListener(new ChatiToolTip("Abwesend"));
                break;
            case BUSY:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_busy"));
                statusImage.addListener(new ChatiToolTip("Beschäftigt"));
                break;
            case INVISIBLE:
                statusImage.setDrawable(Chati.CHATI.getDrawable("status_invisible"));
                statusImage.addListener(new ChatiToolTip("Unsichtbar"));
                break;
            case OFFLINE:
                throw new IllegalArgumentException("Intern user cannot be offline");
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }
    }

    private static class StatusSelectTable extends Table {

        public StatusSelectTable() {
            IInternUserView internUser = Chati.CHATI.getInternUser();

            Image onlineStatusImage = new Image(Chati.CHATI.getDrawable("status_online"));
            Image busyStatusImage = new Image(Chati.CHATI.getDrawable("status_busy"));
            Image invisibleStatusImage = new Image(Chati.CHATI.getDrawable("status_invisible"));

            ChatiTextButton onlineStatusButton = new ChatiTextButton("", false);
            onlineStatusButton.addListener(new ChatiToolTip("Online"));
            if (internUser.getStatus() == Status.ONLINE || internUser.getStatus() == Status.AWAY) {
                onlineStatusButton.setChecked(true);
            }
            onlineStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !onlineStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, Status.ONLINE);
                }
            });

            ChatiTextButton busyStatusButton = new ChatiTextButton("", false);
            busyStatusButton.addListener(new ChatiToolTip("Beschäftigt"));
            if (internUser.getStatus() == Status.BUSY) {
                busyStatusButton.setChecked(true);
            }
            busyStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !busyStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, Status.BUSY);
                }
            });

            ChatiTextButton invisibleStatusButton = new ChatiTextButton("", false);
            invisibleStatusButton.addListener(new ChatiToolTip("Unsichtbar"));
            if (internUser.getStatus() == Status.INVISIBLE) {
                invisibleStatusButton.setChecked(true);
            }
            invisibleStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !invisibleStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, Status.INVISIBLE);
                }
            });

            ButtonGroup<ChatiTextButton> statusButtonGroup = new ButtonGroup<>();
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