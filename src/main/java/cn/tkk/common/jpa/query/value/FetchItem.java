package cn.tkk.common.jpa.query.value;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.criteria.JoinType;

/**
 * Created by Tkk on 2018/7/21.
 */
@Data
@AllArgsConstructor
public class FetchItem {

    /**
     * 数据库字段
     *
     * @return
     */
    String column;


    /**
     * @return
     */
    JoinType join;
}
