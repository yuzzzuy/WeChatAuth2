package com.wechatauth2.model;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 通用的JSON对象封装
 *
 */
@Data
@Slf4j
public class JsonResponse<T> implements Serializable {

    private static final long serialVersionUID = 6831286597318271238L;
    protected T data;
    protected FeedbackEnum code;
    protected String message;


    public JsonResponse() {
        //保留空构造方法
    }

    public JsonResponse(T t) {
        this.code = FeedbackEnum.SUCCESS;
        this.message = FeedbackEnum.SUCCESS.getValue();
        this.data = t;
    }

    public static JsonResponse ok(Object t) {
        return new JsonResponse(t);
    }


    public static JsonResponse createFeedback(FeedbackEnum code) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.code = code;
        jsonResponse.message = code.getValue();
        return jsonResponse;
    }

    public static JsonResponse createFeedback(FeedbackEnum code, String feedbackMessage, String... placeHolders) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.code = code;
        String message = feedbackMessage;
        if (null != feedbackMessage && feedbackMessage.contains("%s")) {
            message = String.format(feedbackMessage, placeHolders);
        }
        jsonResponse.message = message;
        return jsonResponse;
    }

}
