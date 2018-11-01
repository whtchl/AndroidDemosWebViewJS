package com.jdjz.webviewjslib.modle;


import com.jdjz.webviewjslib.modle.entity.SystemInfoEntity;

public class ResultSystemInfo extends ResultTemp{
    private SystemInfoEntity responseResult;


    public SystemInfoEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(SystemInfoEntity responseResult) {
        this.responseResult = responseResult;
    }
}
