package sonnicon.jade.entity;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Events;

import java.util.*;

@EventGenerator(id = "EntityComponentAdd", param = {Entity.class, Component.class}, label = {"entity", "component"})
@EventGenerator(id = "EntityTraitAdd", param = {Entity.class, Trait.class}, label = {"entity", "trait"})
public class Entity {
    public HashMap<Class<? extends Component>, Component> components;
    public HashSet<Trait> traits;
    public final int id;
    private static int nextId = 1;

    public final Events events = new Events();

    public Entity() {
        components = new HashMap<>();
        traits = new HashSet<>();
        id = nextId++;
    }

    public Entity addComponents(Component... comps) {
        for (Component component : comps) {
            addComponent(component);
        }
        return this;
    }

    public void addTrait(Trait trait) {
        traits.add(trait);
        EventTypes.EntityTraitAddEvent.handle(events, this, trait);
    }

    public Entity addTraits(Trait... traits) {
        for (Trait trait : traits) {
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

    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    public Entity copy() {
        Entity newEntity = new Entity();
        newEntity.traits.addAll(traits);

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

        return newEntity;
    }

    public boolean compare(Entity other) {
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
}
