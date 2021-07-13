package com.wechatauth2.model;

import lombok.Data;

/**
 * @author WangChen
 * @date 2021-07-12 15:37
 */
@Data
public class AccessToken {
    /**
     * 授权token
     * 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     */
    private String accessToken;
    /**
     * 过期时间(s)
     * access_token接口调用凭证超时时间，单位（秒）
     */
    private long expiresIn;
    /**
     * 刷新token的参数
     * 用户刷新access_token
     */
    private String refreshToken;
    /**
     * openid
     * 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     */
    private String openId;
    /**
     * scope
     * 用户授权的作用域，使用逗号（,）分隔
     */
    private String scope;
}