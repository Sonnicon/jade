package sonnicon.jade.entity.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.game.collision.CircleCollider;
import sonnicon.jade.game.collision.RectangleCollider;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.world.Chunk;

public class DebugComponent extends Component implements IRenderable {

    private static final class DebugOverlays implements IRenderable {
        private final Chunk chunk;
        private int numTexts = 0;

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

            numTexts = 0;

            int s = PlayerControlComponent.getEntity().getComponent(CollisionComponent.class).quadtrees.size();
            writeText("Player Quadtrees: " + s, b);
        }

        public void writeText(String text, SpriteBatch batch) {
            BitmapFontCache cache = new BitmapFontCache(Gui.getFont());
            cache.setText(text, 10f, Gdx.graphics.getHeight() - 10f - 30f * numTexts);
            cache.draw(batch);
            numTexts++;
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

            if (c.collider instanceof CircleCollider) {
                CircleCollider col = (CircleCollider) c.collider;
                b.draw(Textures.atlasFindRegion("debug-boundCircle"),
                        col.getX() - col.getRadius(), col.getY() - col.getRadius(),
                        col.getRadius() * 2f, col.getRadius() * 2f);
            } else if (c.collider instanceof RectangleCollider) {
                RectangleCollider col = (RectangleCollider) c.collider;
                b.draw(Textures.atlasFindRegion("debug-boundSquare"),
                        col.getX() - col.getWidth() / 2f, col.getY() - col.getHeight() / 2f,
                        col.getWidth(), col.getHeight(), col.getRotation());
            }


        }

        if (entity.hasComponent(PlayerControlComponent.class) && layer == RenderLayer.gui) {


        }
    }
}
