package sonnicon.jade.world;

import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.input.WorldInput;

import java.util.HashMap;

public class World {
    public final HashMap<Integer, Chunk> chunks = new HashMap<>();

    private static final Vector3 TEMP_VEC = new Vector3();

    public World() {
        new WorldInput();
    }

    public Vector3 getTileScreenPosition(Vector3 vec, Tile tile) {
        return Jade.renderer.camera.project(
                vec.set(tile.getDrawX() + Tile.TILE_SIZE / 2f,
                        tile.getDrawY() + Tile.TILE_SIZE / 2f,
                        0f));
    }

    public Tile getScreenPositionTile(int x, int y) {
        TEMP_VEC.set(x, y, 0f);
        Jade.renderer.camera.unproject(TEMP_VEC);
        TEMP_VEC.scl(1f / Tile.TILE_SIZE);
        return getTile((short) TEMP_VEC.x, (short) TEMP_VEC.y);
    }

    public Tile getTile(short x, short y) {
        short chunkX = (short) Math.floorDiv(x, Chunk.CHUNK_SIZE);
        short chunkY = (short) Math.floorDiv(y, Chunk.CHUNK_SIZE);
        Chunk chunk = chunks.get(Chunk.getHashcode(chunkX, chunkY));
        if (chunk == null) {
            return null;
        }
        return chunk.getTile((short) (x % Chunk.CHUNK_SIZE), (short) (y % Chunk.CHUNK_SIZE));
    }
}
