package sonnicon.jade.world;

import sonnicon.jade.content.WorldPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.Events;

import java.util.HashSet;

public class Tile {
    public final short x, y;
    public final Chunk chunk;
    public final HashSet<Entity> entities;
    public final Events<Class<?>> events;

    public static final int TILE_SIZE = 32;

    protected int globalX, globalY;
    protected int drawX, drawY;

    public Tile(short x, short y, Chunk chunk) {
        this.x = x;
        this.y = y;
        this.chunk = chunk;
        this.entities = new HashSet<>();
        this.events = new Events<>();

        updatePositions();
        WorldPrinter.printFloorEntity(this);
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
}
