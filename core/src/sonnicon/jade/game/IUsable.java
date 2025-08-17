package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;

@FunctionalInterface
public interface IUsable {
    boolean use(Entity user, float targetX, float targetY);
}