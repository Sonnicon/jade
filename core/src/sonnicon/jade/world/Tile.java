package sonnicon.jade.world;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.util.*;

import java.util.HashSet;
import java.util.Map;

public class Tile implements IDebuggable, IPosition {
    /**
     * Position of tile within the local chunk.
     */
    private final short localX, localY;
    /**
     * Chunk inside which the tile is.
     */
    public final Chunk chunk;
    /**
     * Entities with center within this tile.
     */
    public final HashSet<Entity> entities;
    /**
     * Trait attributes associated with this tile.
     */
    public final Traits traits;
    /**
     * Event handler for events happening to the tile.
     */
    public final Events events;

    /**
     * World-position floating size of a tile.
     */
    public static final int TILE_SIZE = 32;
    public static final int HALF_TILE_SIZE = TILE_SIZE / 2;

    public Tile(short localX, short y, Chunk chunk) {
        this.localX = localX;
        this.localY = y;
        this.chunk = chunk;

        this.entities = new HashSet<>();
        this.traits = new Traits();
        this.events = new Events();
    }


    /**
     * @return Horizontal X position of the tile in its world.
     */
    public int getTileX() {
        return chunk.chunkX * Chunk.CHUNK_SIZE + localX;
    }

    /**
     * @return Horizontal Y position of the tile in its world.
     */
    public int getTileY() {
        return chunk.chunkY * Chunk.CHUNK_SIZE + localY;
    }


    @Override
    public float getX() {
        return getTileX() * TILE_SIZE + Tile.HALF_TILE_SIZE;
    }

    @Override
    public float getY() {
        return getTileY() * TILE_SIZE + Tile.HALF_TILE_SIZE;
    }

    @Override
    public float getRotation() {
        return 0f;
    }

    @Override
    public World getWorld() {
        return chunk.getWorld();
    }

    public Tile getNearby(byte direction) {
        //todo was this important?
//        direction = Direction.flatten(direction);

        if (direction == 0) {
            return this;
        }

        short targetX = localX, targetY = localY;
        short dChunkX = 0, dChunkY = 0;

        // move target coords
        targetX += Directions.directionX(direction);
        targetY += Directions.directionY(direction);

        // move x chunk
        if (localX != targetX) {
            if (targetX >= Chunk.CHUNK_SIZE) {
                dChunkX++;
                targetX -= Chunk.CHUNK_SIZE;
            } else if (targetX < 0) {
                dChunkX--;
                targetX += Chunk.CHUNK_SIZE;
            }
        }

        // move y chunk
        if (localY != targetY) {
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
            c = chunk.getNearbyChunk(index);
        } else {
            // Moving multiple chunks
            c = chunk.getWorld().chunks.get(Chunk.getHashcode((short) (chunk.chunkX + dChunkX), (short) (chunk.chunkY + dChunkY)));
        }

        if (c != null) {
            return c.getTile(targetX, targetY);
        }
        return null;
    }

    public void allNearbyRound(Consumer2<Tile, Byte> cons) {
        Directions.round((Byte dir) -> {
            Tile other = getNearby(dir);
            if (other != null) {
                cons.apply(other, dir);
            }
        });
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("x", localX, "y", localY, "chunk", chunk, "entities", entities, "traits", traits);
    }
}
