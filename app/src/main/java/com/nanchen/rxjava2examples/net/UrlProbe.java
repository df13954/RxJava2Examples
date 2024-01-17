package com.nanchen.rxjava2examples.net;

import android.util.Log;

import com.nanchen.rxjava2examples.net.api.UrlProbeService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/*
 * @description 探测URL是否可用工具,可以配置超时时间
 * @author dr
 * @time 1/17/24 2:32 PM
 */
public class UrlProbe {
    private static final String TAG = "UrlProbe";
    private final UrlProbeService service;
    private final Scheduler scheduler;

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
                // 必须设置一个地址,不然会崩溃,后面用的时候会替换地址的
                .baseUrl("https://default.com")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        // 探测的get接口
        service = retrofit.create(UrlProbeService.class);
        scheduler = Schedulers.io();
    }

    /**
     * 探测多个地址
     *
     * @param urls 多个地址,不允许null
     * @return 返回探测结果
     */
    public Observable<ProbeResult> probeUrls(List<String> urls) {
        // 在这里可以加empty的限制.
        return Observable
                .fromIterable(urls)
                .concatMap(url -> probeUrlObs(url)
                        .subscribeOn(scheduler)
                        .onErrorReturnItem(new ProbeResult(url, false)))
                .filter(probeResult -> probeResult.flag)
                .take(1)
                .switchIfEmpty(Observable.just(new ProbeResult(urls.get(0), false)));
    }

    /**
     * 探测地址的异步请求
     *
     * @param url      请求地址
     * @param callback 异步回调
     */
    public void probeUrl(String url, Callback<Void> callback) {
        Log.i(TAG, "start probeUrl: " + url);
        Call<Void> call = service.probeUrl(url);
        call.enqueue(callback);
    }

    /**
     * 探测地址是否可用
     *
     * @param url 地址
     * @return 一个可以继续观察的obs
     */
    private Observable<ProbeResult> probeUrlObs(String url) {
        return Observable.create(emitter -> {
            // 发送 HTTP GET 请求并获取响应码, 具体网络请求的实现
            probeUrl(url, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "URL is accessible.");
                        // 发送可以访问的地址到下游
                        emitter.onNext(new ProbeResult(url, true));
                        emitter.onComplete();
                    } else {
                        Log.e(TAG, "onResponse URL returned error: " + response.code());
                        // 发送失败,可以继续下一个
                        emitter.onError(new Exception("Failed to probe " + url));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "onFailure Failed to access URL: " + t.getMessage());
                    // 发送失败,可以继续下一个
                    emitter.onError(new Exception("Failed to probe " + url));
                }
            });
        });
    }
}
