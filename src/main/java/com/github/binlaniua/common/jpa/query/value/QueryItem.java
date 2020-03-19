package com.github.binlaniua.common.jpa.query.value;

import com.github.binlaniua.common.jpa.query.config.QueryConditionType;
import com.github.binlaniua.common.jpa.query.config.QueryJoinType;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Kun Tang on 2018/10/4.
 */
@Data
public class QueryItem {

    public QueryItem(final String column) {
        this.column = column;
    }

    public QueryItem(final String column, final QueryJoinType join) {
        this.column = column;
        this.join = join;
    }

    public QueryItem(final String column, final Object value) {
        this.column = column;
        this.value = value;
    }

    public QueryItem(final String column, final Object value, final QueryConditionType type) {
        this.column = column;
        this.value = value;
        this.type = type;
    }

    public QueryItem(final String column, final Object value, final QueryConditionType type, final QueryJoinType join, final boolean nullAble, final boolean blankAble) {
        this.column = column;
        this.value = value;
        this.type = type;
        this.join = join;
        this.nullAble = nullAble;
        this.blankAble = blankAble;
    }

    private String column;

    private Object value;

    private QueryConditionType type = QueryConditionType.equal;

    private QueryJoinType join = QueryJoinType.None;

    boolean nullAble = false;

    boolean blankAble = false;

    public boolean valid() {
        if (!this.isNullAble() && this.getValue() == null) {
            return false;
        }
        if (!this.isBlankAble() && StringUtils.isBlank(this.getValue()
                                                           .toString())) {
            return false;
        }
        return true;
    }
}
