package com.github.binlaniua.common.http.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Tkk
 */
@Data
@AllArgsConstructor
public class UrlBuilder {

    private String url;

    private Map<String, Object> map;

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(url);
        if (!url.contains("?")) {
            sb.append("?");
        }
        map.forEach((k, v) -> {
            try {
                sb
                        .append(k)
                        .append("=")
                        .append(URLEncoder.encode(v.toString(), "utf-8"))
                        .append("&");
            } catch (UnsupportedEncodingException e) {
            }
        });
        sb.trimToSize();
        return sb.subSequence(0, sb.length() - 1).toString();
    }
}
