package sonnicon.jade.world;

import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.collision.IBoundSquare;
import sonnicon.jade.game.collision.Quadtree;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.SubRenderer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.Directions;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.Map;

import static sonnicon.jade.Jade.renderer;

public class Chunk implements IRenderable, IDebuggable, IPosition {
    public final short chunkX, chunkY;
    private final World world;

    private final Tile[] tiles = new Tile[CHUNK_SIZE * CHUNK_SIZE];
    private final Chunk[] nearbyChunks = new Chunk[4];
    private final SubRenderer subRenderer;
    private boolean culled = true;

    public final Quadtree collisionTree;

    public static final short CHUNK_SIZE = 16;
    public static final float CHUNK_WORLD_SIZE = CHUNK_SIZE * Tile.TILE_SIZE;
    public static final IBoundSquare bound = () -> CHUNK_WORLD_SIZE / 2f;

    public Chunk(short chunkX, short chunkY, World world) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.world = world;
        world.chunks.put(hashCode(), this);

        this.collisionTree = new Quadtree(getX(), getY(), CHUNK_WORLD_SIZE / 2f, world);

        subRenderer = new SubRenderer();
        renderer.addRenderable(this);


        Directions.cardinals(dir -> {
            Chunk other = world.chunks.getOrDefault(
                    getHashcode((short) (chunkX + Directions.directionX(dir)), (short) (chunkY + Directions.directionY(dir))),
                    null);
            if (other != null) {
                byte index = Directions.toCardinalIndex(dir);
                nearbyChunks[index] = other;
                other.nearbyChunks[(index + 2) % 4] = this;
            }
        });

        for (int i = 0; i < CHUNK_SIZE * CHUNK_SIZE; i++) {
            tiles[i] = new Tile((short) (i % CHUNK_SIZE), (short) (i / CHUNK_SIZE), this);
        }
    }

    public Tile getTile(short x, short y) {
        return tiles[x + y * CHUNK_SIZE];
    }

    public Chunk getNearbyChunk(int index) {
        return nearbyChunks[index];
    }

    public Chunk[] getNearbyChunks() {
        return nearbyChunks;
    }

    @Override
    public int hashCode() {
        return getHashcode(chunkX, chunkY);
    }

    public static int getHashcode(short x, short y) {
        return x << (Integer.SIZE / 2) | y;
    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        subRenderer.renderRenderables(batch, delta, layer);
    }

    public void addRenderable(IRenderable renderable, RenderLayer layer) {
        subRenderer.addRenderable(renderable, layer);
    }

    public void removeRenderable(IRenderable renderable) {
        subRenderer.removeRenderable(renderable);
    }

    @Override
    public boolean culled(RenderLayer layer) {
        return culled;
    }

    public void updateCulled() {
        float drawX = chunkX * CHUNK_WORLD_SIZE;
        float drawY = chunkY * CHUNK_WORLD_SIZE;
        boolean newValue = drawX > renderer.getCameraEdgeRight() ||
                (drawX + CHUNK_WORLD_SIZE) < renderer.getCameraEdgeLeft() ||
                drawY > renderer.getCameraEdgeBottom() ||
                (drawY + CHUNK_WORLD_SIZE) < renderer.getCameraEdgeTop();

        if (newValue != culled) {
            culled = newValue;
            for (RenderLayer layer : RenderLayer.all) {
                if (layer.batch instanceof CachedDrawBatch) {
                    ((CachedDrawBatch) layer.batch).invalidate();
                }
            }
        }
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "x", chunkX,
                "y", chunkY,
                "world", world,
                "tiles", tiles,
                "nearbyChunks", nearbyChunks,
                "culled", culled,
                "subrenderer", subRenderer,
                "collisionTree", collisionTree);
    }

    @Override
    public float getX() {
        return chunkX * CHUNK_WORLD_SIZE + CHUNK_WORLD_SIZE / 2f;
    }

    @Override
    public float getY() {
        return chunkY * CHUNK_WORLD_SIZE + CHUNK_WORLD_SIZE / 2f;
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public World getWorld() {
        return world;
    }
}
