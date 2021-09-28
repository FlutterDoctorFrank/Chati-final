package view2.userInterface.hud.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.KeyCommand;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiWindow;

/**
 * Eine Klasse, welche ein Menü zum Einsehen von Informationen zur Anwendung repräsentiert.
 */
public class InformationWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 900;
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

        InformationLabel movementKeysLable = new InformationLabel("menu.label.movement-keys");
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
        InformationLabel closeLabel = new InformationLabel("menu.label.close-window");
        Label closeKeyLabel = new Label(KeyCommand.CLOSE.toString(), Chati.CHATI.getSkin());

        InformationLabel commandsLabel = new InformationLabel("menu.label.commands");
        InformationLabel chatCommandsLabel = new InformationLabel("menu.label.chat-commands");
        InformationLabel chatWhisperMessageLable = new InformationLabel("menu.label.chat-whisper-message");
        InformationLabel chatWhisperMessageCommandLable = new InformationLabel("menu.label.chat-whisper-message-command");
        chatWhisperMessageCommandLable.setColor(Color.GRAY);
        InformationLabel chatRoomMessageLable = new InformationLabel("menu.label.chat-room-message");
        InformationLabel chatRoomMessageCommandLable = new InformationLabel("menu.label.chat-room-message-command");
        chatRoomMessageCommandLable.setColor(Color.ORANGE);
        InformationLabel chatWorldMessageLabel = new InformationLabel("menu.label.chat-world-message");
        InformationLabel chatWorldMessageCommandLabel = new InformationLabel("menu.label.chat-world-message-command");
        chatWorldMessageCommandLabel.setColor(Color.SKY);

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
        movementKeyBindingsContainer.add(movementKeysLable).row();
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
        menuKeysTableContainer.add(openUserMenuLabel, openUserMenuKeyLabel).row();
        menuKeysTableContainer.add(openNotificationMenuLabel, openNotificationMenuKeyLabel).row();
        menuKeysTableContainer.add(openSettingsMenuLabel, openSettingsMenuKeyLabel).row();
        menuKeysTableContainer.add(openCommunicationMenuLabel, openCommunicationMenuKeyLabel).row();
        menuKeysTableContainer.add(toggleMicrophoneLabel, toggleMicrophoneKeyLabel).row();
        menuKeysTableContainer.add(toggleSoundLabel, toggleSoundKeyLabel).row();
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
        chatCommandsContainer.add(commandsLabel).row();
        Table chatCommandsTableContainer = new Table();
        chatCommandsTableContainer.top();
        chatCommandsTableContainer.defaults().pad(SPACE).padLeft(2 * SPACE).growX();
        chatCommandsTableContainer.add(chatWhisperMessageLable, chatWhisperMessageCommandLable).row();
        chatCommandsTableContainer.add(chatRoomMessageLable, chatRoomMessageCommandLable).row();
        chatCommandsTableContainer.add(chatWorldMessageLabel, chatWorldMessageCommandLabel);
        chatCommandsContainer.add(chatCommandsTableContainer);

        commandsContainer.add(chatCommandsContainer);

        informationContainer.add(keyBindingsContainer).row();
        informationContainer.add(chatCommandsContainer);

        add(informationScrollPane).grow();

        // Translatable register
        translates.add(infoLabel);
        translates.add(movementKeysLable);
        translates.add(moveUpLabel);
        translates.add(moveLeftLabel);
        translates.add(moveDownLabel);
        translates.add(moveRightLabel);
        translates.add(sprintLabel);
        translates.add(worldKeysLabel);
        translates.add(showNamesLabel);
        translates.add(zoomLabel);
        translates.add(interactLabel);
        translates.add(sendChatMessageLabel);
        translates.add(pushToTalkLabel);
        translates.add(menuKeysLabel);
        translates.add(openChatLabel);
        translates.add(openUserMenuLabel);
        translates.add(openNotificationMenuLabel);
        translates.add(openSettingsMenuLabel);
        translates.add(openCommunicationMenuLabel);
        translates.add(toggleMicrophoneLabel);
        translates.add(toggleSoundLabel);
        translates.add(closeLabel);
        translates.add(commandsLabel);
        translates.add(chatCommandsLabel);
        translates.add(chatWhisperMessageLable);
        translates.add(chatWhisperMessageCommandLable);
        translates.add(chatRoomMessageLable);
        translates.add(chatRoomMessageCommandLable);
        translates.add(chatWorldMessageLabel);
        translates.add(chatWorldMessageCommandLabel);
        translates.trimToSize();
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public void focus() {
        super.focus();
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
