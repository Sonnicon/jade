package sonnicon.jade.entity;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.*;

import java.util.*;

@EventGenerator(id = "EntityComponentAdd", param = {Entity.class, Component.class}, label = {"entity", "component"})
@EventGenerator(id = "EntityTraitAdd", param = {Entity.class, Traits.Trait.class}, label = {"entity", "trait"})
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

    public <T extends Component> T getComponent(Class<T> type) {
        return (T) components.getOrDefault(type, null);
    }

    public <T extends Component> T getComponentFuzzy(Class<T> type) {
        T comp = getComponent(type);
        if (comp != null) {
            return comp;
        }
        Optional<T> opt = components.entrySet().stream()
                .filter(entry -> type.isAssignableFrom(entry.getKey()))
                .map(entry -> (T) entry.getValue())
                .findAny();

        return opt.orElse(null);
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
        return Structs.mapFrom("components", components, "traits", traits, "id", id);
    }
}
