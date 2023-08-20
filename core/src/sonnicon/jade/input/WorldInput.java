package sonnicon.jade.input;

import sonnicon.jade.content.WorldPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.entity.components.graphical.WallDrawComponent;
import sonnicon.jade.game.Content;
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
        } else {
            Optional<Entity> e = tile.entities.stream().filter(f -> f.hasComponent(WallDrawComponent.class)).findAny();
            if (e.isPresent()) {
                e.get().getComponent(PositionComponent.class).moveToTile(null);
            } else {
                WorldPrinter.printWallEntity(tile);
            }
            //ItemPrinter.printItemDebug(tile);
            return true;
        }
    }
}
