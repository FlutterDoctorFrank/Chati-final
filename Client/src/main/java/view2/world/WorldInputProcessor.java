package view2.world;

import com.badlogic.gdx.InputProcessor;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.KeyCommand;
import model.context.spatial.Direction;

/**
 * Eine Klasse, welche einen InputProcessor repräsentiert, der den Input innerhalb einer Welt verarbeitet.
 */
public class WorldInputProcessor implements InputProcessor {

    private boolean moveUpPressed;
    private boolean moveLeftPressed;
    private boolean moveDownPressed;
    private boolean moveRightPressed;
    private boolean sprintPressed;
    private boolean showNamesPressed;
    private boolean pushToTalkPressed;

    @Override
    public boolean keyDown(final int keycode) {
        if (KeyCommand.MOVE_UP.matches(keycode)) {
            moveUpPressed = true;
        }
        if (KeyCommand.MOVE_LEFT.matches(keycode)) {
            moveLeftPressed = true;
        }
        if (KeyCommand.MOVE_DOWN.matches(keycode)) {
            moveDownPressed = true;
        }
        if (KeyCommand.MOVE_RIGHT.matches(keycode)) {
            moveRightPressed = true;
        }
        if (KeyCommand.SPRINT.matches(keycode)) {
            sprintPressed = true;
        }
        if (KeyCommand.SHOW_NAMES.matches(keycode)) {
            showNamesPressed = true;
        }
        if (KeyCommand.PUSH_TO_TALK.matches(keycode)) {
            pushToTalkPressed = true;
        }
        if (KeyCommand.INTERACT.matches(keycode)) {
            if (Chati.CHATI.getWorldScreen().getInternUserAvatar() != null) {
                Chati.CHATI.getWorldScreen().getInternUserAvatar().interact();
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(final int keycode) {
        if (KeyCommand.MOVE_UP.matches(keycode)) {
            moveUpPressed = false;
        }
        if (KeyCommand.MOVE_LEFT.matches(keycode)) {
            moveLeftPressed = false;
        }
        if (KeyCommand.MOVE_DOWN.matches(keycode)) {
            moveDownPressed = false;
        }
        if (KeyCommand.MOVE_RIGHT.matches(keycode)) {
            moveRightPressed = false;
        }
        if (KeyCommand.SPRINT.matches(keycode)) {
            sprintPressed = false;
        }
        if (KeyCommand.SHOW_NAMES.matches(keycode)) {
            showNamesPressed = false;
        }
        if (KeyCommand.PUSH_TO_TALK.matches(keycode)) {
            pushToTalkPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(final char character) {
        return true;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return true;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return true;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(final float amountX, final float amountY) {
        Chati.CHATI.getWorldScreen().getCamera().zoom(amountY > 0);
        return true;
    }

    /**
     * Gibt zurück, ob momentan die Taste einer Richtungsbewegung gedrückt ist.
     * @param direction Zu überprüfende Richtung.
     * @return true, wenn die Taste der entsprechenden Richtung gedrückt ist, sonst false.
     */
    public boolean isDirectionPressed(@NotNull final Direction direction) {
        switch (direction) {
            case UP:
                return moveUpPressed && KeyCommand.MOVE_UP.isPressed();
            case LEFT:
                return moveLeftPressed && KeyCommand.MOVE_LEFT.isPressed();
            case DOWN:
                return moveDownPressed && KeyCommand.MOVE_DOWN.isPressed();
            case RIGHT:
                return moveRightPressed && KeyCommand.MOVE_RIGHT.isPressed();
            default:
                return false;
        }
    }

    /**
     * Gibt zurück, ob die Taste für das schnelle Fortbewegen des Avatars gedrückt ist.
     * @return true, wenn die Taste gedrückt ist, sonst false.
     */
    public boolean isSprintPressed() {
        return sprintPressed && KeyCommand.SPRINT.isPressed();
    }

    /**
     * Gibt zurück, ob die Taste für das dauerhafte Anzeigen der Benutzernamen über den Avataren gedrückt ist.
     * @return true, wenn die Taste gedrückt ist, sonst false.
     */
    public boolean isShowNamesPressed() {
        return showNamesPressed && KeyCommand.SHOW_NAMES.isPressed();
    }

    /**
     * Gibt zurück, ob die Taste zum Sprechen gedrückt ist.
     * @return true, wenn die Taste gedrückt ist, sonst false.
     */
    public boolean isPushToTalkPressed() {
        return pushToTalkPressed && KeyCommand.PUSH_TO_TALK.isPressed();
    }
}
