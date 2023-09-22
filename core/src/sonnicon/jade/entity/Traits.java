package sonnicon.jade.entity;

import sonnicon.jade.util.IDebuggable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Traits implements IDebuggable {
    private HashMap<Trait, Integer> traits;

    public void addTrait(Trait trait) {
        if (traits == null) {
            traits = new HashMap<>();
        }
        traits.put(trait, traits.getOrDefault(trait, 0) + 1);
    }

    public void addTraits(Trait... traits) {
        for (Trait trait : traits) {
            addTrait(trait);
        }
    }

    public void addTraits(Set<Trait> traits) {
        for (Trait trait : traits) {
            addTrait(trait);
        }
    }

    public void removeTrait(Trait trait) {
        if (traits == null) {
            return;
        }
        int newAmount = traits.getOrDefault(trait, 0) - 1;
        if (newAmount == 0) {
            traits.remove(trait);
        } else if (newAmount > 0) {
            traits.replace(trait, newAmount);
        }
    }

    public void removeTraits(Trait... traits) {
        for (Trait trait : traits) {
            removeTrait(trait);
        }
    }

    public void removeTraits(Set<Trait> traits) {
        for (Trait trait : traits) {
            removeTrait(trait);
        }
    }

    public boolean hasTrait(Trait trait) {
        return traits != null && traits.containsKey(trait);
    }

    public void copyTo(Traits other) {
        if (traits == null) {
            return;
        }
        traits.forEach((Trait t, Integer c) -> other.traits.replace(t, c));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        Traits other = (Traits) o;
        return (traits == null && other.traits == null) ||
                (traits != null && other.traits != null && traits.equals(other.traits));
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return IDebuggable.debugProperties(traits);
    }


    public enum Trait {
        // Other entities can't move to overlap with us if we have this
        blockMovement,
        // We can move to overlap with other entities, even those with blockMovement
        incorporeal,
        // Can be picked up //todo
        stopPickup
    }
}
