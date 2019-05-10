package com.sendroids.auth.service;

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
import com.sendroids.auth.bean.AuthUpBean;
import com.sendroids.auth.common.AuthType;
import com.sendroids.auth.util.BeanConversionMapUtil;
import com.sendroids.auth.util.HttpClientUtil;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements AuthAPI {


    /**
     * @param type       auth type: QQ, WECHAT, ALIPAY, LINE, GOOGLE, FACEBOOK
     * @param authUrl    auth type url
     * @param authUpBean auth up bean
     * @return auth login url
     */
    public String getAuthUrl(
            final AuthType type,
            final String authUrl,
            final AuthUpBean authUpBean
    ) {
        String url = "";
        String clientId = authUpBean.getClient_id();
        String redirectUrl = authUpBean.getRedirect_uri();
        String state = authUpBean.getState();
        String nonce = authUpBean.getNonce();
        String scope;
        String encodedCallbackUrl;

        try {
            encodedCallbackUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        switch (type) {
            case LINE:
                scope = String.join("%20", authUpBean.getScope());
                url = authUrl + "?" +
                        "response_type=code" + "&" +
                        "client_id=" + clientId + "&" +
                        "redirect_uri=" + encodedCallbackUrl + "&" +
                        "state=" + state + "&" +
                        "nonce=" + nonce + "&" +
                        "scope=" + scope;
                break;
            case GOOGLE:
                scope = String.join(" ", authUpBean.getScope());
                url = authUrl + "?" +
                        "response_type=code" + "&" +
                        "access_type=offline" + "&" +
                        "client_id=" + clientId + "&" +
                        "redirect_uri=" + encodedCallbackUrl + "&" +
                        "state=" + state + "&" +
                        "scope=" + scope;
                break;
            case FACEBOOK:
                scope = String.join(",", authUpBean.getScope());
                url = authUrl + "?" +
                        "response_type=code" + "&" +
                        "client_id=" + clientId + "&" +
                        "redirect_uri=" + encodedCallbackUrl + "&" +
                        "state=" + state + "&" +
                        "scope=" + scope;
                break;
            case QQ:
                scope = String.join(",", authUpBean.getScope());
                url = authUrl + "?" +
                        "response_type=code" + "&" +
                        "client_id=" + clientId + "&" +
                        "redirect_uri=" + encodedCallbackUrl + "&" +
                        "state=" + state + "&" +
                        "scope=" + scope;
                break;
            case WECHAT:
                scope = String.join(",", authUpBean.getScope());
                url = authUrl + "?" +
                        "response_type=code" + "&" +
                        "appid=" + authUpBean.getAppid() + "&" +
                        "redirect_uri=" + encodedCallbackUrl + "&" +
                        "state=" + state + "&" +
                        "scope=" + scope;
                break;
            case ALIPAY:
                scope = String.join(",", authUpBean.getScope());
                url = authUrl + "?" +
                        "response_type=code" + "&" +
                        "app_id=" + authUpBean.getApp_id() + "&" +
                        "redirect_uri=" + encodedCallbackUrl + "&" +
                        "state=" + state + "&" +
                        "scope=" + scope;
                break;
        }
        return url;
    }


    /**
     * @param type          auth type: QQ, WECHAT, ALIPAY, LINE, GOOGLE, FACEBOOK
     * @param issueTokenUrl issue token url
     * @param authUpBean    auth up bean
     * @return token info
     */
    public String getToken(
            final AuthType type,
            final String issueTokenUrl,
            final AuthUpBean authUpBean
    ) {
        Map<String, String> params = new HashMap<>();
        try {
            params = BeanConversionMapUtil.targetObj(authUpBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (type.equals(AuthType.QQ) || type.equals(AuthType.WECHAT) || type.equals(AuthType.FACEBOOK)) {
            return HttpClientUtil.httpGet(issueTokenUrl, params);
        }
        return HttpClientUtil.httpPost(issueTokenUrl, params, "utf-8");

    }


    /**
     * @param appId      app id
     * @param privateKey The private key generated by the tool provided by alipay
     * @param publicKey  The public key generated by the tool provided by alipay
     * @param code       auth code
     * @return token info
     */
    public AlipaySystemOauthTokenResponse getAliPayToken(
            final String appId,
            final String privateKey,
            final String publicKey,
            final String code
    ) {
        AlipaySystemOauthTokenResponse response = null;
        AlipayClient alipayClient = getAliPayClient(appId, privateKey, publicKey);
        AlipaySystemOauthTokenRequest aliRequest = new AlipaySystemOauthTokenRequest();
        aliRequest.setCode(code);
        aliRequest.setGrantType("authorization_code");
        try {
            response = alipayClient.execute(aliRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param accessToken    access token
     * @param verifyTokenUrl url
     * @return client_id, expires_in, scope
     */
    public String verifyToken(
            final String verifyTokenUrl,
            final String accessToken
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        return HttpClientUtil.httpGet(verifyTokenUrl, params);
    }


    /**
     * @param verifyIdTokenUrl url
     * @param idToken          id token
     * @return Information in id token
     */
    public String verifyGoogle(
            final String verifyIdTokenUrl,
            final String idToken
    ) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id_token", idToken);
        return HttpClientUtil.httpPost(verifyIdTokenUrl, params, "utf-8");
    }

    /**
     * @param clientId     client id
     * @param clientSecret client secret
     * @param id_token     id token
     * @param nonce        nonce
     * @return true or false
     */
    public boolean verifyLINE(
            final String clientId,
            final String clientSecret,
            final String id_token,
            final String nonce
    ) {
        try {
            JWT.require(
                    Algorithm.HMAC256(clientSecret))
                    .withIssuer("https://access.line.me")
                    .withAudience(clientId)
                    .withClaim("nonce", nonce)
                    .acceptLeeway(60)
                    .build()
                    .verify(id_token);
            return true;
        } catch (UnsupportedEncodingException | JWTVerificationException e) {
            return false;
        }
    }


    /**
     * @param type            auth type: QQ, WECHAT, ALIPAY, LINE, GOOGLE, FACEBOOK
     * @param refreshTokenUrl refresh token url
     * @param authUpBean      auth up bean
     * @return new token info
     */
    public String refreshToken(
            final AuthType type,
            final String refreshTokenUrl,
            final AuthUpBean authUpBean
    ) {
        Map<String, String> params = new HashMap<>();
        try {
            params = BeanConversionMapUtil.targetObj(authUpBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (type.equals(AuthType.QQ) || type.equals(AuthType.WECHAT)) {
            return HttpClientUtil.httpGet(refreshTokenUrl, params);
        }
        return HttpClientUtil.httpPost(refreshTokenUrl, params,
                "utf-8");

    }

    /**
     * @param authUpBean     auth bean
     * @param revokeTokenUrl url
     */
    public void revokeToken(
            final AuthUpBean authUpBean,
            final String revokeTokenUrl
    ) {
        Map<String, String> params = new HashMap<>();
        try {
            params = BeanConversionMapUtil.targetObj(authUpBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpClientUtil.httpPost(revokeTokenUrl, params, "utf-8");
    }


    /**
     * @param authUpBean    auth up bean
     * @param getProfileUrl get profile url
     * @return user profile
     */
    public String getProfile(
            final AuthType type,
            final AuthUpBean authUpBean,
            final String getProfileUrl
    ) {
        if (type.equals(AuthType.LINE) || type.equals(AuthType.GOOGLE)) {
            return HttpClientUtil.httpGetUseHeader(getProfileUrl, authUpBean);
        }
        Map<String, String> params = new HashMap<>();
        try {
            params = BeanConversionMapUtil.targetObj(authUpBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HttpClientUtil.httpGet(getProfileUrl, params);

    }


    /**
     * @param appId       app id
     * @param privateKey  The private key generated by the tool provided by alipay
     * @param publicKey   The public key generated by the tool provided by alipay
     * @param accessToken access token
     * @return profile
     */
    public AlipayUserInfoShareResponse getAliPayProfile(
            final String appId,
            final String privateKey,
            final String publicKey,
            final String accessToken
    ) {
        AlipayUserInfoShareResponse response = null;
        AlipayClient alipayClient = getAliPayClient(appId, privateKey, publicKey);
        AlipayUserInfoShareRequest userInfoRequest = new AlipayUserInfoShareRequest();
        try {
            response = alipayClient.execute(userInfoRequest, accessToken);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * @param appId      app id
     * @param privateKey The private key generated by the tool provided by alipay
     * @param publicKey  The public key generated by the tool provided by alipay
     * @return AliPayClient
     */
    private static AlipayClient getAliPayClient(
            final String appId,
            final String privateKey,
            final String publicKey
    ) {
        return new DefaultAlipayClient(
                "https://openapi.alipaydev.com/gateway.do", appId, privateKey, "json", "UTF-8", publicKey, "RSA2");
    }


}


