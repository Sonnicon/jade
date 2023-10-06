package sonnicon.jade.entity.components.usage;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.world.FloatingPositionComponent;
import sonnicon.jade.entity.components.world.PositionBindComponent;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.game.actions.RunnableAction;
import sonnicon.jade.util.Translation;

public class UseStabComponent extends UseRangeComponent {
    //todo refine this entire component
    private PositionBindComponent positionBindComponent;
    private Translation translation;
    private RunnableAction currentAction;
    private PositionComponent oldPositionComponent;

    //todo reuse objects
    public UseStabComponent() {

    }

    public UseStabComponent(float rangeMin, float rangeMax) {
        super(rangeMin, rangeMax);
    }

    @Override
    public void use(Entity user, float dist, float angle) {
        if (entity.traits.hasTrait(Traits.Trait.inUse)) {
            return;
        }

        translation = Translation.obtain().setRotatedOffset(0f, 16f).setFollowRotationOffset(angle);
        positionBindComponent = new PositionBindComponent(user, translation);

        stabUnsheathe();
    }

    //todo extract interface from this

    public void stabUnsheathe() {
        // Change to floating position
        oldPositionComponent = entity.getComponent(PositionComponent.class);
        entity.removeComponent(PositionComponent.class);
        entity.addComponents(new FloatingPositionComponent());
        // Set up position binding
        entity.addComponent(positionBindComponent);
        // Traits
        entity.addTraits(Traits.Trait.stopPickup, Traits.Trait.inUse);
        //todo
        currentAction = (RunnableAction) Actions.obtain(RunnableAction.class)
                .set(this::stabThrust)
                .time(1f)
                .keepRef()
                .enqueue();
    }

    public void stabThrust() {
        translation.setRotatedOffset(0f, 48f);
        positionBindComponent.moveTo();
        currentAction.set(this::stabSheathe)
                .time(1f)
                .enqueue();
    }

    public void stabSheathe() {
        entity.removeComponent(positionBindComponent);
        entity.removeComponent(PositionComponent.class);
        entity.addComponent(oldPositionComponent);
        entity.removeTraits(Traits.Trait.inUse, Traits.Trait.stopPickup);
        currentAction.free();
    }

    public void stabInterrupt() {
        //todo free
        currentAction.free();
    }
}
