package sonnicon.jade.input;

import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.IUsable;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.game.actions.MoveAction;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.DebugTarget;
import sonnicon.jade.world.Tile;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldInput extends InputInterpreter {

    //todo just raze this entire file

    private static final Vector3 TEMP_VEC1 = new Vector3();

    public WorldInput() {
        Input.inputIngame.addProcessor(this);
    }

    @Override
    public boolean tapped(int screenX, int screenY, int pointer, int button) {
        WorldInput.readScreenPosition(TEMP_VEC1, screenX, screenY);
        // Debug takes priority
        if (button == com.badlogic.gdx.Input.Buttons.MIDDLE) {
            Tile tile = Content.world.getTile((int) (TEMP_VEC1.x), (int) (TEMP_VEC1.y));
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

            TEMP_VEC1.scl(Tile.TILE_SIZE);
            DebugTarget destTarget = new DebugTarget();
            destTarget.forceMoveTo(TEMP_VEC1.x, TEMP_VEC1.y);
            Actions.actionsList.stream().filter(a -> a instanceof MoveAction).forEach(Actions.Action::interrupt);
            MoveAction.createChain(user, destTarget, 1f / Tile.TILE_SIZE).start();
            Content.targetEntity.forceMoveTo(destTarget);

        } else if (!selectedSlot.isEmpty()) {
            Entity selectedEntity = selectedSlot.getEntity();
            Stream<? extends IUsable> usableComponents = selectedEntity.findComponentsFuzzy(IUsable.class);
            usableComponents.collect(Collectors.toList()).forEach((IUsable comp) -> {
                if (comp != null && ((Component) comp).entity == selectedEntity) {
                    comp.use(user, (int) TEMP_VEC1.x, (int) TEMP_VEC1.y);
                }
            });
            return true;
        }
        return false;
    }

    public static Vector3 readScreenPosition(Vector3 out, int x, int y) {
        out.set(x, y, 0f);
        Jade.renderer.camera.unproject(out);
        out.scl(1f / Tile.TILE_SIZE);
        return out;
    }

    public static Vector3 readWorldPosition(Vector3 out, float x, float y) {
        return Jade.renderer.camera.project(out.set(x, y, 0f));
    }

    public static Vector3 readWorldPosition(Vector3 out, IPosition tile) {
        return readWorldPosition(out, tile.getX(), tile.getY());
    }
}
