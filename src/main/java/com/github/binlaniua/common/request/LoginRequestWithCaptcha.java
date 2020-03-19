package com.github.binlaniua.common.request;

import com.github.binlaniua.common.valid.Captcha;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Tkk
 */
@ApiModel
@Getter
@Setter
public class LoginRequestWithCaptcha extends LoginRequest {

    @ApiModelProperty(value = "密码")
    @Captcha
    @NotBlank(message = "请输入验证码")
    private String captcha;

}
