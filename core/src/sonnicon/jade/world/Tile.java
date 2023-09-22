package sonnicon.jade.world;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.world.MoveboxComponent;
import sonnicon.jade.util.*;

import java.util.HashSet;
import java.util.Map;

public class Tile implements IDebuggable {
    private final short x, y;
    public final Chunk chunk;
    public final HashSet<Entity> entities;
    public final HashSet<MoveboxComponent> nearbyMoveboxes;
    public final Traits traits;
    public final Events events;

    private final int globalX, globalY;
    private final int drawX, drawY;
    private final int jointX, jointY;

    // Pixel size of a tile
    public static final int TILE_SIZE = 32;
    // Half of pixel size of a tile
    public static final int HALF_TILE_SIZE = TILE_SIZE / 2;
    // How many positions on each axis there are in each tile
    public static final int SUBTILE_NUM = 4;
    // Pixels between sub-positions in a tile
    public static final int SUBTILE_DELTA = TILE_SIZE / SUBTILE_NUM;

    public Tile(short x, short y, Chunk chunk) {
        this.x = x;
        this.y = y;
        this.chunk = chunk;

        this.globalX = chunk.x * Chunk.CHUNK_SIZE + x;
        this.globalY = chunk.y * Chunk.CHUNK_SIZE + y;
        this.drawX = globalX * TILE_SIZE + Tile.HALF_TILE_SIZE;
        this.drawY = globalY * TILE_SIZE + Tile.HALF_TILE_SIZE;
        this.jointX = globalX * Tile.SUBTILE_NUM;
        this.jointY = globalY * Tile.SUBTILE_NUM;

        this.entities = new HashSet<>();
        this.nearbyMoveboxes = new HashSet<>();
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

    public int getJointX() {
        return jointX;
    }

    public int getJointY() {
        return jointY;
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

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("x", x, "y", y, "chunk", chunk, "entities", entities, "nearbyMoveboxes", nearbyMoveboxes, "traits", traits);
    }
}
