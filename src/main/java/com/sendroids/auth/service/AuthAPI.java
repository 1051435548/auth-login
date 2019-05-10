package com.sendroids.auth.service;


import com.sendroids.auth.bean.AuthUpBean;
import com.sendroids.auth.common.AuthType;

public interface AuthAPI {

    String getAuthUrl(
            AuthType type,
            String authUrl,
            AuthUpBean authUpBean
    );


    String getToken(
            AuthType type,
            String issueTokenUrl,
            AuthUpBean authUpBean
    );


    String verifyToken(
            String accessToken,
            String verifyTokenUrl
    );

    String verifyGoogle(
            String verifyIdTokenUrl,
            String idToken
    );

    boolean verifyLINE(
            String clientId,
            String clientSecret,
            String id_token,
            String nonce
    );

    String refreshToken(
            AuthType type,
            String refreshTokenUrl,
            AuthUpBean authUpBean
    );

    void revokeToken(
            AuthUpBean authUpBean,
            String revokeTokenUrl
    );

    String getProfile(
            AuthType type,
            AuthUpBean authUpBean,
            String getProfileUrl
    );


}
