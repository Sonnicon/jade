package sonnicon.jade.world;

import sonnicon.jade.content.WorldPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.util.Direction;

import java.util.HashSet;

public class Tile {
    public final short x, y;
    public final Chunk chunk;
    public HashSet<Entity> entities;

    public static final int TILE_SIZE = 32;

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

    /*public void renderAllEntities(SpriteBatch batch, float delta) {
        for (Entity entity : entities) {
            IRenderable component = entity.getComponent(TileDrawComponent.class);
            if (component != null && !component.culled()) {
                component.render(batch, delta);
            }
        }
    }*/

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

    public Tile getNearby(byte direction) {
        direction = Direction.flatten(direction);
        if (direction == 0) {
            return this;
        }

        short targetX = x, targetY = y;
        short dChunkX = 0, dChunkY = 0;

        // move target coords
        targetX += (((direction & Direction.EAST) > 0) ? 1 : 0) + (((direction & Direction.WEST) > 0) ? -1 : 0);
        targetY += (((direction & Direction.NORTH) > 0) ? 1 : 0) + (((direction & Direction.SOUTH) > 0) ? -1 : 0);

        // move x chunk
        if (x != targetX) {
            if (targetX >= Chunk.CHUNK_SIZE) {
                dChunkX++;
                targetX -= Chunk.CHUNK_SIZE;
            } else if (targetX < 0) {
                dChunkX--;
                targetX += Chunk.CHUNK_SIZE;
            }
        }

        // move y chunk
        if (y != targetY) {
            if (targetY >= Chunk.CHUNK_SIZE) {
                dChunkY++;
                targetY -= Chunk.CHUNK_SIZE;
            } else if (targetY < 0) {
                dChunkY--;
                targetY += Chunk.CHUNK_SIZE;
            }
        }

        // Not moving chunk
        if (dChunkX == 0 && dChunkY == 0) {
            return chunk.getTile(targetX, targetY);
        }

        // messy
        Chunk c;
        if (dChunkX == 0 || dChunkY == 0) {
            int index = 0;
            if (dChunkX == 1) {
                index = 1;
            } else if (dChunkX == -1) {
                index = 3;
            } else if (dChunkY == 1) {
                index = 2;
            }
            c = chunk.nearbyChunks[index];
        } else {
            // Moving multiple chunks
            c = chunk.world.chunks.get(Chunk.getHashcode((short) (chunk.x + dChunkX), (short) (chunk.y + dChunkY)));
        }

        if (c != null) {
            return c.getTile(targetX, targetY);
        }
        return null;
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
