package sonnicon.jade.entity;

import java.util.HashMap;

public class Traits {
    private HashMap<Trait, Integer> traits;

    public void addTrait(Trait trait) {
        if (traits == null) {
            traits = new HashMap<>();
        }
        traits.put(trait, traits.getOrDefault(trait, 0) + 1);
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

    public boolean hasTrait(Trait trait) {
        return traits.containsKey(trait);
    }

    public void copyTo(Traits other) {
        if (traits == null) {
            return;
        }
        traits.forEach((Trait t, Integer c) -> other.traits.replace(t, c));
    }

    public enum Trait {
        opaque
    }

}
