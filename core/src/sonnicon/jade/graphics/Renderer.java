package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.draw.*;
import sonnicon.jade.graphics.particles.ParticleEngine;
import sonnicon.jade.gui.Gui;

import java.util.Arrays;
import java.util.LinkedList;

public class Renderer {
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

        Batch.terrain.batch = new TerrainSpriteBatch();
        Batch.dynamicTerrain.batch = new TerrainSpriteBatch();
        Batch.world.batch = new SpriteBatch();
        Batch.gui.batch = new SpriteBatch();
        Batch.fow.batch = new FowBatch();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
    }

    public void render(float delta) {


        if (Gamestate.getState() == Gamestate.State.menu) {
            Gui.render(delta);
            return;
        }

        GraphicsBatch batch = null;
        for (RenderLayer layer : RenderLayer.all) {
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
        dynamicTerrain,
        world,
        gui(false),
        fow;

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
        terrain(Batch.dynamicTerrain),
        characters(Batch.world),
        particles,
        fow(Batch.fow),
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
