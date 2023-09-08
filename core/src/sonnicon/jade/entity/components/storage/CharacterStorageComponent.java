package sonnicon.jade.entity.components.storage;

import com.badlogic.gdx.scenes.scene2d.Actor;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.Gamestate.State;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Structs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class CharacterStorageComponent extends StorageComponent {
    public final ArrayList<EntityStorageSlot> hands = new ArrayList<>();
    private Actor guiButton;

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
        if (entity != null && Gamestate.State.ingame.isActive() && PlayerControlComponent.isControlled(entity)) {
            ((StageIngame) State.ingame.getStage()).addHand(slot);
        }
    }

    public void removeHand(EntityStorageSlot slot) {
        hands.remove(slot);
        if (Gamestate.State.ingame.isActive() && PlayerControlComponent.isControlled(entity)) {
            ((StageIngame) State.ingame.getStage()).recreateHands();
        }
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);

        // Handling changes in control
        entity.events.register((EventTypes.EntityControlledEvent) e -> addGuiSlots());
        entity.events.register((EventTypes.EntityUncontrolledEvent) e -> removeGuiSlots());

        // Adding to already controlled entity
        if (PlayerControlComponent.isControlled(entity)) {
            addGuiSlots();
        }
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
    }

    private void addGuiSlots() {
        assert guiButton == null;
        StageIngame stage = (StageIngame) Gamestate.getStage();
        guiButton = stage.addToolbarButton("icon-insert-storage", () -> stage.panelInventory.show(storage));
    }

    private void removeGuiSlots() {
        assert guiButton != null;

        guiButton.remove();
        guiButton = null;
    }

    @Override
    public CharacterStorageComponent copy() {
        CharacterStorageComponent copy = (CharacterStorageComponent) super.copy();

        //todo find a better way to identify hands
        // this relies on order of slots being maintained
        Iterator<EntityStorageSlot> copyIterator = copy.storage.slots.iterator();
        for (EntityStorageSlot slot : storage.slots) {
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
    public boolean compare(IComparable other) {
        return super.compare(other) && ((CharacterStorageComponent) other).hands.size() == hands.size();
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Structs.mapExtendFrom(super.debugProperties(), "hands", hands);
    }
}