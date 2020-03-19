package com.github.binlaniua.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Tkk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeResponse<K, V> {

    private K label;

    private V value;

    private List<TreeNodeResponse<K, V>> children;

    public TreeNodeResponse<K, V> add(TreeNodeResponse<K, V> child) {
        this.children = children == null ? new ArrayList<>() : children;
        this.children.add(child);
        return this;
    }
}
