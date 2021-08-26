package view2.component.world.body;

import view2.Chati;

public enum Direction {
    UP {
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveUpPressed();
        }
    },
    LEFT{
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveLeftPressed();
        }
    },
    DOWN{
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveDownPressed();
        }
    },
    RIGHT{
        @Override
        public boolean isPressed() {
            return Chati.CHATI.getWorldScreen().getWorldInputProcessor().isMoveRightPressed();
        }
    };

    public abstract boolean isPressed();
}
