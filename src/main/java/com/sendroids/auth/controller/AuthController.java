package com.sendroids.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sendroids.auth.bean.AuthConstants;
import com.sendroids.auth.bean.AuthDownBean;
import com.sendroids.auth.bean.AuthUpBean;
import com.sendroids.auth.common.AuthType;
import com.sendroids.auth.service.AuthService;
import com.sendroids.auth.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final String AUTH_LOGIN_STATE = "authState";
    private static final String AUTH_LOGIN_NONCE = "authNonce";

    private final AuthService authService;

    @Autowired
    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    /**
     * Get auth url
     */
    @ResponseBody
    @RequestMapping("/getURL/{type}")
    public String getURL(
            final @PathVariable AuthType type,
            final HttpSession httpSession
    ) {
        String authUrl = "";
        AuthUpBean authUpBean = new AuthUpBean();
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();
        authUpBean.setState(state);
        authUpBean.setNonce(nonce);
        httpSession.setAttribute(AUTH_LOGIN_STATE, state);
        httpSession.setAttribute(AUTH_LOGIN_NONCE, nonce);

        switch (type) {
            case LINE:
                authUrl = AuthConstants.LINE_AUTH_URL;
                authUpBean.setClient_id(AuthConstants.LINE_CLIENT_ID);
                authUpBean.setRedirect_uri(AuthConstants.LINE_REDIRECT_URL);
                authUpBean.setScope(Arrays.asList("profile", "openid", "email"));
                break;
            case GOOGLE:
                authUrl = AuthConstants.GOOGLE_AUTH_URL;
                authUpBean.setClient_id(AuthConstants.GOOGLE_CLIENT_ID);
                authUpBean.setRedirect_uri(AuthConstants.GOOGLE_REDIRECT_URL);
                authUpBean.setScope(Arrays.asList("https://www.googleapis.com/auth/userinfo.profile", "https://mail.google.com/"));
                break;
            case FACEBOOK:
                authUrl = AuthConstants.FACEBOOK_AUTH_URL;
                authUpBean.setClient_id(AuthConstants.FACEBOOK_CLIENT_ID);
                authUpBean.setRedirect_uri(AuthConstants.FACEBOOK_REDIRECT_URL);
                authUpBean.setScope(Arrays.asList("instagram_basic", "publish_video"));
                break;
            case QQ:
                authUrl = AuthConstants.QQ_AUTH_URL;
                authUpBean.setClient_id(AuthConstants.QQ_CLIENT_ID);
                authUpBean.setRedirect_uri(AuthConstants.QQ_REDIRECT_URL);
                authUpBean.setScope(Arrays.asList("get_user_info", "do_like"));
                break;
            case WECHAT:
                authUrl = AuthConstants.WECHAT_AUTH_URL;
                authUpBean.setAppid(AuthConstants.WECHAT_APP_ID);
                authUpBean.setRedirect_uri(AuthConstants.WECHAT_REDIRECT_URL);
                authUpBean.setScope(Arrays.asList("snsapi_login", "snsapi_userinfo"));
                break;
            case ALIPAY:
                List<String> scopes = new ArrayList<>();
                scopes.add("auth_user");
                scopes.add("auth_base");
                authUpBean.setScope(scopes);
                authUrl = AuthConstants.ALIPAY_AUTH_URL;
                authUpBean.setApp_id(AuthConstants.ALIPAY_CLIENT_ID);
                authUpBean.setRedirect_uri(AuthConstants.ALIPAY_REDIRECT_URL);
                break;
        }

        return authService.getAuthUrl(type, authUrl, authUpBean);

    }


    /**
     * Log in using line
     */
    @ResponseBody
    @RequestMapping("/line")
    public JSONObject authLine(
            final HttpServletRequest request,
            final HttpSession httpSession
    ) {
        AuthDownBean authDownBean = new AuthDownBean();
        System.out.println("line");
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        if (!state.equals(httpSession.getAttribute(AUTH_LOGIN_STATE))) {
            throw new IllegalArgumentException("State matching failed!");
        }
        httpSession.removeAttribute(AUTH_LOGIN_STATE);

        AuthUpBean authUpBean = new AuthUpBean();

        authUpBean.setCode(code);
        authUpBean.setClient_id(AuthConstants.LINE_CLIENT_ID);
        authUpBean.setClient_secret(AuthConstants.LINE_CLIENT_SECRET);
        authUpBean.setRedirect_uri(AuthConstants.LINE_REDIRECT_URL);
        authUpBean.setGrant_type("authorization_code");
        String tokenJSON = authService.getToken(
                AuthType.LINE,
                AuthConstants.LINE_ISSUE_TOKEN_URL,
                authUpBean
        );
        JSONObject jsonObject = JSON.parseObject(tokenJSON);
        String idToken = jsonObject.getString("id_token");
        authDownBean.setAccess_token(jsonObject.getString("access_token"));
        authDownBean.setToken_type(jsonObject.getString("token_type"));
        authDownBean.setRefresh_token(jsonObject.getString("refresh_token"));
        authDownBean.setExpires_in(jsonObject.getString("expires_in"));
        authDownBean.setId_token(idToken);
        authDownBean.setScope(jsonObject.getString("scope"));

        System.out.println("Line Token:" + jsonObject.getString("access_token"));
        System.out.println("Line RefreshToken:" + jsonObject.getString("refresh_token"));
        System.out.println("Line IDToken:" + jsonObject.getString("id_token"));
        // 1. Get user information through the API
        AuthUpBean getProfileBean = new AuthUpBean();
        getProfileBean.setAccess_token(authDownBean.getAccess_token());
        String resultJson = authService.getProfile(
                AuthType.LINE,
                getProfileBean,
                AuthConstants.LINE_GET_PROFILE
        );

        JSONObject profilesJson = JSONObject.parseObject(resultJson);
        String openId = profilesJson.getString("userId");
        String name = profilesJson.getString("displayName");
        String picture = profilesJson.getString("pictureUrl");

        // 2.  Get user information through the IdToken
        DecodedJWT jwt = JWT.decode(idToken);
        if (!httpSession.getAttribute(AUTH_LOGIN_NONCE).equals(jwt.getClaim("nonce").asString())) {
            throw new IllegalArgumentException("Nonce matching failed!");
        }
        httpSession.removeAttribute(AUTH_LOGIN_NONCE);
        System.out.println("通过idToken获取到的用户Id" + jwt.getClaim("sub").asString());
        System.out.println("通过idToken获取到的用户名" + jwt.getClaim("name").asString());
        System.out.println("通过idToken获取到的头像" + jwt.getClaim("picture").asString());

        return profilesJson;
    }


    /**
     * Log in using google
     */
    @ResponseBody
    @RequestMapping("/google")
    public JSONObject authGoogle(
            final HttpServletRequest request,
            final HttpSession httpSession
    ) {
        System.out.println("google");
        String code = request.getParameter("code");
        String state = request.getParameter("state");

        AuthUpBean authUpBean = new AuthUpBean();

        authUpBean.setCode(code);
        authUpBean.setClient_id(AuthConstants.GOOGLE_CLIENT_ID);
        authUpBean.setClient_secret(AuthConstants.GOOGLE_CLIENT_SECRET);
        authUpBean.setRedirect_uri(AuthConstants.GOOGLE_REDIRECT_URL);
        authUpBean.setGrant_type("authorization_code");
        String tokenJSON = authService.getToken(
                AuthType.GOOGLE,
                AuthConstants.GOOGLE_ISSUE_TOKEN_URL,
                authUpBean
        );
        if (!state.equals(httpSession.getAttribute(AUTH_LOGIN_STATE))) {
            throw new IllegalArgumentException("State matching failed!");
        }
        httpSession.removeAttribute(AUTH_LOGIN_STATE);

        JSONObject jsonObject = JSONObject.parseObject(tokenJSON);
        if (jsonObject == null) {
            throw new IllegalArgumentException("Token fetch failed!");
        }
        AuthDownBean authDownBean = new AuthDownBean();
        authDownBean.setAccess_token(jsonObject.getString("access_token"));
        authDownBean.setToken_type(jsonObject.getString("token_type"));
        authDownBean.setRefresh_token(jsonObject.getString("refresh_token"));
        authDownBean.setExpires_in(jsonObject.getString("expires_in"));
        authDownBean.setId_token(jsonObject.getString("id_token"));
        authDownBean.setScope(jsonObject.getString("scope"));

        System.out.println("google Token:" + jsonObject.getString("access_token"));
        System.out.println("google RefreshToken:" + jsonObject.getString("refresh_token"));
        System.out.println("google IDToken:" + jsonObject.getString("id_token"));

        AuthUpBean getProfileBean = new AuthUpBean();
        getProfileBean.setAccess_token(authDownBean.getAccess_token());
        String resultJson = authService.getProfile(
                AuthType.GOOGLE,
                getProfileBean,
                AuthConstants.GOOGLE_GET_PROFILE
        );

        JSONObject profilesJson = JSONObject.parseObject(resultJson);
        String openId = profilesJson.getString("id");
        String picture = profilesJson.getString("picture");
        String name = profilesJson.getString("name");
        String familyName = profilesJson.getString("family_name");
        String givenName = profilesJson.getString("given_name");

        return profilesJson;
    }


    /**
     * Log in using facebook
     */
    @ResponseBody
    @RequestMapping("/facebook")
    public void authFacebook(
            final HttpServletRequest request
    ) {
        System.out.println("facebook");

    }


    /**
     * Log in using qq
     */
    @ResponseBody
    @RequestMapping("/qq")
    public JSONObject authQQ(
            final HttpServletRequest request,
            final HttpSession httpSession
    ) {
        String state = request.getParameter("state");
        String code = request.getParameter("code");
        if (!state.equals(httpSession.getAttribute(AUTH_LOGIN_STATE))) {
            throw new IllegalArgumentException("State matching failed!");
        }
        httpSession.removeAttribute(AUTH_LOGIN_STATE);

        AuthUpBean authUpBean = new AuthUpBean();

        authUpBean.setCode(code);
        authUpBean.setClient_id(AuthConstants.QQ_CLIENT_ID);
        authUpBean.setClient_secret(AuthConstants.QQ_CLIENT_SECRET);
        authUpBean.setRedirect_uri(AuthConstants.QQ_REDIRECT_URL);
        authUpBean.setGrant_type("authorization_code");
        // get token
        String tokenInfo = authService.getToken(
                AuthType.QQ,
                AuthConstants.QQ_ISSUE_TOKEN_URL,
                authUpBean
        );

        if (tokenInfo == null) {
            throw new IllegalArgumentException("Token fetch failed!");
        }
        String[] token = tokenInfo.split("&");
        AuthDownBean authDownBean = new AuthDownBean();
        authDownBean.setAccess_token(token[0].substring(token[0].lastIndexOf("=") + 1));
        authDownBean.setExpires_in(token[1].substring(token[1].lastIndexOf("=") + 1));
        authDownBean.setRefresh_token(token[2].substring(token[2].lastIndexOf("=") + 1));
        System.out.println("QQ accessToken:" + token[0].substring(token[0].lastIndexOf("=") + 1));
        authDownBean.setClient_id(authUpBean.getClient_id());
        authDownBean.setClient_secret(authUpBean.getClient_secret());


        // get user profile
        AuthUpBean getProfileBean = new AuthUpBean();
        getProfileBean.setAccess_token(authDownBean.getAccess_token());
        String resultText = authService.getProfile(
                AuthType.QQ,
                getProfileBean,
                AuthConstants.QQ_GET_PROFILE
        );
        String json = resultText.substring(resultText.indexOf("(") + 1, resultText.lastIndexOf(")"));
        JSONObject profilesJson = JSON.parseObject(json);
        String clientId = profilesJson.getString("client_id");
        String openId = profilesJson.getString("openid");


        String refreshToken = authDownBean.getRefresh_token();
        AuthUpBean refreshTokenUpBean = new AuthUpBean();
        refreshTokenUpBean.setClient_id(AuthConstants.QQ_CLIENT_ID);
        refreshTokenUpBean.setClient_secret(AuthConstants.QQ_CLIENT_SECRET);
        refreshTokenUpBean.setRefresh_token(refreshToken);
        refreshTokenUpBean.setGrant_type("refresh_token");

        // refresh token
        String refreshTokenInfo = authService.refreshToken(
                AuthType.QQ,
                AuthConstants.QQ_REFRESH_TOKEN_URL,
                refreshTokenUpBean
        );
        String[] newToken = refreshTokenInfo.split("&");
        System.out.println("QQ newAccessToken:" + newToken[0].substring(token[0].lastIndexOf("=") + 1));

        return profilesJson;
    }

    /**
     * Log in using wechat
     */
    @ResponseBody
    @RequestMapping("/wechat")
    public void authWeChat(
            final HttpServletRequest request
    ) {
        System.out.println("wechat");

    }

    /**
     * Log in using alipay
     */
    @ResponseBody
    @RequestMapping("/alipay")
    public JSONObject authAliPay(
            final HttpServletRequest request
    ) {

        // token
        String app_id = AuthConstants.ALIPAY_CLIENT_ID;
        String privateKey = AuthConstants.ALIPAY_PRIVATE_KEY;
        String publicKey = AuthConstants.ALIPAY_PUBLIC_KEY;
        String code = request.getParameter("auth_code");
        AlipaySystemOauthTokenResponse tokenInfo = authService.getAliPayToken(
                app_id,
                privateKey,
                publicKey,
                code
        );
        String accessToken = tokenInfo.getAccessToken();

        // profile
        AlipayUserInfoShareResponse profileInfo = authService.getAliPayProfile(
                app_id,
                privateKey,
                publicKey,
                accessToken
        );
        String userInfo = profileInfo.getBody();
        return JSON.parseObject(userInfo);
    }


    public static void main(String[] args) {

//        verifyIdToken();
        refreshToken();
//        revokeToken();
    }

    private static void revokeToken() {
        Map<String, String> params = new HashMap<>();
        String url = AuthConstants.GOOGLE_REVOKE_TOKEN_URL;
        String token = "ya29.GlsFB4fhQmk-bnt-TgYzsfOCls8KqmycH0K-pGadfUeauadX339LCXkVcqYbImfwrfNYmJsGm8dMW496DHeMyDYLXus1mqgA2N62SE8skRUZ3JPdqkhxgWRI7tNJ";
//        params.put("client_id", AuthConstants.LINE_CLIENT_ID);
//        params.put("client_secret", AuthConstants.LINE_CLIENT_SECRET);
        params.put("token", token);
        HttpClientUtil.httpPost(url, params, "utf-8");

    }

    private static void refreshToken() {
        Map<String, String> params = new HashMap<>();
        String url = AuthConstants.GOOGLE_REFRESH_TOKEN_URL;
        String token = "1/h5SL_SVYWjzA26XjAE1li9kpbd3Zqhtw1obp976RDeg";
        params.put("client_id", AuthConstants.GOOGLE_CLIENT_ID);
        params.put("client_secret", AuthConstants.GOOGLE_CLIENT_SECRET);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", token);
        String info = HttpClientUtil.httpPost(url, params, "utf-8");

    }

    private static void verifyIdToken() {
        String idToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2FjY2Vzcy5saW5lLm1lIiwic3ViIjoiVTBlODFhMDNhOTMyYWRlOWNhNjYwZTdmMzlhNWRhYTMzIiwiYXVkIjoiMTU2NTQwNTc2MCIsImV4cCI6MTU1NzIwMzU3NiwiaWF0IjoxNTU3MTk5OTc2LCJub25jZSI6IjEzOTM2YThkLTE2YWQtNGY1ZC04ZGE0LWE3MDRjY2I4MzUxNSIsIm5hbWUiOiJ4aWFveWFuZyIsInBpY3R1cmUiOiJodHRwczovL3Byb2ZpbGUubGluZS1zY2RuLm5ldC8waG9ZWnVKdW1vTUhabkRCcDBaYmxQSVZ0SlBoc1FJalktSHo5NVIwUUZiUlVkT1NCd1UyMTZFVUlLUEJGUE5YRnlERzU1RWhBSmFFY2YiLCJlbWFpbCI6InkxMDUxNDM1NTQ4QGdtYWlsLmNvbSJ9.EsZbZnrT1wVuBoQ30V8QtbX3ZeuqHpDtgXsV0QpXUQU";
        try {
            JWT.require(
                    Algorithm.HMAC256(AuthConstants.LINE_CLIENT_SECRET))
                    .withIssuer("https://access.line.me")
                    .withAudience(AuthConstants.LINE_CLIENT_ID)
                    .withClaim("nonce", "13936a8d-16ad-4f5d-8da4-a704ccb83515")
                    .acceptLeeway(60) // add 60 seconds leeway to handle clock skew between client and server sides.
                    .build()
                    .verify(idToken);
            System.out.println("true");
        } catch (UnsupportedEncodingException | JWTVerificationException e) {
            e.printStackTrace();
            System.out.println("false");
        }
    }
}

