package view.UIComponents;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import controller.network.ServerSender;
import model.user.AdministrativeAction;
import model.user.IUserView;
import org.lwjgl.system.CallbackI;

public class UsersListTable extends PopupMenu {
    private String currentList;

    public UsersListTable(DropDownMenu menu) {
        super(menu);
        setName("users-list-table");

        TextButton users = new TextButton("Benutzer", skin);
        users.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ScrollPane list = usersList();
                getCell(findActor(currentList)).setActor(list).expand().top();
                currentList = list.getName();

            }
        });
        TextButton friends = new TextButton("Freunde", skin);
        friends.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ScrollPane list = friendsList();
                getCell(findActor(currentList)).setActor(list).expand().top();
                currentList = list.getName();
            }
        });
        SplitPane splitPane = new SplitPane(users, friends, false, skin);
        add(splitPane).fill();
        row();
        ScrollPane list = usersList();
        add(list).expand().top();
        currentList = list.getName();
    }

    private ScrollPane usersList() {
        Table users = new Table();
        TextButton button = new TextButton("button1", skin);
        users.add(button).width(getWidth());

        ScrollPane scrollPane = new ScrollPane(users);
        scrollPane.setName("users-list");
        return scrollPane;
    }

    private ScrollPane friendsList() {
        Table users = new Table();

        /*
        for( var friend : dropDownMenu.getHud().getApplicationScreen().getGame().getUserManager().getFriends().entrySet()) {
           ListElement element = new ListElement(friend.getValue());
           users.addActor(element);
        } */

        ScrollPane scrollPane = new ScrollPane(users);
        scrollPane.setName("friends-list");
        return scrollPane;
    }

    private class ListElement extends VerticalGroup {

        public ListElement(IUserView user) {
            Label name = new Label(user.getUsername(), skin);
            HorizontalGroup actions = new HorizontalGroup();  // Aktionen hinzufügen

            add(name);
            row();
            add(actions);
        }


        private class ActionBlock extends Container {
            private boolean isBlocked;
            IUserView userView;

            public ActionBlock(IUserView userView) {
                this.isBlocked = userView.isBanned();
                create();
            }

            private void create() {
                Image image;
                if (isBlocked) {
                    image = new Image(new Texture("icons/sperren.svg"));
                    image.addListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            return true;
                        }

                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            Object[] data = {userView.getUserId(), AdministrativeAction.BAN_USER, ""};
                            dropDownMenu.getHud().getApplicationScreen().getGame().getServerSender().send(ServerSender.SendAction.USER_MANAGE, data);
                            isBlocked = false;
                            create();
                        }
                    });
                } else {
                    image = new Image(new Texture("icons/entsperren.svg"));
                    image.addListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            return true;
                        }

                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            Object[] data = {userView.getUserId(), AdministrativeAction.UNBAN_USER, ""};
                            dropDownMenu.getHud().getApplicationScreen().getGame().getServerSender().send(ServerSender.SendAction.USER_MANAGE, data);
                            isBlocked = true;
                            create();
                        }
                    });
                }
                this.setActor(image);
            }
        }

        private class ActionFriend extends Container {
            private boolean isFriend;
            private IUserView userView;

            public ActionFriend (IUserView userView) {
                isFriend= userView.isFriend();
                create();
            }

            private void create() {
                Image image;
                if(isFriend) {
                    image = new Image(new Texture("icons/freund_entfernen.svg"));
                    image.addListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            return true;
                        }

                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            Object[] data = {userView.getUserId(), AdministrativeAction.REMOVE_FRIEND, ""};
                            dropDownMenu.getHud().getApplicationScreen().getGame().getServerSender().send(ServerSender.SendAction.USER_MANAGE, data);
                            isFriend = false;
                            create();
                        }
                    });
                } else {
                    image = new Image(new Texture("icons/freund_hinzufugen.svg"));
                    image.addListener(new InputListener(){
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            return true;
                        }

                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            Object[] data = {userView.getUserId(), AdministrativeAction.INVITE_FRIEND, ""};
                            dropDownMenu.getHud().getApplicationScreen().getGame().getServerSender().send(ServerSender.SendAction.USER_MANAGE, data);
                            isFriend = false;
                            create();
                        }
                    });
                }

                this.setActor(image);
            }
        }

        private class ActionIgnore extends Container {
            private boolean isIgnored;
            private IUserView userView;

            public ActionIgnore () {

            }

        }
    }
}