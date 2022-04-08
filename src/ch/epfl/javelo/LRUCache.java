package ch.epfl.javelo;

import java.util.LinkedHashMap;
import java.util.Map;

    public class LRUCache<K, V> extends LinkedHashMap<K, V> {

        private final int MAX_ENTRIES;

        public LRUCache(int maxCapacity, float loadFactor) {
            super(maxCapacity, loadFactor, true);
            this.MAX_ENTRIES = maxCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }

    }

