package sonnicon.jade.entity.components;

import sonnicon.jade.game.EntityStorage;

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
    public boolean compare(Component other) {
        return other instanceof StorageComponent && storage.compare(((StorageComponent) other).storage);
    }
}
