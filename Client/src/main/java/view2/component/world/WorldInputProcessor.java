package view2.component.world;

import com.badlogic.gdx.InputProcessor;
import view2.component.KeyAction;

public class WorldInputProcessor implements InputProcessor {

    private boolean moveUpPressed;
    private boolean moveLeftPressed;
    private boolean moveDownPressed;
    private boolean moveRightPressed;
    private boolean sprintPressed;
    private boolean showNamesPressed;

    @Override
    public boolean keyDown(int keycode) {
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_UP) {
            moveUpPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_LEFT) {
            moveLeftPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_DOWN) {
            moveDownPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_RIGHT) {
            moveRightPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SPRINT) {
            sprintPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SHOW_NAMES) {
            showNamesPressed = true;
        }
        if (KeyAction.getAction(keycode) == KeyAction.INTERACT) {
            WorldScreen.getInstance().getInternUserAvatar().interact();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_UP) {
            moveUpPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_LEFT) {
            moveLeftPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_DOWN) {
            moveDownPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.MOVE_RIGHT) {
            moveRightPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SPRINT) {
            sprintPressed = false;
        }
        if (KeyAction.getAction(keycode) == KeyAction.SHOW_NAMES) {
            showNamesPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        WorldScreen.getInstance().zoom(amountY > 0);
        return true;
    }

    public boolean isMoveUpPressed() {
        return moveUpPressed;
    }

    public boolean isMoveLeftPressed() {
        return moveLeftPressed;
    }

    public boolean isMoveDownPressed() {
        return moveDownPressed;
    }

    public boolean isMoveRightPressed() {
        return moveRightPressed;
    }

    public boolean isSprintPressed() {
        return sprintPressed;
    }

    public boolean isShowNamesPressed() {
        return showNamesPressed;
    }
}
