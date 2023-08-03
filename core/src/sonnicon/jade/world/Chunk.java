package sonnicon.jade.world;

import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.SubRenderer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.Direction;

import static sonnicon.jade.Jade.renderer;

public class Chunk implements IRenderable {
    public static final short CHUNK_SIZE = 16;
    protected static final float CHUNK_TILE_SIZE = CHUNK_SIZE * Tile.TILE_SIZE;

    public final Tile[] tiles = new Tile[CHUNK_SIZE * CHUNK_SIZE];
    public final short x, y;
    public final Chunk[] nearbyChunks = new Chunk[4];
    public final World world;

    private final SubRenderer subRenderer;

    public Chunk(short x, short y, World world) {
        this.x = x;
        this.y = y;
        this.world = world;
        world.chunks.put(hashCode(), this);

        subRenderer = new SubRenderer();
        renderer.addRenderable(this);

        for (short dir = 0; dir < 4; dir++) {
            short dirX = Direction.directionX((byte) (1 << dir));
            short dirY = Direction.directionY((byte) (1 << dir));
            Chunk other = world.chunks.getOrDefault(getHashcode((short) (x + dirX), (short) (y + dirY)), null);
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
        float drawX = x * CHUNK_TILE_SIZE;
        float drawY = y * CHUNK_TILE_SIZE;
        return drawX > renderer.getCameraEdgeRight() ||
                (drawX + CHUNK_TILE_SIZE) < renderer.getCameraEdgeLeft() ||
                drawY > renderer.getCameraEdgeBottom() ||
                (drawY + CHUNK_TILE_SIZE) < renderer.getCameraEdgeTop();
    }
}
