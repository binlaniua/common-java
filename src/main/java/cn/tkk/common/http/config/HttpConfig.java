package cn.tkk.common.http.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Tkk
 */
@Configuration
@EnableConfigurationProperties(HttpProperty.class)
public class HttpConfig {

    @Autowired
    private HttpProperty sdkProperty;

    @Bean
    OkHttpClient okHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) //
                .followRedirects(false) //
                .readTimeout(1, TimeUnit.MINUTES) //
                .retryOnConnectionFailure(false) //
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectionPool(new ConnectionPool(this.sdkProperty.getHttpMaxConnect(), this.sdkProperty.getHttpKeepAlice(), TimeUnit.MINUTES));

        // 代理
        if (StringUtils.isNotBlank(this.sdkProperty.getProxy())) {
            builder.proxy(new Proxy(
                    this.sdkProperty.getProxyType(),
                    new InetSocketAddress(this.sdkProperty.getProxy(), this.sdkProperty.getProxyPort()))
            );
        }
        return builder.build();
    }

}
