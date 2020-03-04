package cn.tkk.common.http.service;


import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Tkk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpParams {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_FILE = MediaType.parse("application/oct-stream");

    private String method;

    private String url;

    private Map<String, Object> params;

    private boolean json;

    private boolean file;

    private String userAgent;

    private Map<String, String> heads;

    public HttpParams put(final String name, final Object value) {
        if (this.params == null) {
            this.params = new TreeMap<>();
        }
        this.params.put(name, value);
        return this;
    }

    public Request toRequest() {
        final Request.Builder request;
        switch (this.method.toUpperCase()) {
            case "GET":
                request = this.toGet();
                break;
            case "POST":
                request = this.toPost();
                break;
            default:
                throw new RuntimeException("not support => " + this.method);
        }
        if (StringUtils.isNotBlank(this.userAgent)) {
            request.addHeader("User-Agent", this.userAgent);
        }
        if (this.heads != null) {
            this.heads.forEach((k, v) -> {
                request.addHeader(k, v);
            });
        }
        return request.build();
    }


    private Request.Builder toGet() {
        final HttpUrl.Builder builder = HttpUrl.parse(this.url)
                                               .newBuilder();
        if (this.params != null) {
            this.params.forEach((k, v) -> {
                if (v == null) {
                    return;
                }
                builder.addEncodedQueryParameter(k, v.toString());
            });
        }
        return new Request.Builder().url(builder.build());
    }

    private Request.Builder toPost() {
        // json提交
        if (this.json) {
            final String jsonString = JSON.toJSONString(this.params);
            final RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jsonString);
            return new Request.Builder().url(this.url)
                                        .post(body);
        }
        // 上传文件
        else if (this.file) {
            final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            this.params.forEach((k, v) -> {
                if (v == null) {
                    return;
                }
                if (v instanceof File) {
                    final File f = (File) v;
                    builder.addFormDataPart(
                            k, f.getName(), RequestBody.create(MEDIA_TYPE_FILE, f)
                    );
                } else if (v instanceof InputStream) {
                    builder.addFormDataPart(
                            k, k, create(MEDIA_TYPE_FILE, (InputStream) v)
                    );
                } else if (v instanceof HttpMulitiParam) {
                    final HttpMulitiParam p = (HttpMulitiParam) v;
                    builder.addFormDataPart(
                            k, p.getFileName(), create(p.getMediaType(), p.getInputStream())
                    );
                }
                //
                else {
                    builder.addFormDataPart(k, v.toString());
                }
            });
            return new Request.Builder().url(this.url)
                                        .post(builder.build());
        }
        // 上传
        else {
            final FormBody.Builder builder = new FormBody.Builder();
            if (this.params != null) {
                this.params.forEach((k, v) -> {
                    if (v == null) {
                        return;
                    }
                    builder.addEncoded(k, v.toString());
                });
            }
            return new Request.Builder().url(this.url)
                                        .post(builder.build());
        }
    }

    /**
     * @param mediaType
     * @param inputStream
     * @return
     */
    public static RequestBody create(final MediaType mediaType, final InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (final IOException e) {
                    return 0;
                }
            }

            @Override
            public void writeTo(final BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                }
            }
        };
    }

    /**
     * @param url
     * @return
     */
    public static HttpParams get(final String url) {
        return HttpParams
                .builder()
                .url(url)
                .method("GET")
                .params(new TreeMap<>())
                .build();
    }

    /**
     * @param url
     * @return
     */
    public static HttpParams get(final String url, final Map<String, Object> params) {
        return HttpParams
                .builder()
                .url(url)
                .params(params)
                .method("GET")
                .build();
    }

    /**
     * @param url
     * @return
     */
    public static HttpParams post(final String url, final Map<String, Object> params) {
        return HttpParams
                .builder()
                .url(url)
                .params(params)
                .method("POST")
                .build();
    }

    /**
     * @param url
     * @return
     */
    public static HttpParams postFile(final String url, final Map<String, Object> params) {
        return HttpParams
                .builder()
                .url(url)
                .params(params)
                .method("POST")
                .file(true)
                .build();
    }

    /**
     * @param url
     * @return
     */
    public static HttpParams postJson(final String url, final Map<String, Object> params) {
        return HttpParams
                .builder()
                .url(url)
                .params(params)
                .method("POST")
                .json(true)
                .build();
    }


    public HttpParams putHead(final String k, final String v) {
        if (this.heads == null) {
            this.heads = new TreeMap<>();
        }
        this.heads.put(k, v);
        return this;
    }


}


