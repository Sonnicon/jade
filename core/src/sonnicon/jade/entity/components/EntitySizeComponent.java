package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.game.EntitySize;

// mild abuse of the component system
public class EntitySizeComponent extends Component {
    public final EntitySize size;

    public static EntitySizeComponent colossal, huge, large, medium, compact, small, tiny;
    private static final boolean lock;

    static {
        try {
            // todo generate this
            tiny = new EntitySizeComponent(EntitySize.tiny);
            small = new EntitySizeComponent(EntitySize.small);
            compact = new EntitySizeComponent(EntitySize.compact);
            medium = new EntitySizeComponent(EntitySize.medium);
            large = new EntitySizeComponent(EntitySize.large);
            huge = new EntitySizeComponent(EntitySize.huge);
            colossal = new EntitySizeComponent(EntitySize.colossal);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        lock = true;
    }

    public EntitySizeComponent(EntitySize size) throws InstantiationException {
        if (lock) throw new InstantiationException();
        this.size = size;
    }

    public static void setSize(Entity entity, EntitySizeComponent size) {
        entity.components.replace(EntitySizeComponent.class, size);
    }

    public static boolean fitsSize(Entity entity, EntitySize min, EntitySize max) {
        EntitySizeComponent sizeComponent = entity.getComponent(EntitySizeComponent.class);
        if (sizeComponent == null) {
            return false;
        }
        EntitySize size = sizeComponent.size;
        return max.value >= size.value && min.value <= size.value;
    }

    @Override
    public void addToEntity(Entity entity) {
    }

    @Override
    public void removeFromEntity(Entity entity) {
    }

    @Override
    public boolean canAddToEntity(Entity entity) {
        return true;
    }

    @Override
    public Component copy() {
        return this;
    }

    @Override
    public boolean compare(Component other) {
        return this == other;
    }
}
