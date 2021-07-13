package com.wechatauth2.utils;

import com.alibaba.fastjson.JSON;
import com.wechatauth2.enums.Scope;
import com.wechatauth2.model.AccessToken;
import com.wechatauth2.model.WeChatUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author WangChen
 * @date 2021-07-12 10:55
 */
@Slf4j
public class WeChatAuthUtils {

    /**
     * 公众号信息
     */
    private static final String APPID = "wxad8b23b1250624ff";
    private static final String APPSECRET = "d2e542b42703ea40ebb07d46588baa04";


    /**
     * 参数	            是否必须	    说明
     * appid	        是	        公众号的唯一标识
     * redirect_uri 	是	        授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
     * response_type	是	        返回类型，请填写code
     * scope	        是	        应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid）
     * snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     * state	        否	        重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * #wechat_redirect	是	        无论直接打开还是做页面302重定向时候，必须带此参数
     */

    private static final String CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
            "appid=APPID" +
            "&redirect_uri=REDIRECT_URI" +
            "&response_type=code" +
            "&scope=SCOPE" +
            "&state=STATE" +
            "#wechat_redirect";

    private static final String ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
            "appid=APPID" +
            "&secret=SECRET" +
            "&code=CODE" +
            "&grant_type=authorization_code";

    private static final String REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
            "appid=APPID" +
            "&grant_type=refresh_token" +
            "&refresh_token=REFRESH_TOKEN";
    private static final String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    /**
     * 获取Code的URL
     *
     * @param redirectUri 回调地址
     * @param status      自定义返回状态码 (URL 拼接返回，可以填写a-zA-Z0-9的参数值，最多128字节)
     * @param scope       应用授权作用域
     * @return CODEURL
     */
    public static String getCode(String redirectUri, String status, Scope scope) throws UnsupportedEncodingException {
        String url = CODE_URL;
        if (StringUtils.isNotBlank(APPID)) {
            url = url.replace("APPID", APPID);
        }
        if (StringUtils.isNotBlank(redirectUri)) {
            url = url.replace("REDIRECT_URI", URLEncoder.encode(redirectUri, "UTF-8"));
        }
        if (StringUtils.isNotBlank(scope.toString())) {
            url = url.replace("SCOPE", scope.toString());
        }
        if (StringUtils.isNotBlank(status)) {
            url = url.replace("STATE", status);
        }

        return url;
    }

    /**
     * 获取AccessToken 信息
     *
     * @param code 用户授权code
     * @return 以openid 为key 返回结果为 value 的Map
     * @throws IOException
     */
    public static String getAccessToken(String code) throws IOException {
        String url = ACCESS_TOKEN;
        if (StringUtils.isNotBlank(APPID)) {
            url = url.replace("APPID", APPID);
        }
        if (StringUtils.isNotBlank(APPSECRET)) {
            url = url.replace("SECRET", APPSECRET);
        }
        if (StringUtils.isNotBlank(code)) {
            url = url.replace("CODE", code);
        }
        String responseString = OkHttpUtils.getForResponseString(url, new HashMap<>(), new HashMap());
        log.info("AccessToken :{}",responseString);
        try {
            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 刷新授权信息
     *
     * @param refreshToken 上次授权信息中的参数
     * @return AccessToken
     * @throws IOException
     */
    public static String getRefreshAccessToken(String refreshToken) throws IOException {
        String url = REFRESH_TOKEN;
        if (StringUtils.isNotBlank(APPID)) {
            url = url.replace("APPID", APPID);
        }
        if (StringUtils.isNotBlank(refreshToken)) {
            url = url.replace("REFRESH_TOKEN", refreshToken);
        }
        String responseString = OkHttpUtils.getForResponseString(url, new HashMap<>(), new HashMap());
        log.info("Refresh String:{}",responseString);
        try {
            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取用户信息
     * @param accessToken 授权信息
     * @return 用户信息
     * @throws IOException
     * @Description 40014 错误编码 ： accessToken过期
     */
    public static WeChatUserInfo getUserInfo(AccessToken accessToken) throws IOException {
        String url = USER_INFO_URL;
        if (accessToken != null) {
            String access_token = accessToken.getAccessToken();
            String openId = accessToken.getOpenId();
            if (StringUtils.isNotBlank(access_token)){
                url = url.replace("ACCESS_TOKEN", access_token);
            }
            if (StringUtils.isNotBlank(access_token)){
                url = url.replace("OPENID", openId);
            }
        }
        String responseString = OkHttpUtils.getForResponseString(url, new HashMap<>(), new HashMap<>());
        log.info("UserInfo result:{}",responseString);
        if (responseString.contains("40014")){
            responseString = getRefreshAccessToken(accessToken.getRefreshToken());
        }
        try {
            return JSON.parseObject(responseString, WeChatUserInfo.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



}
