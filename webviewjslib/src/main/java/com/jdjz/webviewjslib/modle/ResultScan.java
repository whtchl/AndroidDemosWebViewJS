package com.jdjz.webviewjslib.modle;


import com.jdjz.webviewjslib.modle.entity.ScanEntity;

public class ResultScan extends ResultTemp {
    private ScanEntity responseResult;

    public ScanEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ScanEntity responseResult) {
        this.responseResult = responseResult;
    }
}
