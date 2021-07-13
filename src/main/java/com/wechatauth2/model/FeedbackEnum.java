package com.wechatauth2.model;

import lombok.Getter;

/**
 * @author WangChen
 * @date 2021-07-12 18:01
 */
@Getter
public enum FeedbackEnum {
    /**
     * 成功
     */
    SUCCESS("success"),
    /**
     * 失败
     */
    FAIL("fail"),

    WECHAT_AUTH_FAIL("wechat auth fail");

    private final String value;

    FeedbackEnum(String value) {
        this.value = value;
    }
}