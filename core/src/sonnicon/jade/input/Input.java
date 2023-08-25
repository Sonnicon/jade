package sonnicon.jade.input;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import sonnicon.jade.Jade;
import sonnicon.jade.game.Gamestate;

public class Input implements InputProcessor {
    protected int lastScreenX, lastScreenY;
    protected boolean draggingCamera = false;

    public static InputMultiplexer inputIngame;

    static {
        inputIngame = new InputMultiplexer();
        Gamestate.State.ingame.inputProcessor = inputIngame;
    }

    public Input() {
        inputIngame.addProcessor(this);
    }

    public static void init() {
        new Input();
    }

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
        if (button == com.badlogic.gdx.Input.Buttons.RIGHT && pointer == 0) {
            lastScreenX = screenX;
            lastScreenY = screenY;
            draggingCamera = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == com.badlogic.gdx.Input.Buttons.RIGHT && pointer == 0) {
            draggingCamera = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (draggingCamera && pointer == 0) {
            Jade.renderer.camera.translate(
                    (lastScreenX - screenX) * Jade.renderer.viewportScale,
                    (screenY - lastScreenY) * Jade.renderer.viewportScale);
            lastScreenX = screenX;
            lastScreenY = screenY;
            Jade.renderer.updateCamera();
            return true;
        }

        return false;
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
