package com.nanchen.rxjava2examples.module.rxjava2.operators.item;

import android.annotation.SuppressLint;
import android.util.Log;

import com.nanchen.rxjava2examples.R;
import com.nanchen.rxjava2examples.net.ProbeResult;
import com.nanchen.rxjava2examples.net.UrlProbe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * concatMap
 * <p>
 * concatMap作用和flatMap几乎一模一样，唯一的区别是它能保证事件的顺序
 * <p>
 * <p>
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-06-20  10:27
 */

public class RxConcatMapActivity extends RxOperatorBaseActivity {
    private static final String TAG = "RxConcatMapActivity";
    private Disposable mDisposable;

    @Override
    protected String getSubTitle() {
        return getString(R.string.rx_concatMap);
    }

    /**
     * 探测工具
     */
    private final UrlProbe urlProbe = new UrlProbe();

    @SuppressLint("CheckResult")
    @Override
    protected void doSomething() {

        // 从网络请求获取的 URL 列表 (提取出来的URL-list),可能n个
        List<String> urls = new ArrayList<>();
        urls.add("https://juejin.cn");
        urls.add("https://www.bilibili.com");
        urls.add("https://www.baidu.com");

        mRxOperatorsText.append(urls.toString());
        mDisposable = urlProbe.probeUrls(urls)
                .subscribe(new Consumer<ProbeResult>() {
                    @Override
                    public void accept(@NonNull ProbeResult probeResult) {
                        String result = "accept: " + probeResult.url + ", state: " + probeResult.flag;
                        Log.i(TAG, result);
                        mRxOperatorsText.append("\n");
                        mRxOperatorsText.append(result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) {
                        throwable.printStackTrace();
                        Log.e(TAG, "accept: fail");
                    }
                });
    }

    /**
     * 探测地址是否可用
     *
     * @param url 地址
     * @return 一个可以继续观察的obs
     */
    private Observable<ProbeResult> probeUrl(String url) {
        return Observable.create(emitter -> {
            // 发送 HTTP GET 请求并获取响应码, 具体网络请求的实现
            urlProbe.probeUrl(url, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        System.out.println("URL is accessible.");
                        // 发送可以访问的地址到下游
                        emitter.onNext(new ProbeResult(url, true));
                        emitter.onComplete();
                    } else {
                        System.out.println("URL returned error: " + response.code());
                        // 发送失败,可以继续下一个
                        emitter.onError(new Exception("Failed to probe " + url));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    System.out.println("Failed to access URL: " + t.getMessage());
                    // 发送失败,可以继续下一个
                    emitter.onError(new Exception("Failed to probe " + url));
                }
            });

        });
    }


    @SuppressLint("CheckResult")
    private void rawFunc() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                // e.onNext(2);
                // e.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "concatMap : accept : " + s + "\n");
                        mRxOperatorsText.append("concatMap : accept : " + s + "\n");
                    }
                });
    }
}
