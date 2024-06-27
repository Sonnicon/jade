package sonnicon.jade.game.actions;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;

public class PlayerCharacterMoveAction extends CharacterMoveAction implements Clock.IUpdate {

    @Override
    public void onStart() {
        super.onStart();
        Clock.register(this);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        Clock.unregister(this);
    }

    @Override
    public void update(float delta) {
        ((CachedDrawBatch) Renderer.Batch.terrainDynamic.batch).invalidate();
        ((CachedDrawBatch) Renderer.Batch.fow.batch).invalidate();
        Jade.renderer.viewOverlay.moveTo(target.entity.getComponent(PositionComponent.class));
    }
}