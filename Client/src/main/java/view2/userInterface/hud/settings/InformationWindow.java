package view2.userInterface.hud.settings;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiWindow;

public class InformationWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 650;

    protected InformationWindow() {
        super("window.title.information", WINDOW_WIDTH, WINDOW_HEIGHT);

        infoLabel = new ChatiLabel("window.entry.information");

        InformationWindowLabel keyBindingsLabel = new InformationWindowLabel("menu.label.key-bindings");

        InformationWindowLabel movementKeysLable = new InformationWindowLabel("menu.label.movement-keys");
        InformationWindowLabel moveUpLabel = new InformationWindowLabel("menu.label.movement-up");
        InformationWindowLabel moveUpKeyLabel = new InformationWindowLabel("menu.label.movement-up-key");
        InformationWindowLabel moveDownLabel = new InformationWindowLabel("menu.label.movement-down");
        InformationWindowLabel moveDownKesLabel = new InformationWindowLabel("menu.label.movement-down-key");
        InformationWindowLabel moveLeftLabel = new InformationWindowLabel("menu.label.movement-left");
        InformationWindowLabel moveLeftKeyLabel = new InformationWindowLabel("menu.label.movement-left-key");
        InformationWindowLabel moveRightLabel = new InformationWindowLabel("menu.label.movement-right");
        InformationWindowLabel moveRightKeyLabel = new InformationWindowLabel("menu.label.movement-right-key");
        InformationWindowLabel sprintLabel = new InformationWindowLabel("menu.label.movement-sprint");
        InformationWindowLabel sprintKeyLabel = new InformationWindowLabel("menu.label.movement-sprint-key");

        InformationWindowLabel functionKeysLable = new InformationWindowLabel("menu.label.function-keys");
        InformationWindowLabel interactionLabel = new InformationWindowLabel("menu.label.interaction");
        InformationWindowLabel interactionKeyLabel = new InformationWindowLabel("menu.label.interaction-key");
        InformationWindowLabel openUsersListLabel = new InformationWindowLabel("menu.label.open-users-list");
        InformationWindowLabel openUsersListKeyLabel = new InformationWindowLabel("menu.label.open-users-list-key");
        InformationWindowLabel openNotificationsListLabel = new InformationWindowLabel("menu.label.open-notifications");
        InformationWindowLabel openNotificationsKeyLabel = new InformationWindowLabel("menu.label.open-notifications-key");
        InformationWindowLabel openSettingsLabel = new InformationWindowLabel("menu.label.open-settings");
        InformationWindowLabel openSettingsKeyLabel = new InformationWindowLabel("menu.label.open-settings-key");
        InformationWindowLabel openCommunicationWindowLabel = new InformationWindowLabel("menu.label.open-communication-window");
        InformationWindowLabel openCommunicationWindowKeyLabel = new InformationWindowLabel("menu.label.open-communication-window-key");
        InformationWindowLabel muteMicrophoneLabel = new InformationWindowLabel("menu.label.mute-microphone");
        InformationWindowLabel muteMicrophoneKeyLabel = new InformationWindowLabel("menu.label.mute-microphone-key");
        InformationWindowLabel muteSoundsLabel = new InformationWindowLabel("menu.label.mute-sounds");
        InformationWindowLabel muteSoundsKeyLabel = new InformationWindowLabel("menu.label.mute-sounds-key");

        InformationWindowLabel chatCommandsLabel = new InformationWindowLabel("menu.label.chat-commands");
        InformationWindowLabel chatWorldMessageLabel = new InformationWindowLabel("menu.label.chat-world-message");
        InformationWindowLabel chatWorldMessageCommandLabel = new InformationWindowLabel("menu.label.chat-world-message-command");
        InformationWindowLabel chatRoomMessageLable = new InformationWindowLabel("menu.label.chat-room-message");
        InformationWindowLabel chatRoomMessageCommandLable = new InformationWindowLabel("menu.label.chat-room-message-command");
        InformationWindowLabel chatUserMessageLable = new InformationWindowLabel("menu.label.chat-user-message");
        InformationWindowLabel chatUserMessageCommandLable = new InformationWindowLabel("menu.label.chat-user-message-command");

        infoLabel.setAlignment(Align.center, Align.center);

        // Layout
        setModal(true);
        setMovable(false);


        Table container = new Table();
        ScrollPane informationScrollPane = new ScrollPane(container, Chati.CHATI.getSkin());
        informationScrollPane.setFadeScrollBars(false);
        container.defaults().spaceBottom(SPACING / 2).center().growX();
        container.add(infoLabel).row();

        Table keyBindingsContainer = new Table();
        keyBindingsContainer.defaults().colspan(2).padLeft(SPACING / 2).growX();
        keyBindingsContainer.add(keyBindingsLabel).row();
        //Bewegungstasten
        Table movementKeysContainer = new Table();
        movementKeysContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        movementKeysContainer.add(movementKeysLable).row();
        Table moveUpContainer = new Table();
        moveUpContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        moveUpContainer.add(moveUpLabel);
        moveUpContainer.add(moveUpKeyLabel);
        Table moveDownContainer = new Table();
        moveDownContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        moveDownContainer.add(moveDownLabel);
        moveDownContainer.add(moveDownKesLabel);
        Table moveLeftContainer = new Table();
        moveLeftContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        moveLeftContainer.add(moveLeftLabel);
        moveLeftContainer.add(moveLeftKeyLabel);
        Table moveRightContainer = new Table();
        moveRightContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        moveRightContainer.add(moveRightLabel);
        moveRightContainer.add(moveRightKeyLabel);
        Table sprintContainer = new Table();
        sprintContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        sprintContainer.add(sprintLabel);
        sprintContainer.add(sprintKeyLabel);
        movementKeysContainer.add(moveUpContainer).row();
        movementKeysContainer.add(moveDownContainer).row();
        movementKeysContainer.add(moveLeftContainer).row();
        movementKeysContainer.add(moveRightContainer).row();
        movementKeysContainer.add(sprintContainer).row();
        keyBindingsContainer.add(movementKeysContainer).row();
        //Funktionstasten
        Table functionKeysContainer = new Table();
        functionKeysContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        functionKeysContainer.add(functionKeysLable).row();
        Table interactContainer = new Table();
        interactContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        interactContainer.add(interactionLabel);
        interactContainer.add(interactionKeyLabel);
        Table openUsersListContainer = new Table();
        openUsersListContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        openUsersListContainer.add(openUsersListLabel);
        openUsersListContainer.add(openUsersListKeyLabel);
        Table openNotificationsListContainer = new Table();
        openNotificationsListContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        openNotificationsListContainer.add(openNotificationsListLabel);
        openNotificationsListContainer.add(openNotificationsKeyLabel);
        Table openSettingsContainer = new Table();
        openSettingsContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        openSettingsContainer.add(openSettingsLabel);
        openSettingsContainer.add(openSettingsKeyLabel);
        Table openCommunicationWindowContainer = new Table();
        openCommunicationWindowContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        openCommunicationWindowContainer.add(openCommunicationWindowLabel);
        openCommunicationWindowContainer.add(openCommunicationWindowKeyLabel);
        Table muteMicrophoneContainer = new Table();
        muteMicrophoneContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        muteMicrophoneContainer.add(muteMicrophoneLabel);
        muteMicrophoneContainer.add(muteMicrophoneKeyLabel);
        Table muteSoundContainer = new Table();
        muteSoundContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        muteSoundContainer.add(muteSoundsLabel);
        muteSoundContainer.add(muteSoundsKeyLabel);
        functionKeysContainer.add(interactContainer).row();
        functionKeysContainer.add(openUsersListContainer).row();
        functionKeysContainer.add(openNotificationsListContainer).row();
        functionKeysContainer.add(openSettingsContainer).row();
        functionKeysContainer.add(openCommunicationWindowContainer).row();
        functionKeysContainer.add(muteMicrophoneContainer).row();
        functionKeysContainer.add(muteSoundContainer).row();
        keyBindingsContainer.add(functionKeysContainer).row();

        container.add(keyBindingsContainer).row();

        Table commandsContainer = new Table();
        commandsContainer.defaults().colspan(2).padLeft(SPACING / 2).height(ROW_HEIGHT).growX();
        commandsContainer.add(chatCommandsLabel).row();
        Table worldMessageContainer = new Table();
        worldMessageContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        worldMessageContainer.add(chatWorldMessageLabel);
        worldMessageContainer.add(chatWorldMessageCommandLabel);
        Table roomMessageContainer = new Table();
        roomMessageContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        roomMessageContainer.add(chatRoomMessageLable);
        roomMessageContainer.add(chatRoomMessageCommandLable);
        Table userMessageContainer = new Table();
        userMessageContainer.defaults().colspan(2).padLeft(2 * SPACING).height(ROW_HEIGHT).growX();
        userMessageContainer.add(chatUserMessageLable);
        userMessageContainer.add(chatUserMessageCommandLable);
        commandsContainer.add(worldMessageContainer).row();
        commandsContainer.add(roomMessageContainer).row();
        commandsContainer.add(userMessageContainer).row();
        container.add(commandsContainer).row();

        add(informationScrollPane).padLeft(SPACING).padRight(SPACING).grow();

        // Translatable register
        translates.add(infoLabel);
        translates.add(keyBindingsLabel);
        translates.add(functionKeysLable);
        translates.add(movementKeysLable);
        translates.add(chatCommandsLabel);
        translates.trimToSize();
    }

    private class InformationWindowLabel extends ChatiLabel{

        private static final float LABEL_WIDTH = 450;

        public InformationWindowLabel(@NotNull String messageKey) {
            super(messageKey);
            setWidth(LABEL_WIDTH);
            setWrap(true);
        }
    }
}
