package sonnicon.jade.game.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.game.Entity;
import sonnicon.jade.graphics.Renderer;

public class AutoDrawComponent extends DrawComponent {

    public AutoDrawComponent(TextureRegion region, float width, float height) {
        super(region, width, height);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Renderer.renderList.add(this);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Renderer.renderList.remove(this);
    }
}
