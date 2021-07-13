package com.wechatauth2.utils;

import com.alibaba.fastjson.JSONObject;
import com.wechatauth2.exception.OkHttpException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class OkHttpUtils {
    public static final int HTTP_STATUS_OK = 200;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 15;
    private static final int MAX_IDLE_CONNECTIONS = 15;
    private static final int KEEP_ALIVE_DURATION = 15;

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient
            .Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.MINUTES))
            .build();


    public static String getForResponseString(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        String responseString = null;
        Response okHttpResponse = getOkHttpResponse(url, params, headers);
        if (okHttpResponse != null && okHttpResponse.networkResponse() != null) {
            log.debug("URL:{} ，从发送到收到返回耗时：{} ms", url, okHttpResponse.networkResponse().receivedResponseAtMillis() - okHttpResponse.networkResponse().sentRequestAtMillis());
        }
        checkResponseStatus(okHttpResponse, url);
        if (okHttpResponse.isSuccessful()) {
            ResponseBody responseBody = okHttpResponse.body();
            if (null != responseBody) {
                responseString = responseBody.string();
            }
        }
        return responseString;
    }


    public static ResponseBody getForResponseBody(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        Response okHttpResponse = getOkHttpResponse(url, params, headers);
        checkResponseStatus(okHttpResponse, url);
        return okHttpResponse.body();
    }


    public static ResponseBody postFormForResponseBody(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        FormBody.Builder builder = new FormBody.Builder();
        formBuilderWithFormParams(builder, params);
        requestBuilderWithHeaders(requestBuilder, headers);
        RequestBody requestBodyPost = builder.build();

        Request request = requestBuilder
                .url(url)
                .post(requestBodyPost)
                .build();

        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();

        checkResponseStatus(response, url);
        return response.body();
    }

    public static Response postBinaryForResponse(String url, String content, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(null, content);
        requestBuilderWithHeaders(requestBuilder, headers);
        Request request = requestBuilder
                .url(url)
                .post(requestBody)
                .build();

        return OK_HTTP_CLIENT
                .newCall(request)
                .execute();
    }


    public static ResponseBody postJsonForResponseBody(String url, String json, Map<String, String> headers) throws IOException {
        return postJsonForResponse(url, json, headers).body();
    }

    public static ResponseBody putJsonForResponseBody(String url, String json, Map<String, String> headers) throws IOException {
        return putJsonForResponse(url, json, headers).body();
    }

    public static Response putJsonForResponse(String url, String json, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilderWithHeaders(requestBuilder, headers);
        Request request = requestBuilder.addHeader("Connection", "false")
                .url(url)
                .put(RequestBody.create(JSON_TYPE, json))
                .build();
        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();
//        checkResponseStatus(response, url);
        return response;
    }

    public static Response postJsonForResponse(String url, String json, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilderWithHeaders(requestBuilder, headers);
        Request request = requestBuilder.addHeader("Connection", "false")
                .url(url)
                .post(RequestBody.create(JSON_TYPE, json))
                .build();
        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();
//        checkResponseStatus(response, url);
        return response;
    }

    public static Response postJsonForResponseWithReErr(String url, String json, Map<String, String> headers) throws IOException, OkHttpException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilderWithHeaders(requestBuilder, headers);
        Request request = requestBuilder.addHeader("Connection", "false")
                .url(url)
                .post(RequestBody.create(JSON_TYPE, json))
                .build();
        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();
        checkResponseStatusWithCode(response, url);
        return response;
    }


    private static void checkResponseStatus(Response response, String url) {
        if (response.code() != HTTP_STATUS_OK) {
            try {
                log.error("okHTTP error {} {}", response.code(), url);
            } finally {
                response.close();
            }
        }
    }


    private static void checkResponseStatusWithCode(Response response, String url) throws OkHttpException {
        Integer errCode = response.code();
        if (errCode != HTTP_STATUS_OK) {
            try {
                log.error("okHTTP error {} {}", errCode, url);
                throw new OkHttpException(response.message(), errCode);
            } finally {
                response.close();
            }
        }
    }

    public static int curlHttpStatus(String url) throws Exception {
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .url(url)
                .get()
                .build();
        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();
        response.close();
        return response.code();
    }


    private static Response getOkHttpResponse(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        url = getUrlWithUrlParams(url, params);
        log.debug(">>> OkHttp 请求地址为:{}", url);
        requestBuilderWithHeaders(builder, headers);

        Request request = builder
                .url(url)
                .get()
                .build();

        return OK_HTTP_CLIENT.newCall(request).execute();
    }


    private static String getUrlWithUrlParams(String url, Map<String, String> params) {
        if (!url.contains("?")) {
            url += "?timestamp=" + System.currentTimeMillis();
        }
        StringBuilder appendUrl = new StringBuilder();
        if (null != params) {
            params.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    appendUrl.append("&").append(key).append("=").append(value);
                }
            });
            url = url + appendUrl.toString();
            log.debug(String.format("url:%s", url));
            log.debug(String.format("params:%s", JSONObject.toJSON(params)));
        }
        return url;
    }

    private static void formBuilderWithFormParams(FormBody.Builder builder, Map<String, String> params) {
        if (null != params) {
            params.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    builder.add(key, value);
                }
            });
        }
    }

    private static void requestBuilderWithHeaders(Request.Builder builder, Map<String, String> headers) {
        if (null != headers) {
            headers.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    builder.addHeader(key, value);
                }
            });
        }
    }
}

