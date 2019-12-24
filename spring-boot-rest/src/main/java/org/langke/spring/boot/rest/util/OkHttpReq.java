package org.langke.spring.boot.rest.util;

import com.google.common.collect.Lists;
import okhttp3.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * http 封装
 *
 * @author langke on 2019/12/24
 */
@Service
public class OkHttpReq {

    public static final Logger logger = LoggerFactory.getLogger(OkHttpReq.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final int HTTP_CODE_NORMAL = 200;
    private static final String PROXY_URL_PREFIX = "https://www.google.com/";
    /**
     * 连接器构建
     */
    private OkHttpClient client;

    private OkHttpClient proxyClient;

    @Autowired
    AppConfig appConfig;

    @PostConstruct
    public void init() {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        InetSocketAddress address = null;
        if (appConfig != null && appConfig.getProxyList()!=null && appConfig.getProxyList().size() > 0) {
            String proxy = appConfig.getProxyList().get(0);
            String delimiter = ":";
            int partLen = 2;
            String[] parts = proxy.split(delimiter);
            if (parts.length == partLen) {
                address = new InetSocketAddress(parts[0], Integer.valueOf(parts[1]));
            }
        }

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        if (address != null) {
            builder.proxy(new Proxy(Proxy.Type.HTTP, address));
        }
        proxyClient = builder.build();
    }

    private OkHttpClient switchClient(String url) {
        if (url.startsWith(PROXY_URL_PREFIX)) {
            return proxyClient;
        }
        return client;
    }

    /**
     * get 请求
     *
     * @param url
     * @param <T>
     * @return
     */
    public <T> Optional<T> get(String url, Class<T> clazz,Map<String,String> headers) {
        Request.Builder builder = new Request.Builder();
        if(headers!=null){
            for(String key:headers.keySet()){
                builder.addHeader(key,headers.get(key));
            }
        }
        Request request = builder
                .url(url)
                .build();
        try (Response response = switchClient(url).newCall(request).execute()) {
            return Optional.ofNullable(getObject(clazz, response));
        } catch (Exception e) {
            logger.error("请求失败, url:" + url, clazz, e);
        }
        return Optional.empty();
    }

    public boolean isUrlRight (String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = switchClient(url).newCall(request).execute()) {
            if (response.code() == HTTP_CODE_NORMAL) {
                return true;
            }
        } catch (IOException e) {
            logger.error("请求失败, url:" + url, e);
        }
        return false;
    }

    /**
     * get 请求
     *
     * @return
     */
    public File getToFile(String url, File file) {
        String getUrl = getParamsUrl(url, new HashMap<>(0));
        Request request = new Request.Builder()
                .url(getUrl)
                .build();

        try {
            String suffix = FilenameUtils.getExtension(getUrl);
            int paramIndex = suffix.indexOf("?");
            if (paramIndex != -1) {
                suffix = suffix.substring(0, paramIndex);
            }
            if (file == null) {
                file = File.createTempFile("tmp", FilenameUtils.EXTENSION_SEPARATOR + suffix);
            }
        } catch (IOException e) {
            logger.error("创建临时文件失败", e);
            return null;
        }
        try (Response response = switchClient(url).newCall(request).execute();
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            if (response.code() != HTTP_CODE_NORMAL) {
                logger.error("resp code is:" + response.code() + ",url:" + getUrl);
                return null;
            }
            if (response.body() == null) {
                logger.error("body  is null, " + response.code() + ",url:" + getUrl);
                return null;
            }
            IOUtils.copy(response.body().byteStream(), fileOutputStream);
            return file;
        } catch (IOException e) {
            logger.error("请求失败, url:" + getUrl, e);
        }
        return null;
    }

    public File getToFile(String url) {
        return getToFile(url, null);
    }

    /**
     * post 请求
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Optional<T> post(String url, Class<T> clazz, String json, Map<String, String> params,Map<String, String> headers) throws IOException{
        RequestBody body;
        if (json != null) {
            body = RequestBody.create(JSON, json);
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            body = builder.build();
        }
        Request.Builder builder = new Request.Builder();
        if(headers!=null){
            for(String key:headers.keySet()){
                builder.addHeader(key,headers.get(key));
            }
        }
        Request request = builder
                .url(url)
                .post(body)
                .build();
        try (Response response = switchClient(url).newCall(request).execute()) {
            return Optional.ofNullable(getObject(clazz, response));
        } catch (IOException e) {
            logger.error("请求失败, url:" + url + ",class:" + clazz.getName(), e);
            throw new IOException(e);
        }
        //return Optional.empty();
    }

    /**
     * post 请求
     *
     * @param url
     * @param params
     * @return
     */
    public Integer post(String url, Map<String, String> params) {
        RequestBody body;
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try(Response response = switchClient(url).newCall(request).execute()){
            return response == null ? 0 : response.code();
        }catch (Exception e) {
            logger.error("请求失败, url:" + url + ",params:" + params, e);
        }
        return 0;
    }

    /**
     * 获取下载地址
     * @param url
     * @return
     */
    public String getUrlResponseType(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try(Response response = switchClient(url).newCall(request).execute()){
            int responseCode = response.code();
            if (responseCode != HTTP_CODE_NORMAL) {
                logger.error("获取url返回类型出错,url:{}", url);
                return null;
            }
            if (response.body() == null) {
                return null;
            }
            MediaType mimeType = response.body().contentType();
            if (mimeType == null) {
                return null;
            }
            return mimeType.subtype();
        }catch (Exception e) {
            logger.error("请求失败, url:" + url, e);
        }

        return null;
    }


    private <T> T getObject(Class<T> clazz, Response response) throws IOException {
        if (response.code() != HTTP_CODE_NORMAL) {
            logger.error("返回码错误,code:" + response.code() + ",msg:" + response.message()+",url:"+response.request().url());
            return null;
        }
        if (response.body() == null) {
            return null;
        }
        String resp = response.body().string();
        if (Objects.equals(String.class, clazz)) {
            return (T) resp;
        }

        return JacksonUtil.toObject(resp, clazz);
    }

    private String getParamsUrl(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int pos = 0;
        for (String key : params.keySet()) {
            if (pos > 0) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            //对参数进行URLEncoder
            try {
                sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "utf-8"));
            } catch (UnsupportedEncodingException ignored) {
            }
            pos++;
        }
        return sb.toString();
    }
}
