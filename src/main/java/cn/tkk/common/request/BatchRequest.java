package cn.tkk.common.request;

import lombok.Data;

import java.util.List;

/**
 * @param <T>
 */
@Data
public class BatchRequest<T> {

    private List<T> idList;
}
