package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.TerrainSpriteBatch;
import sonnicon.jade.util.Direction;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

@EventGenerator(id = "WallNearbyChange", param = {Tile.class, Entity.class}, label = {"tile", "wallEntity"})
public class WallDrawComponent extends ChunkDrawComponent {
    private byte direction = 0;


    private static final EventTypes.EntityMoveTileEvent moveEvent = (Entity ent, Tile source, Tile dest) -> {
        WallDrawComponent wdc = ent.getComponent(WallDrawComponent.class);
        if (source != null) {
            wdc.removeNearbyWalls(source);
        }
        if (dest != null) {
            wdc.addNearbyWalls(dest);
        }
        ((CachedDrawBatch) Renderer.Batch.dynamicTerrain.batch).invalidate();
    };

    public WallDrawComponent() {

    }

    public WallDrawComponent(WallTextureSet textures, Renderer.RenderLayer layer) {
        super(textures, Tile.TILE_SIZE, Tile.TILE_SIZE, layer);
    }


    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(moveEvent);
        moveEvent.apply(entity, null, entity.getComponent(PositionComponent.class).tile);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(moveEvent);
        moveEvent.apply(entity, entity.getComponent(PositionComponent.class).tile, null);
    }

    @Override
    protected void addToChunk(Chunk chunk) {
        super.addToChunk(chunk);
    }

    public void addNearbyWalls(Tile origin) {
        origin.allNearby((Tile t, Byte dir) -> {
            WallDrawComponent otherWall;
            for (Entity e : t.entities) {
                if ((otherWall = e.getComponent(WallDrawComponent.class)) != null) {
                    addNearbyWall(dir);
                    otherWall.addNearbyWall(Direction.rotate(dir, (byte) 2));
                }
            }
        });
    }

    public void addNearbyWall(byte direction) {
        this.direction |= direction;
    }

    public void removeNearbyWalls(Tile origin) {
        origin.allNearby((Tile t, Byte dir) -> {
            WallDrawComponent otherWall;
            for (Entity e : t.entities) {
                if ((otherWall = e.getComponent(WallDrawComponent.class)) != null) {
                    removeNearbyWall(dir);
                    otherWall.removeNearbyWall(Direction.rotate(dir, (byte) 2));
                }
            }
        });
    }

    public void removeNearbyWall(byte direction) {
        this.direction &= direction ^ Direction.ALL;
    }

    @Override
    public void render(GraphicsBatch b, float delta, Renderer.RenderLayer layer) {
        if (positionComponent == null || positionComponent.tile == null) {
            return;
        }
        Tile drawTile = positionComponent.tile;
        WallTextureSet wtx = (WallTextureSet) textures;

        Entity playerEntity = PlayerControlComponent.getControlledEntity();
        if (playerEntity == null) {
            return;
        }
        Tile playerTile = playerEntity.getComponent(PositionComponent.class).tile;
        if (playerTile == null) {
            return;
        }
        byte playerDir = Direction.relate(drawTile, playerTile);

        boolean playerCardinal = ((playerDir & (playerDir - 1)) == 0);

        TerrainSpriteBatch batch = (TerrainSpriteBatch) b;
        boolean reverseShift = (playerDir & direction) == playerDir;
        boolean directCorner = ((playerDir & direction) == 0);
        if (!playerCardinal && (directCorner || reverseShift)) {
            byte i = 0;
            for (; i < 4; i++) {
                if ((playerDir & (1 << i)) > 0 && (playerDir & (1 << ((i + 1) % 4))) > 0) {
                    break;
                }
            }
            //todo fix sprites, normal maps
            if (directCorner) {
                batch.draw(wtx.getDrawable(WallTextureType.outer).getRegion(),
                        drawTile.getX(), drawTile.getY(), i);
            } else {
                batch.draw(wtx.getDrawable(WallTextureType.inner).getRegion(),
                        drawTile.getX(), drawTile.getY(), (byte) ((i + 2) % 4));
            }
            return;
        }

        byte flatEdge = (byte) (playerDir & (direction ^ Direction.ALL));
        for (byte i = 0; i < 4; i++) {
            if ((flatEdge & (1 << i)) > 0) {
                batch.draw(wtx.getDrawable(WallTextureType.middle).getRegion(), drawTile.getX(), drawTile.getY(), (byte) (i + 1));
                return;
            }
        }
    }

    @Override
    public WallDrawComponent copy() {
        return (WallDrawComponent) super.copy();
    }

    public static class WallTextureSet extends TextureSet {
        public WallTextureSet(String name, int count) {
            super(name);
            regions = new TextureRegionDrawable[count * 3];
            for (int i = 0; i < count; i++) {
                for (WallTextureType type : WallTextureType.values()) {
                    regions[i + type.getOffset(count)] = Textures.atlasFindDrawable(name + i + type.suffix);
                }
            }
        }

        public TextureRegionDrawable getDrawable(WallTextureType type) {
            return regions[getRegionIndex() + type.getOffset(regions.length / 3)];
        }

        @Override
        protected int getRegionIndex() {
            return Math.floorMod((int) ((animateTick ? Clock.getTickNum() : Clock.getUpdateNum()) * animateSpeed), regions.length / 3);
        }
    }

    private enum WallTextureType {
        middle("middle"),
        inner("inner"),
        outer("outer");

        final String suffix;

        WallTextureType(String suffix) {
            this.suffix = "-" + suffix;
        }

        int getOffset(int count) {
            return ordinal() * count;
        }
    }
}
