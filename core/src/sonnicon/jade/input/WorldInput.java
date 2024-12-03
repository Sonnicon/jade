package sonnicon.jade.input;

import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.entity.components.world.SubtilePositionComponent;
import sonnicon.jade.game.Content;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.IUsable;
import sonnicon.jade.game.actions.PlayerCharacterMoveAction;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.ObjectPool;
import sonnicon.jade.util.Point;
import sonnicon.jade.world.Tile;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldInput extends InputInterpreter {

    private static final Vector3 TEMP_VEC = new Vector3();

    private static PlayerCharacterMoveAction playerCharacterMoveAction = null;

    public WorldInput() {
        Input.inputIngame.addProcessor(this);
    }

    @Override
    public boolean tapped(int screenX, int screenY, int pointer, int button) {
        WorldInput.readScreenPosition(TEMP_VEC, screenX, screenY);
        // Debug takes priority
        if (button == com.badlogic.gdx.Input.Buttons.MIDDLE) {
            Tile tile = Content.world.getTile(
                    (int) (TEMP_VEC.x / Tile.SUBTILE_NUM),
                    (int) (TEMP_VEC.y / Tile.SUBTILE_NUM));
            if (tile != null) {
                ((StageIngame) Gamestate.State.ingame.getStage()).panelDebug.show(tile);
            }
            return true;
        }

        // Don't try things if we aren't controlling anything
        if (!PlayerControlComponent.isControlled()) {
            return false;
        }

        Entity user = PlayerControlComponent.getEntity();
        EntityStorageSlot selectedSlot = PlayerControlComponent.getControlled().getSelectedHandSlot();
        if (selectedSlot == null) {
            int mouseSubX = Math.round(TEMP_VEC.x);
            int mouseSubY = Math.round(TEMP_VEC.y);

            Tile tileDestination = Content.world.getTile(
                    mouseSubX / Tile.SUBTILE_NUM,
                    mouseSubY / Tile.SUBTILE_NUM);
            if (tileDestination == null) return false;
            short pointDestination = Point.getPoint(
                    (byte) (mouseSubX % Tile.SUBTILE_NUM),
                    (byte) (mouseSubY % Tile.SUBTILE_NUM));

            SubtilePositionComponent positionComponent = (SubtilePositionComponent) PlayerControlComponent.getEntity()
                    .getComponentFuzzy(PositionComponent.class);

            playerCharacterMoveAction = ObjectPool.obtain(PlayerCharacterMoveAction.class);
            playerCharacterMoveAction.set(positionComponent, tileDestination, pointDestination)
                    .time(1f).enqueue();
        } else if (!selectedSlot.isEmpty()) {
            Entity selectedEntity = selectedSlot.getEntity();
            Stream<? extends IUsable> usableComponents = selectedEntity.findComponentsFuzzy(IUsable.class);
            usableComponents.collect(Collectors.toList()).forEach((IUsable comp) -> {
                if (comp != null && ((Component) comp).entity == selectedEntity) {
                    comp.use(user, (int) TEMP_VEC.x, (int) TEMP_VEC.y);
                }
            });
            return true;
        }
        return false;
    }

    public static Vector3 readScreenPosition(Vector3 out, int x, int y) {
        out.set(x, y, 0f);
        Jade.renderer.camera.unproject(out);
        out.scl((float) Tile.SUBTILE_NUM / Tile.TILE_SIZE);
        return out;
    }

    public static Vector3 readWorldPosition(Vector3 out, float x, float y) {
        return Jade.renderer.camera.project(out.set(x, y, 0f));
    }

    public static Vector3 readWorldPosition(Vector3 out, Tile tile) {
        return readWorldPosition(out, tile.getDrawX(), tile.getDrawY());
    }

    public static Vector3 readWorldPosition(Vector3 out, PositionComponent pos) {
        return readWorldPosition(out, pos.getFloatingX(), pos.getFloatingY());
    }
}
