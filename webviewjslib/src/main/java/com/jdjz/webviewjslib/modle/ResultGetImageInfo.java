package com.jdjz.webviewjslib.modle;


import com.jdjz.webviewjslib.modle.entity.GetImageInfoEntity;

public class ResultGetImageInfo extends ResultTemp {
    private GetImageInfoEntity responseResult;

    public GetImageInfoEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(GetImageInfoEntity responseResult) {
        this.responseResult = responseResult;
    }
}
