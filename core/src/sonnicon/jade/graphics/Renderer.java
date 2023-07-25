package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.particles.ParticleEngine;

import java.util.LinkedList;

public class Renderer {
    public SpriteBatch spriteBatch;
    public Viewport viewport;
    public OrthographicCamera camera;
    public ParticleEngine particles;

    public float viewportScale = 0.25f;

    private float cameraEdgeLeft;
    private float cameraEdgeRight;
    private float cameraEdgeTop;
    private float cameraEdgeBottom;

    private final LinkedList<IRenderable> renderFullList;
    private final SubRenderer subRenderer;

    public Renderer() {
        subRenderer = new SubRenderer();
        renderFullList = new LinkedList<>();

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        particles = new ParticleEngine(this);
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gamestate.getState() == Gamestate.State.menu) return;

        spriteBatch.begin();

        // We use our own algorithm, to inject all the renderFullList entries
        RenderLayer layer = Renderer.RenderLayer.all()[0];
        int index = 0, layerIndex = 0;
        for (IRenderable renderable : subRenderer.renderList) {
            while (index >= subRenderer.renderLayers[layerIndex]) {
                for (IRenderable fullRenderable : renderFullList) {
                    if (fullRenderable.culled()) continue;
                    fullRenderable.render(spriteBatch, delta, layer);
                }
                layerIndex++;
                if (layerIndex >= subRenderer.renderLayers.length) {
                    return;
                }
                layer = RenderLayer.all()[layerIndex];
            }

            if (renderable.culled()) continue;
            renderable.render(spriteBatch, delta, layer);
            index++;
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

    public void resize(int width, int height) {
        viewport.update(width, height);
        updateCamera();
    }

    public void updateCamera() {
        camera.zoom = viewportScale;
        viewport.apply();
        spriteBatch.setProjectionMatrix(camera.combined);

        cameraEdgeLeft = camera.position.x - camera.viewportWidth / 2;
        cameraEdgeRight = camera.position.x + camera.viewportWidth / 2;
        cameraEdgeTop = camera.position.y - camera.viewportHeight / 2;
        cameraEdgeBottom = camera.position.y + camera.viewportHeight / 2;
    }

    public float getCameraEdgeLeft() {
        return cameraEdgeLeft;
    }

    public float getCameraEdgeRight() {
        return cameraEdgeRight;
    }

    public float getCameraEdgeTop() {
        return cameraEdgeTop;
    }

    public float getCameraEdgeBottom() {
        return cameraEdgeBottom;
    }

    public void addRenderable(IRenderable renderable) {
        renderFullList.add(renderable);
    }

    public void addRenderable(IRenderable renderable, RenderLayer layer) {
        subRenderer.addRenderable(renderable, layer);
    }

    public boolean removeRenderable(IRenderable renderable) {
        return renderFullList.remove(renderable) || subRenderer.removeRenderable(renderable);
    }

    public enum RenderLayer {
        bottom,
        floor,
        terrain,
        characters,
        particles,
        overlay,
        top;

        private static final RenderLayer[] all = values();

        static RenderLayer[] all() {
            return all;
        }
    }
}
