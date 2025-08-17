package sonnicon.jade.entity.components.world;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.generated.EventTypes;

public class PositionRelativeComponent extends Component {
    protected float x = 0f;
    protected float y = 0f;
    protected float rotation = 0f;

    protected Entity binding;
    // Kept inactive while component is not assigned to an entity
    protected EventTypes.EntityMoveEvent onBindingMove = ignored -> onBindingMoved();
    protected EventTypes.EntityMoveEvent onEntityMove = ignored -> onEntityMoved();

    private float lastEntityX;
    private float lastEntityY;
    private float lastEntityRotation;

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        if (binding != null) {
            binding.events.register(onBindingMove);
            onBindingMoved();
        }
        entity.events.register(onEntityMove);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        if (binding != null) {
            binding.events.unregister(onBindingMove);
        }
        entity.events.unregister(onEntityMove);
    }

    public PositionRelativeComponent bindToEntity(Entity newBound) {
        // Unregister from previous
        if (binding != null) {
            binding.events.unregister(onBindingMove);
        }

        // Register to new
        this.binding = newBound;
        if (entity != null) {
            binding.events.register(onBindingMove);
            onBindingMoved();
        }
        return this;
    }

    protected void onBindingMoved() {
        if (binding == null || entity == null) return;

        float bindingRotation = binding.getRotation();
        float sin = MathUtils.sinDeg(bindingRotation);
        float cos = MathUtils.cosDeg(bindingRotation);

        float newX = binding.getX() + (x * cos + y * sin);
        float newY = binding.getY() + (x * -sin + y * cos);
        float newRotation = bindingRotation + rotation;

        lastEntityX = newX;
        lastEntityY = newY;
        lastEntityRotation = newRotation;
        entity.forceMoveTo(newX, newY);
        entity.rotateTo(newRotation);
    }

    protected void onEntityMoved() {
        if (binding == null || entity == null) return;

        float bindingRotation = binding.getRotation();
        float sin = MathUtils.sinDeg(bindingRotation);
        float cos = MathUtils.cosDeg(bindingRotation);

        float eX = entity.getX();
        float eY = entity.getY();
        float eRotation = entity.getRotation();

        //todo get rid of this
        if (eX == lastEntityX && eY == lastEntityY && eRotation == lastEntityRotation) return;

        this.x = (eX * cos + eY * -sin) - binding.getX();
        this.y = (eX * sin + eY * cos) - binding.getY();
        this.rotation = eRotation - bindingRotation;

    }

    public float getOffsetX() {
        return x;
    }

    public float getOffsetY() {
        return y;
    }

    public Entity getBinding() {
        return binding;
    }
}
