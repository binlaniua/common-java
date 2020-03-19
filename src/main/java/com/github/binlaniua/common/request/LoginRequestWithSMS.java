package com.github.binlaniua.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 *  Tkk
 */
@ApiModel
@Getter
@Setter
public class LoginRequestWithSMS extends LoginRequest {

    @ApiModelProperty(value = "短信验证码")
    @NotBlank(message = "请输入短信验证码")
    private String code;

}
