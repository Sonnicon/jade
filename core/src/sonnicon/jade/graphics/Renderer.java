package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sonnicon.jade.EventGenerator;
import sonnicon.jade.content.Content;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.particles.ParticleEngine;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.util.Events;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Chunk;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

@EventGenerator(id = "CameraMove", param = {Camera.class}, label = {"camera"})
public class Renderer implements IDebuggable {
    public Viewport viewport;
    public OrthographicCamera camera;
    public ParticleEngine particles;

    public float viewportScale = 0.25f;
    public final float[] resolution = new float[2];

    private float cameraEdgeLeft;
    private float cameraEdgeRight;
    private float cameraEdgeTop;
    private float cameraEdgeBottom;
    public final Events events = new Events();

    private final LinkedList<IRenderable> renderFullList;
    private final SubRenderer subRenderer;

    public Renderer() {
        subRenderer = new SubRenderer();
        renderFullList = new LinkedList<>();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);

        for (RenderLayer layer : Gamestate.getState().getLayersToRender()) {
            layer.begin();

            GraphicsBatch batch = layer.batch;
            boolean cached = (batch instanceof CachedDrawBatch) && !((CachedDrawBatch) batch).invalidated;
            if (cached) {
                // Cached things get flushed
                batch.flush();
            } else {
                // Dirty things get re-rendered
                batch.begin();
                renderLayer(batch, delta, layer);
                batch.end();
            }

            layer.end();
        }
    }

    private void renderLayer(GraphicsBatch batch, float delta, RenderLayer layer) {
        subRenderer.renderRenderables(batch, delta, layer);

        for (IRenderable r : renderFullList) {
            if (!r.culled(layer)) {
                r.render(batch, delta, layer);
            }
        }
    }

    public void resize(int width, int height) {
        resolution[0] = width;
        resolution[1] = height;

        viewport.update(width, height);
        updateCamera();
        Gui.resize(width, height);
    }

    public void updateCamera() {
        camera.zoom = viewportScale;
        viewport.apply();

        Arrays.stream(RenderLayer.all)
                .filter(r -> r.project)
                .map(r -> r.batch)
                .filter(Objects::nonNull)
                .forEach(batch -> batch.setProjectionMatrix(camera.combined));

        cameraEdgeLeft = camera.position.x - camera.viewportWidth / 2;
        cameraEdgeRight = camera.position.x + camera.viewportWidth / 2;
        cameraEdgeTop = camera.position.y - camera.viewportHeight / 2;
        cameraEdgeBottom = camera.position.y + camera.viewportHeight / 2;

        if (Content.world != null) {
            Content.world.chunks.values().forEach(Chunk::updateCulled);
        }
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

    private final Vector3 TEMP_VEC = new Vector3();

    public Vector2 worldToScreen(Vector2 world, Vector2 screen) {
        TEMP_VEC.set(world, 0f);
        camera.project(TEMP_VEC);
        screen.set(TEMP_VEC.x, TEMP_VEC.y);
        return screen;
    }

    public Vector2 screenToWorld(Vector2 screen, Vector2 world) {
        TEMP_VEC.set(screen, 0f);
        camera.unproject(TEMP_VEC);
        world.set(TEMP_VEC.x, TEMP_VEC.y);
        return world;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "particle_engine", particles,
                "viewportScale", viewportScale,
                "resolution", resolution,
                "camEdgeLeft", cameraEdgeLeft,
                "camEdgeRight", cameraEdgeRight,
                "camEdgeTop", cameraEdgeTop,
                "camEdgeBottom", cameraEdgeBottom,
                "renderFullList", renderFullList,
                "subRenderer", subRenderer
        );
    }
}
