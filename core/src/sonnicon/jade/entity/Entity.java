package sonnicon.jade.entity;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.*;

import java.util.*;
import java.util.stream.Stream;

@EventGenerator(id = "EntityComponentAdd", param = {Entity.class, Component.class}, label = {"entity", "component"})
@EventGenerator(id = "EntityComponentRemove", param = {Entity.class, Component.class}, label = {"entity", "component"})
@EventGenerator(id = "EntityTraitAdd", param = {Entity.class, Traits.Trait.class}, label = {"entity", "trait"})
@EventGenerator(id = "EntityTraitRemove", param = {Entity.class, Traits.Trait.class}, label = {"entity", "trait"})
public class Entity implements ICopyable, IComparable, IDebuggable {
    public HashMap<Class<? extends Component>, Component> components;
    public Traits traits;
    public final int id;
    private static int nextId = 1;

    public final Events events = new Events();

    public Entity() {
        components = new HashMap<>();
        traits = new Traits();
        id = nextId++;
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
        return component.canAddToEntity(this);
    }

    public boolean addComponent(Component component) {
        if (!canAddComponent(component)) {
            return false;
        }
        if (components.containsKey(component.getKeyClass())) {
            return false;
        }
        components.put(component.getKeyClass(), component);
        component.addToEntity(this);
        EventTypes.EntityComponentAddEvent.handle(events, this, component);
        return true;
    }

    public boolean removeComponent(Component component) {
        //todo dependency checking
        components.remove(component.getKeyClass(), component);
        component.removeFromEntity(this);
        EventTypes.EntityComponentRemoveEvent.handle(events, this, component);
        return true;
    }

    public boolean removeComponent(Class<? extends Component> type) {
        //todo dependency checking
        Component comp = components.remove(type);
        comp.removeFromEntity(this);
        EventTypes.EntityComponentRemoveEvent.handle(events, this, comp);
        return true;
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

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("components", components, "traits", traits, "id", id, "events", events);
    }
}
