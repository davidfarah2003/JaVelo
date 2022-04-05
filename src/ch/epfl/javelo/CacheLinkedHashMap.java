package ch.epfl.javelo;

import java.util.LinkedHashMap;
import java.util.Map;

    public class CacheLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

        private static final int MAX_ENTRIES = 100;

        public CacheLinkedHashMap(
                int initialCapacity, float loadFactor, boolean accessOrder) {

            super(initialCapacity, loadFactor, accessOrder);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }

    }

