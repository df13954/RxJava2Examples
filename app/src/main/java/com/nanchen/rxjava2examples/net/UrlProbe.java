package com.nanchen.rxjava2examples.net;

import com.nanchen.rxjava2examples.net.api.UrlProbeService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * 探测URL是否可用工具,可以配置超时时间
 */
public class UrlProbe {

    private final UrlProbeService service;
    /**
     * 超时定制
     */
    private final static int timeOut = 2;

    public UrlProbe() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 连接超时时间
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                // 写入超时时间
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                // 读取超时时间
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                // 默认 URL
                .baseUrl("https://default.com")
                // 添加自定义的 OkHttpClient
                .client(okHttpClient)
                .build();
        service = retrofit.create(UrlProbeService.class);
    }

    public void probeUrl(String url, Callback<Void> callback) {
        Call<Void> call = service.probeUrl(url);
        call.enqueue(callback);
    }
}
