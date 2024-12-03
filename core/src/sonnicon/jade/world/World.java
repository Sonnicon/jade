package sonnicon.jade.world;

import sonnicon.jade.input.WorldInput;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class World implements IDebuggable {
    public final HashMap<Integer, Chunk> chunks = new HashMap<>();

    public World() {
        new WorldInput();
    }

    public Tile getTile(int x, int y) {
        short chunkX = (short) Math.floorDiv(x, Chunk.CHUNK_SIZE);
        short chunkY = (short) Math.floorDiv(y, Chunk.CHUNK_SIZE);
        Chunk chunk = chunks.get(Chunk.getHashcode(chunkX, chunkY));
        if (chunk == null) {
            return null;
        }
        return chunk.getTile((short) (x % Chunk.CHUNK_SIZE), (short) (y % Chunk.CHUNK_SIZE));
    }

    public Tile getTileJoint(int x, int y) {
        return getTile(Math.floorDiv(x, Tile.SUBTILE_NUM), Math.floorDiv(y, Tile.SUBTILE_NUM));
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("chunks", chunks);
    }
}
