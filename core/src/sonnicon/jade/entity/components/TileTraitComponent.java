package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Structs;
import sonnicon.jade.world.Tile;

import java.util.HashSet;
import java.util.Map;

public class TileTraitComponent extends Component {
    private HashSet<Traits.Trait> traits;

    private static final EventTypes.EntityMoveTileEvent MOVE_TILE_EVENT = (Entity e, Tile from, Tile to) -> {
        if (from != null) {
            from.traits.removeTraits(e.getComponent(TileTraitComponent.class).traits);
        }
        if (to != null) {
            to.traits.addTraits(e.getComponent(TileTraitComponent.class).traits);
        }
    };

    public TileTraitComponent() {
        setup(new HashSet<>());
    }

    public TileTraitComponent(Traits.Trait... traits) {
        setup(Structs.setFrom(traits));
    }

    public TileTraitComponent setup(HashSet<Traits.Trait> traits) {
        this.traits = traits;
        return this;
    }

    public void addTrait(Traits.Trait trait) {
        if (entity != null) {
            entity.getComponent(PositionComponent.class).tile.traits.addTrait(trait);
            traits.add(trait);
        }
    }

    public void removeTrait(Traits.Trait trait) {
        if (entity != null) {
            entity.getComponent(PositionComponent.class).tile.traits.removeTrait(trait);
            traits.remove(trait);
        }
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(MOVE_TILE_EVENT);
        MOVE_TILE_EVENT.apply(entity, null, entity.getComponent(PositionComponent.class).tile);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(MOVE_TILE_EVENT);
        MOVE_TILE_EVENT.apply(entity, entity.getComponent(PositionComponent.class).tile, null);
    }

    @Override
    public Component copy() {
        return ((TileTraitComponent) super.copy()).setup(new HashSet<>(traits));
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Structs.setFrom(PositionComponent.class);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Structs.mapExtendFrom(super.debugProperties(), "traits", traits);
    }
}
