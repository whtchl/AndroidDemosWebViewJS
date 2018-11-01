package com.jdjz.webviewjslib.modle;


import com.jdjz.webviewjslib.modle.entity.NetworkStatusEntity;

public class ResultNetworkStatus extends ResultTemp{
    private NetworkStatusEntity responseResult;

    public NetworkStatusEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(NetworkStatusEntity responseResult) {
        this.responseResult = responseResult;
    }
}
