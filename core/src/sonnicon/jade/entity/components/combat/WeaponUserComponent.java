package sonnicon.jade.entity.components.combat;

import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.graphical.WorldDrawComponent;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.particles.TextParticle;
import sonnicon.jade.input.WorldInput;
import sonnicon.jade.util.Utils;

import java.util.HashSet;

public class WeaponUserComponent extends Component implements IRenderable {

    public void attack(EntityStorageSlot weapon) {
        Vector3 temp = new Vector3();
        WorldInput.readWorldPosition(temp, entity.getComponent(PositionComponent.class).tile);
        Jade.renderer.particles.createParticle(TextParticle.class, temp.x, temp.y).setText("atk");
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.getComponentFuzzy(WorldDrawComponent.class).addJoined(this);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(WorldDrawComponent.class);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {

    }
}
