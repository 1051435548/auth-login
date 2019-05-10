package com.sendroids.auth.util;

import com.sendroids.auth.bean.AuthUpBean;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {

    public static String httpGet(
            final String url,
            final Map<String, String> params
    ) {

        StringBuilder stringBuilder = new StringBuilder(url + "?");
        for (String key : params.keySet()) {
            String value = params.get(key);
            stringBuilder.append(key).append("=").append(value).append("&");
        }
        String fixBuilder = stringBuilder.toString();
        HttpGet httpGet = new HttpGet(fixBuilder.substring(0, fixBuilder.length() - 1));

        return dealResponse(httpGet);
    }

    public static String httpGetUseHeader(
            String url,
            AuthUpBean authUpBean
    ) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Authorization", "Bearer " + authUpBean.getAccess_token());
        return dealResponse(httpGet);
    }

    public static String httpPost(
            final String url,
            final Map<String, String> params,
            final String charset
    ) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        httpPost.setConfig(requestConfig);

        List<NameValuePair> pairList = new ArrayList<>();
        for (String key : params.keySet()) {
            pairList.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity urlEncodedFormEntity = null;
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(urlEncodedFormEntity);

        return dealResponse(httpPost);
    }

    private static String dealResponse(
            final HttpRequestBase httpMethod
    ) {
        String result = null;
        try (
                CloseableHttpClient httpClient = HttpClients.custom()
                        .setRoutePlanner(new DefaultProxyRoutePlanner(new HttpHost("127.0.0.1", 1080))).build();
                CloseableHttpResponse response = httpClient.execute(httpMethod)
        ) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException("statusCode = " + statusCode);
            }
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
