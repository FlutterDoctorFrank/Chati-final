package view.userInterface.hud.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.KeyCommand;
import view.userInterface.actor.ChatiLabel;
import view.userInterface.actor.ChatiWindow;

/**
 * Eine Klasse, welche ein Menü zum Einsehen von Informationen zur Anwendung repräsentiert.
 */
public class InformationWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 925;
    private static final float WINDOW_HEIGHT = 650;

    private final ScrollPane informationScrollPane;

    /**
     * Erzeugt eine neue Instanz des InformationWindow.
     */
    protected InformationWindow() {
        super("window.title.information", WINDOW_WIDTH, WINDOW_HEIGHT);

        infoLabel = new ChatiLabel("window.entry.information");

        Table informationContainer = new Table();
        informationScrollPane = new ScrollPane(informationContainer, Chati.CHATI.getSkin());
        informationScrollPane.setOverscroll(false, false);
        informationScrollPane.setFadeScrollBars(false);

        InformationLabel keyBindingsLabel = new InformationLabel("menu.label.key-bindings");

        InformationLabel movementKeysLabel = new InformationLabel("menu.label.movement-keys");
        InformationLabel moveUpLabel = new InformationLabel("menu.label.movement-up");
        Label moveUpKeyLabel = new Label(KeyCommand.MOVE_UP.toString(), Chati.CHATI.getSkin());
        InformationLabel moveLeftLabel = new InformationLabel("menu.label.movement-left");
        Label moveLeftKeyLabel = new Label(KeyCommand.MOVE_LEFT.toString(), Chati.CHATI.getSkin());
        InformationLabel moveDownLabel = new InformationLabel("menu.label.movement-down");
        Label moveDownKeyLabel = new Label(KeyCommand.MOVE_DOWN.toString(), Chati.CHATI.getSkin());
        InformationLabel moveRightLabel = new InformationLabel("menu.label.movement-right");
        Label moveRightKeyLabel = new Label(KeyCommand.MOVE_RIGHT.toString(), Chati.CHATI.getSkin());
        InformationLabel sprintLabel = new InformationLabel("menu.label.movement-sprint");
        Label sprintKeyLabel = new Label(KeyCommand.SPRINT.toString(), Chati.CHATI.getSkin());

        InformationLabel worldKeysLabel = new InformationLabel("menu.label.function-keys");
        InformationLabel showNamesLabel = new InformationLabel("menu.label.show-names");
        Label showNamesKeyLabel = new Label(KeyCommand.SHOW_NAMES.toString(), Chati.CHATI.getSkin());
        InformationLabel zoomLabel = new InformationLabel("menu.label.zoom");
        Label zoomKeyLabel = new Label(KeyCommand.ZOOM.toString(), Chati.CHATI.getSkin());
        InformationLabel interactLabel = new InformationLabel("menu.label.interact");
        Label interactKeyLabel = new Label(KeyCommand.INTERACT.toString(), Chati.CHATI.getSkin());
        InformationLabel sendChatMessageLabel = new InformationLabel("menu.label.send-message");
        Label sendChatMessageKeyLabel = new Label(KeyCommand.SEND_CHAT_MESSAGE.toString(), Chati.CHATI.getSkin());
        InformationLabel pushToTalkLabel = new InformationLabel("menu.label.push-to-talk");
        Label pushToTalkKeyLabel = new Label(KeyCommand.PUSH_TO_TALK.toString(), Chati.CHATI.getSkin());

        InformationLabel menuKeysLabel = new InformationLabel("menu.label.menu-keys");
        InformationLabel openChatLabel = new InformationLabel("menu.label.open-chat");
        Label openChatKeyLabel = new Label(KeyCommand.OPEN_CHAT.toString(), Chati.CHATI.getSkin());
        InformationLabel openUserMenuLabel = new InformationLabel("menu.label.open-user-menu");
        Label openUserMenuKeyLabel = new Label(KeyCommand.OPEN_USER_MENU.toString(), Chati.CHATI.getSkin());
        InformationLabel openNotificationMenuLabel = new InformationLabel("menu.label.open-notification-menu");
        Label openNotificationMenuKeyLabel = new Label(KeyCommand.OPEN_NOTIFICATION_MENU.toString(), Chati.CHATI.getSkin());
        InformationLabel openSettingsMenuLabel = new InformationLabel("menu.label.open-settings-menu");
        Label openSettingsMenuKeyLabel = new Label(KeyCommand.OPEN_SETTINGS_MENU.toString(), Chati.CHATI.getSkin());
        InformationLabel openCommunicationMenuLabel = new InformationLabel("menu.label.open-communication-menu");
        Label openCommunicationMenuKeyLabel = new Label(KeyCommand.OPEN_COMMUNICATION_MENU.toString(), Chati.CHATI.getSkin());
        InformationLabel toggleMicrophoneLabel = new InformationLabel("menu.label.toggle-microphone");
        Label toggleMicrophoneKeyLabel = new Label(KeyCommand.TOGGLE_MICROPHONE.toString(), Chati.CHATI.getSkin());
        InformationLabel toggleSoundLabel = new InformationLabel("menu.label.toggle-sound");
        Label toggleSoundKeyLabel = new Label(KeyCommand.TOGGLE_SOUND.toString(), Chati.CHATI.getSkin());
        InformationLabel toggleFullscreenLabel = new InformationLabel("menu.label.toggle-fullscreen");
        Label toggleFullscreenKeyLabel = new Label(KeyCommand.TOGGLE_FULLSCREEN.toString(), Chati.CHATI.getSkin());
        InformationLabel closeLabel = new InformationLabel("menu.label.close-window");
        Label closeKeyLabel = new Label(KeyCommand.CLOSE.toString(), Chati.CHATI.getSkin());
        InformationLabel openVideoChatLabel = new InformationLabel("menu.label.open-video-chat");
        Label openVideoChatKeyLabel = new Label(KeyCommand.OPEN_VIDEO_CHAT.toString(), Chati.CHATI.getSkin());

        InformationLabel commandsLabel = new InformationLabel("menu.label.commands");
        InformationLabel chatCommandsLabel = new InformationLabel("menu.label.chat-commands");
        InformationLabel chatWhisperMessageLabel = new InformationLabel("menu.label.chat-whisper-message");
        InformationLabel chatWhisperMessageCommandLabel = new InformationLabel("menu.label.chat-whisper-message-command");
        chatWhisperMessageCommandLabel.setColor(Color.GRAY);
        InformationLabel chatAreaMessageLabel = new InformationLabel("menu.label.chat-area-message");
        InformationLabel chatAreaMessageCommandLabel = new InformationLabel("menu.label.chat-area-message-command");
        chatAreaMessageCommandLabel.setColor(Color.LIME);
        InformationLabel chatRoomMessageLabel = new InformationLabel("menu.label.chat-room-message");
        InformationLabel chatRoomMessageCommandLabel = new InformationLabel("menu.label.chat-room-message-command");
        chatRoomMessageCommandLabel.setColor(Color.CORAL);
        InformationLabel chatWorldMessageLabel = new InformationLabel("menu.label.chat-world-message");
        InformationLabel chatWorldMessageCommandLabel = new InformationLabel("menu.label.chat-world-message-command");
        chatWorldMessageCommandLabel.setColor(Color.SKY);
        InformationLabel chatGlobalMessageLabel = new InformationLabel("menu.label.chat-global-message");
        InformationLabel chatGlobalMessageCommandLabel = new InformationLabel("menu.label.chat-global-message-command");
        chatGlobalMessageCommandLabel.setColor(Color.GOLD);

        // Layout
        setModal(true);
        setMovable(false);

        infoLabel.setAlignment(Align.center, Align.center);
        add(infoLabel).padTop(2 * SPACE).padBottom(2 * SPACE).row();

        informationContainer.top();
        informationContainer.defaults().growX();
        informationContainer.add(keyBindingsLabel).row();

        Table keyBindingsContainer = new Table();
        keyBindingsContainer.top();
        keyBindingsContainer.defaults().pad(SPACE).growX();
        keyBindingsContainer.add(keyBindingsLabel).row();

        Table movementKeyBindingsContainer = new Table();
        movementKeyBindingsContainer.top();
        movementKeyBindingsContainer.defaults().pad(SPACE).padLeft(2 * SPACE).growX();
        movementKeyBindingsContainer.add(movementKeysLabel).row();
        Table movementKeysTableContainer = new Table();
        movementKeysTableContainer.top();
        movementKeysTableContainer.defaults().colspan(2).pad(SPACE).padLeft(2 * SPACE).growX();
        movementKeysTableContainer.add(moveUpLabel, moveUpKeyLabel).row();
        movementKeysTableContainer.add(moveLeftLabel, moveLeftKeyLabel).row();
        movementKeysTableContainer.add(moveDownLabel, moveDownKeyLabel).row();
        movementKeysTableContainer.add(moveRightLabel, moveRightKeyLabel).row();
        movementKeysTableContainer.add(sprintLabel, sprintKeyLabel);
        movementKeyBindingsContainer.add(movementKeysTableContainer);

        Table worldKeyBindingsContainer = new Table();
        worldKeyBindingsContainer.top();
        worldKeyBindingsContainer.defaults().pad(SPACE).padLeft(2 * SPACE).growX();
        worldKeyBindingsContainer.add(worldKeysLabel).row();
        Table worldKeysTableContainer = new Table();
        worldKeysTableContainer.top();
        worldKeysTableContainer.defaults().colspan(2).pad(SPACE).padLeft(2 * SPACE).growX();
        worldKeysTableContainer.add(showNamesLabel, showNamesKeyLabel).row();
        worldKeysTableContainer.add(zoomLabel, zoomKeyLabel).row();
        worldKeysTableContainer.add(interactLabel, interactKeyLabel).row();
        worldKeysTableContainer.add(sendChatMessageLabel, sendChatMessageKeyLabel).row();
        worldKeysTableContainer.add(pushToTalkLabel, pushToTalkKeyLabel);
        worldKeyBindingsContainer.add(worldKeysTableContainer);

        Table menuKeyBindingsContainer = new Table();
        menuKeyBindingsContainer.top();
        menuKeyBindingsContainer.defaults().pad(SPACE).padLeft(2 * SPACE).growX();
        menuKeyBindingsContainer.add(menuKeysLabel).row();
        Table menuKeysTableContainer = new Table();
        menuKeysTableContainer.top();
        menuKeysTableContainer.defaults().colspan(2).pad(SPACE).padLeft(2 * SPACE).growX();
        menuKeysTableContainer.add(openChatLabel, openChatKeyLabel).row();
        menuKeysTableContainer.add(openVideoChatLabel, openVideoChatKeyLabel).row();
        menuKeysTableContainer.add(openUserMenuLabel, openUserMenuKeyLabel).row();
        menuKeysTableContainer.add(openNotificationMenuLabel, openNotificationMenuKeyLabel).row();
        menuKeysTableContainer.add(openSettingsMenuLabel, openSettingsMenuKeyLabel).row();
        menuKeysTableContainer.add(openCommunicationMenuLabel, openCommunicationMenuKeyLabel).row();
        menuKeysTableContainer.add(toggleMicrophoneLabel, toggleMicrophoneKeyLabel).row();
        menuKeysTableContainer.add(toggleSoundLabel, toggleSoundKeyLabel).row();
        menuKeysTableContainer.add(toggleFullscreenLabel, toggleFullscreenKeyLabel).row();
        menuKeysTableContainer.add(closeLabel, closeKeyLabel);
        menuKeyBindingsContainer.add(menuKeysTableContainer);

        keyBindingsContainer.add(movementKeyBindingsContainer).row();
        keyBindingsContainer.add(worldKeyBindingsContainer).row();
        keyBindingsContainer.add(menuKeyBindingsContainer);

        Table commandsContainer = new Table();
        commandsContainer.top();
        commandsContainer.defaults().pad(SPACE).growX();
        commandsContainer.add(commandsLabel).row();

        Table chatCommandsContainer = new Table();
        chatCommandsContainer.top();
        chatCommandsContainer.defaults().pad(SPACE).padLeft(2 * SPACE).growX();
        chatCommandsContainer.add(chatCommandsLabel).row();
        Table chatCommandsTableContainer = new Table();
        chatCommandsTableContainer.top();
        chatCommandsTableContainer.defaults().pad(SPACE).padLeft(2 * SPACE).growX();
        chatCommandsTableContainer.add(chatWhisperMessageLabel, chatWhisperMessageCommandLabel).row();
        chatCommandsTableContainer.add(chatAreaMessageLabel, chatAreaMessageCommandLabel).row();
        chatCommandsTableContainer.add(chatRoomMessageLabel, chatRoomMessageCommandLabel).row();
        chatCommandsTableContainer.add(chatWorldMessageLabel, chatWorldMessageCommandLabel).row();
        chatCommandsTableContainer.add(chatGlobalMessageLabel, chatGlobalMessageCommandLabel);
        chatCommandsContainer.add(chatCommandsTableContainer);

        commandsContainer.add(chatCommandsContainer);

        informationContainer.add(keyBindingsContainer).row();
        informationContainer.add(commandsContainer);

        add(informationScrollPane).grow();

        // Translatable register
        translatables.add(infoLabel);
        translatables.add(movementKeysLabel);
        translatables.add(moveUpLabel);
        translatables.add(moveLeftLabel);
        translatables.add(moveDownLabel);
        translatables.add(moveRightLabel);
        translatables.add(sprintLabel);
        translatables.add(worldKeysLabel);
        translatables.add(showNamesLabel);
        translatables.add(zoomLabel);
        translatables.add(interactLabel);
        translatables.add(sendChatMessageLabel);
        translatables.add(pushToTalkLabel);
        translatables.add(menuKeysLabel);
        translatables.add(openChatLabel);
        translatables.add(openUserMenuLabel);
        translatables.add(openNotificationMenuLabel);
        translatables.add(openSettingsMenuLabel);
        translatables.add(openCommunicationMenuLabel);
        translatables.add(toggleMicrophoneLabel);
        translatables.add(toggleSoundLabel);
        translatables.add(toggleFullscreenLabel);
        translatables.add(closeLabel);
        translatables.add(commandsLabel);
        translatables.add(chatCommandsLabel);
        translatables.add(chatWhisperMessageLabel);
        translatables.add(chatWhisperMessageCommandLabel);
        translatables.add(chatRoomMessageLabel);
        translatables.add(chatRoomMessageCommandLabel);
        translatables.add(chatWorldMessageLabel);
        translatables.add(chatWorldMessageCommandLabel);
        translatables.trimToSize();
    }

    @Override
    public void focus() {
        if (getStage() != null) {
            getStage().setScrollFocus(informationScrollPane);
        }
    }

    /**
     * Eine Klasse, welches ein Label dieses Menüs repräsentiert.
     */
    private static class InformationLabel extends ChatiLabel {

        /**
         * Erzeugt eine neue Instanz des InformationLabel.
         * @param messageKey Kennung der anzuzeigenden Nachricht.
         */
        public InformationLabel(@NotNull final String messageKey) {
            super(messageKey);
            setWrap(true);
        }
    }
}
