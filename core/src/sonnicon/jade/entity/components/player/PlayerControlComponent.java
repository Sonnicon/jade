package sonnicon.jade.entity.components.player;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.gui.actors.InventoryHandButton;
import sonnicon.jade.util.Events;
import sonnicon.jade.util.IComparable;

@EventGenerator(id = "EntityControlled", param = {Entity.class}, label = {"entity"})
@EventGenerator(id = "EntityUncontrolled", param = {Entity.class}, label = {"entity"})
public class PlayerControlComponent extends Component {
    public short selectedHand = InventoryHandButton.HAND_NONE;

    private static final PlayerControlComponent comp;
    public static final Events events = new Events();

    static {
        final EventTypes.EntityControlledEvent controlledHandler =
                (Entity e) -> ((StageIngame) Gamestate.State.ingame.getStage()).recreate();
        events.register(controlledHandler);

        comp = new PlayerControlComponent();
    }

    private PlayerControlComponent() {
    }

    public static void setControlledEntity(Entity entity) {
        if (comp.entity != null) {
            comp.entity.removeComponent(comp);
        }
        if (entity != null) {
            entity.addComponent(comp);
        }
    }

    @Override
    public boolean canAddToEntity(Entity entity) {
        return super.canAddToEntity(entity) && getEntity() == null;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        EventTypes.EntityControlledEvent.handle(entity.events, entity);
        EventTypes.EntityControlledEvent.handle(events, entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        EventTypes.EntityUncontrolledEvent.handle(entity.events, entity);
        EventTypes.EntityUncontrolledEvent.handle(events, entity);
    }

    public void setSelectedHand(short hand) {
        selectedHand = hand;
    }

    public short getSelectedHand() {
        return selectedHand;
    }


    @Override
    public boolean compare(IComparable other) {
        return super.compare(other) && ((PlayerControlComponent) other).entity == entity;
    }

    public static PlayerControlComponent getControlled() {
        return comp;
    }

    public static Entity getEntity() {
        return comp.entity;
    }

    public static boolean isControlled(Entity entity) {
        return getEntity() == entity;
    }
}
