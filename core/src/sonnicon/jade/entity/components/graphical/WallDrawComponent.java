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
import sonnicon.jade.graphics.draw.DarknessBatch;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.TerrainSpriteBatch;
import sonnicon.jade.util.Direction;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

@EventGenerator(id = "WallNearbyChange", param = {Tile.class, Entity.class}, label = {"tile", "wallEntity"})
public class WallDrawComponent extends ChunkDrawComponent {
    private byte direction = 0;

    private static final float VIEW_BIG_DISTANCE = 500f;
    private static final float[] ROTATE_1 = new float[]{-Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE, -Tile.HALF_TILE_SIZE};

    private static final EventTypes.EntityMoveEvent moveEvent = (Entity ent, Tile source, Tile dest) -> {
        WallDrawComponent wdc = ent.getComponent(WallDrawComponent.class);
        if (source != null) {
            wdc.removeNearbyWalls(source);
        }
        if (dest != null) {
            wdc.addNearbyWalls(dest);
        }
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
        Tile tile = entity.getComponent(PositionComponent.class).tile;
        if (tile != null) {
            addNearbyWalls(tile);
        }
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(moveEvent);

        Tile tile = entity.getComponent(PositionComponent.class).tile;
        if (tile != null) {
            removeNearbyWalls(tile);
        }
    }

    @Override
    protected void addToChunk(Chunk chunk) {
        super.addToChunk(chunk);
        chunk.addRenderable(this, Renderer.RenderLayer.darkness);
    }

    public void addNearbyWalls(Tile origin) {
        origin.allNearby((Tile t, Byte dir) -> {
            WallDrawComponent otherWall;
            for (Entity e : t.entities) {
                if ((otherWall = e.getComponent(WallDrawComponent.class)) != null) {
                    addNearbyWall(dir);
                    otherWall.addNearbyWall(Direction.rotate(dir, (byte) 2));
                    break;
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
                    break;
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

        //todo refactor to new component, adjacent merging
        if (layer == this.layer) {
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
                            drawTile.getGlobalX(), drawTile.getGlobalY(), i);
                } else {
                    batch.draw(wtx.getDrawable(WallTextureType.inner).getRegion(),
                            drawTile.getGlobalX(), drawTile.getGlobalY(), (byte) ((i + 2) % 4));
                }
                return;
            }

            byte flatEdge = (byte) (playerDir & (direction ^ Direction.ALL));
            for (byte i = 0; i < 4; i++) {
                if ((flatEdge & (1 << i)) > 0) {
                    batch.draw(wtx.getDrawable(WallTextureType.middle).getRegion(), drawTile.getGlobalX(), drawTile.getGlobalY(), (byte) (i + 1));
                    return;
                }
            }
        } else if (layer == Renderer.RenderLayer.darkness && drawTile != playerTile) {
            DarknessBatch batch = (DarknessBatch) b;
            // Tile centers
            float[] dm = new float[]{drawTile.getDrawMiddleX(), drawTile.getDrawMiddleY()};
            float[] pm = new float[]{playerTile.getDrawMiddleX(), playerTile.getDrawMiddleY()};

            if (playerCardinal) {
                // Cardinal shadows

                // Directions and rotate offset
                byte shadowDirection = (byte) (((playerDir << 2) | (playerDir >> 2)) & Direction.ALL);
                int shadowIndex = (shadowDirection & 2) / 2 + (shadowDirection & 4) / 2 + (shadowDirection & 8) / 8 * 3;
                boolean shadowOnX = (shadowDirection & (Direction.EAST | Direction.WEST)) > 0;
                int sonx = shadowOnX ? 1 : 0, sony = sonx ^ 1;
                // Rotated corner coordinates
                float[] r1 = new float[]{ROTATE_1[shadowIndex], ROTATE_1[(shadowIndex + 1) % 4]};

                // Vector from player to close edge
                float[] dp = new float[]{dm[0] - r1[0] - pm[0], dm[1] - r1[1] - pm[1]};
                // rect extend offset
                float[] of = new float[]{r1[0], r1[1]};
                of[sonx] = (shadowIndex < 2 ? 1 : -1) * dp[sonx] * Tile.TILE_SIZE / dp[sony] + dp[sonx];

                boolean drawLeftTriangle = (direction & ((shadowDirection >> 1) | (shadowDirection << 3))) == 0;
                boolean drawRightTriangle = (direction & ((shadowDirection << 1) | (shadowDirection >> 3))) == 0;

                float x1 = dm[sonx] - VIEW_BIG_DISTANCE * dp[sonx];
                float x2 = dm[sonx] - of[sonx];
                float x3 = dm[sonx] + r1[sonx];
                float x4 = dm[sonx] - r1[sonx];
                float x5 = dm[sonx] + of[sonx];
                float x6 = dm[sonx] + VIEW_BIG_DISTANCE * dp[sonx];
                float y1 = dm[sony] + VIEW_BIG_DISTANCE * dp[sony];
                float y2 = dm[sony] + r1[sony];
                float y3 = dm[sony] - r1[sony];
                if (drawLeftTriangle && drawRightTriangle) {
                    batch.drawCShadowTwo(x1, x3, x4, x6, y1, y2, y3, shadowOnX);
                } else if (drawLeftTriangle) {
                    batch.drawCShadowOne(x1, x3, x5, x6, y1, y2, y3, shadowOnX);
                } else if (drawRightTriangle) {
                    batch.drawCShadowOne(x6, x4, x2, x1, y1, y2, y3, shadowOnX);
                } else {
                    batch.drawCShadowZero(x1, x2, x5, x6, y1, y2, shadowOnX);
                }
            } else {
                // Diagonal shadows

                byte shadowDirection = Direction.relate(playerTile, drawTile);
                byte shadowLeftIndex = 0;
                for (byte i = 0; i < 4; i++) {
                    if ((shadowDirection & (1 << i)) > 0 && (shadowDirection & (1 << ((i + 1) % 4))) > 0) {
                        shadowLeftIndex = i;
                        break;
                    }
                }
                byte shadowRightIndex = (byte) ((shadowLeftIndex + 1) % 4);
                float[] c1 = new float[]{ROTATE_1[shadowLeftIndex], ROTATE_1[shadowRightIndex]};
                float[] c2 = new float[]{c1[1], ROTATE_1[(shadowRightIndex + 1) % 4]};
                float[] c3 = new float[]{c2[1], ROTATE_1[(shadowRightIndex + 2) % 4]};

                boolean shadowOnX = (shadowLeftIndex == 1 || shadowLeftIndex == 3);
                int sonx = shadowOnX ? 1 : 0, sony = sonx ^ 1;

                float[] dp1 = new float[]{dm[0] + c1[0] - pm[0], dm[1] + c1[1] - pm[1]};
                float[] dp2 = new float[]{dm[0] + c3[0] - pm[0], dm[1] + c3[1] - pm[1]};

                float[] of1 = new float[]{c2[0], c2[1]};
                float[] of2 = new float[]{c2[0], c2[1]};
                of1[sony] += (shadowLeftIndex == 1 || shadowLeftIndex == 2 ? -1 : 1) * dp1[sony] * Tile.TILE_SIZE / dp1[sonx];
                of2[sonx] += (shadowLeftIndex == 3 || shadowLeftIndex == 2 ? -1 : 1) * dp2[sonx] * Tile.TILE_SIZE / dp2[sony];

                float[] fc1 = new float[]{dp1[0] * VIEW_BIG_DISTANCE, dp1[1] * VIEW_BIG_DISTANCE};
                float[] fc2 = new float[]{dp2[0] * VIEW_BIG_DISTANCE, dp2[1] * VIEW_BIG_DISTANCE};

                boolean shadowLeft = (direction & (1 << shadowLeftIndex)) == 0;
                shadowLeft |= (direction & (1 << ((shadowLeftIndex + 3) % 4))) > 0;
                boolean shadowRight = (direction & (1 << shadowRightIndex)) == 0;
                shadowRight |= (direction & (1 << ((shadowRightIndex + 1) % 4))) > 0;

                float x2 = dm[sony] + c2[sony];
                float x4 = dm[sony] + fc1[sony];
                float x5 = dm[sony] + fc2[sony];
                float y2 = dm[sonx] + c2[sonx];
                float y4 = dm[sonx] + fc1[sonx];
                float y5 = dm[sonx] + fc2[sonx];

                if (shadowLeft && shadowRight) {
                    batch.drawDShadowTwo(
                            dm[sony] + c3[sony], x2, x4, x5,
                            dm[sonx] + c1[sonx], y2, y4, y5,
                            !shadowOnX
                    );
                } else if (shadowLeft || shadowRight) {
                    batch.drawDShadowOne(
                            x2, dm[sony] + of1[sony], x4, x5,
                            y2, dm[sonx] + of2[sonx], y4, y5,
                            shadowLeft ? dm[sonx] + c1[sonx] : dm[sony] + c3[sony],
                            !shadowOnX, shadowLeft
                    );
                } else {
                    batch.drawDShadowZero(
                            x2, dm[sony] + of1[sony], x4, x5,
                            y2, dm[sonx] + of2[sonx], y4, y5,
                            !shadowOnX
                    );
                }
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
