package view2.userInterface.hud.settings;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiWindow;

public class InformationWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 650;

    private final ScrollPane informationScrollPane;

    protected InformationWindow() {
        super("window.title.information", WINDOW_WIDTH, WINDOW_HEIGHT);

        infoLabel = new ChatiLabel("window.entry.information");

        InformationWindowLabel keyBindingsLabel = new InformationWindowLabel("menu.label.key-bindings");

        InformationWindowLabel movementKeysLable = new InformationWindowLabel("menu.label.movement-keys");
        InformationWindowLabel moveUpLabel = new InformationWindowLabel("menu.label.movement-up");
        InformationWindowLabel moveUpKeyLabel = new InformationWindowLabel("menu.label.movement-up-key");
        InformationWindowLabel moveDownLabel = new InformationWindowLabel("menu.label.movement-down");
        InformationWindowLabel moveDownKeyLabel = new InformationWindowLabel("menu.label.movement-down-key");
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

        InformationWindowLabel commandsLabel = new InformationWindowLabel("menu.label.commands");
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
        informationScrollPane = new ScrollPane(container, Chati.CHATI.getSkin());
        informationScrollPane.setFadeScrollBars(false);
        container.defaults().pad(SPACING / 2).center().growX();
        container.add(infoLabel).row();

        Table keyBindingsContainer = new Table();
        keyBindingsContainer.defaults().colspan(2).padLeft(SPACING).padBottom(SPACING).growX();
        keyBindingsContainer.add(keyBindingsLabel).row();
        //Bewegungstasten
        Table movementKeysContainer = new Table();
        movementKeysContainer.defaults().colspan(2).padLeft(2 * SPACING).padBottom(SPACING).growX();
        movementKeysContainer.add(movementKeysLable).row();
        Table moveUpContainer = new Table();
        moveUpContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        moveUpContainer.add(moveUpLabel);
        moveUpContainer.add(moveUpKeyLabel);
        Table moveDownContainer = new Table();
        moveDownContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        moveDownContainer.add(moveDownLabel);
        moveDownContainer.add(moveDownKeyLabel);
        Table moveLeftContainer = new Table();
        moveLeftContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        moveLeftContainer.add(moveLeftLabel);
        moveLeftContainer.add(moveLeftKeyLabel);
        Table moveRightContainer = new Table();
        moveRightContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        moveRightContainer.add(moveRightLabel);
        moveRightContainer.add(moveRightKeyLabel);
        Table sprintContainer = new Table();
        sprintContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
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
        functionKeysContainer.defaults().colspan(2).padLeft(2 * SPACING).padBottom(SPACING).growX();
        functionKeysContainer.add(functionKeysLable).row();
        Table interactContainer = new Table();
        interactContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        interactContainer.add(interactionLabel);
        interactContainer.add(interactionKeyLabel);
        Table openUsersListContainer = new Table();
        openUsersListContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        openUsersListContainer.add(openUsersListLabel);
        openUsersListContainer.add(openUsersListKeyLabel);
        Table openNotificationsListContainer = new Table();
        openNotificationsListContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        openNotificationsListContainer.add(openNotificationsListLabel);
        openNotificationsListContainer.add(openNotificationsKeyLabel);
        Table openSettingsContainer = new Table();
        openSettingsContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        openSettingsContainer.add(openSettingsLabel);
        openSettingsContainer.add(openSettingsKeyLabel);
        Table openCommunicationWindowContainer = new Table();
        openCommunicationWindowContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        openCommunicationWindowContainer.add(openCommunicationWindowLabel);
        openCommunicationWindowContainer.add(openCommunicationWindowKeyLabel);
        Table muteMicrophoneContainer = new Table();
        muteMicrophoneContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        muteMicrophoneContainer.add(muteMicrophoneLabel);
        muteMicrophoneContainer.add(muteMicrophoneKeyLabel);
        Table muteSoundContainer = new Table();
        muteSoundContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
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
        commandsContainer.defaults().colspan(2).padLeft(SPACING).padBottom(SPACING).growX();
        commandsContainer.add(commandsLabel).row();
        Table chatCommandsContainer = new Table();
        chatCommandsContainer.defaults().colspan(2).padLeft(2 * SPACING).padBottom(SPACING).growX();
        chatCommandsContainer.add(chatCommandsLabel).row();
        Table worldMessageContainer = new Table();
        worldMessageContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        worldMessageContainer.add(chatWorldMessageLabel);
        worldMessageContainer.add(chatWorldMessageCommandLabel);
        Table roomMessageContainer = new Table();
        roomMessageContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        roomMessageContainer.add(chatRoomMessageLable);
        roomMessageContainer.add(chatRoomMessageCommandLable);
        Table userMessageContainer = new Table();
        userMessageContainer.defaults().colspan(2).padLeft(2 * SPACING).growX();
        userMessageContainer.add(chatUserMessageLable);
        userMessageContainer.add(chatUserMessageCommandLable);
        chatCommandsContainer.add(worldMessageContainer).row();
        chatCommandsContainer.add(roomMessageContainer).row();
        chatCommandsContainer.add(userMessageContainer).row();
        commandsContainer.add(chatCommandsContainer).row();
        container.add(commandsContainer).row();

        add(informationScrollPane).padLeft(SPACING).padRight(SPACING).grow();

        // Translatable register
        translates.add(infoLabel);
        translates.add(keyBindingsLabel);
        translates.add(movementKeysLable);
        translates.add(moveUpLabel);
        translates.add(moveUpKeyLabel);
        translates.add(moveDownLabel);
        translates.add(moveDownKeyLabel);
        translates.add(moveLeftLabel);
        translates.add(moveLeftKeyLabel);
        translates.add(moveRightLabel);
        translates.add(moveRightKeyLabel);
        translates.add(sprintLabel);
        translates.add(sprintKeyLabel);
        translates.add(functionKeysLable);
        translates.add(interactionLabel);
        translates.add(interactionKeyLabel);
        translates.add(openUsersListLabel);
        translates.add(openUsersListKeyLabel);
        translates.add(openNotificationsListLabel);
        translates.add(openNotificationsKeyLabel);
        translates.add(openSettingsLabel);
        translates.add(openSettingsKeyLabel);
        translates.add(openCommunicationWindowLabel);
        translates.add(openCommunicationWindowKeyLabel);
        translates.add(muteMicrophoneLabel);
        translates.add(muteMicrophoneKeyLabel);
        translates.add(muteSoundsLabel);
        translates.add(muteSoundsKeyLabel);
        translates.add(commandsLabel);
        translates.add(chatCommandsLabel);
        translates.add(chatWorldMessageLabel);
        translates.add(chatWorldMessageCommandLabel);
        translates.add(chatRoomMessageLable);
        translates.add(chatRoomMessageCommandLable);
        translates.add(chatUserMessageLable);
        translates.add(chatUserMessageCommandLable);
        translates.trimToSize();
    }

    @Override
    public void open() {
        super.open();

        Chati.CHATI.getScreen().getStage().setScrollFocus(informationScrollPane);
    }

    private static class InformationWindowLabel extends ChatiLabel {

        private static final float LABEL_WIDTH = 450;

        public InformationWindowLabel(@NotNull final String messageKey) {
            super(messageKey);
            setWidth(LABEL_WIDTH);
            setWrap(true);
        }
    }
}
