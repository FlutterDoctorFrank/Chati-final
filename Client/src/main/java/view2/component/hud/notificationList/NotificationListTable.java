package view2.component.hud.notificationList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.MessageBundle;
import model.notification.Notification;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;
import view2.component.hud.HudMenuTable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class NotificationListTable extends HudMenuTable {

    private ButtonGroup<TextButton> tabButtonGroup;
    private TextButton globalNotificationTabButton;
    private TextButton worldNotificationTabButton;
    private ScrollPane notificationListScrollPane;
    private Table notificationListContainer;

    private Set<NotificationListEntry> globalNotificationEntries;
    private Set<NotificationListEntry> worldNotificationEntries;

    public NotificationListTable() {
        this.globalNotificationEntries = new HashSet<>();
        this.worldNotificationEntries = new HashSet<>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    private void enableGlobalNotificationTab() {
    }

    private void enableWorldNotificationTab() {
    }

    @Override
    protected void create() {
        tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);

        notificationListContainer = new Table(Chati.SKIN);
        notificationListScrollPane = new ScrollPane(notificationListContainer, Chati.SKIN);

        Skin globalNotificationTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        globalNotificationTabButton = new TextButton("Global", globalNotificationTabButtonSkin);
        globalNotificationTabButton.setStyle(HeadUpDisplay.disabledStyle);
        globalNotificationTabButton.getLabel().setColor(Color.DARK_GRAY);
        globalNotificationTabButton.setDisabled(true);

        Skin worldNotificationTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        worldNotificationTabButton = new TextButton("Welt", worldNotificationTabButtonSkin);
        worldNotificationTabButton.setStyle(HeadUpDisplay.disabledStyle);
        worldNotificationTabButton.getLabel().setColor(Color.DARK_GRAY);
        worldNotificationTabButton.setDisabled(true);
    }

    @Override
    protected void setLayout() {
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE);
        Window window = new Window("Benachrichtigungen", Chati.SKIN);
        window.setMovable(false);
        window.top();

        // Test ///////////////////////////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i<200; i++) {
            Notification notification = new Notification(UUID.randomUUID(), null, new MessageBundle("eifhbewrofhef"), LocalDateTime.now(), new Random().nextBoolean());
            NotificationListEntry entry = new NotificationListEntry(notification);
            notificationListContainer.top().left().add(entry).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX() * 2).row();
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Table buttonContainer = new Table(Chati.SKIN);
        buttonContainer.add(globalNotificationTabButton).width((HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX()) / 2);
        buttonContainer.add(worldNotificationTabButton).width((HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX()) / 2);
        window.add(buttonContainer).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX()).row();

        window.add(notificationListScrollPane).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX())
                .height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT - HeadUpDisplay.HUD_MENU_TABLE_TAB_HEIGHT - window.getPadY());
        add(window).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT);
    }
}
