package com.nanchen.rxjava2examples.net;

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
