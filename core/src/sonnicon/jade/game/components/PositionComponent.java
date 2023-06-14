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

    @Override
    public boolean compare(Component other) {
        // Checking if two entities are in the same place isn't really the goal of this function
        return true;
    }
}
