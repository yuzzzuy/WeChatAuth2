package com.wechatauth2.controller;

import com.alibaba.fastjson.JSON;
import com.wechatauth2.enums.Scope;
import com.wechatauth2.model.*;
import com.wechatauth2.utils.WeChatAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author WangChen
 * @date 2021-07-12 10:38
 */
@Slf4j
@Controller
public class IndexController {


    @RequestMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        String url = WeChatAuthUtils.getCode("http://127.0.0.1:8080/index","123", Scope.snsapi_userinfo);
        response.sendRedirect(url);
    }



    @ResponseBody
    @RequestMapping("/index")
    public JsonResponse index(@RequestParam("code") String code,
                              @RequestParam("state") String state) throws IOException {
        log.info("code : {},state:{}",code,state);
        String responseString = WeChatAuthUtils.getAccessToken(code);
        if (responseString.contains("errcode")){
            WeChatErrorMessage weChatErrorMessage = JSON.parseObject(responseString, WeChatErrorMessage.class);
            log.error("wechat auth error:{}",weChatErrorMessage);
            return JsonResponse.createFeedback(FeedbackEnum.WECHAT_AUTH_FAIL,weChatErrorMessage.getErrMsg());
        }
        AccessToken accessToken = JSON.parseObject(responseString,AccessToken.class);
        WeChatUserInfo weChatUserInfo = WeChatAuthUtils.getUserInfo(accessToken);
        if (weChatUserInfo != null){
            weChatUserInfo.convertCode();
        }
        return JsonResponse.ok(weChatUserInfo);
    }


}
