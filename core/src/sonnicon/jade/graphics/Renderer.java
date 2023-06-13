package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;


public class Renderer {
    public static SpriteBatch spriteBatch;
    private static Viewport viewport;
    public static OrthographicCamera camera;
    public static LinkedList<Renderable> renderList;

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

    public static void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        for (Renderable renderable : renderList) {
            if (renderable.culled()) continue;
            renderable.render(spriteBatch);
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
        camera.viewportWidth = width * viewportScale;
        camera.viewportHeight = height * viewportScale;
        updateCamera();
    }

    public static void updateCamera() {
        camera.update();
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
