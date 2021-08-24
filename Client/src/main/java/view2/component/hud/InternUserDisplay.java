package view2.component.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import model.role.Role;
import model.user.IInternUserView;
import model.user.Status;
import view2.Chati;
import view2.Assets;
import view2.component.ChatiTable;
import view2.component.ChatiToolTip;

import java.util.ArrayList;
import java.util.List;

public class InternUserDisplay extends HudMenuTable {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;

    private Table roleIconContainer;
    private StatusSelectTable statusSelectTable;

    private final List<Image> roleIcons;
    private Image statusImage;
    private Label usernameLabel;
    private TextButton statusButton;

    public InternUserDisplay() {
        this.roleIcons = new ArrayList<>();
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.getInstance().isUserInfoChanged() || Chati.getInstance().isWorldChanged()
                || Chati.getInstance().isRoomChanged()) {
            IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
            if (internUser != null) {
                //clear();
                //create();
                //setLayout();
                setStatusImage();
                setRoleImage();
            }
        }
        super.act(delta);
    }

    @Override
    protected void create() {
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        usernameLabel = new Label(internUser.getUsername(), Assets.SKIN);

        statusButton = new TextButton("", Assets.getNewSkin());
        statusButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (statusSelectTable == null) {
                    statusSelectTable = new StatusSelectTable();
                    add(statusSelectTable).top().left().row();
                    statusButton.getStyle().up = HudMenuTable.PRESSED_BUTTON_IMAGE;
                } else {
                    statusSelectTable.remove();
                    statusSelectTable = null;
                    statusButton.getStyle().up = HudMenuTable.UNPRESSED_BUTTON_IMAGE;
                }
            }
        });

        statusImage = new Image();
        setStatusImage();
        roleIconContainer = new Table();
        setRoleImage();
    }

    @Override
    protected void setLayout() {
        setFillParent(false);
        Table container = new Table();

        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Assets.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        container.setBackground(controlsBackground);

        container.left().defaults().top().padTop(VERTICAL_SPACING);

        Table userInfoContainer = new Table();
        statusButton.add(statusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                .padLeft(USER_INFO_ICON_SIZE).padRight(USER_INFO_ICON_SIZE);
        userInfoContainer.add(statusButton).width(2 * USER_INFO_ICON_SIZE).height(2 * USER_INFO_ICON_SIZE).space(HORIZONTAL_SPACING);
        userInfoContainer.add(usernameLabel).space(HORIZONTAL_SPACING);
        roleIconContainer.defaults().width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE).space(HORIZONTAL_SPACING / 2);
        userInfoContainer.add(roleIconContainer);

        container.add(userInfoContainer).left().padLeft(HORIZONTAL_SPACING).height(USER_INFO_ICON_SIZE).row();
        add(container).top().left().width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.BUTTON_SIZE).expandY().fillY().row();
    }

    private void setStatusImage() {
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        statusImage.getListeners().forEach(statusImage::removeListener);
        switch (internUser.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Assets.ONLINE_ICON);
                statusImage.addListener(new ChatiToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Assets.AWAY_ICON);
                statusImage.addListener(new ChatiToolTip("Abwesend"));
                break;
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }
    }

    private void setRoleImage() {
        roleIcons.clear();
        roleIconContainer.clear();
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        if (internUser.hasRole(Role.OWNER)) {
            usernameLabel.setColor(Color.GOLD);
            Image ownerImage = new Image(Assets.OWNER_ICON);
            ownerImage.addListener(new ChatiToolTip("Besitzer"));
            roleIcons.add(ownerImage);
        } else if (internUser.hasRole(Role.ADMINISTRATOR)) {
            usernameLabel.setColor(Color.SKY);
            Image administratorImage = new Image(Assets.ADMINISTRATOR_ICON);
            administratorImage.addListener(new ChatiToolTip("Administrator"));
            roleIcons.add(administratorImage);
        } else if (internUser.hasRole(Role.MODERATOR)) {
            usernameLabel.setColor(Color.ORANGE);
            Image moderatorImage = new Image(Assets.MODERATOR_ICON);
            moderatorImage.addListener(new ChatiToolTip("Moderator"));
            roleIcons.add(moderatorImage);
        } else {
            usernameLabel.setColor(Color.WHITE);
        }
        if (internUser.hasRole(Role.ROOM_OWNER)) {
            Image roomOwnerImage = new Image(Assets.ROOM_OWNER_ICON);
            roomOwnerImage.addListener(new ChatiToolTip("Raumbesitzer"));
            roleIcons.add(roomOwnerImage);
        }
        if (internUser.hasRole(Role.AREA_MANAGER)) {
            Image areaManagerImage = new Image(Assets.AREA_MANAGER_ICON);
            areaManagerImage.addListener(new ChatiToolTip("Bereichsberechtigter"));
            roleIcons.add(areaManagerImage);
        }
        roleIcons.forEach(roleIcon -> roleIconContainer.add(roleIcon));
    }

    private static class StatusSelectTable extends ChatiTable {

        ButtonGroup<TextButton> statusButtonGroup;
        private Image onlineStatusImage;
        private Image busyStatusImage;
        private Image offlineStatusImage;
        private TextButton onlineStatusButton;
        private TextButton busyStatusButton;
        private TextButton offlineStatusButton;

        public StatusSelectTable() {
            create();
            setLayout();
        }

        @Override
        protected void create() {
            IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
            statusButtonGroup = new ButtonGroup<>();
            statusButtonGroup.setMinCheckCount(1);
            statusButtonGroup.setMaxCheckCount(1);
            statusButtonGroup.setUncheckLast(true);

            onlineStatusImage = new Image(Assets.ONLINE_ICON);
            busyStatusImage = new Image(Assets.BUSY_ICON);
            offlineStatusImage = new Image(Assets.OFFLINE_ICON);

            onlineStatusButton = new TextButton("", Assets.getNewSkin());
            onlineStatusButton.addListener(new ChatiToolTip("Online"));
            if (internUser.getStatus() == Status.ONLINE || internUser.getStatus() == Status.AWAY) {
                onlineStatusButton.setChecked(true);
                onlineStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
            }
            onlineStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !onlineStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    onlineStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
                    busyStatusButton.getStyle().up = UNPRESSED_BUTTON_IMAGE;
                    offlineStatusButton.getStyle().up = UNPRESSED_BUTTON_IMAGE;
                }
            });

            busyStatusButton = new TextButton("", Assets.getNewSkin());
            busyStatusButton.addListener(new ChatiToolTip("Beschäftigt"));
            /*
            if (internUser.getStatus() == Status.BUSY) {
                busyStatusButton.setChecked(true);
                busyStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
            }

             */

            busyStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !busyStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    onlineStatusButton.getStyle().up = UNPRESSED_BUTTON_IMAGE;
                    busyStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
                    offlineStatusButton.getStyle().up = UNPRESSED_BUTTON_IMAGE;
                }
            });

            offlineStatusButton = new TextButton("", Assets.getNewSkin());
            offlineStatusButton.addListener(new ChatiToolTip("Unsichtbar"));
            /*
            if (internUser.getStatus() == Status.INVISIBLE) {
                offlineStatusButton.setChecked(true);
                offlineStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
            }

             */

            offlineStatusButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return !offlineStatusButton.isChecked();
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    onlineStatusButton.getStyle().up = UNPRESSED_BUTTON_IMAGE;
                    busyStatusButton.getStyle().up = UNPRESSED_BUTTON_IMAGE;
                    offlineStatusButton.getStyle().up = PRESSED_BUTTON_IMAGE;
                }
            });

            statusButtonGroup.add(onlineStatusButton);
            statusButtonGroup.add(busyStatusButton);
            statusButtonGroup.add(offlineStatusButton);
        }

        @Override
        protected void setLayout() {
            Table container = new Table();

            NinePatchDrawable controlsBackground =
                    new NinePatchDrawable(new NinePatch(Assets.SKIN.getRegion("panel1"), 10, 10, 10, 10));
            container.setBackground(controlsBackground);

            onlineStatusButton.add(onlineStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);
            busyStatusButton.add(busyStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);
            offlineStatusButton.add(offlineStatusImage).size(USER_INFO_ICON_SIZE).pad(USER_INFO_ICON_SIZE);

            container.add(onlineStatusButton).size(2 * USER_INFO_ICON_SIZE)
                   .spaceBottom(VERTICAL_SPACING).padTop(VERTICAL_SPACING).row();
            container.add(busyStatusButton).size(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).row();
            container.add(offlineStatusButton).size(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).padBottom(VERTICAL_SPACING);

            add(container).top().left().width(HeadUpDisplay.BUTTON_SIZE + HORIZONTAL_SPACING)
                    .padTop(HeadUpDisplay.BUTTON_SIZE - VERTICAL_SPACING).expand();
        }
    }
}