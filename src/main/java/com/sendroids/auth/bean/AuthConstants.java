package com.sendroids.auth.bean;

public class AuthConstants {

    /**
     * line url
     */
    public static final String LINE_AUTH_URL = "https://access.line.me/oauth2/v2.1/authorize";

    public static final String LINE_ISSUE_TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";

    public static final String LINE_VERIFY_TOKEN_URL = "https://api.line.me/oauth2/v2.1/verify";

    public static final String LINE_REFRESH_TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";

    public static final String LINE_REVOKE_TOKEN_URL = "https://api.line.me/oauth2/v2.1/revoke";

    public static final String LINE_GET_PROFILE = "https://api.line.me/v2/profile";
    /**
     * line params
     */
    public static final String LINE_CLIENT_ID = "1565405760";
    public static final String LINE_CLIENT_SECRET = "9e4ee767999b200b8ae8eb8064fc8ce9";
    public static final String LINE_REDIRECT_URL = "http://127.0.0.1:8181/auth/line";
    public static final String LINE_DEFAULT_SCOPE = "openid%20profile";


    /**
     * google url
     */
    public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    public static final String GOOGLE_ISSUE_TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";

    public static final String GOOGLE_REFRESH_TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";

    public static final String GOOGLE_REVOKE_TOKEN_URL = "https://accounts.google.com/o/oauth2/revoke";

    public static final String GOOGLE_VERIFY_IDTOKEN_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo";

    public static final String GOOGLE_GET_PROFILE = "https://www.googleapis.com/oauth2/v2/userinfo";
    /**
     * google params
     */
    public static final String GOOGLE_CLIENT_ID = "429276749345-bqstgkmhmdn5df4r4urr80939jjvvt6g.apps.googleusercontent.com";
    public static final String GOOGLE_CLIENT_SECRET = "RNuXEBAbfnKmT8MWK0o3jocw";
    public static final String GOOGLE_REDIRECT_URL = "http://127.0.0.1:8181/auth/google";
    public static final String GOOGLE_DEFAULT_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";


    /**
     * facebook url
     */
    public static final String FACEBOOK_AUTH_URL = "https://www.facebook.com/v3.2/dialog/oauth";

    public static final String FACEBOOK_ISSUE_TOKEN_URL = "https://graph.facebook.com/v3.2/oauth/access_token";

    public static final String FACEBOOK_GET_PROFILE = "https://graph.facebook.com/me";
    /**
     * facebook params
     */
    public static final String FACEBOOK_CLIENT_ID = "";
    public static final String FACEBOOK_CLIENT_SECRET = "";
    public static final String FACEBOOK_REDIRECT_URL = "http://127.0.0.1:8181/auth/facebook";


    /**
     * wechat url
     */
    public static final String WECHAT_AUTH_URL = "https://open.weixin.qq.com/connect/qrconnect";

    public static final String WECHAT_ISSUE_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    public static final String WECHAT_REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

    public static final String WECHAT_GET_PROFILE = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * wechat params
     */
    public static final String WECHAT_APP_ID = "";
    public static final String WECHAT_SECRET = "";
    public static final String WECHAT_REDIRECT_URL = "http://127.0.0.1:8181/auth/wechat";


    /**
     * qq url
     */
    public static final String QQ_AUTH_URL = "https://graph.qq.com/oauth2.0/authorize";

    public static final String QQ_ISSUE_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";

    public static final String QQ_REFRESH_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";

    public static final String QQ_GET_PROFILE = "https://graph.qq.com/oauth2.0/me";
    /**
     * qq params
     */
    public static final String QQ_CLIENT_ID = "101567971";
    public static final String QQ_CLIENT_SECRET = "10b76e5102e170dbb865ccf7b80e2b36";
    public static final String QQ_REDIRECT_URL = "http://127.0.0.1:8181/auth/qq";
    public static final String QQ_DEFAULT_SCOPE = "get_user_info";


    /**
     * alipay url
     */
    public static final String ALIPAY_AUTH_URL = "https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm";

    public static final String ALIPAY_ISSUE_TOKEN_URL = "https://openapi.alipay.com/gateway.do";

    public static final String ALIPAY_REFRESH_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";

    public static final String ALIPAY_GET_PROFILE = "https://openapi.alipay.com/gateway.do";
    /**
     * alipay params
     */
    public static final String ALIPAY_CLIENT_ID = "2016100100636979";
    public static final String ALIPAY_CLIENT_SECRET = "";
    public static final String ALIPAY_REDIRECT_URL = "http://127.0.0.1:8181/auth/alipay";
    public static final String ALIPAY_DEFAULT_SCOPE = "auth_user";
    public static final String ALIPAY_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCKAAJqgZ9HAi022w0zWoZav68xzv3hos9xqOZQrmeLYwcKozZjbX2jg9do90HrVrA4Lg/H6apb9TR71Mn+1QY23FTk/Uj4Ij6UsIQjw5TQg1ds1hc0tV2fwpMHyHV6x2lJ4dyJneQOvE88p42Zw1lmSbZO10sDnHNDGg5BYdInKdLMcYqDzttkNOE4NzT0qDC8umsmWZ8jBDTKGHEj7VbryuwDKULVF/evjVK/glIGKx+CuPyJsqAQyODeNjnXq1943ob6qYJ6ksWVK82pvB/+SJQ6SSlgR99KPmN/3LR8kG52Nga8N5V4EmzSyaxduiO3mQJ71A498GdDgzFjImkLAgMBAAECggEBAInlmIR1IoQCWMwH+BjozHjCMn7DKE7gMgqRjBPzMuTF/7NRhHe2YWWTyRzKdsSDy3NYE8zgYauiSt+ysmZC2ALaCLsJnnDVR1OtT12tgncZx7Kp/GZfFT8fX2zXkw0tVrjNSWL6s3lGEqe5hnWwDxV3cwU1fXAOVd8nmjcbWLRoTsm+67N4iOMcTor6QVsE45bwMWlUtoZ831Ik8DATXp8DOMtHpNznHsf33ZDKAtj0DQQihrJKogzdOatNxXheoS2tOS+zFGt0PKSvcKZnlIzDPeiaW82lSmxDypInAtqTxoZrzFiVRbT/06KD50WNk+JRlj3LuTlNaULhjLxqDAECgYEAwM0J7qeKpdI6khUrmwC2oOiZJpBDkpcowpOAc5qerWATg+Llgr8ISxgXTllr75PDra6+mPdFi5TfZlA1Lj7Itp3LoSlks6JJsvJA+9VHMrWgLegKnCAP2po5hY51Cy5schMytEDryTPFAMtnxvOeNLbhqb+IpfCqNg8p1ChV3OsCgYEAtzxVUvw19oqDtz9IGPZ7yyNsWlCi5xmiocB9lkDtiKMp9cxTkukLQBQGuf97kchQp5XfM6SfKYhciZhLiMo3z0X4igMh5C3AcOusYMlqoGXmFzeRSFtMAF2nNYvjC27zgjHnyo1tlE+J/sCNE7lV8HEW8hmq8zJKf3K6P8dQHGECgYAcHwv+0lMnwpUmb44Pqrf4mlSUSmCAxil8z/ThZ8ETOrs99l9RhbmY8meb1vEmXaAh+iX0u8RaCpnXG4XWO/QcpsFnvVjh1p73SdxunvomjFVTvM+nXAnT8cDIGTI7MkdBEEnJ/tolW7a1WfXGEAyYVcBuePBee87n2ahZOJTlhwKBgE3nCcOhVPP/ieZjsixixtjjs5JmTAwC2yD37Kzv5806yYEmILYJ/G1+1ilH1Gpzhs7pKh8Ts1N+FRGuvRZoGvg1oGhU0pjnVrTrjbsMhxewmvVt+4eOwS46ovdiBOJ8hmzo+Mol/NucQWgcxx4GouH5wq6leQeybvfmeAbbn1ShAoGAMHfVGtqHrSOL1kOo7mj+vjBIabHL46cbl/BcYTjBtHKHft6UMNs/hFeQT56eDVEpXnGDP2V+zcpZcJuCl8GPfnLESgnLxxy7dtFwq8l8RU5YfbXiT34tWR8Q5FoufgR2CUxF6ZllWqFx0hcVn84Fg5IOvh2KTAwMo8AK+nDuJSE=";
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhaTHY5+0EgFi2YM+UqSjKu0vZQ5fl1XCuGOOYVkA8eBhnu8PncJHmhQ1M6+Ikt4gIxwqL4A0Pt88APg6Sh+gTrIYvYI5UFRXR5Y4Rs/8f/SaMn00KMUhO322pQ+rrXBJtckFGbLcz4WptqZYm2QW5GFAzjuMa0soKTTAv0N+gWcsqr911AStf9DraBPqJV/GlppOH6pYKudHQEpCxKyA7VPu9ekOPtKK7SIhRQqar+mtSiTN6WGBPYxSD9smPW3cp4kI4EPnCouvkw99Z/RJKzLuqBQ6fVSHehSL0rI7WVk4/LJj9HrrtturKhl90b0s+NK1qogpnnfCGExNLNp+HwIDAQAB";


}
