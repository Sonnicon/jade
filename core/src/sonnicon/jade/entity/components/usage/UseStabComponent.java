package sonnicon.jade.entity.components.usage;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.world.PositionBindComponent;

public class UseStabComponent extends UseRangeComponent {


    public UseStabComponent() {

    }

    public UseStabComponent(float rangeMin, float rangeMax) {
        super(rangeMin, rangeMax);
    }

    @Override
    public void use(Entity user, float dist, float angle) {
        entity.addComponent(new PositionBindComponent(user));
        entity.addTrait(Traits.Trait.stopPickup);
    }
}
