package view2.component.world.body;

import view2.Chati;
import view2.component.KeyAction;

public enum Direction {
    UP {
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveUpPressed() && KeyAction.MOVE_UP.isPressed();
        }
    },
    LEFT{
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveLeftPressed() && KeyAction.MOVE_LEFT.isPressed();
        }
    },
    DOWN{
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveDownPressed() && KeyAction.MOVE_DOWN.isPressed();
        }
    },
    RIGHT{
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveRightPressed() && KeyAction.MOVE_RIGHT.isPressed();
        }
    };

    public abstract boolean isPressed();
}
