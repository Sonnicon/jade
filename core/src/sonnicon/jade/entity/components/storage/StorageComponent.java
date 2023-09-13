package sonnicon.jade.entity.components.storage;

import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;

import java.util.Map;

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
        return super.compare(other) && storage.compare(((StorageComponent) other).storage);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "storage", storage);
    }
}
