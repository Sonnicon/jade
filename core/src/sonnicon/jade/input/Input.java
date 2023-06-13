package sonnicon.jade.input;

import com.badlogic.gdx.InputProcessor;
import sonnicon.jade.graphics.Renderer;

public class Input implements InputProcessor {
    protected int lastScreenX, lastScreenY;

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == com.badlogic.gdx.Input.Buttons.LEFT || pointer == 0) {
            lastScreenX = screenX;
            lastScreenY = screenY;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == 0) {
            Renderer.camera.translate(
                    (lastScreenX - screenX) * Renderer.viewportScale,
                    (screenY - lastScreenY) * Renderer.viewportScale);
            lastScreenX = screenX;
            lastScreenY = screenY;
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
