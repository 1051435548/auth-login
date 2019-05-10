package com.sendroids.auth.bean;

import lombok.Data;

import java.util.List;


@Data
public class AuthUpBean {

    private String client_id;

    private String appid;

    private String app_id;

    private String client_secret;

    private String secret;

    private String redirect_uri;

    private String state;

    private List<String> scope;

    private String nonce;

    private String prompt;

    private String max_age;

    private String bot_prompt;

    private String login_hint;

    private String include_granted_scopes;

    private String display;

    private String grant_type;

    private String code;

    private String sign_type;

    private String sign;

    private String timestamp;

    private String method;

    private String format;

    private String version;

    private String charset;

    private String refresh_token;

    private String access_token;

    private String Authorization;

    private String openid;

    private String lang;

    private String access_type;

}
