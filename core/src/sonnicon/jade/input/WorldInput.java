package sonnicon.jade.input;

import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.game.Content;
import sonnicon.jade.world.Tile;

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
            ItemPrinter.printItemDebug(tile);
            return true;
        }
    }
}
