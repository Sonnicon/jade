package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import sonnicon.jade.content.Content;
import sonnicon.jade.graphics.draw.*;

public enum RenderLayer {
    // Floor, bottom layer of everything
    floor(new StaticTerrainSpriteBatch(), () -> {
        Content.viewOverlay.start();
    }, null),

    // Static objects that don't have perspective
    terrainBottom(new StaticTerrainSpriteBatch()),

    // Perspective sides of terrain
    terrainSides(new SideTerrainSpriteBatch(), false),

    // Perspective top of terrain
    terrainTop(new TopTerrainSpriteBatch(), false),

    // Things in the world (items, monsters, etc)
    objects(new SpriteBatch()),

    // Particles in the world (in visibility field)
    particles(new SpriteBatch()),

    // Fog-of-war / shadows / etc that occludes world
    fow(new FowBatch()),

    // Effects rendered above view region, e.g. some ambience particles
    overfow(new SpriteBatch(), () -> {
//        Gdx.gl.glDepthMask(false);
//        Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);

        // View circle
        //todo move this to an entity
        //Jade.renderer.viewOverlay.render((SpriteBatch) RenderLayer.overfow.batch);
    }, () -> {
        Content.viewOverlay.end();
    }),

    // HUD (note: actual HUD is on its own spritebatch afterwards, not part of layer system due to todo)
    gui(new SpriteBatch(), true, () -> {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }, () -> {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    }, false);


    public final GraphicsBatch batch;
    public final int index;
    private final Runnable begin;
    private final Runnable end;
    public final boolean project;

    // Hack to access static fields from enum constructor
    private static class RenderLayerInternal {
        public static int layerNextIndex = 0;
    }

    public static final RenderLayer[] all = values();

    RenderLayer() {
        this(null);
    }

    RenderLayer(GraphicsBatch batch) {
        this(batch, true);
    }

    RenderLayer(GraphicsBatch batch, boolean separate) {
        this(batch, separate, null, null);
    }

    RenderLayer(Runnable begin, Runnable end) {
        this(null, begin, end);
    }

    RenderLayer(GraphicsBatch batch, Runnable begin, Runnable end) {
        this(batch, true, begin, end);
    }

    RenderLayer(GraphicsBatch batch, boolean separate, Runnable begin, Runnable end) {
        this(batch, separate, begin, end, true);
    }

    RenderLayer(GraphicsBatch batch, boolean separate, Runnable begin, Runnable end, boolean project) {
        this.batch = batch;
        this.index = separate ? ++RenderLayerInternal.layerNextIndex : RenderLayerInternal.layerNextIndex;
        this.begin = begin;
        this.end = end;
        this.project = project;
    }

    static int size() {
        return RenderLayerInternal.layerNextIndex + 1;
    }

    void begin() {
        if (this.begin != null) {
            this.begin.run();
        }
    }

    void end() {
        if (this.end != null) {
            this.end.run();
        }
    }

    RenderLayer next() {
        return all[ordinal() + 1];
    }
}
