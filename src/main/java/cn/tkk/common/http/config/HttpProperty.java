package cn.tkk.common.http.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;

/**
 * Tkk
 */
@Data
@ConfigurationProperties(prefix = "app.http")
public class HttpProperty {

    private int httpMaxConnect = 10;
    private long httpKeepAlice = 1;

    private String proxy;
    private int proxyPort;
    private Proxy.Type proxyType;
}
