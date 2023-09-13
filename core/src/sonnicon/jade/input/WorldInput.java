package sonnicon.jade.input;

import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.content.WorldPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.entity.components.graphical.WallDrawComponent;
import sonnicon.jade.game.Content;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.world.Tile;

import java.util.Optional;

public class WorldInput extends InputInterpreter {

    private static final Vector3 TEMP_VEC = new Vector3();

    public WorldInput() {
        Input.inputIngame.addProcessor(this);
    }

    @Override
    public boolean tapped(int screenX, int screenY, int pointer, int button) {
        // debug box spawner
        WorldInput.readScreenPosition(TEMP_VEC, screenX, screenY);
        Tile tile = Content.world.getTile((short) (TEMP_VEC.x / Tile.SUBTILE_NUM), (short) (TEMP_VEC.y / Tile.SUBTILE_NUM));

        if (tile == null) {
            return false;
        } else if (button == com.badlogic.gdx.Input.Buttons.MIDDLE) {
            ((StageIngame) Gamestate.State.ingame.getStage()).panelDebug.show(tile);
        } else {
            Optional<Entity> e = tile.entities.stream().filter(f -> f.hasComponent(WallDrawComponent.class)).findAny();
            if (e.isPresent()) {
                e.get().getComponent(PositionComponent.class).moveTo(null);
            } else {
                WorldPrinter.printWallEntity(tile);
            }
            //ItemPrinter.printItemDebug(tile);
        }
        return true;
    }

    public static Vector3 readScreenPosition(Vector3 out, int x, int y) {
        out.set(x, y, 0f);
        Jade.renderer.camera.unproject(out);
        out.scl((float) Tile.SUBTILE_NUM / Tile.TILE_SIZE);
        return out;
    }

    public static Vector3 readWorldPosition(Vector3 out, Tile tile, short subx, short suby) {
        return Jade.renderer.camera.project(
                out.set(tile.getDrawX() + Tile.SUBTILE_DELTA * subx,
                        tile.getDrawY() + Tile.SUBTILE_DELTA * suby,
                        0f));
    }

    public static Vector3 readWorldPosition(Vector3 out, Tile tile) {
        return readWorldPosition(out, tile, (short) (Tile.SUBTILE_NUM / 2), (short) (Tile.SUBTILE_NUM / 2));

    }
}
