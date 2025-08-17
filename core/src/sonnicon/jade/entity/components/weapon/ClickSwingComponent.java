package sonnicon.jade.entity.components.weapon;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.world.PositionRelativeComponent;
import sonnicon.jade.game.IUsable;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.game.actions.ClickSwingAction;
import sonnicon.jade.graphics.particles.CrossParticle;
import sonnicon.jade.util.ObjectPool;

public class ClickSwingComponent extends Component implements IUsable {
    protected boolean isSwinging = false;
    protected PositionRelativeComponent posRelative;

    public void startSwinging() {
        isSwinging = true;
        //todo remove
        posRelative = new PositionRelativeComponent().bindToEntity(PlayerControlComponent.getEntity());
        entity.addComponent(posRelative);
    }

    public void stopSwinging() {
        isSwinging = false;
        entity.removeComponent(posRelative);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        //todo actually stop swinging when not swinging
        stopSwinging();
    }

    @Override
    public boolean use(Entity user, float targetX, float targetY) {
        if (!isSwinging) {
            startSwinging();
            return false;
        }

        ClickSwingAction action = ObjectPool.obtain(ClickSwingAction.class);
        action.set(entity, 48f, targetX, targetY);
        Jade.renderer.particles.createParticle(CrossParticle.class, targetX, targetY).scale = 0.2f;

        ClickSwingAction alreadySwinging = (ClickSwingAction) Actions.actionsList.stream().filter(a -> a instanceof ClickSwingAction).findFirst().orElse(null);
        if (alreadySwinging == null) {
            action.start();
        } else {
            ClickSwingAction toCancel = (ClickSwingAction) alreadySwinging.then.stream().filter(a -> a instanceof ClickSwingAction).findFirst().orElse(null);
            if (toCancel != null) {
                toCancel.free();
                alreadySwinging.then.remove(toCancel);
            }

            alreadySwinging.then(action);
        }

        return true;
    }
}
