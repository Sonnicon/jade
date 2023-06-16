package sonnicon.jade.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sonnicon.jade.content.WorldPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.TileDrawComponent;
import sonnicon.jade.graphics.IRenderable;

import java.util.HashSet;

public class Tile {
    public final short x, y;
    public final Chunk chunk;
    public HashSet<Entity> entities;

    public static final int TILE_SIZE = 16;

    protected int globalX, globalY;
    protected int drawX, drawY;

    public Tile(short x, short y, Chunk chunk) {
        this.x = x;
        this.y = y;
        this.chunk = chunk;
        this.entities = new HashSet<>();

        updatePositions();
        WorldPrinter.printFloorEntity(this);
    }

    public void renderAllEntities(SpriteBatch batch, float delta) {
        for (Entity entity : entities) {
            IRenderable component = entity.getComponent(TileDrawComponent.class);
            if (component != null && !component.culled()) {
                component.render(batch, delta);
            }
        }
    }

    protected void updatePositions() {
        globalX = chunk.x * Chunk.CHUNK_SIZE + x;
        globalY = chunk.y * Chunk.CHUNK_SIZE + y;
        drawX = globalX * TILE_SIZE;
        drawY = globalY * TILE_SIZE;
    }

    public int getGlobalX() {
        return globalX;
    }

    public int getGlobalY() {
        return globalY;
    }

    public float getDrawX() {
        return drawX;
    }

    public float getDrawY() {
        return drawY;
    }

    //todo make these less boilerplate
    public Tile getNearbyNorth() {

        if (y < Chunk.CHUNK_SIZE - 1) {
            return chunk.getTile(x, (short) (y + 1));
        } else if (chunk.nearbyChunks[0] != null) {
            return chunk.nearbyChunks[0].getTile(x, (short) 0);
        }
        return null;
    }

    public Tile getNearbyEast() {
        if (x < Chunk.CHUNK_SIZE - 1) {
            return chunk.getTile((short) (x + 1), y);
        } else if (chunk.nearbyChunks[1] != null) {
            return chunk.nearbyChunks[1].getTile((short) 0, y);
        }
        return null;
    }

    public Tile getNearbySouth() {
        if (y > 0) {
            return chunk.getTile(x, (short) (y - 1));
        } else if (chunk.nearbyChunks[2] != null) {
            return chunk.nearbyChunks[2].getTile(x, (short) (Chunk.CHUNK_SIZE - 1));
        }
        return null;
    }

    public Tile getNearbyWest() {
        if (x > 0) {
            return chunk.getTile((short) (x - 1), y);
        } else if (chunk.nearbyChunks[3] != null) {
            return chunk.nearbyChunks[3].getTile((short) (Chunk.CHUNK_SIZE - 1), y);
        }
        return null;
    }
}
