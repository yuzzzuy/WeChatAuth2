package com.wechatauth2.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class OkHttpException extends Exception {
    private Integer errorCode;

    public OkHttpException() {
        super();
    }

    public OkHttpException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
