package sonnicon.jade.game.actions;

import sonnicon.jade.entity.components.graphical.AnimationComponent;
import sonnicon.jade.entity.components.world.SubtilePositionComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.animation.Animation;
import sonnicon.jade.graphics.animation.TranslateAnimation;
import sonnicon.jade.util.Point;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;
import sonnicon.jade.world.pathfinder.LocalPathfinder;
import sonnicon.jade.world.pathfinder.TilePathfinder;

import java.util.ArrayList;
import java.util.Map;

public class CharacterMoveAction extends Actions.Action {
    protected SubtilePositionComponent target;
    protected ArrayList<Short> pointQueue = new ArrayList<>();
    protected ArrayList<Tile> tileQueue = new ArrayList<>();
    protected TranslateAnimation animation;

    protected short destPoint;

    static TilePathfinder tilePathfinder = new TilePathfinder();
    static LocalPathfinder localPathfinder = new LocalPathfinder();

    public CharacterMoveAction set(SubtilePositionComponent target, Tile dest, short destPoint) {
        this.target = target;
        this.destPoint = destPoint;
        keepRef();
        pointQueue.clear();

        ArrayList<Tile> longTileQueue = tilePathfinder.findPath(target.getTile(), dest);

        if (longTileQueue != null) {
            tileQueue.clear();
            for (int i = 0; i < longTileQueue.size(); i += 3) {
                tileQueue.add(longTileQueue.get(i));
            }

            Tile lastTile = tileQueue.get(tileQueue.size() - 1);
            if (lastTile != dest) {
                tileQueue.add(dest);
            }
        }
        return this;
    }


    @Override
    public void onStart() {
        // Refill pont queue from tile queue
        if ((tileQueue == null || tileQueue.isEmpty()) && (pointQueue == null || pointQueue.isEmpty())) {
            interrupt();
            return;
        }

        if (pointQueue.isEmpty()) {
            short originPoint = Point.getPoint(target.getSubX(), target.getSubY());
            Tile dt = tileQueue.remove(0);
            if (!tileQueue.isEmpty()) {
                pointQueue = localPathfinder.findPath(target.getTile(), originPoint, dt, target.entity);
            } else {
                pointQueue = localPathfinder.findPath(target.getTile(), originPoint, dt, destPoint, target.entity, true);
            }

            if (pointQueue == null) {
                interrupt();
                return;
            }
            Point.pointListToDeltas(pointQueue);
        }

        short movePoint = pointQueue.get(0);
        byte moveX = Point.getXFromPoint(movePoint);
        byte moveY = Point.getYFromPoint(movePoint);

        if (!target.canMoveByPos(moveX, moveY)) {
            interrupt();
            return;
        }

        AnimationComponent ac = target.entity.getComponent(AnimationComponent.class);
        if (ac != null) {
            animation = Animation.obtain(TranslateAnimation.class);
            float duration = timeFinish - Clock.getTickNum();
            animation.init(0, 0, moveX * Tile.SUBTILE_DELTA, moveY * Tile.SUBTILE_DELTA, duration);
            ac.play(animation);
        }
    }

    @Override
    public void onFinish() {
        if (animation != null) {
            animation.stop();
        }

        short movePoint = pointQueue.remove(0);
        byte moveX = Point.getXFromPoint(movePoint);
        byte moveY = Point.getYFromPoint(movePoint);
        if (!target.tryMoveBy(moveX, moveY)) {
            System.out.println("Cant Move 2");
        } else if (!pointQueue.isEmpty() || !tileQueue.isEmpty()) {
            enqueue();
        }
    }


    @Override
    public void interrupt() {
        super.interrupt();
        //todo keepRef tracking properly
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(),
                "target", target,
                "destinationPointX", Point.getXFromPoint(destPoint),
                "destinationPointY", Point.getYFromPoint(destPoint),
                "tileQueue", tileQueue, "pointQueue", pointQueue,
                "animation", animation);
    }
}