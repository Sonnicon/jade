package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.*;
import sonnicon.jade.util.Directions;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

//todo make the funky shadows extend instead of being per-wall
public class WallDrawComponent extends ChunkDrawComponent {
    // bitshifting byte will promote it to signed int, which makes >> and >>> equivalent (problem)
    private byte nearbyWalls = 0;

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

        ((CachedDrawBatch) RenderLayer.terrainBottom.batch).invalidate();
        ((CachedDrawBatch) RenderLayer.terrainSides.batch).invalidate();
        ((CachedDrawBatch) RenderLayer.terrainTop.batch).invalidate();
    };


    public WallDrawComponent() {

    }

    public WallDrawComponent(TextureSet textures) {
        super(textures, Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.terrainTop);
    }


    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(moveEvent);
        moveEvent.apply(entity, null, entity.getTile());
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(moveEvent);
        moveEvent.apply(entity, entity.getTile(), null);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        //todo
        switch (layer) {
            case terrainBottom: {
                drawWallBottomShadow(batch, delta);
                break;
            }

            case terrainSides:
                drawWallSides(batch, delta);
                break;

            case terrainTop: {
                drawWallTop(batch, delta);
                drawWallTopShadow(batch, delta);
                break;
            }
        }

        if (joinedRenderables != null) {
            joinedRenderables.forEach(r -> r.render(batch, delta, layer));
        }
    }

    private void drawWallBottomShadow(GraphicsBatch batch, float delta) {
        StaticTerrainSpriteBatch b = (StaticTerrainSpriteBatch) batch;

        // Surrounding shadows
        float drawx = entity.getX() - 8f - Tile.HALF_TILE_SIZE - FowBatch.PIXEL_FIXER;
        float drawy = entity.getY() - 8f - Tile.HALF_TILE_SIZE - FowBatch.PIXEL_FIXER;
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
    }

    private void drawWallSides(GraphicsBatch batch, float delta) {
        SideTerrainSpriteBatch b = (SideTerrainSpriteBatch) batch;

        // Internal shadows
        Entity playerEntity = PlayerControlComponent.getEntity();
        if (playerEntity == null) {
            return;
        }

        byte playerDir = Directions.relate(entity, playerEntity, Tile.HALF_TILE_SIZE + 0.01f);

        float drawX = entity.getX();
        float drawY = entity.getY();
        float camX = Jade.renderer.camera.position.x;
        float camY = Jade.renderer.camera.position.y;

        float bx1 = drawX - width / 2f;
        float by1 = drawY - height / 2f;
        float bx2 = drawX + width / 2f;
        float by2 = drawY + height / 2f;

        float tx1 = project(bx1, camX);
        float ty1 = project(by1, camY);
        float tx2 = project(bx2, camX);
        float ty2 = project(by2, camY);

        TextureRegion region = textures.getDrawable().getRegion();


        if (Directions.is(playerDir, Directions.SOUTHWARD) && !Directions.is(nearbyWalls, Directions.SOUTH)) {
            b.draw(region,
                    bx1, by1,
                    tx1, ty1,
                    tx2, ty1,
                    bx2, by1);
        }

        if (Directions.is(playerDir, Directions.WESTWARD) && !Directions.is(nearbyWalls, Directions.WEST)) {
            b.draw(region,
                    bx1, by2,
                    tx1, ty2,
                    tx1, ty1,
                    bx1, by1);
        }

        if (Directions.is(playerDir, Directions.NORTHWARD) && !Directions.is(nearbyWalls, Directions.NORTH)) {
            b.draw(region,
                    bx2, by2,
                    tx2, ty2,
                    tx1, ty2,
                    bx1, by2);
        }

        if (Directions.is(playerDir, Directions.EASTWARD) && !Directions.is(nearbyWalls, Directions.EAST)) {
            b.draw(region,
                    bx2, by1,
                    tx2, ty1,
                    tx2, ty2,
                    bx2, by2);
        }
    }

    private void drawWallTop(GraphicsBatch batch, float delta) {
        StaticTerrainSpriteBatch b = (StaticTerrainSpriteBatch) batch;
        TextureRegion region = textures.getDrawable().getRegion();

        float drawX = entity.getX();
        float drawY = entity.getY();

        float x1 = drawX - width / 2f;
        float y1 = drawY - height / 2f;
        float x2 = drawX + width / 2f;
        float y2 = drawY + height / 2f;

        b.drawPoly(region, x1, y1, x2, y2);
    }

    private void drawWallTopShadow(GraphicsBatch batch, float delta) {
        StaticTerrainSpriteBatch b = (StaticTerrainSpriteBatch) batch;

        // Internal shadows
        Entity playerEntity = PlayerControlComponent.getEntity();
        if (playerEntity == null) {
            return;
        }

        short playerDir = Directions.toCompact(Directions.relate(
                entity,
                playerEntity,
                Tile.HALF_TILE_SIZE + 0.01f));
        short playerAwayDir = (short) ((playerDir << 2) | (playerDir >>> 2));

        byte leftIndexDir = 0;
        for (byte i = 0; i < 4; i++) {
            if ((playerAwayDir & (1 << i)) > 0 && (playerAwayDir & (1 << ((i + 1) % 4))) > 0) {
                leftIndexDir = i;
                break;
            }
        }

        float x1 = entity.getX() - width / 2f;
        float y1 = entity.getY() - height / 2f;

        byte drawnInner = 0;
        for (byte i = 0; i < 4; i++) {
            if (((nearbyWalls & (1 << (i * 2)))) == 0 ||
                    (((1 << i) & playerAwayDir) != 0 &&
                            (((1 << i) & playerDir) == 0 &&
                                    (nearbyWalls & (1 <<
                                            Math.floorMod((2 * i + (i == leftIndexDir ? -2 : 2)), 8))) != 0 ||
                                    (playerDir & (playerDir - 1)) == 0))) {
                b.draw(texg0, x1, y1, width, height, i);
                drawnInner |= 1 << i;
            }
        }

        for (byte i = 0; i < 4; i++) {
            byte bit = (byte) (1 << i);
            if ((drawnInner & bit) == 0 && (drawnInner & (bit << 1 | bit >>> 3)) == 0) {
                b.draw(texg1, x1, y1, width, height, (byte) ((i + 1) % 4));
            }
        }
    }

    private static float project(float v, float fromV) {
        final float distance = 1.2f;
        return (v - fromV) * distance + fromV;
    }

    private void nearbyChange(boolean adding, Tile target) {
        target.allNearbyRound((Tile other, Byte dir) -> {
            for (Entity otherE : other.entities) {

                WallDrawComponent otherComp = otherE.getComponent(WallDrawComponent.class);
                if (otherComp == null) {
                    continue;
                }

                byte opposite = Directions.opposite(dir);
                if (adding) {
                    nearbyWalls |= dir;

                    otherComp.nearbyWalls |= opposite;
                } else {
                    otherComp.nearbyWalls ^= opposite;
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
        Map<Object, Object> result = Utils.mapExtendFrom(super.debugProperties(),
                "nearbyWalls", Directions.toString(nearbyWalls));


        Entity playerEntity = PlayerControlComponent.getEntity();
        if (playerEntity != null) {
            byte playerDir = Directions.relate(entity, playerEntity, Tile.HALF_TILE_SIZE + 0.01f);
            result.put("playerDir", Directions.toString(playerDir));
        }

        entity.getTile().allNearbyRound((Tile tile, Byte dir) -> {
            result.put("nearbyTiles " + Directions.toString(dir), tile);
        });


        return result;
    }
}
