package cn.tkk.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *  Tkk
 */
@ApiModel
@Data
public class LoginRequest {

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "请输入手机号")
    private String mobile;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "请输入密码")
    private String password;


}
