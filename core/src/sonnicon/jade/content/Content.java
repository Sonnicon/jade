package sonnicon.jade.content;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.graphical.WallDrawComponent;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.overlays.GridOverlay;
import sonnicon.jade.graphics.overlays.ViewOverlay;
import sonnicon.jade.graphics.particles.ParticleEngine;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;
import sonnicon.jade.world.World;

//todo this whole file is temporary
public class Content {
    public static World world;
    public static ViewOverlay viewOverlay;
    public static Entity targetEntity;
    //todo this is temporary
    public static Entity swordEntity;

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
        viewOverlay = new ViewOverlay(240);
        targetEntity = ControlPrinter.targetEntity();

        world = new World();
        for (int i = 0; i < 4; i++) {
            Chunk c = new Chunk((short) (i / 2), (short) (i % 2), world);
            for (int j = 0; j < 16 * 16; j++) {
                WorldPrinter.printFloorEntity(c.getTile((short) (j / 16), (short) (j % 16)));
            }
        }

        CharacterPrinter.printCharacterPlayer(world.chunks.get(0).getTile((short) 4, (short) 4));
        for (int i = 0; i < 1; i++) {
            swordEntity = ItemPrinter.printWeaponDebug(world.chunks.get(0).getTile((short) 2, (short) 2));
        }

        WorldPrinter.printRedboxEntity(world.chunks.get(0).getTile((short) 3, (short) 3));


        for (short i = 0; i < 32 * 4; i++) {
            if (i % 5 == 0) continue;
            short c = 0;
            switch (i / 32) {
                case 0:
                    c = 0;
                    break;
                case 1:
                    c = 5;
                    break;
                case 2:
                    c = 9;
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

//        for (int i = 0; i < 6; i++) {
//            ItemPrinter.printItemDebug(world.getTile(random.nextInt(32), random.nextInt(32)));
//            ItemPrinter.printWeaponDebug(world.getTile(random.nextInt(32), random.nextInt(32)));
//            CharacterPrinter.printCharacterEnemy(world.getTile(random.nextInt(32), random.nextInt(32)));
//        }
    }
}
