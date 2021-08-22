package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {

    public static final Drawable USER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/userIcon.png")));
    public static final Drawable CHECKED_USER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/userIconChecked.png")));
    public static final Drawable NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/notificationIcon.png")));
    public static final Drawable CHECKED_NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/notificationIconChecked.png")));
    public static final Drawable SETTINGS_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/settingsIcon.png")));
    public static final Drawable CHECKED_SETTINGS_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/settingsIconChecked.png")));
    public static final Drawable CHAT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/chatIcon.png")));
    public static final Drawable CHECKED_CHAT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/chatIcon_checked.png")));

    public static final Drawable ONLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/online.png")));
    public static final Drawable AWAY_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/away.png")));
    public static final Drawable BUSY_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/busy.png")));
    public static final Drawable OFFLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/offline.png")));

    public static final Drawable OWNER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_gold.png")));
    public static final Drawable ADMINISTRATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_silver.png")));
    public static final Drawable MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_bronze.png")));
    public static final Drawable ROOM_OWNER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_blue.png")));
    public static final Drawable AREA_MANAGER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_green.png")));

    public static final Drawable ADD_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addFriend_color.png")));
    public static final Drawable DISABLED_ADD_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addFriend_grey.png")));
    public static final Drawable REMOVE_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeFriend_color.png")));
    public static final Drawable DISABLED_REMOVE_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeFriend_grey.png")));
    public static final Drawable IGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ignore_color.png")));
    public static final Drawable DISABLED_IGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ignore_grey.png")));
    public static final Drawable UNIGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unignore_color.png")));
    public static final Drawable DISABLED_UNIGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unignore_grey.png")));
    public static final Drawable ROOM_INVITE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_invite_color.png")));
    public static final Drawable DISABLED_ROOM_INVITE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_invite_grey.png")));
    public static final Drawable ROOM_KICK_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_kick_color.png")));
    public static final Drawable DISABLED_ROOM_KICK_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_kick_grey.png")));
    public static final Drawable TELEPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_teleport_color.png")));
    public static final Drawable DISABLED_TELEPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_teleport_grey.png")));
    public static final Drawable REPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_report_color.png")));
    public static final Drawable DISABLED_REPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_report_grey.png")));
    public static final Drawable MUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_mute_color.png")));
    public static final Drawable DISABLED_MUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_mute_grey.png")));
    public static final Drawable UNMUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unmute_color.png")));
    public static final Drawable DISABLED_UNMUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unmute_grey.png")));
    public static final Drawable BAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ban_color.png")));
    public static final Drawable DISABLED_BAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ban_grey.png")));
    public static final Drawable UNBAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unban_color.png")));
    public static final Drawable DISABLED_UNBAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unban_grey.png")));
    public static final Drawable ASSIGN_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addModerator_color.png")));
    public static final Drawable DISABLED_ASSIGN_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addModerator_grey.png")));
    public static final Drawable WITHDRAW_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeModerator_color.png")));
    public static final Drawable DISABLED_WITHDRAW_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeModerator_grey.png")));

    public static final Drawable ACCEPT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_accept_color.png")));
    public static final Drawable DISABLED_ACCEPT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_accept_grey.png")));
    public static final Drawable DECLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_decline_color.png")));
    public static final Drawable DISABLED_DECLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_decline_grey.png")));
    public static final Drawable DELETE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_delete_color.png")));
    public static final Drawable DISABLED_DELETE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_delete_grey.png")));

    public static final Skin SKIN = new Skin();

    private Assets() {
    }

    public static void initialize() {
        initializeSkin(SKIN);
    }

    public static Skin getNewSkin() {
        Skin skin = new Skin();
        initializeSkin(skin);
        return skin;
    }

    private static void initializeSkin(Skin skin) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        BitmapFont fontTitle = generator.generateFont(parameter);
        parameter.size = 22;
        BitmapFont fontLabel = generator.generateFont(parameter);
        parameter.size = 18;
        BitmapFont fontButton = generator.generateFont(parameter);
        generator.dispose();
        skin.add("font-title", fontTitle);
        skin.add("font-label", fontLabel);
        skin.add("font-button", fontButton);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("shadeui/uiskin.atlas")));
        skin.load(Gdx.files.internal("shadeui/uiskin.json"));
    }
}
