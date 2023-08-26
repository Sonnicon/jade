package sonnicon.jade.world;

import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.SubRenderer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Structs;

import java.util.Map;

import static sonnicon.jade.Jade.renderer;

public class Chunk implements IRenderable, IDebuggable {
    public final short x, y;
    public final World world;

    private final Tile[] tiles = new Tile[CHUNK_SIZE * CHUNK_SIZE];
    private final Chunk[] nearbyChunks = new Chunk[4];
    private final SubRenderer subRenderer;
    private boolean culled = true;

    public static final short CHUNK_SIZE = 16;
    public static final float CHUNK_TILE_SIZE = CHUNK_SIZE * Tile.TILE_SIZE;

    public Chunk(short x, short y, World world) {
        this.x = x;
        this.y = y;
        this.world = world;
        world.chunks.put(hashCode(), this);

        subRenderer = new SubRenderer();
        renderer.addRenderable(this);

        for (short dir = 0; dir < 4; dir++) {
            Chunk other = world.chunks.getOrDefault(getHashcode(
                    (short) (x + Direction.directionX((byte) (1 << dir))),
                    (short) (y + Direction.directionY((byte) (1 << dir)))), null);
            if (other != null) {
                nearbyChunks[dir] = other;
                other.nearbyChunks[(dir + 2) % 4] = this;
            }
        }

        for (int i = 0; i < CHUNK_SIZE * CHUNK_SIZE; i++) {
            tiles[i] = new Tile((short) (i % CHUNK_SIZE), (short) (i / CHUNK_SIZE), this);
        }
    }

    public Tile getTile(short x, short y) {
        return tiles[x + y * CHUNK_SIZE];
    }

    public Chunk getNearby(int index) {
        return nearbyChunks[index];
    }

    @Override
    public int hashCode() {
        return getHashcode(x, y);
    }

    public static int getHashcode(short x, short y) {
        return x << (Integer.SIZE / 2) | y;
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        subRenderer.renderRenderables(batch, delta, layer);
    }

    public void addRenderable(IRenderable renderable, Renderer.RenderLayer layer) {
        subRenderer.addRenderable(renderable, layer);
    }

    public void removeRenderable(IRenderable renderable) {
        subRenderer.removeRenderable(renderable);
    }

    @Override
    public boolean culled(Renderer.RenderLayer layer) {
        return culled;
    }

    public void updateCulled() {
        float drawX = x * CHUNK_TILE_SIZE;
        float drawY = y * CHUNK_TILE_SIZE;
        boolean newValue = drawX > renderer.getCameraEdgeRight() ||
                (drawX + CHUNK_TILE_SIZE) < renderer.getCameraEdgeLeft() ||
                drawY > renderer.getCameraEdgeBottom() ||
                (drawY + CHUNK_TILE_SIZE) < renderer.getCameraEdgeTop();

        if (newValue != culled) {
            culled = newValue;
            for (Renderer.Batch b : Renderer.Batch.allFollowing) {
                if (b.batch instanceof CachedDrawBatch) {
                    ((CachedDrawBatch) b.batch).invalidate();
                }
            }
        }
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Structs.mapFrom("x", x, "y", y, "world", world, "tiles", tiles, "nearbyChunks", nearbyChunks, "culled", culled, "subrenderer", subRenderer);
    }
}
