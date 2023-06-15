package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sonnicon.jade.game.Gamestate;

import java.util.LinkedList;

public class Renderer {
    public static SpriteBatch spriteBatch;
    public static Viewport viewport;
    public static OrthographicCamera camera;
    public static LinkedList<IRenderable> renderList;

    public static float viewportScale = 0.25f;

    private static float cameraEdgeLeft;
    private static float cameraEdgeRight;
    private static float cameraEdgeTop;
    private static float cameraEdgeBottom;

    public static void init() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);


        renderList = new LinkedList<>();
    }

    public static void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gamestate.getState() == Gamestate.State.menu) return;

        spriteBatch.begin();
        for (IRenderable renderable : renderList) {
            if (renderable.culled()) continue;
            renderable.render(spriteBatch, delta);
        }
        spriteBatch.end();

        //todo move this
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }
        updateCamera();
    }

    public static void resize(int width, int height) {
        viewport.update(width, height);
        updateCamera();
    }

    public static void updateCamera() {
        camera.zoom = viewportScale;
        viewport.apply();
        spriteBatch.setProjectionMatrix(camera.combined);

        cameraEdgeLeft = camera.position.x - camera.viewportWidth / 2;
        cameraEdgeRight = camera.position.x + camera.viewportWidth / 2;
        cameraEdgeTop = camera.position.y - camera.viewportHeight / 2;
        cameraEdgeBottom = camera.position.y + camera.viewportHeight / 2;
    }

    public static float getCameraEdgeLeft() {
        return cameraEdgeLeft;
    }

    public static float getCameraEdgeRight() {
        return cameraEdgeRight;
    }

    public static float getCameraEdgeTop() {
        return cameraEdgeTop;
    }

    public static float getCameraEdgeBottom() {
        return cameraEdgeBottom;
    }
}
