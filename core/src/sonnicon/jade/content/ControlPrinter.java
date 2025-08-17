package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.graphical.ChunkDrawComponent;
import sonnicon.jade.entity.components.graphical.RotatingComponent;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.world.Tile;

public class ControlPrinter {

    public static Entity targetEntity() {
        return new Entity(
                new ChunkDrawComponent(new TextureSet("target"), Tile.TILE_SIZE / 2f, RenderLayer.overfow),
                new RotatingComponent(90f));
    }
}
