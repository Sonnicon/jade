package sonnicon.jade.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class Pathfinder<T> {
    private final HashMap<T, T> transitions = new HashMap<>();
    private final SortedTripletList<Float, Float, T> queue = new SortedTripletList<>();


    protected ArrayList<T> findPath(T origin) {
        // Prepare
        transitions.clear();
        queue.clear();
        queue.add(predicate(origin), 0f, origin);

        T destination = null;

        // Search
        while (!queue.isEmpty()) {
            // Which node are we currently searching
            float currentDistance = queue.getBIndex(0);
            T currentNode = queue.getCIndex(0);
            queue.removeIndex(0);

            // Found destination, break
            if (isDestination(currentNode)) {
                destination = currentNode;
                break;
            }

            allAdjacent(currentNode, (T newNode, Float cost) -> {
                // Already solved
                if (transitions.containsKey(newNode)) {
                    return;
                }

                // New metrics
                float newDistance = currentDistance + cost;
                float newScore = newDistance + predicate(newNode);

                // Might already be in queue
                int existingIndex = queue.indexOfC(newNode);
                if (existingIndex >= 0) {
                    // Might already have a better path
                    if (newScore >= queue.getAIndex(existingIndex)) {
                        return;
                    }

                    queue.removeIndex(existingIndex);
                }
                queue.add(newScore, newDistance, newNode);
                transitions.put(newNode, currentNode);
            });
        }

        if (destination == null) {
            return null;
        }

        // Assemble path
        ArrayList<T> path = new ArrayList<>();
        T assembleNode = destination;
        while (assembleNode != origin) {
            path.add(assembleNode);
            assembleNode = transitions.get(assembleNode);
        }
        Collections.reverse(path);
        return path;
    }


    protected abstract void allAdjacent(T from, Consumer2<T, Float> cons);

    protected abstract float predicate(T source);

    protected abstract boolean isDestination(T point);
}
