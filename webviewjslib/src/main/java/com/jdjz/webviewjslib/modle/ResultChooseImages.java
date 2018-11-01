package com.jdjz.webviewjslib.modle;


import com.jdjz.webviewjslib.modle.entity.ChooseImagesFileInfoEntity;

public class ResultChooseImages extends  ResultTemp {

    private ChooseImagesFileInfoEntity responseResult;

    public ChooseImagesFileInfoEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ChooseImagesFileInfoEntity responseResult) {
        this.responseResult = responseResult;
    }
}
