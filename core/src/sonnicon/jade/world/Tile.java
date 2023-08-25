package sonnicon.jade.world;

import sonnicon.jade.content.WorldPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.util.Consumer2;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.Events;

import java.util.HashSet;

public class Tile {
    private final short x, y;
    public final Chunk chunk;
    public final HashSet<Entity> entities;
    public final Traits traits;
    public final Events events;

    private final int globalX, globalY;
    private final int drawX, drawY;
    private final int drawMiddleX, drawMiddleY;

    public static final int TILE_SIZE = 32;
    public static final int HALF_TILE_SIZE = TILE_SIZE / 2;

    public Tile(short x, short y, Chunk chunk) {
        this.x = x;
        this.y = y;
        this.chunk = chunk;

        this.globalX = chunk.x * Chunk.CHUNK_SIZE + x;
        this.globalY = chunk.y * Chunk.CHUNK_SIZE + y;
        this.drawX = globalX * TILE_SIZE;
        this.drawY = globalY * TILE_SIZE;
        this.drawMiddleX = (int) (drawX + TILE_SIZE * .5f);
        this.drawMiddleY = (int) (drawY + TILE_SIZE * .5f);

        this.entities = new HashSet<>();
        this.traits = new Traits();
        this.events = new Events();
    }

    public int getX() {
        return globalX;
    }

    public int getY() {
        return globalY;
    }

    public int getLocalX() {
        return x;
    }

    public int getLocalY() {
        return y;
    }

    public float getDrawX() {
        return drawX;
    }

    public float getDrawY() {
        return drawY;
    }

    public float getDrawMiddleX() {
        return drawMiddleX;
    }

    public float getDrawMiddleY() {
        return drawMiddleY;
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
            } else if (dChunkY == -1) {
                index = 2;
            }
            c = chunk.getNearby(index);
        } else {
            // Moving multiple chunks
            c = chunk.world.chunks.get(Chunk.getHashcode((short) (chunk.x + dChunkX), (short) (chunk.y + dChunkY)));
        }

        if (c != null) {
            return c.getTile(targetX, targetY);
        }
        return null;
    }

    public void allNearby(Consumer2<Tile, Byte> cons) {
        Direction.cardinals((Byte dir) -> {
            Tile other = getNearby(dir);
            if (other != null) {
                cons.apply(other, dir);
            }
        });
    }
}
