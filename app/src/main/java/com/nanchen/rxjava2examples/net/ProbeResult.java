package com.nanchen.rxjava2examples.net;

/*
 * @description 探测地址的结果
 * @author dr
 * @time 1/17/24 2:34 PM
 */
public class ProbeResult {

    public final String url;
    public final boolean flag;

    public ProbeResult(String url, boolean flag) {
        this.url = url;
        this.flag = flag;
    }

    public String getUrl() {
        return url;
    }

    public boolean isFlag() {
        return flag;
    }
}
