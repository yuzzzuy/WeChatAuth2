# WeChatAuth2

微信授权

## WeChatAuthUtils

### 配置

- APPID:公众号唯一标识
- APPSECRET:公众号的appsecret
- REDIRECT_URL:授权后重定向的回调链接地址， 请使用 `urlEncode` 对链接进行处理

### 获取Code

1.getCode

```java
/**
 * @param redirectUri 回调地址
 * @param status      自定义返回状态码 (URL 拼接返回，可以填写a-zA-Z0-9的参数值，最多128字节)
 * @param scope       应用授权作用域
 * @return CODEURL 
 */
```

2.getAccessToken

```java
 /**
     * 刷新授权信息
     *
     * @param refreshToken 上次授权信息中的参数
     * @return AccessToken 
     * @throws IOException
     */
```

3.getUserInfo

```java
   /**
     * 获取用户信息
     * @param accessToken 授权信息
     * @return 用户信息
     * @throws IOException
     * @Description 40014 错误编码 ： accessToken过期
     */
```

