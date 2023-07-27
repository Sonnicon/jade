package sonnicon.jade.game;

import sonnicon.jade.Jade;
import sonnicon.jade.content.CharacterPrinter;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.graphics.particles.ParticleEngine;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.World;

public class Content {
    public static World world;
    //todo this is temporary

    public static void init() {
        Gamestate.events.register(Gamestate.State.ingame, (i1, i2) -> create());
    }

    public static void create() {
        Jade.renderer.particles = new ParticleEngine(Jade.renderer);

        world = new World();
        for (int i = 0; i < 16; i++) {
            new Chunk((short) (i / 4), (short) (i % 4), world);
        }
        CharacterPrinter.printCharacterPlayer(world.chunks.get(0).tiles[0]);
        ItemPrinter.printItemDebug(world.chunks.get(1).tiles[155]);
        ItemPrinter.printItemDebug(world.chunks.get(2).tiles[22]);
        ItemPrinter.printItemDebug(world.chunks.get(1).tiles[55]);
        ItemPrinter.printItemDebug(world.chunks.get(0).tiles[55]);
        ItemPrinter.printItemDebug(world.chunks.get(0).tiles[129]);
    }
}
