package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Ein Enum, welches die durch das Drücken von Tasten durchführbaren Aktionen repräsentiert.
 */
public enum KeyCommand {

    /** Repräsentiert das Fortbewegen des Avatars nach oben. */
    MOVE_UP,

    /** Repräsentiert das Fortbewegen des Avatars nach links. */
    MOVE_LEFT,

    /** Repräsentiert das Fortbewegen des Avatars nach unten. */
    MOVE_DOWN,

    /** Repräsentiert das Fortbewegen des Avatars nach rechts. */
    MOVE_RIGHT,

    /** Repräsentiert das schnelle Fortbewegen des Avatars. */
    SPRINT,

    /** Repräsentiert das Anzeigen der Namen aller Benutzer über deren Avatar. */
    SHOW_NAMES,

    /** Repräsentiert eine Taste, die gedrückt sein muss damit mit dem Mausrad gezoomt werden kann. */
    ZOOM,

    /** Repräsentiert das Interagieren mit einem Interaktionsobjekt. */
    INTERACT,

    /** Repräsentiert das Senden einer Chatnachricht. */
    SEND_CHAT_MESSAGE,

    /** Repräsentiert das Aufnahmen mit dem Mikrofon. */
    PUSH_TO_TALK,

    /** Repräsentiert das Öffnen des Chats. */
    OPEN_CHAT,

    /** Repräsentiert das Öffnen des Benutzermenüs. */
    OPEN_USER_MENU,

    /** Repräsentiert das Öffnen des Benachrichtigungsmenüs. */
    OPEN_NOTIFICATION_MENU,

    /** Repräsentiert das Öffnen des Einstellungsmenüs. */
    OPEN_SETTINGS_MENU,

    /** Repräsentiert das Öffnen des Kommunikationsmenüs. */
    OPEN_COMMUNICATION_MENU,

    /** Repräsentiert das Ein- beziehungsweise Ausschalten des Mikrofons. */
    TOGGLE_MICROPHONE,

    /** Repräsentiert das Ein- beziehungsweise Ausschalten des Tons. */
    TOGGLE_SOUND,

    /** Repräsentiert das Schließen eines aktuell geöffneten Menüs oder Fenster beziehungsweise das Öffnen des
     * Einstellungsmenüs, wenn kein anderes Menü oder Fenster geöffnet ist. */
    CLOSE;

    private final List<Integer> keyBindings;

    /**
     * Erzeugt eine neue Instanz eines KeyCommand.
     */
    KeyCommand() {
        this.keyBindings = new ArrayList<>();
    }

    /**
     * Fügt die Tastenbelegung zu einem Kommando hinzu.
     * @param keyBindings Tastenbelegung des Kommandos.
     */
    public void addKeyBindings(@NotNull final List<Integer> keyBindings) {
        this.keyBindings.addAll(keyBindings);
    }

    /**
     * Prüft, ob eine Taste in der Tastenbelegung eines Kommandos enthalten ist.
     * @param inputKeycode Code der zu überprüfenden Taste.
     * @return true, wenn die Taste enthalten ist, sonst false.
     */
    public boolean matches(final int inputKeycode) {
        return keyBindings.contains(inputKeycode);
    }

    /**
     * Prüft, ob eine Taste aus der Tastenbelegung eines Kommandos gedrückt ist.
     * @return true, wenn eine Taste gedrückt ist, sonst false.
     */
    public boolean isPressed() {
        return keyBindings.stream().anyMatch(Gdx.input::isKeyPressed);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keyBindings.size(); i++) {
            stringBuilder.append(Input.Keys.toString(keyBindings.get(i)));
            if (i < keyBindings.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
