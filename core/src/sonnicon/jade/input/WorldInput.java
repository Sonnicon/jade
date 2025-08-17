package sonnicon.jade.input;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.IUsable;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.world.Tile;

import java.util.List;
import java.util.stream.Collectors;

public class WorldInput extends InputInterpreter {

    //todo just raze this entire file

    private static final Vector2 TEMP_VEC1 = new Vector2();
    private static final Vector2 TEMP_VEC2 = new Vector2();
    public static final float CAMERA_ZOOM_MIN = 0.25f;
    public static final float CAMERA_ZOOM_MAX = 0.75f;

    //todo remove this

    public WorldInput() {
        Input.inputIngame.addProcessor(this);
    }

    @Override
    public boolean tapped(int screenX, int screenY, int pointer, int button) {
        TEMP_VEC2.set(screenX, screenY);
        Jade.renderer.screenToWorld(TEMP_VEC2, TEMP_VEC1);
        // Debug takes priority
        if (button == com.badlogic.gdx.Input.Buttons.MIDDLE) {
            Tile tile = Content.world.getTile(TEMP_VEC1.x, TEMP_VEC1.y);
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

        } else if (!selectedSlot.isEmpty()) {
            Entity selectedEntity = selectedSlot.getEntity();
            List<IUsable> usables = selectedEntity.findComponentsFuzzy(IUsable.class)
                    .filter((IUsable comp) -> comp != null && ((Component) comp).entity == selectedEntity)
                    .collect(Collectors.toList());
            for (IUsable usable : usables) {
                if (usable.use(user, (int) TEMP_VEC1.x, (int) TEMP_VEC1.y)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Jade.renderer.viewportScale = MathUtils.clamp(
                Jade.renderer.viewportScale + amountY / 100f, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
        Jade.renderer.updateCamera();
        return true;
    }
}
