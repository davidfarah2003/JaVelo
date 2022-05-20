package ch.epfl.javelo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class that represents Least Recently Used (LRU) cache with a certain capacity
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int MAX_ENTRIES;

    /**
     * Constructor
     * @param maxCapacity Maximum capacity of the cache
     * @param loadFactor the load factor
     */
    public LRUCache(int maxCapacity, float loadFactor) {
            super(maxCapacity, loadFactor, true);
            this.MAX_ENTRIES = maxCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    }

