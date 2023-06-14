package sonnicon.jade.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sonnicon.jade.content.WorldTemplates;
import sonnicon.jade.game.Entity;
import sonnicon.jade.game.components.DrawComponent;
import sonnicon.jade.graphics.Renderable;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class Tile {
    public final short x, y;
    public final Chunk chunk;
    public HashSet<Entity> entities;

    public static final int TILE_SIZE = 16;

    protected int globalX, globalY;
    protected int drawX, drawY;

    public Tile(short x, short y, Chunk chunk) {
        this.x = x;
        this.y = y;
        this.chunk = chunk;
        this.entities = new HashSet<>();

        updatePositions();
        entities.add(WorldTemplates.createFloorEntity(this));
    }

    public void renderAllEntities(SpriteBatch batch, float delta) {
        for (Entity entity : entities) {
            Renderable component = (Renderable) entity.components.getOrDefault(DrawComponent.class, null);
            if (component != null && !component.culled()) {
                component.render(batch, delta);
            }
        }
    }

    protected void updatePositions() {
        globalX = chunk.x * Chunk.CHUNK_SIZE + x;
        globalY = chunk.y * Chunk.CHUNK_SIZE + y;
        drawX = globalX * TILE_SIZE;
        drawY = globalY * TILE_SIZE;
    }

    public int getGlobalX() {
        return globalX;
    }

    public int getGlobalY() {
        return globalY;
    }

    public float getDrawX() {
        return drawX;
    }

    public float getDrawY() {
        return drawY;
    }
}
