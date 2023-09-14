package sonnicon.jade.game;

import sonnicon.jade.Jade;
import sonnicon.jade.content.CharacterPrinter;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.content.WorldPrinter;
import sonnicon.jade.entity.components.graphical.WallDrawComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.overlays.GridOverlay;
import sonnicon.jade.graphics.particles.ParticleEngine;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;
import sonnicon.jade.world.World;

import java.util.Random;

public class Content {
    public static World world;
    //todo this is temporary

    private static final EventTypes.StateSetEvent stateChangeListener = (Gamestate.State state) -> {
        if (state == Gamestate.State.ingame) {
            create();
            if (Content.world != null) {
                Content.world.chunks.values().forEach(Chunk::updateCulled);
            }
            new GridOverlay();
        }
    };

    public static void init() {
        Gamestate.events.register(stateChangeListener);
    }

    public static void create() {
        Jade.renderer.particles = new ParticleEngine(Jade.renderer);

        world = new World();
        for (int i = 0; i < 4; i++) {
            Chunk c = new Chunk((short) (i / 2), (short) (i % 2), world);
            for (int j = 0; j < 16 * 16; j++) {
                WorldPrinter.printFloorEntity(c.getTile((short) (j / 16), (short) (j % 16)));
            }
        }
        CharacterPrinter.printCharacterPlayer(world.chunks.get(0).getTile((short) 4, (short) 4));

        for (short i = 0; i < 32 * 4; i++) {
            if (i % 5 == 0) continue;
            short c = 0;
            switch (i / 32) {
                case 0:
                    c = 0;
                    break;
                case 1:
                    c = 7;
                    break;
                case 2:
                    c = 15;
                    break;
                case 3:
                    c = 31;
                    break;
            }
            final short r = (short) (i % 32);

            WorldPrinter.printWallEntity(world.getTile(c, r));
        }

        for (short i = 0; i < 32 * 4; i++) {
            if (i % 5 == 0) continue;
            short c = 0;
            switch (i / 32) {
                case 0:
                    c = 0;
                    break;
                case 1:
                    c = 7;
                    break;
                case 2:
                    c = 15;
                    break;
                case 3:
                    c = 31;
                    break;
            }
            final short r = (short) (i % 32);

            Tile t = world.getTile(r, c);
            if (t.entities.stream().anyMatch(e -> e.hasComponent(WallDrawComponent.class))) {
                continue;
            }
            WorldPrinter.printWallEntity(t);
        }

        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            ItemPrinter.printItemDebug(world.getTile((short) random.nextInt(32), (short) random.nextInt(32)));
            ItemPrinter.printWeaponDebug(world.getTile((short) random.nextInt(32), (short) random.nextInt(32)));
            CharacterPrinter.printCharacterEnemy(world.getTile((short) random.nextInt(32), (short) random.nextInt(32)));
        }
    }
}
