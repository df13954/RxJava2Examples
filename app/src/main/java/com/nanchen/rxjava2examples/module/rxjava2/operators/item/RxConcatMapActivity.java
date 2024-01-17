package com.nanchen.rxjava2examples.module.rxjava2.operators.item;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nanchen.rxjava2examples.R;
import com.nanchen.rxjava2examples.net.ProbeResult;
import com.nanchen.rxjava2examples.net.UrlProbe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
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

    @BindView(R.id.btn_all)
    public Button btnAll;

    @BindView(R.id.btn_1)
    public Button btn1;

    @BindView(R.id.btn_2)
    public Button btn2;

    @BindView(R.id.btn_3)
    public Button btn3;

    @Override
    protected String getSubTitle() {
        return getString(R.string.rx_concatMap);
    }

    /**
     * 探测工具
     */
    private final UrlProbe urlProbe = new UrlProbe();

    // 从网络请求获取的 URL 列表 (提取出来的URL-list),可能n个
    List<String> urls = new ArrayList<>();


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAll(urls);
                getHttp();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test1(urls);
                getHttp();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test2(urls);
                getHttp();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test3(urls);
                getHttp();
            }
        });
    }

    private void getHttp() {
        mRxOperatorsText.setText("");
        for (String ss : urls) {
            mRxOperatorsText.append(ss);
            mRxOperatorsText.append("\n");
        }

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

    @SuppressLint("CheckResult")
    @Override
    protected void doSomething() {

        // 从网络请求获取的 URL 列表 (提取出来的URL-list),可能n个
        List<String> urls = new ArrayList<>();
        //
        testAll(urls);

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


    private void testAll(List<String> urls) {
        urls.clear();
        urls.add("https://juejin_bad.cn");
        urls.add("https://www.bilibili_bad.com");
        urls.add("https://www.baidu.com_bad");
    }

    private void test1(List<String> urls) {
        urls.clear();

        urls.add("https://juejin.cn");
        urls.add("https://www.bilibili_bad.com");
        urls.add("https://www.baidu.com_bad");
    }

    private void test2(List<String> urls) {
        urls.clear();

        urls.add("https://juejin_bad.cn");
        urls.add("https://www.bilibili.com");
        urls.add("https://www.baidu.com_bad");
    }

    private void test3(List<String> urls) {
        urls.clear();

        urls.add("https://juejin_bad.cn");
        urls.add("https://www.bilibili_bad.com");
        urls.add("https://www.baidu.com");
    }

    /**
     * 原来的实现在这里
     */
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
