package com.github.binlaniua.common.http.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;

import java.io.InputStream;

/**
 * Tkk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpMulitiParam {

    private String fileName;

    private InputStream inputStream;

    private MediaType mediaType;
}
