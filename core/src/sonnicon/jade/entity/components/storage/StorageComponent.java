package sonnicon.jade.entity.components.storage;

import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.util.IComparable;

public class StorageComponent extends Component {
    public EntityStorage storage;

    public StorageComponent() {
    }

    public StorageComponent(EntityStorage storage) {
        setup(storage);
    }

    private StorageComponent setup(EntityStorage storage) {
        this.storage = storage;
        return this;
    }

    @Override
    public StorageComponent copy() {
        StorageComponent copy = (StorageComponent) super.copy();
        if (storage != null) {
            copy.setup(storage.copy());
        }
        return copy;
    }

    @Override
    public boolean compare(IComparable other) {
        return other instanceof StorageComponent && storage.compare(((StorageComponent) other).storage);
    }
}
