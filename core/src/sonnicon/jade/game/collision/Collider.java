package sonnicon.jade.game.collision;

import sonnicon.jade.content.Content;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Collider implements IPositionMoving, IHitbox {
    protected float x;
    protected float y;
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
        return 0f;
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
        return false;
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

    public void containingChunks(ArrayList<Chunk> results) {
        TEMP_SEARCHLIST.clear();
        if (getTile() == null) return;

        // Breadth-first search containing chunks
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
    }
}
