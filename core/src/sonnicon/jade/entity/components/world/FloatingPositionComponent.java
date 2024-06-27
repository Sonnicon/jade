package sonnicon.jade.entity.components.world;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.graphical.AnimationComponent;
import sonnicon.jade.game.Content;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Translation;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

public class FloatingPositionComponent extends PositionComponent {
    public float x, y;
    public float rotation = 0f;

    public FloatingPositionComponent() {

    }

    public FloatingPositionComponent(float x, float y) {
        setup(x, y);
    }

    private FloatingPositionComponent setup(float x, float y) {
        moveTo(x, y);
        return this;
    }

    @Override
    public void moveToOther(PositionComponent other) {
        moveTo(other.getDrawX(), other.getDrawY());
    }

    @Override
    public void moveToOther(PositionComponent other, Translation translation) {
        translation.apply(other);
        moveTo(translation.getResX(), translation.getResY());
        rotateTo(translation.getResR());
    }

    @Override
    public void moveToNull() {
        moveTo(Float.NaN, Float.NaN);
    }

    public void moveTo(float x, float y) {
        if (entity == null) {
            this.x = x;
            this.y = y;
        } else {
            Tile told = getTile();
            this.x = x;
            this.y = y;
            Tile tnew = getTile();

            if (told != tnew) {
                if (told != null) {
                    told.entities.remove(entity);
                }
                if (tnew != null) {
                    tnew.entities.add(entity);
                }
                fireTileEvent(entity, told, tnew);
            }
            EventTypes.EntityMoveEvent.handle(entity.events, entity);
        }
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);

        Tile t = getTile();
        if (t != null) {
            t.entities.add(entity);
            fireTileEvent(entity, null, t);
            EventTypes.EntityMoveEvent.handle(entity.events, entity);
        }
    }

    @Override
    public void removeFromEntity(Entity entity) {
        moveToNull();
        super.removeFromEntity(entity);
    }

    @Override
    public boolean isInNull() {
        return Float.isNaN(x) || Float.isNaN(y);
    }

    @Override
    public void rotate(float deltaAngle) {
        rotateTo((rotation + deltaAngle) % 360f);
    }

    @Override
    public void rotateTo(float newAngle) {
        rotation = newAngle;
        EventTypes.EntityRotateEvent.handle(entity.events, entity, rotation);
    }

    @Override
    public float getDrawX() {
        AnimationComponent ac = entity.getComponent(AnimationComponent.class);
        if (ac == null || !ac.isAnimating()) {
            return x;
        } else {
            return x + ac.getX();
        }
    }

    @Override
    public float getDrawY() {
        AnimationComponent ac = entity.getComponent(AnimationComponent.class);
        if (ac == null || !ac.isAnimating()) {
            return y;
        } else {
            return y + ac.getY();
        }
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
        AnimationComponent ac = entity.getComponent(AnimationComponent.class);
        if (ac == null || !ac.isAnimating()) {
            return rotation;
        } else {
            return rotation + ac.getRotation();
        }
    }

    @Override
    public Tile getTile() {
        return Content.world.getTile(getTileX(), getTileY());
    }

    @Override
    public FloatingPositionComponent copy() {
        return ((FloatingPositionComponent) super.copy()).setup(x, y);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "x", x, "y", y);
    }
}
