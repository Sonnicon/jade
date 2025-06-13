package sonnicon.jade.game;

import sonnicon.jade.world.Tile;
import sonnicon.jade.world.World;

public interface IPosition {
    /**
     * @return World-space horizontal X position of object's center.
     */
    float getX();

    /**
     * @return World-space horizontal Y position object's center.
     */
    float getY();

    /**
     * @return Clockwise rotation from vertical up.
     */
    float getRotation();

    /**
     * @return Tile containing center of this object.
     */
    default Tile getTile() {
        if (Float.isNaN(getX()) || Float.isNaN(getY())) {
            return null;
        } else {
            return getWorld().getTile((int) (getX() / Tile.TILE_SIZE), (int) (getY() / Tile.TILE_SIZE));
        }
    }

    /**
     * @return World containing this object.
     */
    World getWorld();
}
