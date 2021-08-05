package view.UIComponents;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.notification.INotificationView;

import java.util.UUID;

public class NotificationsTable extends PopupMenu {
    private String currentList;

    public NotificationsTable(DropDownMenu menu) {
        super(menu);
        setName("users-list-table");

        TextButton generalNotifications = new TextButton("Allgemein", skin);
        generalNotifications.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ScrollPane list = generalList();
                getCell(findActor(currentList)).setActor(list).expand().top();
                currentList = list.getName();

            }
        });
        TextButton worldNotifications = new TextButton("Welt", skin);
        worldNotifications.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ScrollPane list = worldNotificationsList();
                getCell(findActor(currentList)).setActor(list).expand().top();
                currentList = list.getName();
            }
        });
        SplitPane splitPane = new SplitPane(generalNotifications, worldNotifications, false, skin);
        add(splitPane).fill();
        row();
        ScrollPane list = generalList();
        add(list).expand().top();
        currentList = list.getName();
    }

    private ScrollPane generalList() {
        Table users = new Table();
        TextButton button = new TextButton("button1", skin);
        users.add(button).width(getWidth());

        /*
        Map<UUID, INotificationView> globalNotifications = dropDownMenu.getHud().getApplicationScreen().getGame().getUserManager().getInternUserController().getGlobalNotifications();
        for(var entry : globalNotifications.entrySet()) {
            new Notification(entry.getKey(), entry.getValue());

        }
         */

        ScrollPane scrollPane = new ScrollPane(users);
        scrollPane.setName("users-list");
        return scrollPane;
    }

    private ScrollPane worldNotificationsList() {
        Table users = new Table();



        ScrollPane scrollPane = new ScrollPane(users);
        scrollPane.setName("world-notifications-list");
        return scrollPane;
    }

    private class Notification extends HorizontalGroup{

        public Notification (UUID id, INotificationView iNotificationView) {
           // String message = iNotificationView.getMessageBundle();
            Label notification = new Label("", skin);
            add(notification);
        }
    }
}