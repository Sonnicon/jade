package sonnicon.jade.input;

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
    public WorldInput() {
        Input.inputIngame.addProcessor(this);
    }

    @Override
    public boolean tapped(int screenX, int screenY, int pointer, int button) {
        // debug box spawner
        Tile tile = Content.world.getScreenPositionTile(screenX, screenY);

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
}
