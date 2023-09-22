package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.*;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

import java.util.Map;

//todo make the funky shadows extend instead of being per-wall
public class WallDrawComponent extends ChunkDrawComponent {
    // bitshifting byte will promote it to signed int, which makes >> and >>> equivalent (problem)
    private short nearbyWalls = 0;

    private static final TextureRegion texo0 = Textures.atlasFindRegion("occlusion-0");
    private static final TextureRegion texo1 = Textures.atlasFindRegion("occlusion-1");
    private static final TextureRegion texg0 = Textures.atlasFindRegion("gradient-0");
    private static final TextureRegion texg1 = Textures.atlasFindRegion("gradient-1");


    private static final EventTypes.EntityMoveTileEvent moveEvent = (Entity ent, Tile source, Tile dest) -> {
        WallDrawComponent comp = ent.getComponent(WallDrawComponent.class);
        comp.nearbyWalls = 0;

        if (source != null) {
            comp.nearbyChange(false, source);
        }

        if (dest != null) {
            comp.nearbyChange(true, dest);
        }

        ((CachedDrawBatch) Renderer.Batch.terrain.batch).invalidate();
        ((CachedDrawBatch) Renderer.Batch.terrainDynamic.batch).invalidate();
    };


    public WallDrawComponent() {

    }

    public WallDrawComponent(TextureSet textures) {
        super(textures, Tile.TILE_SIZE, Tile.TILE_SIZE, Renderer.RenderLayer.terrain);
    }


    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(moveEvent);
        moveEvent.apply(entity, null, entity.getComponent(PositionComponent.class).getTile());
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(moveEvent);
        moveEvent.apply(entity, entity.getComponent(PositionComponent.class).getTile(), null);
    }

    @Override
    protected void addToChunk(Chunk chunk) {
        super.addToChunk(chunk);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        TerrainSpriteBatch b = (TerrainSpriteBatch) batch;
        switch (layer) {
            case terrainBottom: {
                // Surrounding shadows
                float drawx = positionComponent.getDrawX() - 8f - Tile.HALF_TILE_SIZE - FowBatch.PIXEL_FIXER;
                float drawy = positionComponent.getDrawY() - 8f - Tile.HALF_TILE_SIZE - FowBatch.PIXEL_FIXER;
                float draww = Tile.TILE_SIZE + 16f + FowBatch.PIXEL_FIXER_XL;
                float drawh = Tile.TILE_SIZE + 16f + FowBatch.PIXEL_FIXER_XL;

                // Cardinal shadows
                b.draw(texo0, drawx, drawy, draww, drawh);

                // Corner shadows
                for (byte i = 0; i < 4; i += 1) {
                    byte ibit = (byte) (1 << (i * 2));
                    if ((nearbyWalls & (ibit | ibit << 1)) == 0 && (nearbyWalls & ((ibit << 2) | (ibit >>> 6))) == 0) {
                        b.draw(texo1, drawx, drawy, draww, drawh, (byte) ((i + 1) % 4));
                    }
                }
                break;
            }

            case terrain:
                // Actual wall
                super.render(batch, delta, layer);
                break;

            case terrainTop: {
                // Internal shadows
                Entity playerEntity = PlayerControlComponent.getEntity();
                if (playerEntity == null) {
                    return;
                }
                PositionComponent playerPos = playerEntity.getComponent(PositionComponent.class);

                short playerDir = Direction.relate(positionComponent, playerPos, Tile.SUBTILE_NUM / 2f);
                short playerAwayDir = (short) ((playerDir << 2) | (playerDir >>> 2));

                byte leftIndexDir = 0;
                for (byte i = 0; i < 4; i++) {
                    if ((playerAwayDir & (1 << i)) > 0 && (playerAwayDir & (1 << ((i + 1) % 4))) > 0) {
                        leftIndexDir = i;
                        break;
                    }
                }

                float drawx = positionComponent.getDrawX() - Tile.HALF_TILE_SIZE - DrawBatch.PIXEL_FIXER;
                float drawy = positionComponent.getDrawY() - Tile.HALF_TILE_SIZE - DrawBatch.PIXEL_FIXER;
                float draww = Tile.TILE_SIZE + DrawBatch.PIXEL_FIXER_XL;
                float drawh = Tile.TILE_SIZE + DrawBatch.PIXEL_FIXER_XL;

                byte drawnInner = 0;
                for (byte i = 0; i < 4; i++) {
                    if (((nearbyWalls & (1 << (i * 2)))) == 0 ||
                            (((1 << i) & playerAwayDir) != 0 &&
                                    (((1 << i) & playerDir) == 0 &&
                                            (nearbyWalls & (1 <<
                                                    Math.floorMod((2 * i + (i == leftIndexDir ? -2 : 2)), 8))) != 0 ||
                                            (playerDir & (playerDir - 1)) == 0))) {
                        b.draw(texg0, drawx, drawy, draww, drawh, i);
                        drawnInner |= 1 << i;
                    }
                }

                for (byte i = 0; i < 4; i++) {
                    byte bit = (byte) (1 << i);
                    if ((drawnInner & bit) == 0 && (drawnInner & (bit << 1 | bit >>> 3)) == 0) {
                        b.draw(texg1, drawx, drawy, draww, drawh, (byte) ((i + 1) % 4));
                    }
                }
                break;
            }
        }
    }

    private static short dirToBit(short dir) {
        for (int i = 0; i < 4; i++) {
            if ((dir & (1 << i)) > 0 && (dir & (1 << (i + 3 % 4))) == 0) {
                return (short) (1 << (i * 2 + ((dir & (1 << ((i + 1) % 4))) > 0 ? 1 : 0)));
            }
        }
        throw new IllegalArgumentException();
    }

    private void nearbyChange(boolean adding, Tile target) {
        Direction.round(dir -> {
            short bdir = dirToBit(dir);
            Tile other = target.getNearby(dir);

            if (other != null) {
                for (Entity otherE : other.entities) {
                    WallDrawComponent otherComp = otherE.getComponent(WallDrawComponent.class);
                    if (otherComp == null) {
                        continue;
                    }
                    short oper = (short) (((bdir << 4) | (bdir >>> 4)) & 0b11111111);
                    if (adding) {
                        nearbyWalls |= bdir;
                        otherComp.nearbyWalls |= oper;
                    } else {
                        otherComp.nearbyWalls ^= oper;
                    }
                }
            }
        });
    }

    @Override
    public WallDrawComponent copy() {
        return (WallDrawComponent) super.copy();
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "nearbyWalls", Integer.toBinaryString(nearbyWalls));
    }
}
