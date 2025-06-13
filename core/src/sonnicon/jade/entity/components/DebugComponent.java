package sonnicon.jade.entity.components;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.entity.components.world.PositionRelativeComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.collision.CircleCollider;
import sonnicon.jade.game.collision.SquareCollider;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;
import sonnicon.jade.world.Chunk;

public class DebugComponent extends Component implements IRenderable {
//    public BitmapFontCache cache = new BitmapFontCache(Gui.getFont());

    private static final class DebugOverlays implements IRenderable {
        private final Chunk chunk;

        public DebugOverlays(Chunk chunk) {
            this.chunk = chunk;
            chunk.addRenderable(this, RenderLayer.overfow);
            chunk.addRenderable(this, RenderLayer.gui);
        }

        @Override
        public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
            //todo
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

        if (entity.hasComponent(PlayerControlComponent.class)) {
//            renderPlayer(b, delta, layer);
            return;
        }

        if (entity.hasComponent(PositionRelativeComponent.class)) {
            PositionRelativeComponent component = entity.getComponent(PositionRelativeComponent.class);
            float offset = (entity.id * 45f + Clock.getFrameNum() * 90f) % 360f;
            component.forceMoveTo(MathUtils.sinDeg(offset) * 32f, MathUtils.cosDeg(offset) * 32f);
            component.rotateBy(delta * (entity.id % 4) * 20f);
        }
//
//
//        b.draw(Textures.atlasFindRegion("debugPoint"),
//                entity.getX() - 16f, entity.getY() - 16f, 32f, 32f);
//        cache.setText(String.valueOf(entity.getTile().nearbyMoveboxes.size()),
//                entity.getX() - Tile.HALF_TILE_SIZE / 2f,
//                entity.getY() + Tile.HALF_TILE_SIZE * 0.8f);
//
//        if (layer == RenderLayer.gui) {
//            if (entity.getTile().traits.hasTrait(Traits.Trait.blockMovement)) cache.tint(Color.FIREBRICK);
//            else cache.tint(Color.ORANGE);
//        }
//        cache.draw(b);
    }
}
