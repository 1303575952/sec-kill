package com.doudizu.seckill.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;

public class HttpClient {
    /**
     * 请求编码
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 执行HTTP POST请求
     *
     * @param url   url
     * @param param 参数
     * @return
     */
    public static String httpPost(String url, Map<String, ?> param) {
        CloseableHttpClient client = null;

        try {
            if (url == null || url.trim().length() == 0) {
                throw new Exception("URL is null");
            }
            HttpPost httpPost = new HttpPost(url);
            client = HttpClients.createDefault();
            if (param != null) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(param.toString(), DEFAULT_CHARSET);
                entity.setContentEncoding(DEFAULT_CHARSET);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            HttpResponse resp = client.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(client);
        }

        return null;
    }

    /**
     * 执行post+json请求
     *
     * @param url
     * @param json
     * @return
     */
    public static String httpPostWithJSON(String url, String json) {
        CloseableHttpClient client = null;
        try {
            // 创建默认的httpClient实例
            client = HttpClients.createDefault();
            // 创建httppost
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Content-type", "application/json; charset=utf-8");
            // 向POST请求中添加消息实体
            StringEntity se = new StringEntity(json, "UTF-8");
            httppost.setEntity(se);
            // 执行post请求
            HttpResponse resp = client.execute(httppost);
            if (resp.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(client);
        }
        return null;
    }

    /**
     * 执行HTTP GET请求
     *
     * @param url   url
     * @param param 参数
     * @return
     */
    public static String httpGet(String url, Map<String, ?> param) {
        CloseableHttpClient client = null;

        try {
            if (url == null || url.trim().length() == 0) {
                throw new Exception("URL is null");
            }

            client = HttpClients.createDefault();

            if (param != null) {
                StringBuffer sb = new StringBuffer("?");

                for (String key : param.keySet()) {
                    sb.append(key).append("=").append(param.get(key)).append("&");
                }

                url = url.concat(sb.toString());
                url = url.substring(0, url.length() - 1);
            }

            HttpGet httpGet = new HttpGet(url);
            HttpResponse resp = client.execute(httpGet);

            if (resp.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(client);
        }

        return null;
    }

    /**
     * 关闭HTTP请求
     *
     * @param client
     */
    private static void close(CloseableHttpClient client) {
        if (client == null) {
            return;
        }

        try {
            client.close();
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        String url = "http://127.0.0.1:8888/token";
        String json = "{\n" +
                "        \"uid\": 45678,\n" +
                "        \"price\": 1223,\n" +
                "        \"order_id\": \"1245667\"\n" +
                "}";
        System.out.println(httpPostWithJSON(url, json));
    }
}
