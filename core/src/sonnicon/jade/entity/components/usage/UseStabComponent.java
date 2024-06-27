package sonnicon.jade.entity.components.usage;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.graphical.AnimationComponent;
import sonnicon.jade.entity.components.world.FloatingPositionComponent;
import sonnicon.jade.entity.components.world.PositionBindComponent;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.game.actions.RunnableAction;
import sonnicon.jade.graphics.animation.Animation;
import sonnicon.jade.util.Translation;
import sonnicon.jade.util.Utils;

import java.util.HashSet;
import java.util.Map;

public class UseStabComponent extends UseRangeComponent {
    //todo refine this entire component
    private PositionBindComponent positionBindComponent;
    private Translation translation;
    private RunnableAction currentAction;
    private PositionComponent oldPositionComponent;
    private Animation animation;

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

        // Moving animation
        AnimationComponent ac = entity.getComponent(AnimationComponent.class);
        translation.mark();
        translation.setRotatedOffset(0f, 48f);
        animation = translation.getAnimation(1f).play(ac);
    }

    public void stabThrust() {
        // Actual moving
        animation.stop();
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

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(AnimationComponent.class);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "positionBind", positionBindComponent, "translation", translation, "currentAction", currentAction, "oldPositionComponent", oldPositionComponent);
    }
}
