package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {

    public static final TextureRegionDrawable USER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/userIcon.png")));
    public static final TextureRegionDrawable CHECKED_USER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/userIconChecked.png")));
    public static final TextureRegionDrawable NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/notificationIcon.png")));
    public static final TextureRegionDrawable CHECKED_NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/notificationIconChecked.png")));
    public static final TextureRegionDrawable SETTINGS_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/settingsIcon.png")));
    public static final TextureRegionDrawable CHECKED_SETTINGS_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/settingsIconChecked.png")));
    public static final TextureRegionDrawable CHAT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/chatIcon.png")));
    public static final TextureRegionDrawable CHECKED_CHAT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/chatIcon_checked.png")));

    public static final TextureRegionDrawable ONLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/online.png")));
    public static final TextureRegionDrawable AWAY_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/away.png")));
    public static final TextureRegionDrawable BUSY_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/busy.png")));
    public static final TextureRegionDrawable OFFLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/offline.png")));

    public static final TextureRegionDrawable OWNER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_gold.png")));
    public static final TextureRegionDrawable ADMINISTRATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_silver.png")));
    public static final TextureRegionDrawable MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_bronze.png")));
    public static final TextureRegionDrawable ROOM_OWNER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_blue.png")));
    public static final TextureRegionDrawable AREA_MANAGER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/crown_green.png")));

    public static final TextureRegionDrawable ADD_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addFriend_color.png")));
    public static final TextureRegionDrawable DISABLED_ADD_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addFriend_grey.png")));
    public static final TextureRegionDrawable REMOVE_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeFriend_color.png")));
    public static final TextureRegionDrawable DISABLED_REMOVE_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeFriend_grey.png")));
    public static final TextureRegionDrawable IGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ignore_color.png")));
    public static final TextureRegionDrawable DISABLED_IGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ignore_grey.png")));
    public static final TextureRegionDrawable UNIGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unignore_color.png")));
    public static final TextureRegionDrawable DISABLED_UNIGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unignore_grey.png")));
    public static final TextureRegionDrawable ROOM_INVITE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_invite_color.png")));
    public static final TextureRegionDrawable DISABLED_ROOM_INVITE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_invite_grey.png")));
    public static final TextureRegionDrawable ROOM_KICK_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_kick_color.png")));
    public static final TextureRegionDrawable DISABLED_ROOM_KICK_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_kick_grey.png")));
    public static final TextureRegionDrawable TELEPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_teleport_color.png")));
    public static final TextureRegionDrawable DISABLED_TELEPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_teleport_grey.png")));
    public static final TextureRegionDrawable REPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_report_color.png")));
    public static final TextureRegionDrawable DISABLED_REPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_report_grey.png")));
    public static final TextureRegionDrawable MUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_mute_color.png")));
    public static final TextureRegionDrawable DISABLED_MUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_mute_grey.png")));
    public static final TextureRegionDrawable UNMUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unmute_color.png")));
    public static final TextureRegionDrawable DISABLED_UNMUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unmute_grey.png")));
    public static final TextureRegionDrawable BAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ban_color.png")));
    public static final TextureRegionDrawable DISABLED_BAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_ban_grey.png")));
    public static final TextureRegionDrawable UNBAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unban_color.png")));
    public static final TextureRegionDrawable DISABLED_UNBAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_unban_grey.png")));
    public static final TextureRegionDrawable ASSIGN_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addModerator_color.png")));
    public static final TextureRegionDrawable DISABLED_ASSIGN_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_addModerator_grey.png")));
    public static final TextureRegionDrawable WITHDRAW_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeModerator_color.png")));
    public static final TextureRegionDrawable DISABLED_WITHDRAW_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_removeModerator_grey.png")));

    public static final TextureRegionDrawable ACCEPT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_accept_color.png")));
    public static final TextureRegionDrawable DISABLED_ACCEPT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_accept_grey.png")));
    public static final TextureRegionDrawable DECLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_decline_color.png")));
    public static final TextureRegionDrawable DISABLED_DECLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_decline_grey.png")));
    public static final TextureRegionDrawable DELETE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_delete_color.png")));
    public static final TextureRegionDrawable DISABLED_DELETE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/new_delete_grey.png")));

    public static final Skin SKIN = new Skin();
    private static final int TITLE_FONT_SIZE = 20;
    private static final int LABEL_FONT_SIZE = 22;
    private static final int BUTTON_FONT_SIZE = 18;
    private static final int WORLD_FONT_SIZE = 30;

    public static BitmapFont WORLD_FONT;


    private Assets() {
    }

    public static void initialize() {
        initializeSkin(SKIN);
        initializeWorldFont();
    }

    public static Skin getNewSkin() {
        Skin skin = new Skin();
        initializeSkin(skin);
        return skin;
    }

    private static void initializeSkin(Skin skin) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.size = TITLE_FONT_SIZE;
        BitmapFont fontTitle = generator.generateFont(parameter);
        fontTitle.setUseIntegerPositions(false);
        parameter.size = LABEL_FONT_SIZE;
        BitmapFont fontLabel = generator.generateFont(parameter);
        fontLabel.setUseIntegerPositions(false);
        parameter.size = BUTTON_FONT_SIZE;
        BitmapFont fontButton = generator.generateFont(parameter);
        fontButton.setUseIntegerPositions(false);
        generator.dispose();
        skin.add("font-title", fontTitle);
        skin.add("font-label", fontLabel);
        skin.add("font-button", fontButton);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("shadeui/uiskin.atlas")));
        skin.load(Gdx.files.internal("shadeui/uiskin.json"));
    }

    private static void initializeWorldFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = WORLD_FONT_SIZE;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.DARK_GRAY;
        WORLD_FONT = generator.generateFont(parameter);
        WORLD_FONT.getData().setScale(0.05f);
        WORLD_FONT.setUseIntegerPositions(false);
    }
}
