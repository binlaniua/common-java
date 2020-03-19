package com.github.binlaniua.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Tkk
 */
public class MapHelper<K, V> {

    private Map<K, V> r = new HashMap<>();

    public MapHelper put(K k, V v) {
        r.put(k, v);
        return this;
    }

    public Map<K, V> getMap() {
        return this.r;
    }

    public static MapHelper builder() {
        return new MapHelper();
    }
}
