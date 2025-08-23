package sonnicon.jade.game.collision;

import sonnicon.jade.content.Content;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public abstract class Collider implements IPositionMoving, IHitbox, IDebuggable {
    protected float x;
    protected float y;
    protected float rotation;

    private static final ArrayList<Chunk> TEMP_SEARCHLIST = new ArrayList<>();

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public World getWorld() {
        //todo
        return Content.world;
    }

    @Override
    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean rotateTo(float degrees) {
        rotation = degrees;
        return true;
    }

    public boolean containsPoint(float otherX, float otherY) {
        return containsPoint(this, otherX, otherY);
    }

    public boolean intersects(IBound otherBound, IPosition otherPos) {
        return intersects(this, otherBound, otherPos);
    }

    public boolean intersects(IHitbox other) {
        return intersects(other, other);
    }

    public boolean containsPoint(IPosition other) {
        return containsPoint(this, other);
    }

    public ArrayList<Chunk> containingChunks(ArrayList<Chunk> results) {
        if (getTile() == null) return results;

        // Breadth-first search containing chunks
        TEMP_SEARCHLIST.clear();
        TEMP_SEARCHLIST.add(getTile().chunk);
        int i = 0;
        while (TEMP_SEARCHLIST.size() > i) {
            Chunk other = TEMP_SEARCHLIST.get(i);
            if (intersects(Chunk.bound, other)) {
                results.add(other);
                Arrays.stream(other.getNearbyChunks())
                        .filter(o -> o != null && !TEMP_SEARCHLIST.contains(o))
                        .forEach(TEMP_SEARCHLIST::add);
            }
            i++;
        }
        return results;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "x", getX(),
                "y", getY(),
                "rotation", getRotation(),
                "world", getWorld(),
                "containingChunks", containingChunks(new ArrayList<>())
        );
    }
}
