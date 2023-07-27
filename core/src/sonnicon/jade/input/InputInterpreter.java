package sonnicon.jade.input;

import com.badlogic.gdx.InputAdapter;

public class InputInterpreter extends InputAdapter {
    public static final float TAP_DISTANCE = 4f;
    public static final long HOLD_LENGTH = 1000;

    private int tapStartX, tapStartY, tapPointer, tapButton;
    private boolean tapActive = false;
    private long tapStartTime;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!tapActive) {
            tapStartX = screenX;
            tapStartY = screenY;
            tapPointer = pointer;
            tapButton = button;
            tapActive = true;
            tapStartTime = System.currentTimeMillis();
        }
        // I don't want to deal with multiple taps at the same time
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (tapActive && pointer == tapPointer && button == tapButton) {
            tapActive = false;

            // No reason to make it circular
            if (Math.abs(screenX - tapStartX) > TAP_DISTANCE || Math.abs(screenY - tapStartY) > TAP_DISTANCE) {
                //todo hold dragging
                return false;
            }

            if (System.currentTimeMillis() >= tapStartTime + HOLD_LENGTH) {
                if (holdTapped(tapStartX, tapStartY, pointer, button)) {
                    return true;
                }
            }

            return tapped(tapStartX, tapStartY, pointer, button);
        }

        return false;
    }

    public boolean tapped(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean holdTapped(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
