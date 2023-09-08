package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sonnicon.jade.game.Content;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.draw.*;
import sonnicon.jade.graphics.overlays.ViewOverlay;
import sonnicon.jade.graphics.particles.ParticleEngine;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.world.Chunk;

import java.util.Arrays;
import java.util.LinkedList;

public class Renderer {
    public Viewport viewport;
    public OrthographicCamera camera;
    public ParticleEngine particles;

    public float viewportScale = 0.25f;
    public final float[] resolution = new float[2];

    private float cameraEdgeLeft;
    private float cameraEdgeRight;
    private float cameraEdgeTop;
    private float cameraEdgeBottom;

    private final LinkedList<IRenderable> renderFullList;
    private final SubRenderer subRenderer;

    public final ViewOverlay viewOverlay;

    public Renderer() {
        subRenderer = new SubRenderer();
        renderFullList = new LinkedList<>();

        Batch.terrain.batch = new TerrainSpriteBatch();
        Batch.world.batch = new SpriteBatch();
        Batch.gui.batch = new SpriteBatch();
        Batch.fow.batch = new FowBatch();
        Batch.overfow.batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        viewOverlay = new ViewOverlay();
        viewOverlay.setRadius(160);
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
        Gui.render(delta);

        if (Gamestate.getState() == Gamestate.State.menu) {
            return;
        }

        // Clear depth
        Gdx.gl.glClearDepthf(1f);

        // Draw depth mask
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Batch.overfow.batch.begin();
        Gdx.gl.glDepthMask(true);
        viewOverlay.render((SpriteBatch) Batch.overfow.batch);
        Batch.overfow.batch.end();

        // Hide things we can't see
        Gdx.gl.glDepthMask(false);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        GraphicsBatch batch = null;
        for (RenderLayer layer : RenderLayer.all) {
            // Don't want to mask out the GUI
            if (layer == RenderLayer.overfow) {
                Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);
            }

            if (layer.batchType != null) {
                if (batch != null) {
                    if (batch instanceof CachedDrawBatch && (!((CachedDrawBatch) batch).invalidated)) {
                        batch.flush();
                    } else {
                        batch.end();
                    }
                }
                batch = layer.batchType.batch;
                if (batch instanceof CachedDrawBatch && (!((CachedDrawBatch) batch).invalidated)) {
                    continue;
                }
                batch.begin();
            }

            if (batch == null) {
                throw new EnumConstantNotPresentException(RenderLayer.class, layer.name() + ".batchType.batch");
            }

            // Draw view circle
            if (layer == RenderLayer.overfow) {
                viewOverlay.render((SpriteBatch) Batch.overfow.batch);
            }

            if (batch instanceof CachedDrawBatch && !((CachedDrawBatch) batch).invalidated) {
                continue;
            }

            subRenderer.renderRenderables(batch, delta, layer);

            for (IRenderable r : renderFullList) {
                if (!r.culled(layer)) {
                    r.render(batch, delta, layer);
                }
            }
        }

        if (batch != null) {
            if (batch instanceof CachedDrawBatch && (!((CachedDrawBatch) batch).invalidated)) {
                batch.flush();
            } else {
                batch.end();
            }
        }

        boolean cameraMoved = false;
        //todo move this
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
            cameraMoved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
            cameraMoved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
            cameraMoved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
            cameraMoved = true;
        }
        if (cameraMoved) {
            updateCamera();
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

        for (Batch batch : Batch.allFollowing) {
            batch.batch.setProjectionMatrix(camera.combined);
        }

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

    public enum Batch {
        terrain,
        world,
        gui(false),
        fow,
        overfow;

        public GraphicsBatch batch;
        public final boolean followCamera;

        Batch() {
            this(true);
        }

        Batch(boolean followCamera) {
            this.followCamera = followCamera;
        }


        public static final Batch[] all = values();
        public static final Batch[] allFollowing = Arrays.stream(values()).filter(b -> b.followCamera).toArray(Batch[]::new);
    }

    public enum RenderLayer {
        bottom(Batch.terrain),
        floor,
        terrain,
        characters(Batch.world),
        particles,
        fow(Batch.fow),
        overfow(Batch.overfow),
        overlay(Batch.gui),
        gui,
        top;

        public final Batch batchType;

        private static final RenderLayer[] all = values();

        RenderLayer() {
            this.batchType = null;
        }

        RenderLayer(Batch batchType) {
            this.batchType = batchType;
        }

        RenderLayer next() {
            if (ordinal() == all().length - 1) {
                return null;
            }
            return all()[ordinal() + 1];
        }

        static RenderLayer[] all() {
            return all;
        }
    }
}
