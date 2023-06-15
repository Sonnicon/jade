package sonnicon.jade.entity.components;

import sonnicon.jade.game.EntityStorage;

public class StorageComponent extends Component {
    public EntityStorage storage;

    public StorageComponent() {
        storage = new EntityStorage();
    }

    public StorageComponent(EntityStorage storage) {
        this.storage = storage;
    }

    @Override
    public Component copy() {
        return new StorageComponent(storage.copy());
    }

    @Override
    public boolean compare(Component other) {
        return other instanceof StorageComponent && storage.compare(((StorageComponent) other).storage);
    }
}
