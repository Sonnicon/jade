package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;

@FunctionalInterface
public interface IUsable {
    void use(Entity user, int jointX, int jointY);
}