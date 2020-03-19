package com.github.binlaniua.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Tkk
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonResponse<T> {

    @ApiModelProperty(value = "状态码 000000 正常, 000000 不正常")
    private String result;

    @ApiModelProperty(value = "当result为 000000 获取这个值")
    private T data;

    @ApiModelProperty(value = "当result不为 000000 获取这个值")
    private String error;

    /**
     * @param object
     * @return
     */
    public static <T> JsonResponse<T> success(T object) {
        JsonResponse<T> o = new JsonResponse<>();
        o.setData(object);
        o.setResult("000000");
        return o;
    }

    /**
     * @return
     */
    public static JsonResponse success() {
        return success("");
    }

    /**
     * @param message
     * @return
     */
    public static JsonResponse fail(String errorCode, String message) {
        return JsonResponse
                .builder()
                .result(errorCode)
                .error(message)
                .build();
    }

    /**
     * @return
     */
    public static JsonResponse fail() {
        return fail("000001", "系统错误");
    }

    /**
     * @param message
     * @return
     */
    public static JsonResponse fail(String message) {
        return fail("000001", message);
    }
}
