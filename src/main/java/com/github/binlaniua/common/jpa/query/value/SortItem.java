package com.github.binlaniua.common.jpa.query.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Tkk on 2018/7/21.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortItem {

    /**
     * 数据库字段
     *
     * @return
     */
    String column;


    /**
     * @return
     */
    boolean desc = false;
}
