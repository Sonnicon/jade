package sonnicon.jade.game.components;

import sonnicon.jade.world.Tile;

public class PositionComponent extends Component {
    public Tile tile;

    public PositionComponent(Tile tile) {
        this.tile = tile;
    }

    @Override
    public Component copy() {
        return new PositionComponent(tile);
    }
}
