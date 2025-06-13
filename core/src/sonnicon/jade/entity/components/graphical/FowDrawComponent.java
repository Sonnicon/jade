package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.draw.FowBatch;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.Directions;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

import java.util.Map;

//todo stop using == for float comparison
//todo optimise the conditions
//todo adapt to unified direction system
public class FowDrawComponent extends Component implements IRenderable {
    // Unique component data
    private Chunk currentChunk;
    // Number of connected FowDrawComponents in each direction
    private final short[] directionCounts = {0, 0, 0, 0};
    // Direction bits in which the edge of a chunk is reached, and is obscured in the next chunk
    private byte directionEdges = 0;
    // FowDrawComponents at end of row in each direction
    private final FowDrawComponent[] endFows = new FowDrawComponent[]{this, this, this, this};
    // This gets used for wall ordering, won't re-calculate the same thing
    public boolean bigShadowX, bigShadowY;

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
                        comp.removeFromChunk(comp.currentChunk);
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

                ((CachedDrawBatch) RenderLayer.fow.batch).invalidate();
            };

    public FowDrawComponent() {

    }

    public void addNearbyFows() {
        directionEdges = 0;
        Tile entityTile = entity.getTile();
        for (byte dirIndex = 0; dirIndex < 4; dirIndex++) {
            FowDrawComponent comp = getFowInDirection(entityTile, dirIndex);
            if (comp == null) {
                NEARBY_FOWS[dirIndex] = null;
            } else if (comp.entity.getTile().chunk != entityTile.chunk) {
                NEARBY_FOWS[dirIndex] = null;
                directionCounts[dirIndex] = 0;
                directionEdges |= (byte) (1 << dirIndex);
                comp.propagateNearbyFows(dirIndex, (byte) ((dirIndex + 2) % 4), (short) 0, true, null);
            } else {
                directionCounts[dirIndex] = (short) (comp.directionCounts[dirIndex] + 1);
                directionEdges |= (byte) (comp.directionEdges & (1 << dirIndex));
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
            if (otherComp.entity.getTile().chunk == tile.chunk) {
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
        directionEdges = (byte) ((edge ? (1 << dirBackIndex) : 0) | (((1 << dirBackIndex) ^ 0b1111) & directionEdges));

        if (directionCounts[dirIndex] > 0) {
            Tile positionComponentTile = entity.getTile();
            FowDrawComponent comp = getFowInDirection(positionComponentTile, dirIndex);
            if (comp != null && comp.entity.getTile().chunk == positionComponentTile.chunk) {
                return comp.propagateNearbyFows(dirIndex, dirBackIndex, dcount, edge, source);
            } else {
                return this;
            }
        }
        return this;
    }

    private FowDrawComponent getFowInDirection(Tile tile, byte dirIndex) {
        Tile otherTile = tile.getNearby((byte) (1 << (dirIndex * 2)));
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
        moveHandler.apply(entity, null, entity.getTile());
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        moveHandler.apply(entity, entity.getTile(), null);
        entity.events.unregister(moveHandler);
    }

    protected void addToChunk(Chunk chunk) {
        currentChunk = chunk;
        chunk.addRenderable(this, RenderLayer.fow);
    }

    protected void removeFromChunk(Chunk chunk) {
        chunk.removeRenderable(this);
        currentChunk = null;
    }

    @Override
    public void render(GraphicsBatch b, float delta, RenderLayer layer) {
        //todo
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
        if (entity == null || playerEntity == null) {
            return;
        }
        Tile playerTile = playerEntity.getTile();
        Tile drawTile = entity.getTile();
        if (drawTile == playerTile) {
            return;
        }

        byte playerDir = Directions.toCompact(Directions.relate(
                entity,
                playerEntity,
                Tile.HALF_TILE_SIZE + 0.001f));

        FowBatch batch = (FowBatch) b;

        // Tile centers
        float[] dm = new float[]{drawTile.getX(), drawTile.getY()};
        float[] pm = new float[]{playerEntity.getX(), playerEntity.getY()};

        //// Cardinal shadows

        // Which axis need cardinal shadows
        bigShadowX = (dm[0] + directionCounts[1] * Tile.TILE_SIZE + Tile.HALF_TILE_SIZE >= pm[0] &&
                dm[0] - directionCounts[3] * Tile.TILE_SIZE - Tile.HALF_TILE_SIZE <= pm[0]) &&
                (directionCounts[1] == 0 || (directionCounts[2] != 0 && playerDir == 4));
        bigShadowY = (dm[1] + directionCounts[0] * Tile.TILE_SIZE + Tile.HALF_TILE_SIZE >= pm[1] &&
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
        byte shadowDirection = Directions.toCompact(Directions.relate(
                playerEntity,
                entity,
                Tile.HALF_TILE_SIZE));
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
        float camX = Jade.renderer.camera.position.x;
        float camY = Jade.renderer.camera.position.y;

        float[] dm = new float[]{entity.getX(), entity.getY()};

        // Directions and rotate offset
        int yp = dm[1] > pm[1] ? 1 : 0, yn = yp ^ 1;

        // Rotated corner coordinates
        float[] distX = {
                (singleTile ? 0 : -Tile.TILE_SIZE * directionCounts[3]) - Tile.HALF_TILE_SIZE,
                Tile.HALF_TILE_SIZE
        };
        float[] distY = {Tile.HALF_TILE_SIZE, -Tile.HALF_TILE_SIZE};

        // Vector from player to close edge
        float dpy = dm[1] + distY[yp] - pm[1];
        float dp0 = dm[0] + distX[yp] - pm[0];
        float dp1 = dm[0] + distX[yn] - pm[0];

        float x1 = project(pm[0] + VIEW_BIG_DISTANCE * dp1, camX);
        float x2 = project(dm[0] + distX[yn] + dp1 * (distY[yn] - distY[yp]) / dpy, camX);
        float x3 = project(dm[0] + distX[yn], camX);
        float x4 = project(dm[0] + distX[yp], camX);
        float x5 = project(dm[0] + distX[yp] + dp0 * (distY[yn] - distY[yp]) / dpy, camX);
        float x6 = project(pm[0] + VIEW_BIG_DISTANCE * dp0, camX);
        float y1 = project(pm[1] + VIEW_BIG_DISTANCE * dpy, camY);
        float y2 = project(dm[1] + distY[yn], camY);
        float y3 = project(dm[1] + distY[yp], camY);

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
        float camX = Jade.renderer.camera.position.x;
        float camY = Jade.renderer.camera.position.y;

        float[] dm = new float[]{entity.getX(), entity.getY()};

        // Directions and rotate offset
        int xp = dm[0] > pm[0] ? 1 : 0, xn = xp ^ 1;

        // Corner distances
        float[] distX = {-Tile.HALF_TILE_SIZE, Tile.HALF_TILE_SIZE};
        float[] distY = {Tile.HALF_TILE_SIZE, (singleTile ? 0 : -Tile.TILE_SIZE * directionCounts[2]) - Tile.HALF_TILE_SIZE};

        // Player->Corner distances
        float dpx = dm[0] + distX[xn] - pm[0];
        float dp0 = dm[1] + distY[xn] - pm[1];
        float dp1 = dm[1] + distY[xp] - pm[1];

        float x1 = project(pm[1] + VIEW_BIG_DISTANCE * dp0, camY);
        float x2 = project(dm[1] + distY[xn] + dp0 * (distX[xp] - distX[xn]) / dpx, camY);
        float x3 = project(dm[1] + distY[xn], camY);
        float x4 = project(dm[1] + distY[xp], camY);
        float x5 = project(dm[1] + distY[xp] + dp1 * (distX[xp] - distX[xn]) / dpx, camY);
        float x6 = project(pm[1] + VIEW_BIG_DISTANCE * dp1, camY);
        float y1 = project(pm[0] + VIEW_BIG_DISTANCE * dpx, camX);
        float y2 = project(dm[0] + distX[xp], camX);
        float y3 = project(dm[0] + distX[xn], camX);

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
        float[] dm = new float[]{entity.getX(), entity.getY()};
        byte shadowRightIndex = (byte) ((shadowLeftIndex + 1) % 4);

        // Offsets from middle to corner
        float[] r0 = new float[]{rx[R_INDEX_X[shadowLeftIndex]], ry[R_INDEX_Y[shadowLeftIndex]]};
        float[] r1 = new float[]{rx[R_INDEX_X[shadowRightIndex]], ry[R_INDEX_Y[shadowRightIndex]]};
        float[] r2 = new float[]{rx[R_INDEX_X[(shadowLeftIndex + 2) % 4]], ry[R_INDEX_Y[(shadowLeftIndex + 2) % 4]]};

        // Redefining x and y helps with mirroring of shadow
        int defy = shadowLeftIndex & 1, defx = defy ^ 1;

        float[] cam = {
                Jade.renderer.camera.position.x,
                Jade.renderer.camera.position.y
        };

        // Distances between players and corners
        float[] dp0 = new float[]{dm[0] + r0[0] - pm[0], dm[1] + r0[1] - pm[1]};
        float[] dp1 = new float[]{dm[0] + r1[0] - pm[0], dm[1] + r1[1] - pm[1]};
        float[] dp2 = new float[]{dm[0] + r2[0] - pm[0], dm[1] + r2[1] - pm[1]};

        // End of the line for deciding shadows
        int otherFowIndex = i == 0 ? (dm[0] > pm[0] ? 1 : 3) : (dm[1] > pm[1] ? 0 : 2);
        FowDrawComponent farComp = endFows[otherFowIndex];
        FowDrawComponent nearComp = endFows[(otherFowIndex + 2) % 4];

        float[] dme = new float[]{nearComp.entity.getX(), nearComp.entity.getY()};

        // Base shadow
        batch.drawDiag(
                project(dm[defx] + r2[defx], cam[defx]),
                project(dm[defx] + r0[defx], cam[defx]),
                project(dm[defx] + dp0[defx] * VIEW_BIG_DISTANCE, cam[defx]),
                project(dm[defx] + dp2[defx] * VIEW_BIG_DISTANCE, cam[defx]),
                project(dm[defy] + r0[defy], cam[defy]),
                project(dm[defy] + r1[defy], cam[defy]),
                project(dm[defy] + dp0[defy] * VIEW_BIG_DISTANCE, cam[defy]),
                project(dm[defy] + dp2[defy] * VIEW_BIG_DISTANCE, cam[defy]),
                defy == 0);

        // Left shadow
        FowDrawComponent leftEdgeFow = shadowLeftIndex % 2 == i ? nearComp : farComp;
        if ((shadowLeftIndex % 2 == i || (farComp.directionEdges & (1 << shadowLeftIndex)) != 0) &&
                leftEdgeFow.isConnected(shadowLeftIndex) && !leftEdgeFow.isConnected((shadowLeftIndex + 3) % 4)) {
            if (defy == 1 ^ (i == 0)) {
                batch.drawDiagLeftShallow(
                        project(dm[defx] + r0[defx] +
                                Tile.TILE_SIZE * dp0[defx] / dp0[defy] *
                                        (shadowLeftIndex == 1 || shadowLeftIndex == 2 ? -1 : 1), cam[defx]),
                        project(dme[defy] + ROTATE_1[(shadowLeftIndex + 2) % 4], cam[defy]));
            } else {
                batch.drawDiagLeftDeep(project(dm[defx] + r1[defx] - dp1[defx] +
                        dp1[defy] * dp0[defx] / dp0[defy], cam[defx]));
            }
        }

        // Right shadow
        FowDrawComponent rightEdgeRow = shadowRightIndex % 2 == i ? nearComp : farComp;
        if ((shadowRightIndex % 2 == i || (farComp.directionEdges & (1 << shadowRightIndex)) != 0) &&
                rightEdgeRow.isConnected(shadowRightIndex) && !rightEdgeRow.isConnected((shadowRightIndex + 1) % 4)) {
            if (defy == 1 ^ (i == 1)) {
                batch.drawDiagRightShallow(
                        project(dme[defx] + ROTATE_1[shadowRightIndex], cam[defx]),
                        project(dm[defy] + r2[defy] +
                                Tile.TILE_SIZE * dp2[defy] / dp2[defx] *
                                        (shadowLeftIndex == 3 || shadowLeftIndex == 2 ? -1 : 1), cam[defy]));
            } else {
                batch.drawDiagRightDeep(dm[defy] + r1[defy] - dp1[defy] +
                        dp1[defx] * dp2[defy] / dp2[defx]);
            }
        }
    }

    private boolean isConnected(int direction) {
        return directionCounts[direction] > 0 || (directionEdges & (1 << direction)) != 0;
    }

    private static float project(float v, float fromV) {
        final float distance = 1.2f;
        return (v - fromV) * distance + fromV;
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
