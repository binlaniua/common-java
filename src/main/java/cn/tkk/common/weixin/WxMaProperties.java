package cn.tkk.common.weixin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *  Tkk
 */
@Data
@ConfigurationProperties(prefix = "wx.ma")
public class WxMaProperties {
    /**
     * 设置微信公众号的appid
     */
    private String appId;

    /**
     * 设置微信公众号的app secret
     */
    private String secret;


}
