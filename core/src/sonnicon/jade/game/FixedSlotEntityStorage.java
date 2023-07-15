package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.util.Function3;

public class FixedSlotEntityStorage extends EntityStorage {

    public EntityStorageSlot addSlot(EntitySize minimumSize, EntitySize maximumSize, int maximumAmount,
                                          Function3<EntityStorageSlot, Entity, Integer, Integer> restriction) {
        EntityStorageSlot slot = super.appendNewSlot(null, 0);
        slot.minimumSize = minimumSize;
        slot.maximumSize = maximumSize;
        slot.maximumAmount = maximumAmount;
        slot.restriction = restriction;
        return slot;
    }

    @Override
    protected EntityStorageSlot appendNewSlot(Entity entity, int amount) {
        return null;
    }

    @Override
    public boolean appendSlot(EntityStorageSlot slot) {
        return false;
    }

    @Override
    public boolean disconnectSlot(EntityStorageSlot slot) {
        return false;
    }
}