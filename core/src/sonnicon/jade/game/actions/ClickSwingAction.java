package sonnicon.jade.game.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.world.PositionRelativeComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.RigidLineInterpolator;
import sonnicon.jade.util.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ClickSwingAction extends Actions.Action implements IRenderable {
    public Entity weapon;
    public float weaponLength;
    public final Vector2 swingTarget = new Vector2();
    public float reachRange;
    public float speedMoveScale;
    public float speedRotateScale;
    protected RigidLineInterpolator interpolator = new RigidLineInterpolator();

    private static final Vector2 TEMP_VECTOR = new Vector2();

    public ClickSwingAction set(Entity weapon, float weaponLength, float targetX, float targetY) {
        this.weapon = weapon;
        this.weaponLength = weaponLength;
        this.swingTarget.set(targetX, targetY);

        reachRange = 96f;
        speedMoveScale = 96f;
        speedRotateScale = 180f;
        return this;
    }


    @Override
    public void onStart() {
        Jade.renderer.addRenderable(this, RenderLayer.overfow);
        precalculateTrajectory(weapon.getX(), weapon.getY(), weapon.getRotation());
    }

    protected void precalculateTrajectory(float fromX, float fromY, float fromRotation) {
        //todo swinger by parameter
        Entity entityOrigin = PlayerControlComponent.getEntity();
        entityOrigin.getPosition(TEMP_VECTOR);

        // Clamp swing distance
        swingTarget.sub(TEMP_VECTOR);
        swingTarget.clamp(weaponLength, reachRange);
        swingTarget.add(TEMP_VECTOR);
        // Prepare interpolator
        interpolator.set(TEMP_VECTOR.x, TEMP_VECTOR.y, fromX, fromY, fromRotation, weaponLength, swingTarget.x, swingTarget.y);

        // Duration for moving
        float timeMove = interpolator.getLength() / speedMoveScale;
        // Duration for rotating
        float distanceFromOrigin = TEMP_VECTOR.scl(-1f).add(swingTarget).len();
        float rotationRate = Interpolation.pow2Out.apply(MathUtils.clamp(1f - distanceFromOrigin / 96f, 0f, 1f)) * 0.4f + 0.6f;
        float rotationAngle = Utils.lengthDeg(interpolator.startRotation, interpolator.endRotation);
        float timeRotate = Math.abs(rotationAngle / (speedRotateScale * rotationRate));
        // Low rotation makes moving faster (for thrusts)
        float thrustDiscount = MathUtils.clamp((5f - rotationAngle) / 5f, 0f, 0.2f);
        // Set duration
        float duration = Math.max(0.01f, Math.max(timeMove - thrustDiscount, timeRotate));
        setDuration(duration);
    }

    protected float curveRotateProgress(float progress) {
        if (Math.abs(interpolator.getRotationLength()) < 45f || interpolator.getLength() < 48f) {
            return Interpolation.pow2In.apply(progress);
        } else {
            return Interpolation.swingIn.apply(progress);
        }
    }

    protected float curveMoveProgress(float progress) {
        return Interpolation.pow4In.apply(progress);
    }

    @Override
    public void onFinish() {
        PositionRelativeComponent relativeComponent = weapon.getComponent(PositionRelativeComponent.class);
        if (relativeComponent == null) return;
        weapon.moveBy(interpolator.interpolateX(1f) - relativeComponent.getOffsetX(),
                interpolator.interpolateY(1f) - relativeComponent.getOffsetY());
        weapon.rotateTo(interpolator.endRotation);
        Jade.renderer.removeRenderable(this);
    }

    @Override
    protected void onInterrupt() {
        Jade.renderer.removeRenderable(this);
    }


    @Override
    protected void onFrame() {
        PositionRelativeComponent relativeComponent = weapon.getComponent(PositionRelativeComponent.class);
        if (relativeComponent == null) return;

        float progress = getProgress();
        float curveMove = curveMoveProgress(progress);
        float curveRotate = curveRotateProgress(progress);

        weapon.forceMoveBy(interpolator.interpolateX(curveMove) - relativeComponent.getOffsetX(),
                interpolator.interpolateY(curveMove) - relativeComponent.getOffsetY());
        weapon.rotateTo(interpolator.interpolateRotation(curveRotate));
    }

    @Override
    protected void onAlign() {
        PositionRelativeComponent relativeComponent = weapon.getComponent(PositionRelativeComponent.class);
        if (relativeComponent == null) return;

        float progress = getProgress();
        float curveMove = curveMoveProgress(progress);
        float curveRotate = curveRotateProgress(progress);

        //todo simultaneous movement
        //todo (i know it's not quite right as is)
        weapon.moveBy(interpolator.interpolateX(curveMove) - relativeComponent.getOffsetX(),
                interpolator.interpolateY(curveMove) - relativeComponent.getOffsetY());
        weapon.rotateTo(interpolator.interpolateRotation(curveRotate));
    }

    @Override
    protected void onTick() {
    }


    //todo remove most of this render stuff
    public static ShapeDrawer shapeDrawer;

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        if (shapeDrawer == null) {
            shapeDrawer = new ShapeDrawer((Batch) layer.batch, Textures.atlasFindRegion("blank"));
        }

        PositionRelativeComponent relativeComponent = weapon.getComponent(PositionRelativeComponent.class);
        if (relativeComponent == null) return;

        float relx = relativeComponent.getBinding().getX();
        float rely = relativeComponent.getBinding().getY();

        drawInterpolation(relx, rely, 1f, true);

        ClickSwingAction nextAction = (ClickSwingAction) then.stream()
                .filter(a -> a instanceof ClickSwingAction)
                .findAny().orElse(null);
        if (nextAction != null) {
            Vector2 tip = new Vector2();
            interpolator.interpolateTipPosition(1f, tip);
            nextAction.precalculateTrajectory(
                    interpolator.endMidX + relx,
                    interpolator.endMidY + rely,
                    interpolator.endRotation);
            nextAction.drawInterpolation(relx, rely, 0.5f, false);
        }


        shapeDrawer.circle(relx, rely, reachRange);


        float chartProgress = (Clock.getTickNum() - timeStart) / duration;
        // move
        drawDebugChart(Interpolation.pow4, 0, chartProgress, Color.ORANGE);
        drawDebugChart(Math.abs(interpolator.getRotationLength()) < 45f || interpolator.getLength() < 48f ?
                        Interpolation.pow2In : Interpolation.swingIn,
                1, chartProgress, Color.PURPLE);
    }

    protected void drawInterpolation(float relx, float rely, float dotRadius, boolean trail) {
        for (float progress = 0f; progress < 1f; progress += duration / 500f) {
            Vector2 direction = new Vector2(1f, 0f);
            float smoothRotate;
            if (Math.abs(interpolator.getRotationLength()) < 45f || interpolator.getLength() < 48f) {
                smoothRotate = Interpolation.pow2In.apply(progress);
            } else {
                smoothRotate = Interpolation.swingIn.apply(progress);
            }
            direction.setAngleDeg(90f - interpolator.interpolateRotation(smoothRotate));
            direction.setLength(24f);
            float smoothMove = Interpolation.pow4In.apply(progress);
            float posx = interpolator.interpolateX(smoothMove) + relx;
            float posy = interpolator.interpolateY(smoothMove) + rely;
            Vector2 direction2 = new Vector2(direction);
            direction.add(posx, posy);
            shapeDrawer.filledCircle(direction, dotRadius, Color.GREEN);

            if (trail && progress < (Clock.getTickNum() - timeStart) / duration) {
                Color color = Color.valueOf("00AAAA10");
                direction2.scl(-1f);
                direction2.add(posx, posy);
                shapeDrawer.line(direction, direction2, color, 2f);
            }
        }
    }

    protected void drawDebugChart(Interpolation interp, int offset, float progress, Color color) {
        final float posX = 32f;
        final float posY = 32f;
        final float size = 96f;
        final float resolution = 0.0001f;

        float lastX = 0f;
        for (float x = resolution; x < 1f; x += resolution) {
            shapeDrawer.line(
                    posX + size * lastX,
                    posY + size * interp.apply(lastX) + size * offset,
                    posX + size * x,
                    posX + size * interp.apply(x) + size * offset,
                    2f, color, color);
            lastX = x;
        }
        shapeDrawer.rectangle(posX, posY + size * offset, size, size);
        shapeDrawer.line(posX + size * progress, posY + size * offset,
                posX + size * progress, posY + size * (offset + 1),
                Color.GREEN, 1f);
    }
}