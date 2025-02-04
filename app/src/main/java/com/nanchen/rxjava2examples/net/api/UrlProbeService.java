package com.nanchen.rxjava2examples.net.api;

import com.nanchen.rxjava2examples.net.ProbeResult;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/*
 * @description 探测URL地址是否可用的请求接口
 * @author dr
 * @time 1/17/24 2:35 PM
 */
public interface UrlProbeService {
    /**
     * 需要探测的地址
     *
     * @param url 探测的地址
     * @return Call
     */
    @GET
    Call<Void> probeUrl(@Url String url);
}
