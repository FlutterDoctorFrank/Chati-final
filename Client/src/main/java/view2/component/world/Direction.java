package view2.component.world;

public enum Direction {
    UP {
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveUpPressed();
        }
    },
    LEFT{
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveLeftPressed();
        }
    },
    DOWN{
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveDownPressed();
        }
    },
    RIGHT{
        @Override
        public boolean isPressed() {
            return WorldScreen.getInstance().getWorldInputProcessor().isMoveRightPressed();
        }
    };

    public abstract boolean isPressed();
}
