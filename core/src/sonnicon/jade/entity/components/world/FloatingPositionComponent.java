package sonnicon.jade.entity.components.world;

import sonnicon.jade.game.Content;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

public class FloatingPositionComponent extends PositionComponent {
    public float x, y;
    public float rotation = 0f;

    public FloatingPositionComponent() {

    }

    public FloatingPositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void moveToOther(PositionComponent other) {
        x = other.getDrawX();
        y = other.getDrawY();
    }

    @Override
    public void moveToNull() {
        x = Float.NaN;
        y = Float.NaN;
    }

    @Override
    public boolean isInNull() {
        return Float.isNaN(x) || Float.isNaN(y);
    }

    @Override
    public float getDrawX() {
        return x;
    }

    @Override
    public float getDrawY() {
        return y;
    }

    @Override
    public int getJointX() {
        return (int) (x / Tile.SUBTILE_DELTA);
    }

    @Override
    public int getJointY() {
        return (int) (y / Tile.SUBTILE_DELTA);
    }

    @Override
    public int getTileX() {
        return (int) (x / Tile.TILE_SIZE);
    }

    @Override
    public int getTileY() {
        return (int) (y / Tile.TILE_SIZE);
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public Tile getTile() {
        return Content.world.getTile(getTileX(), getTileY());
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "x", x, "y", y);
    }
}
