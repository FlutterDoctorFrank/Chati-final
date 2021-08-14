package view2.component.hud.internUserDisplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import model.role.Role;
import model.user.IInternUserView;
import view2.Chati;
import view2.Texture;
import view2.component.ChatiToolTip;

import java.util.ArrayList;
import java.util.List;

public class InternUserDisplay extends Table {

    private static final float USER_INFO_ICON_SIZE = 22.5f;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 10;
    private static final float LABEL_FONT_SCALE_FACTOR = 1.5f;

    private final IInternUserView internUser;
    private final List<Image> roleIcons;
    private Image statusImage;
    private Label usernameLabel;

    public InternUserDisplay() {
        this.internUser = Chati.getInstance().getUserManager().getInternUserView();
        this.roleIcons = new ArrayList<>();
        create();
        setLayout();
    }

    private void create() {
        usernameLabel = new Label(internUser.getUsername(), Chati.SKIN);
        usernameLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        if (internUser.hasRole(Role.OWNER)) {
            usernameLabel.setColor(Color.GOLD);
            Image ownerImage = new Image(Texture.OWNER_ICON);
            ownerImage.addListener(new ChatiToolTip("Besitzer"));
            roleIcons.add(ownerImage);
        } else if (internUser.hasRole(Role.ADMINISTRATOR)) {
            usernameLabel.setColor(Color.SKY);
            Image administratorImage = new Image(Texture.ADMINISTRATOR_ICON);
            administratorImage.addListener(new ChatiToolTip("Administrator"));
            roleIcons.add(administratorImage);
        } else if (internUser.hasRole(Role.MODERATOR)) {
            usernameLabel.setColor(Color.ORANGE);
            Image moderatorImage = new Image(Texture.MODERATOR_ICON);
            moderatorImage.addListener(new ChatiToolTip("Moderator"));
            roleIcons.add(moderatorImage);
        }
        if (internUser.hasRole(Role.ROOM_OWNER)) {
            Image roomOwnerImage = new Image(Texture.ROOM_OWNER_ICON);
            roomOwnerImage.addListener(new ChatiToolTip("Raumbesitzer"));
            roleIcons.add(roomOwnerImage);
        }
        if (internUser.hasRole(Role.AREA_MANAGER)) {
            Image areaManagerImage = new Image(Texture.AREA_MANAGER_ICON);
            areaManagerImage.addListener(new ChatiToolTip("Bereichsberechtigter"));
            roleIcons.add(areaManagerImage);
        }

        statusImage = new Image();
        switch (internUser.getStatus()) {
            case ONLINE:
                statusImage.setDrawable(Texture.ONLINE_ICON);
                statusImage.addListener(new ChatiToolTip("Online"));
                break;
            case AWAY:
                statusImage.setDrawable(Texture.AWAY_ICON);
                statusImage.addListener(new ChatiToolTip("Abwesend"));
                break;
            default:
                throw new IllegalArgumentException("There is no icon for this user status.");
        }
        // Potentieller Button um Status selbst zu ändern ? Beschäftigt, Unsichtbar, custom Status...
    }

    private void setLayout() {
        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Chati.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        setBackground(controlsBackground);

        left().defaults().padTop(VERTICAL_SPACING);

        Table userInfoContainer = new Table();
        userInfoContainer.add(statusImage).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE).space(HORIZONTAL_SPACING);
        userInfoContainer.add(usernameLabel).space(HORIZONTAL_SPACING);
        roleIcons.forEach(roleIcon -> userInfoContainer.add(roleIcon).width(USER_INFO_ICON_SIZE).height(USER_INFO_ICON_SIZE)
                .space(HORIZONTAL_SPACING / 2));

        add(userInfoContainer).left().padLeft(HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).height(USER_INFO_ICON_SIZE).row();
    }
}
