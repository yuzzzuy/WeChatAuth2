package com.wechatauth2.model;

import lombok.Data;

/**
 * @author WangChen
 * @date 2021-07-12 17:25
 */
@Data
public class WeChatErrorMessage {
    private String errCode;
    private String errMsg;
    private String rid;
}
