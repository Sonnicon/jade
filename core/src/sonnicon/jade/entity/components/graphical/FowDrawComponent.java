package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.draw.FowBatch;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

import java.util.HashSet;
import java.util.Map;

//todo stop using == for float comparison
//todo optimise the conditions
public class FowDrawComponent extends Component implements IRenderable {
    // Unique component data
    private Chunk currentChunk;
    // Number of connected FowDrawComponents in each direction
    private final short[] directionCounts = {0, 0, 0, 0};
    // Direction bits in which the edge of a chunk is reached, and is obscured in the next chunk
    private byte directionEdges = 0;
    // FowDrawComponents at end of row in each direction
    private final FowDrawComponent[] endFows = new FowDrawComponent[]{this, this, this, this};

    // Temporary variable
    private static final FowDrawComponent[] NEARBY_FOWS = new FowDrawComponent[4];

    // Constants
    // How far to extend a shadow behind source
    private static final float VIEW_BIG_DISTANCE = 50f;
    // Rotation offsets
    private static final float[] ROTATE_1 = {-Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE, -Tile.HALF_TILE_SIZE};
    private static final int[] R_INDEX_X = {0, 1, 1, 0};
    private static final int[] R_INDEX_Y = {0, 0, 1, 1};
    private static final float[] DEFAULT_RX = new float[]{-Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE};
    private static final float[] DEFAULT_RY = new float[]{Tile.HALF_TILE_SIZE, -Tile.HALF_TILE_SIZE};

    private static final EventTypes.EntityMoveTileEvent moveHandler =
            (Entity entity, Tile source, Tile dest) -> {
                FowDrawComponent comp = entity.getComponent(FowDrawComponent.class);
                Chunk c = dest == null ? null : dest.chunk;

                if (c != comp.currentChunk) {
                    if (comp.currentChunk != null) {
                        comp.removeFromChunk();
                    }

                    if (c != null) {
                        comp.addToChunk(c);
                    }
                }

                if (source != null) {
                    comp.removeNearbyFows(source);
                }
                if (dest != null) {
                    comp.addNearbyFows();
                }

                ((CachedDrawBatch) Renderer.Batch.fow.batch).invalidate();
            };

    public FowDrawComponent() {

    }

    public void addNearbyFows() {
        directionEdges = 0;
        Tile positionComponentTile = entity.getComponent(PositionComponent.class).getTile();
        for (byte dirIndex = 0; dirIndex < 4; dirIndex++) {
            FowDrawComponent comp = getFowInDirection(positionComponentTile, dirIndex);
            if (comp == null) {
                NEARBY_FOWS[dirIndex] = null;
            } else if (comp.entity.getComponent(PositionComponent.class).getTile().chunk != positionComponentTile.chunk) {
                NEARBY_FOWS[dirIndex] = null;
                directionCounts[dirIndex] = 0;
                directionEdges |= 1 << dirIndex;
                comp.propagateNearbyFows(dirIndex, (byte) ((dirIndex + 2) % 4), (short) 0, true, null);
            } else {
                directionCounts[dirIndex] = (short) (comp.directionCounts[dirIndex] + 1);
                directionEdges |= comp.directionEdges & (1 << dirIndex);
                NEARBY_FOWS[dirIndex] = comp;
                endFows[dirIndex] = comp.endFows[dirIndex];
            }
        }
        for (byte dirIndex = 0; dirIndex < 4; dirIndex++) {
            FowDrawComponent otherComp = NEARBY_FOWS[dirIndex];
            if (otherComp == null) {
                continue;
            }
            byte dirBackIndex = (byte) ((dirIndex + 2) % 4);

            FowDrawComponent backComp = NEARBY_FOWS[dirBackIndex];
            if (backComp == null) {
                backComp = this;
            }

            otherComp.propagateNearbyFows(dirIndex, dirBackIndex,
                    (short) (directionCounts[dirBackIndex] + 1),
                    (directionEdges & (1 << dirBackIndex)) > 0,
                    backComp);
        }
    }

    public void removeNearbyFows(Tile tile) {
        for (byte dirIndex = 0; dirIndex < 4; dirIndex++) {
            FowDrawComponent otherComp = getFowInDirection(tile, dirIndex);
            if (otherComp == null) {
                continue;
            }
            byte dirBackIndex = (byte) ((dirIndex + 2) % 4);
            if (otherComp.entity.getComponent(PositionComponent.class).getTile().chunk == tile.chunk) {
                otherComp.propagateNearbyFows(dirIndex, dirBackIndex, (short) (-directionCounts[dirBackIndex] - 1), false, null);
            } else {
                otherComp.propagateNearbyFows(dirIndex, dirBackIndex, (short) 0, false, null);
            }
        }
    }

    private FowDrawComponent propagateNearbyFows(byte dirIndex, byte dirBackIndex, short dcount, boolean edge, FowDrawComponent source) {
        if (source == null) {
            source = this;
            endFows[dirBackIndex] = this;
        } else {
            endFows[dirBackIndex] = source.endFows[dirBackIndex];
        }
        directionCounts[dirBackIndex] += dcount;
        directionEdges = (byte) ((edge ? (1 << dirBackIndex) : 0) | (((1 << dirBackIndex) ^ Direction.ALL) & directionEdges));

        if (directionCounts[dirIndex] > 0) {
            Tile positionComponentTile = entity.getComponent(PositionComponent.class).getTile();
            FowDrawComponent comp = getFowInDirection(positionComponentTile, dirIndex);
            if (comp != null && comp.entity.getComponent(PositionComponent.class).getTile().chunk == positionComponentTile.chunk) {
                return comp.propagateNearbyFows(dirIndex, dirBackIndex, dcount, edge, source);
            } else {
                return this;
            }
        }
        return this;
    }

    private FowDrawComponent getFowInDirection(Tile tile, byte dirIndex) {
        Tile otherTile = tile.getNearby((byte) (1 << dirIndex));
        if (otherTile != null) {
            for (Entity e : otherTile.entities) {
                FowDrawComponent comp = e.getComponent(FowDrawComponent.class);
                if (comp != null) {
                    return comp;
                }
            }
        }
        return null;
    }


    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);

        entity.events.register(moveHandler);
        moveHandler.apply(entity, null, entity.getComponent(PositionComponent.class).getTile());
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(moveHandler);
        if (currentChunk != null) {
            removeFromChunk();
        }
    }

    protected void addToChunk(Chunk chunk) {
        currentChunk = chunk;
        chunk.addRenderable(this, Renderer.RenderLayer.fow);
    }

    protected void removeFromChunk() {
        currentChunk.removeRenderable(this);
        currentChunk = null;
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(PositionComponent.class);
    }

    @Override
    public void render(GraphicsBatch b, float delta, Renderer.RenderLayer layer) {
        if (!((directionCounts[0] == 0 && directionCounts[2] > 0) ||
                (directionCounts[1] == 0 && directionCounts[3] > 0) ||
                (directionCounts[0] == 0 && directionCounts[1] == 0 && directionCounts[2] == 0 && directionCounts[3] == 0) ||
                (directionCounts[2] == 0 && (directionEdges & (1 << 2)) > 0) ||
                (directionCounts[3] == 0 && (directionEdges & (1 << 3)) > 0)
        )) {
            return;
        }

        // General variables
        Entity playerEntity = PlayerControlComponent.getEntity();
        if (playerEntity == null) {
            return;
        }
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        PositionComponent playerPos = playerEntity.getComponent(PositionComponent.class);
        if (positionComponent == null || positionComponent.isInNull() || playerPos == null || playerPos.isInNull()) {
            return;
        }
        Tile playerTile = playerPos.getTile();
        Tile drawTile = positionComponent.getTile();
        if (drawTile == playerTile) {
            return;
        }

        byte playerDir = Direction.relateDraw(positionComponent, playerPos, Tile.HALF_TILE_SIZE + 0.001f);

        FowBatch batch = (FowBatch) b;

        // Tile centers
        float[] dm = new float[]{drawTile.getDrawX(), drawTile.getDrawY()};
        float[] pm = new float[]{playerPos.getDrawX(), playerPos.getDrawY()};

        //// Cardinal shadows

        // Which axis need cardinal shadows
        final boolean bigShadowX = (dm[0] + directionCounts[1] * Tile.TILE_SIZE + Tile.HALF_TILE_SIZE >= pm[0] &&
                dm[0] - directionCounts[3] * Tile.TILE_SIZE - Tile.HALF_TILE_SIZE <= pm[0]) &&
                (directionCounts[1] == 0 || (directionCounts[2] != 0 && playerDir == 4));
        final boolean bigShadowY = (dm[1] + directionCounts[0] * Tile.TILE_SIZE + Tile.HALF_TILE_SIZE >= pm[1] &&
                dm[1] - directionCounts[2] * Tile.TILE_SIZE - Tile.HALF_TILE_SIZE <= pm[1]) &&
                (directionCounts[0] == 0 || (directionCounts[3] != 0 && playerDir == 8));

        // Cardinal shadows across x
        if (bigShadowX) {
            if (playerDir == 4 && directionCounts[2] > 0) {
                endFows[2].drawCardinalX(batch, pm, true);
            }
            drawCardinalX(batch, pm, false);
        }

        // Cardinal shadows across y
        if (bigShadowY) {
            if (playerDir == 8 && directionCounts[3] > 0) {
                endFows[3].drawCardinalY(batch, pm, true);
            }
            drawCardinalY(batch, pm, false);
        }

        // If we are cardinal, we are certainly not drawing diagonal shadows
        if ((playerDir & (playerDir - 1)) == 0) {
            return;
        }
        //// Diagonal shadows

        // which way is the shadow going
        byte shadowDirection = Direction.relateDraw(playerPos, positionComponent, Tile.SUBTILE_NUM / 2f);
        byte shadowLeftIndex = 0;
        for (byte i = 0; i < 4; i++) {
            if ((shadowDirection & (1 << i)) > 0 && (shadowDirection & (1 << ((i + 1) % 4))) > 0) {
                shadowLeftIndex = i;
                break;
            }
        }

        boolean somethingDrawn = bigShadowX || bigShadowY;
        // Two directions -> two diagonal shadows
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                if (directionCounts[1] > 0 || directionCounts[3] == 0 || bigShadowX) {
                    continue;
                }
                drawDiagonal(batch, pm, new float[]{
                                -Tile.TILE_SIZE * directionCounts[3] - Tile.HALF_TILE_SIZE,
                                Tile.HALF_TILE_SIZE}, DEFAULT_RY,
                        shadowLeftIndex, i);

                somethingDrawn = true;
            } else {
                // Failsafes to ensure at least one shadow is drawn
                if (somethingDrawn && (directionCounts[0] > 0 || directionCounts[2] == 0 || bigShadowY)) {
                    continue;
                }
                drawDiagonal(batch, pm, DEFAULT_RX, new float[]{
                                Tile.TILE_SIZE * directionCounts[0] + Tile.HALF_TILE_SIZE,
                                -Tile.TILE_SIZE * directionCounts[2] - Tile.HALF_TILE_SIZE},
                        shadowLeftIndex, i);
            }
        }
    }

    private void drawCardinalX(FowBatch batch, float[] pm, boolean singleTile) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        float[] dm = new float[]{positionComponent.getDrawX(), positionComponent.getDrawY()};

        // Directions and rotate offset
        int yp = dm[1] > pm[1] ? 1 : 0, yn = yp ^ 1;

        // Rotated corner coordinates
        float[] distX = {(singleTile ? 0 : -Tile.TILE_SIZE * directionCounts[3]) - Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE};
        float[] distY = {Tile.HALF_TILE_SIZE, -Tile.HALF_TILE_SIZE};

        // Vector from player to close edge
        float dpy = dm[1] + distY[yp] - pm[1];
        float dp0 = dm[0] + distX[yp] - pm[0];
        float dp1 = dm[0] + distX[yn] - pm[0];

        float x1 = pm[0] + VIEW_BIG_DISTANCE * dp1;
        float x2 = dm[0] + distX[yn] + dp1 * (distY[yn] - distY[yp]) / dpy;
        float x3 = dm[0] + distX[yn];
        float x4 = dm[0] + distX[yp];
        float x5 = dm[0] + distX[yp] + dp0 * (distY[yn] - distY[yp]) / dpy;
        float x6 = pm[0] + VIEW_BIG_DISTANCE * dp0;
        float y1 = pm[1] + VIEW_BIG_DISTANCE * dpy;
        float y2 = dm[1] + distY[yn];
        float y3 = dm[1] + distY[yp];

        boolean drawLeftTriangle = (directionEdges & (1 << (3 - yn * 2))) == 0 &&
                (!singleTile || directionCounts[3 - yn * 2] == 0);
        boolean drawRightTriangle = (directionEdges & (1 << (3 - yp * 2))) == 0 &&
                (!singleTile || directionCounts[3 - yp * 2] == 0);

        if (drawLeftTriangle && drawRightTriangle) {
            batch.drawCShadowTwo(x1, x3, x4, x6, y1, y2, y3, false);
        } else if (drawLeftTriangle) {
            batch.drawCShadowOne(x1, x3, x5, x6, y1, y2, y3, false);
        } else if (drawRightTriangle) {
            batch.drawCShadowOne(x6, x4, x2, x1, y1, y2, y3, false);
        } else {
            batch.drawCShadowZero(x1, x2, x5, x6, y1, y2, false);
        }
    }

    private void drawCardinalY(FowBatch batch, float[] pm, boolean singleTile) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        float[] dm = new float[]{positionComponent.getDrawX(), positionComponent.getDrawY()};

        // Directions and rotate offset
        int xp = dm[0] > pm[0] ? 1 : 0, xn = xp ^ 1;

        // Corner distances
        float[] distX = {-Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE};
        float[] distY = {Tile.HALF_TILE_SIZE, (singleTile ? 0 : -Tile.TILE_SIZE * directionCounts[2]) - Tile.HALF_TILE_SIZE};

        // Player->Corner distances
        float dpx = dm[0] + distX[xn] - pm[0];
        float dp0 = dm[1] + distY[xn] - pm[1];
        float dp1 = dm[1] + distY[xp] - pm[1];

        float x1 = pm[1] + VIEW_BIG_DISTANCE * dp0;
        float x2 = dm[1] + distY[xn] + dp0 * (distX[xp] - distX[xn]) / dpx;
        float x3 = dm[1] + distY[xn];
        float x4 = dm[1] + distY[xp];
        float x5 = dm[1] + distY[xp] + dp1 * (distX[xp] - distX[xn]) / dpx;
        float x6 = pm[1] + VIEW_BIG_DISTANCE * dp1;
        float y1 = pm[0] + VIEW_BIG_DISTANCE * dpx;
        float y2 = dm[0] + distX[xp];
        float y3 = dm[0] + distX[xn];

        boolean drawLeftTriangle = (directionEdges & (1 << (xn * 2))) == 0 &&
                (!singleTile || directionCounts[xn * 2] == 0);
        boolean drawRightTriangle = (directionEdges & (1 << (xp * 2))) == 0 &&
                (!singleTile || directionCounts[xp * 2] == 0);

        if (drawLeftTriangle && drawRightTriangle) {
            batch.drawCShadowTwo(x1, x3, x4, x6, y1, y2, y3, true);
        } else if (drawLeftTriangle) {
            batch.drawCShadowOne(x1, x3, x5, x6, y1, y2, y3, true);
        } else if (drawRightTriangle) {
            batch.drawCShadowOne(x6, x4, x2, x1, y1, y2, y3, true);
        } else {
            batch.drawCShadowZero(x1, x2, x5, x6, y1, y2, true);
        }
    }

    private void drawDiagonal(FowBatch batch, float[] pm, float[] rx, float[] ry, byte shadowLeftIndex, int i) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        float[] dm = new float[]{positionComponent.getDrawX(), positionComponent.getDrawY()};
        byte shadowRightIndex = (byte) ((shadowLeftIndex + 1) % 4);

        // Offsets from middle to corner
        float[] r0 = new float[]{rx[R_INDEX_X[shadowLeftIndex]], ry[R_INDEX_Y[shadowLeftIndex]]};
        float[] r1 = new float[]{rx[R_INDEX_X[shadowRightIndex]], ry[R_INDEX_Y[shadowRightIndex]]};
        float[] r2 = new float[]{rx[R_INDEX_X[(shadowLeftIndex + 2) % 4]], ry[R_INDEX_Y[(shadowLeftIndex + 2) % 4]]};

        // Redefining x and y helps with mirroring of shadow
        int defy = shadowLeftIndex & 1, defx = defy ^ 1;

        // Distances between players and corners
        float[] dp0 = new float[]{dm[0] + r0[0] - pm[0], dm[1] + r0[1] - pm[1]};
        float[] dp1 = new float[]{dm[0] + r1[0] - pm[0], dm[1] + r1[1] - pm[1]};
        float[] dp2 = new float[]{dm[0] + r2[0] - pm[0], dm[1] + r2[1] - pm[1]};

        // End of the line for deciding shadows
        int otherFowIndex = i == 0 ? (dm[0] > pm[0] ? 1 : 3) : (dm[1] > pm[1] ? 0 : 2);
        FowDrawComponent farComp = endFows[otherFowIndex];
        FowDrawComponent nearComp = endFows[(otherFowIndex + 2) % 4];

        PositionComponent nearCompPositionComponent = nearComp.entity.getComponent(PositionComponent.class);
        float[] dme = new float[]{nearCompPositionComponent.getDrawX(),
                nearCompPositionComponent.getDrawY()};

        // Base shadow
        batch.drawDiag(
                dm[defx] + r2[defx],
                dm[defx] + r0[defx],
                dm[defx] + dp0[defx] * VIEW_BIG_DISTANCE,
                dm[defx] + dp2[defx] * VIEW_BIG_DISTANCE,
                dm[defy] + r0[defy],
                dm[defy] + r1[defy],
                dm[defy] + dp0[defy] * VIEW_BIG_DISTANCE,
                dm[defy] + dp2[defy] * VIEW_BIG_DISTANCE,
                defy == 0);

        // Left shadow
        FowDrawComponent leftEdgeFow = shadowLeftIndex % 2 == i ? nearComp : farComp;
        if ((shadowLeftIndex % 2 == i || (farComp.directionEdges & (1 << shadowLeftIndex)) != 0) &&
                leftEdgeFow.isConnected(shadowLeftIndex) && !leftEdgeFow.isConnected((shadowLeftIndex + 3) % 4)) {
            if (defy == 1 ^ (i == 0)) {
                batch.drawDiagLeftShallow(
                        dm[defx] + r0[defx] +
                                Tile.TILE_SIZE * dp0[defx] / dp0[defy] *
                                        (shadowLeftIndex == 1 || shadowLeftIndex == 2 ? -1 : 1),
                        dme[defy] + ROTATE_1[(shadowLeftIndex + 2) % 4]);
            } else {
                batch.drawDiagLeftDeep(dm[defx] + r1[defx] - dp1[defx] +
                        dp1[defy] * dp0[defx] / dp0[defy]);
            }
        }

        // Right shadow
        FowDrawComponent rightEdgeRow = shadowRightIndex % 2 == i ? nearComp : farComp;
        if ((shadowRightIndex % 2 == i || (farComp.directionEdges & (1 << shadowRightIndex)) != 0) &&
                rightEdgeRow.isConnected(shadowRightIndex) && !rightEdgeRow.isConnected((shadowRightIndex + 1) % 4)) {
            if (defy == 1 ^ (i == 1)) {
                batch.drawDiagRightShallow(
                        dme[defx] + ROTATE_1[shadowRightIndex],
                        dm[defy] + r2[defy] +
                                Tile.TILE_SIZE * dp2[defy] / dp2[defx] *
                                        (shadowLeftIndex == 3 || shadowLeftIndex == 2 ? -1 : 1));
            } else {
                batch.drawDiagRightDeep(dm[defy] + r1[defy] - dp1[defy] +
                        dp1[defx] * dp2[defy] / dp2[defx]);
            }
        }
    }

    private boolean isConnected(int direction) {
        return directionCounts[direction] > 0 || (directionEdges & (1 << direction)) != 0;
    }

    @Override
    public boolean compare(IComparable other) {
        return true;
    }

    @Override
    public FowDrawComponent copy() {
        return (FowDrawComponent) super.copy();
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "directionCounts", directionCounts, "directionEdges", directionEdges, "endFows", endFows);
    }
}
