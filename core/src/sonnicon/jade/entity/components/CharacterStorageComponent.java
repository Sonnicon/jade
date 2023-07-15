package sonnicon.jade.entity.components;

import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.gui.Gui;

import java.util.ArrayList;
import java.util.Iterator;

public class CharacterStorageComponent extends StorageComponent {
    public ArrayList<EntityStorageSlot> hands = new ArrayList<>();

    public CharacterStorageComponent() {

    }

    public CharacterStorageComponent(EntityStorage storage) {
        super(storage);
    }

    @Override
    public Class<? extends Component> getKeyClass() {
        return StorageComponent.class;
    }

    public void addHand(EntityStorageSlot slot) {
        hands.add(slot);
        if (Gui.activeStage == Gui.stageIngame && Gui.stageIngame.getControlledEntity() == entity) {
            Gui.stageIngame.addHand(slot);
        }
    }

    public void removeHand(EntityStorageSlot slot) {
        hands.remove(slot);
        if (Gui.activeStage == Gui.stageIngame && Gui.stageIngame.getControlledEntity() == entity) {
            Gui.stageIngame.recreateHands();
        }
    }

    @Override
    public CharacterStorageComponent copy() {
        CharacterStorageComponent copy = (CharacterStorageComponent) super.copy();

        //todo find a better way to identify hands
        // this relies on order of slots being maintained
        Iterator<EntityStorageSlot> copyIterator = copy.storage.slots.iterator();
        for(EntityStorageSlot slot : storage.slots) {
            EntityStorageSlot copySlot = copyIterator.next();
            if (hands.contains(slot)) {
                copy.hands.add(copySlot);
            }
            if (hands.size() == copy.hands.size()) {
                break;
            }
        }
        return copy;
    }

    @Override
    public boolean compare(Component other) {
        return super.compare(other) &&
                (other instanceof CharacterStorageComponent &&
                        ((CharacterStorageComponent) other).hands.size() == hands.size());
    }
}