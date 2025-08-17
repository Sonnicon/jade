package sonnicon.jade.entity.components;

import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.game.collision.CircleCollider;
import sonnicon.jade.game.collision.SquareCollider;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;
import sonnicon.jade.world.Chunk;

public class DebugComponent extends Component implements IRenderable {
    private static final class DebugOverlays implements IRenderable {
        private final Chunk chunk;

        public DebugOverlays(Chunk chunk) {
            this.chunk = chunk;
            chunk.addRenderable(this, RenderLayer.overfow);
            chunk.addRenderable(this, RenderLayer.gui);
        }

        @Override
        public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
            SpriteBatch b = (SpriteBatch) batch;
            b.draw(Textures.atlasFindRegion("debugBox2"),
                    chunk.getX() + 1f - Chunk.CHUNK_WORLD_SIZE / 2f,
                    chunk.getY() + 1f - Chunk.CHUNK_WORLD_SIZE / 2f,
                    Chunk.CHUNK_WORLD_SIZE - 2f, Chunk.CHUNK_WORLD_SIZE - 2f);
        }
    }

    private static boolean overlaysActive = false;

    public DebugComponent() {
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Jade.renderer.addRenderable(this, RenderLayer.gui);
        Jade.renderer.addRenderable(this, RenderLayer.overfow);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Jade.renderer.removeRenderable(this);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        //todo
        if (!overlaysActive) {
            overlaysActive = true;
            for (Chunk c : Content.world.chunks.values()) {
                new DebugOverlays(c);
            }
        }

        SpriteBatch b = (SpriteBatch) batch;

        CollisionComponent c = entity.getComponent(CollisionComponent.class);
        if (c != null) {

            float colliderRadius = 0f;
            if (c.collider instanceof CircleCollider) colliderRadius = ((CircleCollider) c.collider).getRadius();
            else if (c.collider instanceof SquareCollider) colliderRadius = ((SquareCollider) c.collider).getRadius();

            b.draw(Textures.atlasFindRegion("debugPoint"),
                    c.collider.getX() - colliderRadius, c.collider.getY() - colliderRadius,
                    colliderRadius * 2f, colliderRadius * 2f);
        }
    }
}
