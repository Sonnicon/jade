package sonnicon.jade.game;

import com.badlogic.gdx.Game;
import sonnicon.jade.content.CharacterPrinter;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.World;

public class Content {
    //todo this is temporary

    public static void init() {
        Gamestate.events.register(Gamestate.State.ingame, ignored -> create());
    }

    public static void create() {

        World w = new World();
        for (int i = 0; i < 16; i++) {
            new Chunk((short) (i / 4), (short) (i % 4), w);
        }
        CharacterPrinter.printCharacterPlayer(w.chunks.get(0).tiles[0]);
        ItemPrinter.printItemDebug(w.chunks.get(1).tiles[155]);
        ItemPrinter.printItemDebug(w.chunks.get(2).tiles[22]);
        ItemPrinter.printItemDebug(w.chunks.get(1).tiles[55]);
        ItemPrinter.printItemDebug(w.chunks.get(0).tiles[55]);
        ItemPrinter.printItemDebug(w.chunks.get(0).tiles[129]);
    }
}
