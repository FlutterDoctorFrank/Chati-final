package view2.component.hud;

import com.badlogic.gdx.Gdx;
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
import view2.Icon;
import view2.component.ChatiTable;
import view2.component.ChatiToolTip;

import java.util.ArrayList;
import java.util.List;

public class InternUserDisplay extends HudMenuTable {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;
    private static final float LABEL_FONT_SCALE_FACTOR = 1.5f;

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
                clear();
                create();
                setLayout();
            }
        }
        super.act(delta);
    }

    @Override
    protected void create() {
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        usernameLabel = new Label(internUser.getUsername(), Chati.SKIN);
        usernameLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        setRoleImage();

        setStatusImage();

        Skin statusButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        statusButton = new TextButton("", statusButtonSkin);
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
    }

    @Override
    protected void setLayout() {
        setFillParent(false);
        Table container = new Table();

        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Chati.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        container.setBackground(controlsBackground);

        container.left().defaults().top().padTop(VERTICAL_SPACING);

        Table userInfoContainer = new Table();
        statusButton.add(statusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                .padLeft(USER_INFO_ICON_SIZE).padRight(USER_INFO_ICON_SIZE);
        userInfoContainer.add(statusButton).width(2 * USER_INFO_ICON_SIZE).height(2 * USER_INFO_ICON_SIZE).space(HORIZONTAL_SPACING);
        userInfoContainer.add(usernameLabel).space(HORIZONTAL_SPACING);
        roleIcons.forEach(roleIcon -> userInfoContainer.add(roleIcon).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                .space(HORIZONTAL_SPACING / 2));

        container.add(userInfoContainer).left().padLeft(HORIZONTAL_SPACING).height(USER_INFO_ICON_SIZE).row();
        add(container).top().left().width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.BUTTON_SIZE).expandY().fillY().row();
    }

    private void setStatusImage() {
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        statusImage = new Image();
        switch (internUser.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Icon.ONLINE_ICON);
                statusImage.addListener(new ChatiToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Icon.AWAY_ICON);
                statusImage.addListener(new ChatiToolTip("Abwesend"));
                break;
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }
    }

    private void setRoleImage() {
        roleIcons.clear();
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        if (internUser.hasRole(Role.OWNER)) {
            usernameLabel.setColor(Color.GOLD);
            Image ownerImage = new Image(Icon.OWNER_ICON);
            ownerImage.addListener(new ChatiToolTip("Besitzer"));
            roleIcons.add(ownerImage);
        } else if (internUser.hasRole(Role.ADMINISTRATOR)) {
            usernameLabel.setColor(Color.SKY);
            Image administratorImage = new Image(Icon.ADMINISTRATOR_ICON);
            administratorImage.addListener(new ChatiToolTip("Administrator"));
            roleIcons.add(administratorImage);
        } else if (internUser.hasRole(Role.MODERATOR)) {
            usernameLabel.setColor(Color.ORANGE);
            Image moderatorImage = new Image(Icon.MODERATOR_ICON);
            moderatorImage.addListener(new ChatiToolTip("Moderator"));
            roleIcons.add(moderatorImage);
        }
        if (internUser.hasRole(Role.ROOM_OWNER)) {
            Image roomOwnerImage = new Image(Icon.ROOM_OWNER_ICON);
            roomOwnerImage.addListener(new ChatiToolTip("Raumbesitzer"));
            roleIcons.add(roomOwnerImage);
        }
        if (internUser.hasRole(Role.AREA_MANAGER)) {
            Image areaManagerImage = new Image(Icon.AREA_MANAGER_ICON);
            areaManagerImage.addListener(new ChatiToolTip("Bereichsberechtigter"));
            roleIcons.add(areaManagerImage);
        }
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

            onlineStatusImage = new Image(Icon.ONLINE_ICON);
            busyStatusImage = new Image(Icon.BUSY_ICON);
            offlineStatusImage = new Image(Icon.OFFLINE_ICON);

            Skin onlineStatusButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
            onlineStatusButton = new TextButton("", onlineStatusButtonSkin);
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

            Skin busyStatusButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
            busyStatusButton = new TextButton("", busyStatusButtonSkin);
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

            Skin offlineStatusButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
            offlineStatusButton = new TextButton("", offlineStatusButtonSkin);
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
                    new NinePatchDrawable(new NinePatch(Chati.SKIN.getRegion("panel1"), 10, 10, 10, 10));
            container.setBackground(controlsBackground);

            onlineStatusButton.add(onlineStatusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                    .padLeft(USER_INFO_ICON_SIZE).padRight(USER_INFO_ICON_SIZE);;
            busyStatusButton.add(busyStatusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                    .padLeft(USER_INFO_ICON_SIZE).padRight(USER_INFO_ICON_SIZE);;
            offlineStatusButton.add(offlineStatusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                    .padLeft(USER_INFO_ICON_SIZE).padRight(USER_INFO_ICON_SIZE);;

            container.add(onlineStatusButton).width(2 * USER_INFO_ICON_SIZE).height(2 * USER_INFO_ICON_SIZE)
                    .spaceBottom(VERTICAL_SPACING).padTop(VERTICAL_SPACING).row();
            container.add(busyStatusButton).width(2 * USER_INFO_ICON_SIZE).height(2 * USER_INFO_ICON_SIZE).
                    spaceBottom(VERTICAL_SPACING).row();
            container.add(offlineStatusButton).width(2 * USER_INFO_ICON_SIZE).height(2 * USER_INFO_ICON_SIZE).
                    spaceBottom(VERTICAL_SPACING).padBottom(VERTICAL_SPACING);

            add(container).top().left().width(HeadUpDisplay.BUTTON_SIZE + HORIZONTAL_SPACING)
                    .padTop(HeadUpDisplay.BUTTON_SIZE - VERTICAL_SPACING).expand();
        }
    }
}