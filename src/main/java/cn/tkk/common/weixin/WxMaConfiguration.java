package cn.tkk.common.weixin;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tkk
 */
@Configuration
@ConditionalOnProperty(name = "wx.ma.app-id")
@EnableConfigurationProperties({
        WxMaProperties.class,
})
public class WxMaConfiguration {

    @Autowired
    private WxMaProperties wxMaProperties;

    @Bean
    public WxMaConfig maConfig() {
        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
        config.setAppid(this.wxMaProperties.getAppId());
        config.setSecret(this.wxMaProperties.getSecret());
        return config;
    }

    @Bean
    public WxMaService wxMaService(WxMaConfig maConfig) {
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(maConfig);
        return service;
    }
}
