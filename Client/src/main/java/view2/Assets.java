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

    public static final TextureRegionDrawable USER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/user_menu_closed.png")));
    public static final TextureRegionDrawable CHECKED_USER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/user_menu_open.png")));
    public static final TextureRegionDrawable NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_menu_closed.png")));
    public static final TextureRegionDrawable CHECKED_NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_menu_open.png")));
    public static final TextureRegionDrawable SETTINGS_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/settings_menu_closed.png")));
    public static final TextureRegionDrawable CHECKED_SETTINGS_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/settings_menu_open.png")));
    public static final TextureRegionDrawable CHAT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/chat_closed.png")));
    public static final TextureRegionDrawable CHECKED_CHAT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/chat_open.png")));
    public static final TextureRegionDrawable COMMUNICABLE_LIST_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/communicable_list_closed.png")));
    public static final TextureRegionDrawable CHECKED_COMMUNICABLE_LIST_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/communicable_list_open.png")));
    public static final TextureRegionDrawable MIC_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/microphone_on.png")));
    public static final TextureRegionDrawable CHECKED_MIC_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/microphone_off.png")));
    public static final TextureRegionDrawable SPEAKER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/sound_on.png")));
    public static final TextureRegionDrawable CHECKED_SPEAKER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/sound_off.png")));

    public static final TextureRegionDrawable ONLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/status_online.png")));
    public static final TextureRegionDrawable AWAY_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/status_away.png")));
    public static final TextureRegionDrawable BUSY_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/status_busy.png")));
    public static final TextureRegionDrawable INVISIBLE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/status_invisible.png")));
    public static final TextureRegionDrawable OFFLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/status_offline.png")));

    public static final TextureRegionDrawable CURRENT_WORLD_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/current_world.png")));

    public static final TextureRegionDrawable OWNER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/role_owner.png")));
    public static final TextureRegionDrawable ADMINISTRATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/role_administrator.png")));
    public static final TextureRegionDrawable MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/role_moderator.png")));
    public static final TextureRegionDrawable ROOM_OWNER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/role_room_owner.png")));
    public static final TextureRegionDrawable AREA_MANAGER_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/role_area_manager.png")));

    public static final TextureRegionDrawable ADD_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_add_friend.png")));
    public static final TextureRegionDrawable REMOVE_FRIEND_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_remove_friend.png")));
    public static final TextureRegionDrawable IGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_ignore_user.png")));
    public static final TextureRegionDrawable UNIGNORE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_unignore_user.png")));
    public static final TextureRegionDrawable ROOM_INVITE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_invite_user.png")));
    public static final TextureRegionDrawable DISABLED_ROOM_INVITE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_invite_user_disabled.png")));
    public static final TextureRegionDrawable ROOM_KICK_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_kick_user.png")));
    public static final TextureRegionDrawable TELEPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_teleport_to_user.png")));
    public static final TextureRegionDrawable DISABLED_TELEPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_teleport_to_user_disabled.png")));
    public static final TextureRegionDrawable REPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_report_user.png")));
    public static final TextureRegionDrawable DISABLED_REPORT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_report_user_disabled.png")));
    public static final TextureRegionDrawable MUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_mute_user.png")));
    public static final TextureRegionDrawable DISABLED_MUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_mute_user_disabled.png")));
    public static final TextureRegionDrawable UNMUTE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_unmute_user.png")));
    public static final TextureRegionDrawable BAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_ban_user.png")));
    public static final TextureRegionDrawable DISABLED_BAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_ban_user_disabled.png")));
    public static final TextureRegionDrawable UNBAN_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_unban_user.png")));
    public static final TextureRegionDrawable ASSIGN_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_assign_moderator.png")));
    public static final TextureRegionDrawable DISABLED_ASSIGN_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_assign_moderator_disabled.png")));
    public static final TextureRegionDrawable WITHDRAW_MODERATOR_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/action_withdraw_moderator.png")));

    public static final TextureRegionDrawable NEW_NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_new.png")));
    public static final TextureRegionDrawable ACCEPTED_NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_accepted.png")));
    public static final TextureRegionDrawable DECLINED_NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_declined.png")));
    public static final TextureRegionDrawable PENDING_NOTIFICATION_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_pending.png")));

    public static final TextureRegionDrawable ACCEPT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_action_accept.png")));
    public static final TextureRegionDrawable DISABLED_ACCEPT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_action_accept_disabled.png")));
    public static final TextureRegionDrawable DECLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_action_decline.png")));
    public static final TextureRegionDrawable DISABLED_DECLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_action_decline_disabled.png")));
    public static final TextureRegionDrawable DELETE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/notification_action_delete.png")));

    public static final TextureRegionDrawable COMMUNICABLE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("images/communicable_avatar.png")));

    public static final TextureRegionDrawable PLAY_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_play.png")));
    public static final TextureRegionDrawable PAUSE_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_pause.png")));
    public static final TextureRegionDrawable PREVIOUS_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_back.png")));
    public static final TextureRegionDrawable NEXT_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_skip.png")));
    public static final TextureRegionDrawable STOP_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_stop.png")));
    public static final TextureRegionDrawable LOOPING_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_loop.png")));
    public static final TextureRegionDrawable RANDOM_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_random.png")));
    public static final TextureRegionDrawable VOLUME_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_volume.png")));

    public static final TextureRegionDrawable DISABLED_PLAY_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_play_disabled.png")));
    public static final TextureRegionDrawable DISABLED_PAUSE_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_pause_disabled.png")));
    public static final TextureRegionDrawable DISABLED_PREVIOUS_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_back_disabled.png")));
    public static final TextureRegionDrawable DISABLED_NEXT_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_skip_disabled.png")));
    public static final TextureRegionDrawable DISABLED_STOP_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_stop_disabled.png")));
    public static final TextureRegionDrawable DISABLED_LOOPING_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_loop_disabled.png")));
    public static final TextureRegionDrawable DISABLED_RANDOM_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_random_disabled.png")));
    public static final TextureRegionDrawable DISABLED_VOLUME_BUTTON_IMAGE = new TextureRegionDrawable(new TextureRegion(new Texture("images/music_volume_disabled.png")));


    public static final Skin SKIN = new Skin();
    private static final int TITLE_FONT_SIZE = 22;
    private static final int LABEL_FONT_SIZE = 23;
    private static final int BUTTON_FONT_SIZE = 21;

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
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.shadowColor = Color.DARK_GRAY;
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
}
