package cn.tkk.common.jpa.query.value;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * where exist (select 1 from thirdTableEntityClass where columnClass(column) in (columnValueList) and id = existColumn)
 *
 * @param <T>
 */
@Data
@NoArgsConstructor
public class ExistColumn<T> {

    /**
     * 查询条件
     */
    private String column;

    /**
     * 判断存在的字段
     */
    private Class columnClass;

    /**
     * 条件值
     */
    private List<T> columnValueList;

    /**
     * 本实体字段
     */
    private String existColumn;

    /**
     * 第三方表
     */
    private Class thirdTableEntityClass;

}
