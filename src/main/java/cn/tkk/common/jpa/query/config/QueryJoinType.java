package cn.tkk.common.jpa.query.config;

import javax.persistence.criteria.JoinType;

/**
 * Created by Tkk on 2018/7/31.
 */
public enum QueryJoinType {

    None,
    Left,
    Right,
    Inner;

    public JoinType toJoinType() {
        switch (this) {
            case Left:
                return JoinType.LEFT;
            case Right:
                return JoinType.RIGHT;
            default:
                return JoinType.INNER;
        }
    }
}
