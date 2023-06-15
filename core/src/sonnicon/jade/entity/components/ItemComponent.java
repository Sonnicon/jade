package sonnicon.jade.entity.components;

import sonnicon.jade.game.EntitySize;

public class ItemComponent extends Component {
    public EntitySize size = EntitySize.medium;

    @Override
    public Component copy() {
        ItemComponent copy = new ItemComponent();
        copy.size = size;
        return copy;
    }

    @Override
    public boolean compare(Component other) {
        ItemComponent comp = (ItemComponent) other;
        return size == comp.size;
    }
}
