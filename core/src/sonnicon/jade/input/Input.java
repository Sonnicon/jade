package sonnicon.jade.input;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.Jade;
import sonnicon.jade.game.Gamestate;

public class Input implements InputProcessor {
    public static InputMultiplexer inputIngame;
    public static final float CAMERA_ZOOM_MIN = 0.25f;
    public static final float CAMERA_ZOOM_MAX = 0.75f;

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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        //todo get rid of this
        Jade.renderer.viewportScale = MathUtils.clamp(
                Jade.renderer.viewportScale + amountY / 100f, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
        Jade.renderer.updateCamera();
        return true;
    }
}
