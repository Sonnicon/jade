package sonnicon.jade.game;

import sonnicon.jade.Jade;
import sonnicon.jade.content.CharacterPrinter;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.particles.ParticleEngine;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.World;

public class Content {
    public static World world;
    //todo this is temporary

    private static final EventTypes.StateSetEvent stateChangeListener = (Gamestate.State state) -> {
        if (state == Gamestate.State.ingame) {
            create();
        }
    };

    public static void init() {
        Gamestate.events.register(stateChangeListener);
    }

    public static void create() {
        Jade.renderer.particles = new ParticleEngine(Jade.renderer);

        world = new World();
        for (int i = 0; i < 16; i++) {
            new Chunk((short) (i / 4), (short) (i % 4), world);
        }
        CharacterPrinter.printCharacterPlayer(world.chunks.get(0).getTile((short) 0, (short) 0));
        ItemPrinter.printItemDebug(world.chunks.get(0).getTile((short) 5, (short) 6));
        ItemPrinter.printItemDebug(world.chunks.get(0).getTile((short) 10, (short) 8));
    }
}
