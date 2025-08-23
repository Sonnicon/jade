package sonnicon.jade.entity;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.*;
import sonnicon.jade.world.Tile;
import sonnicon.jade.world.World;

import java.util.*;
import java.util.stream.Stream;

@EventGenerator(id = "EntityComponentAdd", param = {Entity.class, Component.class}, label = {"entity", "component"})
@EventGenerator(id = "EntityComponentRemove", param = {Entity.class, Component.class}, label = {"entity", "component"})
@EventGenerator(id = "EntityTraitAdd", param = {Entity.class, Traits.Trait.class}, label = {"entity", "trait"})
@EventGenerator(id = "EntityTraitRemove", param = {Entity.class, Traits.Trait.class}, label = {"entity", "trait"})
@EventGenerator(id = "EntityMove", param = {Entity.class}, label = {"entity"})
@EventGenerator(id = "EntityMoveTile", param = {Entity.class, Tile.class, Tile.class}, label = {"entity", "from", "dest"})
public class Entity implements ICopyable, IComparable, IDebuggable, IPositionMoving {
    public HashMap<Class<? extends Component>, Component> components;
    public Traits traits;
    public final int id;
    private static int nextId = 1;

    public final Events events = new Events();

    protected float x = Float.NaN;
    protected float y = Float.NaN;
    protected float rotation;

    public Entity() {
        components = new HashMap<>();
        traits = new Traits();
        id = nextId++;
    }

    public Entity(Component... components) {
        this();
        addComponents(components);
    }

    public Entity addComponents(Component... comps) {
        for (Component component : comps) {
            addComponent(component);
        }
        return this;
    }

    public void addTrait(Traits.Trait trait) {
        traits.addTrait(trait);
        EventTypes.EntityTraitAddEvent.handle(events, this, trait);
    }

    public Entity addTraits(Traits.Trait... traits) {
        for (Traits.Trait trait : traits) {
            addTrait(trait);
        }
        return this;
    }

    public void removeTrait(Traits.Trait trait) {
        traits.removeTrait(trait);
        EventTypes.EntityTraitRemoveEvent.handle(events, this, trait);
    }

    public Entity removeTraits(Traits.Trait... traits) {
        for (Traits.Trait trait : traits) {
            removeTrait(trait);
        }
        return this;
    }

    public boolean canAddComponent(Component component) {
        if (component == null) {
            return false;
        }

        if (component.entity != null || components.containsKey(component.getKeyClass())) {
            return false;
        }
        return component.canAddToEntity(this);
    }

    public boolean canRemoveComponent(Component component) {
        if (component == null) {
            return false;
        }

        if (component.entity != this || !hasComponent(component.getClass())) {
            return false;
        }
        return component.canRemoveFromEntity(this);
    }

    public boolean addComponent(Component component) {
        if (!canAddComponent(component)) {
            return false;
        }

        components.put(component.getKeyClass(), component);
        component.addToEntity(this);
        EventTypes.EntityComponentAddEvent.handle(events, this, component);
        return true;
    }

    public boolean removeComponent(Component component) {
        if (!canRemoveComponent(component)) {
            return false;
        }

        components.remove(component.getKeyClass(), component);
        component.removeFromEntity(this);
        EventTypes.EntityComponentRemoveEvent.handle(events, this, component);
        return true;
    }

    public boolean removeComponent(Class<? extends Component> type) {
        return removeComponent(getComponent(type));
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return (T) components.getOrDefault(type, null);
    }

    public <T extends Component> T getComponentFuzzy(Class<T> type) {
        T comp = getComponent(type);
        if (comp != null) {
            return comp;
        }
        Optional<T> opt = (Optional<T>) findComponentsFuzzy(type).findAny();
        return opt.orElse(null);
    }

    // todo find a way to make the bound include T and Component
    public <T> Stream<? extends T> findComponentsFuzzy(Class<T> type) {
        return (Stream<? extends T>) components.entrySet().stream()
                .filter(entry -> type.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue);
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    public boolean hasComponentFuzzy(Class<? extends Component> type) {
        if (hasComponent(type)) {
            return true;
        } else {
            return components.entrySet().stream().anyMatch(entry -> type.isAssignableFrom(entry.getKey()));
        }
    }

    @Override
    public Entity copy() {
        Entity newEntity = new Entity();

        // Component dependency order resolution

        // Output list of sort
        LinkedList<Class<? extends Component>> ordered = new LinkedList<>();
        // Map of node:outgoing graph links
        Map<Class<?>, HashSet<Class<? extends Component>>> graph = new HashMap<>();
        components.forEach((key, value) -> graph.put(key, value.getDependencies()));
        // Nodes without incoming edges
        HashSet<Class<? extends Component>> roots = new HashSet<>(components.keySet());
        HashSet<Class<? extends Component>> newRoots = new HashSet<>();
        graph.values().stream()
                .filter(Objects::nonNull)
                .forEach(set -> set.forEach(roots::remove));

        while (!roots.isEmpty()) {
            Iterator<Class<? extends Component>> iter = roots.iterator();

            while (iter.hasNext()) {
                Class<? extends Component> node = iter.next();
                iter.remove();
                ordered.add(node);
                HashSet<Class<? extends Component>> nodeSet = graph.getOrDefault(node, null);
                if (nodeSet == null) {
                    continue;
                }
                Iterator<Class<? extends Component>> iter2 = nodeSet.iterator();
                while (iter2.hasNext()) {
                    Class<? extends Component> edge = iter2.next();
                    iter2.remove();
                    if (graph.values().stream().filter(Objects::nonNull).noneMatch(set -> set.contains(edge))) {
                        newRoots.add(edge);
                    }
                }
            }
            roots.addAll(newRoots);
            newRoots.clear();
        }

        ordered.descendingIterator().forEachRemaining(comp -> newEntity.addComponent(components.get(comp).copy()));

        traits.copyTo(newEntity.traits);

        return newEntity;
    }

    @Override
    public boolean compare(IComparable o) {
        if (!(o instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) o;

        if (!traits.equals(other.traits) ||
                components.size() != other.components.size()) {
            return false;
        }

        for (Map.Entry<Class<? extends Component>, Component> comp : components.entrySet()) {
            if (!other.components.containsKey(comp.getKey()) ||
                    !comp.getValue().compare(other.getComponent(comp.getKey()))) {
                return false;
            }
        }
        return true;
    }

    public void moveTo(float x, float y) {
        Tile tileBefore = getTile();
        this.x = x;
        this.y = y;

        Tile tileAfter = getTile();
        if (tileBefore != tileAfter) {
            if (tileBefore != null) {
                tileBefore.entities.remove(this);
            }
            if (tileAfter != null) {
                tileAfter.entities.add(this);
            }
            EventTypes.EntityMoveTileEvent.handle(events, this, tileBefore, tileAfter);
        }

        EventTypes.EntityMoveEvent.handle(events, this);
    }

    public void moveBy(float x, float y) {
        moveTo(this.x + x, this.y + y);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public float getRotation() {
        return rotation;
    }

    @Override
    public World getWorld() {
        //todo
        return Content.world;
    }

    public boolean rotateTo(float degrees) {
        this.rotation = Utils.normalizeAngle(degrees);
        EventTypes.EntityMoveEvent.handle(events, this);
        return true;
    }


    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "components", components,
                "traits", traits,
                "id", id,
                "events", events,
                "getX", getX(), "getY", getY(),
                "getRotation", getRotation());
    }

    @Override
    public String debugName() {
        return IDebuggable.super.debugName() + " (" + components.size() + " components)";
    }

    @Override
    public Map<Object, Runnable> debugActions() {
        return Utils.mapFrom(
                "Move to null", (Runnable) () -> moveTo(Float.NaN, Float.NaN),
                "Move to camera", (Runnable) () -> moveTo(Jade.renderer.camera.position),
                "Rotate 15 Clockwise", (Runnable) () -> rotateBy(15f)
        );
    }
}
