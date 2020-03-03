package cn.tkk.common.jpa.query.value;

import cn.tkk.common.jpa.query.config.QueryConidtonType;
import cn.tkk.common.jpa.query.config.QueryJoinType;
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

    public QueryItem(final String column, final Object value, final QueryConidtonType type) {
        this.column = column;
        this.value = value;
        this.type = type;
    }

    public QueryItem(final String column, final Object value, final QueryConidtonType type, final QueryJoinType join, final boolean nullAble, final boolean blankAble) {
        this.column = column;
        this.value = value;
        this.type = type;
        this.join = join;
        this.nullAble = nullAble;
        this.blankAble = blankAble;
    }

    String column;

    Object value;

    QueryConidtonType type = QueryConidtonType.equal;

    QueryJoinType join = QueryJoinType.None;

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
