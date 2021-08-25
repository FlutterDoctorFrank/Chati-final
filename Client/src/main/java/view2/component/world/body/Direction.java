package view2.component.world.body;

import view2.component.KeyAction;
import view2.component.world.WorldScreen;

public enum Direction {
    UP {
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveUpPressed() && KeyAction.MOVE_UP.isPressed();
        }
    },
    LEFT{
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveLeftPressed() && KeyAction.MOVE_LEFT.isPressed();
        }
    },
    DOWN{
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveDownPressed() && KeyAction.MOVE_DOWN.isPressed();
        }
    },
    RIGHT{
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveRightPressed() && KeyAction.MOVE_RIGHT.isPressed();
        }
    };

    public abstract boolean isPressed();
}
