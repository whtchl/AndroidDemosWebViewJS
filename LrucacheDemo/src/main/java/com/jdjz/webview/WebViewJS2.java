package com.jdjz.webview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.webkit.ValueCallback;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jdjz.common.TransScanEntity;
import com.jdjz.contact.ChooseModel;
import com.jdjz.contact.ContactInfo;
import com.jdjz.lrucachedemo.R;
import com.jdjz.webviewjslib.jsbridge.BridgeHandler;
import com.jdjz.webviewjslib.jsbridge.BridgeWebView;
import com.jdjz.webviewjslib.jsbridge.BridgeWebViewClient;
import com.jdjz.webviewjslib.jsbridge.CallBackFunction;
import com.jdjz.webviewjslib.jsbridge.DefaultHandler;
import com.jdjz.webviewjslib.modle.ModleConfig;
import com.jdjz.webviewjslib.modle.RequestParams.RequestEnterpriseChatParams;
import com.jdjz.webviewjslib.modle.RequestParams.RequestUserProfileParams;
import com.jdjz.webviewjslib.modle.ResultSaveImage;
import com.jdjz.webviewjslib.modle.ResultToken;
import com.jdjz.weexlib.date.DateConfig;
import com.jdjz.weexlib.date.DateDialog;
import com.jdjz.weexlib.weex.activity.ContactListActivity;
import com.jdjz.weexlib.weex.activity.UserProfileActivity;

import com.jdjz.weexlib.weex.modle.Brand;
import com.jdjz.weexlib.weex.modle.Event.ContactEvent;
import com.jdjz.weexlib.weex.modle.Event.ContactsEvent;
import com.jdjz.weexlib.weex.modle.Event.GetImageInfoEvent;
import com.jdjz.weexlib.weex.modle.Event.LocationEvent;
import com.jdjz.weexlib.weex.modle.Event.PhoneEvent;
import com.jdjz.weexlib.weex.modle.Event.PreviewImageEvent;
import com.jdjz.weexlib.weex.modle.Event.SaveImageToPhotosAlbumEvent;
import com.jdjz.weexlib.weex.modle.Event.ScanEvent;
import com.jdjz.weexlib.weex.modle.Event.StartAutoLBSEvent;
import com.jdjz.weexlib.weex.modle.Event.StopAutoLBSEvent;
import com.jdjz.weexlib.weex.modle.PreviewImagesData;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestChooseImagesParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestContactsParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestGetImageInfoParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestLBSParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestLBSWGS84_GCJ02Params;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestPhoneParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestPreviewImagesParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestSaveImg;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestScanParams;
import com.jdjz.weexlib.weex.modle.RequestParams.ReuquestDateParams;
import com.jdjz.weexlib.weex.modle.ResultChooseImages;
import com.jdjz.weexlib.weex.modle.ResultContact;
import com.jdjz.weexlib.weex.modle.ResultContacts;
import com.jdjz.weexlib.weex.modle.ResultDate;
import com.jdjz.weexlib.weex.modle.ResultGetImageInfo;
import com.jdjz.weexlib.weex.modle.ResultLBS;
import com.jdjz.weexlib.weex.modle.ResultNetworkStatus;
import com.jdjz.weexlib.weex.modle.ResultScan;
import com.jdjz.weexlib.weex.modle.ResultSystemInfo;
import com.jdjz.weexlib.weex.modle.ResultTemp;
import com.jdjz.weexlib.weex.modle.entity.ChooseImagesFileInfoEntity;
import com.jdjz.weexlib.weex.modle.entity.GetImageInfoEntity;
import com.jdjz.weexlib.weex.modle.entity.LBSEntity;
import com.jdjz.weexlib.weex.modle.entity.NetworkStatusEntity;
import com.jdjz.weexlib.weex.modle.entity.ScanEntity;
import com.jdjz.weexlib.weex.modle.entity.Street;
import com.jdjz.weexlib.weex.modle.entity.SystemInfoEntity;
import com.jdjz.weexlib.weex.modle.entity.TempFile;
import com.jdjz.weexlib.weex.util.ImgUtil;
import com.jdjz.weexlib.weex.util.StrUtil;
import com.jdjz.weexlib.weex.view.WXWebViewJsBridge;
import com.jude.utils.JUtils;
import com.jude.utils.permission.PermissionNameList;
import com.jude.utils.permission.PermissionRequestCode;
import com.jude.utils.permission.PermissionsUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.whamu2.previewimage.Preview;
import com.whamu2.previewimage.entity.Image;
import com.yzq.zxinglibrary.android.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;


public class WebViewJS2 extends AppCompatActivity {
    private BridgeWebView mWebView;

    ValueCallback<Uri> mUploadMessage;

    int RESULT_CODE = 0;

    private static final String TAG = "MainActivity";
    //CallBackFunction call;
    Context mContext;

    CallBackFunction callBackFunction;
    String h5Data;  //来自网页端的数据


    String lbs_type ="0";//默认是0，获取经纬度。lbs_type>1 需要街道级别逆地理的才会有的字段，街道门牌信息：lbs_type>2,需要POI级别逆地理的才会有的字段，定位点附近的 POI 信息
    int responseTypeLBS=0;//0:返回给getLocation()； 1：返回给NativeToJSstartAutoLBS
    String gs84_gcj02;//wgs84是gps坐标，gcj02是火星坐标（默认）

    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;

    //高德地图
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webviewjs2);
        EventBus.getDefault().register(this);
        mContext = this;
        mWebView = (BridgeWebView) findViewById(R.id.webView);
        initWebView();
        monitorBatteryState();
        initLocation();
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        //支持屏幕缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        // 设置具体WebViewClient
        mWebView.setWebViewClient(new MyWebViewClient(mWebView));
        // set HadlerCallBack
        mWebView.setDefaultHandler(new myHadlerCallBack());
        // setWebChromeClient
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

       // mWebView.loadUrl("file:///android_asset/webviewjs.html");
        mWebView.loadUrl("http://juziwalk.cn/jsbridge/demo.html");

        //必须和js函数名字一致，注册好具体执行回调函数，类似java实现类。
        mWebView.registerHandler("submitFromWeb", new  BridgeHandler() {

            @Override
            public void handler(String data,  CallBackFunction function) {

                String str = "这是html返回给java的数据:" + data;
                // 例如你可以对原始数据进行处理
                str = str + ",Java经过处理后截取了一部分：" + str.substring(0, 5);
                Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                Toast.makeText(WebViewJS2.this, str, Toast.LENGTH_SHORT).show();
                //回调返回给Js
                function.onCallBack(str + ",Java经过处理后截取了一部分：" + str.substring(0, 5));
            }

        });

        mWebView.registerHandler("functionOpen",  new  BridgeHandler() {

            @Override
            public void handler(String data,  CallBackFunction function) {
                Toast.makeText(WebViewJS2.this, "网页在打开你的下载文件预览", Toast.LENGTH_SHORT).show();
                pickFile();
                callBackFunction = function;

            }

        });
        //模拟用户信息 获取本地位置，用户名返回给html
        User2 user = new User2();
        user.setLocation("上海");
        user.setName("Bruce");
        // 回调 "functionInJs"
        mWebView.callHandler("functionInJs", new Gson().toJson(user), new  CallBackFunction() {
            @Override
            public void onCallBack(String data) {

                Toast.makeText(WebViewJS2.this, "网页在获取你的位置，"+ data, Toast.LENGTH_SHORT).show();

            }
        });




        reqGetOpenId();
        mWebView.registerHandler("requestFromNativeTypeSetPrivateToken", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeTypeSetPrivateToken, data from web = " + data);
                JUtils.getSharedPreference().edit().putString("tokenMyServer",data).apply();

                function.onCallBack("点击“获取token”按钮查看" );
            }

        });

        reqGetNetworkType();
        reqMakePhoneCall();
        reqGetSystemInfo();
        reqDatePicker();
        reqScan();
        reqGetLocation();
        reqStopAutoLBS();
        reqStartAutoLBS();
        reqOpenEnterpriseChat();
        reqOpenUserProfile();
        reqSelectEnterpriseContact();
        reqPreviewImage();
        reqChooseImage();
        reqSaveImageToPhotosAlbum();
        reqGetImageInfo();
        reqChangeScreen();
        mWebView.send("hello");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
        unregisterReceiver(batteryLevelRcvr);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JUtils.Log("onRequestPermissionsResult:" + requestCode);
        for(int i=0;i<permissions.length;i++){
            JUtils.Log("permission name:"+permissions[i]+"  grantResults:"+grantResults[i]);
        }

        JUtils.Log("PermissionsUtil.isGranted:"+ PermissionsUtil.isGranted(grantResults)+"  "+ PermissionsUtil.hasPermission(this, permissions));

        if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new PhoneEvent());
            handleMakePhoneCall();
            JUtils.Toast("可以使用电话功能了了");
        } else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new LocationEvent());
            handleGetLocation();
            JUtils.Toast("可以定位了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new ContactsEvent());
            handleSelectEnterpriseContact();
            JUtils.Toast("可以使用联系人了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new StartAutoLBSEvent());
            handleStartAutoLBS();
            JUtils.Toast("可以自动获取地理职位了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new StopAutoLBSEvent());
            handleStopAutoLBS();
            JUtils.Toast("可以停止自动获取地理职位了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new PreviewImageEvent());
            handlePreviewImage();
            JUtils.Toast("可以预览图片了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new GetImageInfoEvent());
            handleGetImageInfo();
            JUtils.Toast("可以获取图片信息了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new SaveImageToPhotosAlbumEvent());
            handleSaveImageToPhotosAblum();
            JUtils.Toast("可以保存图片到相册了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_CHOOSEIMAGE && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new ChooseImagesFileInfoEntity());
            handleChooseImage();
            JUtils.Toast("可以选择照片了");
        }else if (requestCode == PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SCAN && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            //EventBus.getDefault().post(new ScanEvent());
            handleScan();
            JUtils.Toast("可以扫描二维码了");
        }
        else {
            JUtils.Toast(this.getString(com.jdjz.weexlib.R.string.permissontip));
        }
    }

    /**
     * 自定义的WebViewClient
     */
    class MyWebViewClient extends BridgeWebViewClient {

        public MyWebViewClient( BridgeWebView webView) {
            super(webView);
        }
    }


    /**
     * 自定义回调
     */
    class myHadlerCallBack extends DefaultHandler {

        @Override
        public void handler(String data,  CallBackFunction function) {
            if(function != null){

                Toast.makeText(WebViewJS2.this, data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("tchl","ddddd"+requestCode);

        /*if (requestCode == RESULT_CODE) {
            *//*if (null == mUploadMessage) {
                return;
            }*//*
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
            *//*mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;*//*

            Log.i("tchl",result.toString());
            call.onCallBack(result.toString());
        }*/
    }




    /**
     * 获取openid
     */
    private void reqGetOpenId(){
        mWebView.registerHandler("reqGetOpenId", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqGetOpenId, data from web = " + data);

                String str = JUtils.getSharedPreference().getString("tokenMyServer", ModleConfig.RES404);
                ResultToken resultToken = new ResultToken();
                if (str.equals(ModleConfig.RES404)) {
                    resultToken.setResponseCode(ModleConfig.RES404);
                    resultToken.setResponseMsg(ModleConfig.RES_FAIL);
                    resultToken.setResponseResult("");
                } else {
                    resultToken.setResponseCode(ModleConfig.RES200);
                    resultToken.setResponseMsg(ModleConfig.RES_SUCCESS);
                    JUtils.Log("get openid:"+str);
                    resultToken.setResponseResult(str);
                }
                String str2 = new Gson().toJson(resultToken);

                function.onCallBack(str2);
            }

        });
    }

    /**
     *开启持续定位
     */
    private void reqStartAutoLBS(){
        mWebView.registerHandler("reqStartAutoLBS", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqStartAutoLBS, data from web = " + data);
                h5Data=  data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_LOCATION, PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION);
            }

        });
    }

    /**
     * 创建会话
     */
    private void reqOpenEnterpriseChat(){
        mWebView.registerHandler("reqOpenEnterpriseChat", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqOpenEnterpriseChat, data from web = " + data);
                RequestEnterpriseChatParams requestEnterpriseChatParams = new RequestEnterpriseChatParams();
                requestEnterpriseChatParams = new Gson().fromJson(data,RequestEnterpriseChatParams.class);
                String[] listUserIds = requestEnterpriseChatParams.getUserIds().split(";");

                function.onCallBack("未确定用哪家的IM");
            }

        });
    }

    /**
     * 打开个人信息页
     */
    private void reqOpenUserProfile(){
        mWebView.registerHandler("reqOpenUserProfile", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqOpenUserProfile, data from web = " + data);
                RequestUserProfileParams requestUserProfileParams= new RequestUserProfileParams();
                requestUserProfileParams = new Gson().fromJson(data,RequestUserProfileParams.class);
                // String[] listUserIds = requestEnterpriseChatParams.getUserIds().split(";");
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("type",requestUserProfileParams.getType());
                intent.putExtra("userid",requestUserProfileParams.getUserid());
                mContext.startActivity(intent);
                ResultTemp resultTemp = new ResultTemp();
                resultTemp.setResponseCode(ModleConfig.RES200);
                resultTemp.setResponseMsg(ModleConfig.RES_SUCCESS);
                function.onCallBack(new Gson().toJson(resultTemp));
            }

        });
    }

    /**
     * 获取联系人
     */
    private void reqSelectEnterpriseContact(){
        mWebView.registerHandler("reqSelectEnterpriseContact", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqSelectEnterpriseContact, data from web = " + data);
                h5Data=  data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_CONTACTS,PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS);
            }

        });
    }


    /**
     * 预览图片
     */
    private void reqPreviewImage(){
        mWebView.registerHandler("reqPreviewImage", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqPreviewImage, data from web = " + data);
                h5Data = data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_STORAGE,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE);


            }

        });
    }

    /**
     * 选择照片
     */
    private void reqChooseImage() {
        mWebView.registerHandler("reqChooseImage", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqChooseImage, data from web = " + data);


                h5Data=  data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_STORAGE,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_CHOOSEIMAGE);

            }
        });
    }

    /**
     * 保存图片到相册
     */
    private void reqSaveImageToPhotosAlbum(){
        mWebView.registerHandler("reqSaveImageToPhotosAlbum", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqSaveImageToPhotosAlbum, data from web = " + data);
                h5Data = data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_STORAGE,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM);
            }
        });
    }

    /**
     * 获取图片信息
     */
    private void reqGetImageInfo(){

        mWebView.registerHandler("reqGetImageInfo", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqGetImageInfo, data from web = " + data);
                callBackFunction = function;
                h5Data = data;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_STORAGE,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO);
            }
        });
    }

    /**
     * 横竖屏幕切换
     */
    private void reqChangeScreen(){
        mWebView.registerHandler("reqChangeScreen", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqChangeScreen, data from web = " + data);
                /**
                 * int ORIENTATION_PORTRAIT = 1;  竖屏
                 * int ORIENTATION_LANDSCAPE = 2; 横屏
                 */
                //获取屏幕的方向  ,数值1表示竖屏，数值2表示横屏
                int screenNum = mContext.getResources().getConfiguration().orientation;
                //（如果竖屏）设置屏幕为横屏
                if (screenNum == 1) {
                    ( (Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //设置为置屏幕为竖屏
                } else {
                    ( (Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
    }

    /**
     * 获取网络状态
     */
    private void reqGetNetworkType() {

        mWebView.registerHandler("reqGetNetworkType", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqGetNetworkType, data from web = " + data);
                ResultNetworkStatus resultNetworkStatus = new ResultNetworkStatus();
                NetworkStatusEntity networkStatusEntity = new NetworkStatusEntity();
                boolean isNetWorkAvilable =  JUtils.isNetWorkAvilable();

                resultNetworkStatus.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
                resultNetworkStatus.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_MSG_NETWORKSTATUS_SUC);

                networkStatusEntity.setNetworkAvailable(isNetWorkAvilable);
                String type  = "";
                switch (JUtils.getNetworkState(mContext)){
                    case 1:
                        type = "WWAN";
                        break;
                    case 2:
                        type = "2G";
                        break;

                    case 3:
                        type = "3G";
                        break;

                    case 4:
                        type = "4G";
                        break;

                    default:
                        type = "UNKNOWN ";
                        break;
                }

                if(TextUtils.isEmpty(isNetWorkReachable(mContext))){
                    type = "NOTREACHABLE ";
                }
               /* if(!JUtils.checkOnlineState(mContext)){
                    type = "NOTREACHABLE";
                }*/

                if(isNetWorkAvilable){
                    networkStatusEntity.setType(type);
                }else{
                    networkStatusEntity.setType(type);
                }
                resultNetworkStatus.setResponseResult(networkStatusEntity);

                String str2 = new Gson().toJson(resultNetworkStatus);
                JUtils.Log("call back");
                function.onCallBack(str2);
            }

        });
    }

    /**
     * 打电话
     */
    private void reqMakePhoneCall() {
        mWebView.registerHandler("reqMakePhoneCall", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqMakePhoneCall, data from web = " + data);

                callBackFunction = function;
                h5Data = data;
                //华为手机在禁止拨打电话权限后，拨打电话时，有拨打电话的系统权限，但是不让打电话，log显示not startActivity****。 所以对华为手机改为跳转到拨号界面
                if(JUtils.getDeviceBrand().contains(Brand.HUAWEI) || JUtils.getDeviceBrand().contains(Brand.HUAWEI.toLowerCase())){
                    startDialActivity();
                }else{
                    checkPermission((Activity)mContext,PermissionNameList.PERMISSIONS_PHONE,PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE);

                }
            }
        });
    }

    /**
     *  //获取设备信息
     */
    private void reqGetSystemInfo() {

        mWebView.registerHandler("reqGetSystemInfo", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqGetSystemInfo, data from web = " + data);
                ResultSystemInfo resultSystemInfo = new ResultSystemInfo();
                SystemInfoEntity systemInfoEntity = new SystemInfoEntity();
                systemInfoEntity.setModel(JUtils.getSystemModel());
                systemInfoEntity.setPixelRatio(JUtils.getPixelRatio());
                systemInfoEntity.setWindowHeight(JUtils.getWindowHeight(mContext));
                systemInfoEntity.setWindowWidth(JUtils.getWindowWidth(mContext));
                systemInfoEntity.setLanguage(JUtils.getSystemLanguage());
                systemInfoEntity.setVersion(JUtils.getAppVersionCode()+"");
                systemInfoEntity.setStorage(JUtils.readSystemStorage()+"KB");
                systemInfoEntity.setCurrentBattery(JUtils.getSharedPreference().getString(StrUtil.BATTYER_LEVEL,""));
                systemInfoEntity.setSystem(JUtils.getSystemVersion());
                systemInfoEntity.setPlatform("Android");

                systemInfoEntity.setScreenHeight(JUtils.getScreenHeight());
                systemInfoEntity.setScreeWidth(JUtils.getScreenWidth());
                systemInfoEntity.setBrand(JUtils.getDeviceBrand());
                systemInfoEntity.setFontSizeSetting(JUtils.getFontSize(mContext));

                resultSystemInfo.setResponseResult(systemInfoEntity);
                resultSystemInfo.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
                resultSystemInfo.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_SUCCESS);
                String str2 = new Gson().toJson(resultSystemInfo);
                function.onCallBack(str2);

            }

        });
    }


    private void reqDatePicker() {
        mWebView.registerHandler("reqDatePicker", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqDatePicker, data from web = " + data);
                ReuquestDateParams dateEntity = new Gson().fromJson(data,ReuquestDateParams.class);

                //final CallBackFunction f = function;
                int mode= DateDialog.MODE_2;
                if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-dd")){
                    mode = DateDialog.MODE_2;
                }else if(dateEntity.getFormat().equalsIgnoreCase("HH:mm")){
                    mode = DateDialog.MODE_3;
                }else if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-dd HH:mm")){
                    mode = DateDialog.MODE_1;
                }else if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-dd HH:mm:ss")) {
                    mode = DateDialog.MODE_5;
                }
                else{
                    mode = DateDialog.MODE_2;
                }
                JUtils.Log("currentDate2:"+dateEntity.getCurrentDate());
                final   CallBackFunction f = function;
                final ResultDate resultDate = new ResultDate();
                final int finalMode = mode;
                DateDialog dateDialog = new DateDialog(mContext, "时间日期", mode, dateEntity.getCurrentDate(), dateEntity.getStartDate(), dateEntity.getEnzzate(),
                        new DateDialog.InterfaceDateDialog() {
                            @Override
                            public void getTime(String dateTime)  {
                                if (TextUtils.isEmpty(dateTime)) {
                                    resultDate.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);

                                    resultDate.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_FAIL);
                                    resultDate.setResponseResult("");
                                } else {

                                    resultDate.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);

                                    resultDate.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_SUCCESS);

                                    if(DateDialog.MODE_3 == finalMode){
                                        resultDate.setResponseResult(dateTime);
                                    }else{
                                        resultDate.setResponseResult( Long.toString(DateConfig.convertTimeToLong2(dateTime, finalMode)));
                                    }
                                }
                                String str2 = new Gson().toJson(resultDate);
                                // final JSONObject jsresult = new JSONObject(new Gson().toJson(result));
                                f.onCallBack(str2);
                            }
                        });
                dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dateDialog.show();
            }
        });
    }


    /**
     *  二维码 条码扫描
     */
    private void reqScan() {


        mWebView.registerHandler("reqScan", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqScan, data from web = " + data);
                //RequestScanParams scanEntity = new Gson().fromJson(data,RequestScanParams.class);
                h5Data = data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_SCAN,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SCAN);

            }
        });
    }

    private void saveImage(String localPath){
        ResultSaveImage resultSaveImage = new ResultSaveImage();

        //String localPath = "/sdcard/jpg.jpg";
        Bitmap bmp = BitmapFactory.decodeFile(localPath);
        if(bmp!=null){
            String filepath = "";
            filepath = ImgUtil.saveImageToGallery(mContext,bmp);
            if(!filepath.equals("")){
                resultSaveImage.setResponseCode(ModleConfig.RES200);
                resultSaveImage.setResponseMsg(ModleConfig.RES_SUCCESS);
                resultSaveImage.setResponseResult(filepath);
            }else{
                resultSaveImage.setResponseCode(ModleConfig.RES404);
                resultSaveImage.setResponseMsg(ModleConfig.RES_FAIL);
                resultSaveImage.setResponseResult("文件保存失败");
            }
        }else{
            JUtils.Log("bmp is null");
            resultSaveImage.setResponseCode(ModleConfig.RES404);
            resultSaveImage.setResponseMsg(ModleConfig.RES_FAIL);
            resultSaveImage.setResponseResult("文件不存在");
        }
        String str = new Gson().toJson(resultSaveImage);
        callBackFunction.onCallBack(str);
    }


    /**
     * 获取一次地理位置信息
     */
    private void reqGetLocation() {
        //获取一次地理位置信息。 getLocation
        mWebView.registerHandler("reqGetLocation", new   BridgeHandler() {

            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqGetLocation, data from web = " + data);
                h5Data=  data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_LOCATION,PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION);
            }

        });
    }

    /**
     * 停止定位
     */
    private void reqStopAutoLBS() {

        //停止定位。 stopAutoLBS
        mWebView.registerHandler("reqStopAutoLBS", new   BridgeHandler() {




            @Override
            public void handler(String data,   CallBackFunction function) {
                JUtils.Log("handler = reqStopAutoLBS, data from web = " + data);

                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_LOCATION,PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION);

            }

        });
    }


    private void checkPermission(Activity activity, @NonNull String[] permissions, @NonNull int reqCode) {
        //Build.VERSION_CODES.M

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            switch (reqCode) {
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION:
                    handleGetLocation();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS:
                    handleSelectEnterpriseContact();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE:
                    handleMakePhoneCall();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION:
                    handleStartAutoLBS();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION:
                    handleStopAutoLBS();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO:
                    handleGetImageInfo();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE:
                    handlePreviewImage();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM:
                    handleSaveImageToPhotosAblum();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_CHOOSEIMAGE:
                    handleChooseImage();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SCAN:
                    handleScan();
                    break;
                default:
                    break;
            }
        } else {
            if (PermissionsUtil.hasPermission(mContext, permissions)) {
                JUtils.Log("checkPermission: have the permission");
                switch (reqCode) {
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION:
                        handleGetLocation();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS:
                        handleSelectEnterpriseContact();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE:
                        handleMakePhoneCall();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION:
                        handleStartAutoLBS();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION:
                        handleStopAutoLBS();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO:
                        handleGetImageInfo();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE:
                        handlePreviewImage();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM:
                        handleSaveImageToPhotosAblum();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_CHOOSEIMAGE:
                        handleChooseImage();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SCAN:
                        handleScan();
                        break;
                    default:
                        break;

                }
            } else {

                ActivityCompat.requestPermissions((Activity) mContext, permissions, reqCode);
                JUtils.Log("have no permissions");
            }
        }
    }

    private void handleSelectEnterpriseContact() {

        //JUtils.Toast("联系人暂无");
        RequestContactsParams requestContactsParams = new RequestContactsParams();
        requestContactsParams = new Gson().fromJson(h5Data,RequestContactsParams.class);
        String str2 ;//= new Gson().toJson(resultToken);
        ResultContact resultContact = new ResultContact();
        if(TextUtils.isEmpty(requestContactsParams.getContacts())){
            JUtils.Log("404");
            resultContact.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
            resultContact.setResponseMsg("输入参数为空");
            resultContact.setResponseResult(null);
            str2 =  new Gson().toJson(resultContact);
            callBackFunction.onCallBack(str2);
            return;
        }else if(requestContactsParams.getContacts().equals(com.jdjz.weexlib.weex.modle.ModleConfig.RESMULIT)){
            JUtils.Log("RESMULIT");
            Intent intent = new Intent(mContext, ContactListActivity.class);
            intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_MULTI);
            mContext.startActivity(intent);


        }else if(requestContactsParams.getContacts().equals(com.jdjz.weexlib.weex.modle.ModleConfig.RESSINGLE)){
            JUtils.Log("RESSINGLE");
            Intent intent = new Intent(mContext, ContactListActivity.class);
            intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_SINGLE);
            mContext.startActivity(intent);
        }

    }

    private void handleGetLocation() {
        RequestLBSParams requestLBSParams = new Gson().fromJson(h5Data,RequestLBSParams.class);
        ResultLBS resultLBS = new ResultLBS();
        responseTypeLBS = 0;
        lbs_type = requestLBSParams.getType();
        if(lbs_type.equals("1") ||lbs_type.equals("2") ||lbs_type.equals("3")){
            ;
        }else{
            lbs_type="0";
        }
        resetOption(true);
        startLocation();
    }


    /**
     * 直接打电话
     */
    private void handleMakePhoneCall() {
        RequestPhoneParams jsParams = new Gson().fromJson(h5Data,RequestPhoneParams.class);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:"+jsParams.getPhone()));
        mContext.startActivity(intent);
    }

    private void handleStartAutoLBS() {
        RequestLBSWGS84_GCJ02Params requestLBSWGS84_gcj02Params = new Gson().fromJson(h5Data, RequestLBSWGS84_GCJ02Params.class);
        responseTypeLBS = 1;
        resetOption(false);
        startLocation();
    }

    private void handleStopAutoLBS() {
        ResultTemp resultTemp = new ResultTemp();
        String str2 = "";
        if(locationClient!=null){
            stopLocation();
            resultTemp.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
            resultTemp.setResponseMsg("停止定位成功");
            str2 = new Gson().toJson(resultTemp);
            callBackFunction.onCallBack(str2);
        }else{
            resultTemp.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
            resultTemp.setResponseMsg("停止定位前先开启定位");
            str2 = new Gson().toJson(resultTemp);
            callBackFunction.onCallBack(str2);
        }
    }

    private void handleGetImageInfo() {
        RequestGetImageInfoParams requestGetImageInfoParams = new RequestGetImageInfoParams();
        requestGetImageInfoParams = new Gson().fromJson(h5Data,RequestGetImageInfoParams.class);
        //saveImage(requestGetImageInfoParams.getSrc());
        getImageInfo(requestGetImageInfoParams.getSrc());
        //requestStorage(requestGetImageInfoParams.getSrc(),RequestPermissonType.REQ_GET_IMAGE_INFO);
    }

    private void handlePreviewImage() {

        RequestPreviewImagesParams requestPreviewImagesParams = new RequestPreviewImagesParams();
        requestPreviewImagesParams = new Gson().fromJson(h5Data,RequestPreviewImagesParams.class);
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        List<PreviewImagesData> datas = new ArrayList<>();

        for(int i=0;i<requestPreviewImagesParams.getUrls().size();i++){
            PreviewImagesData previewImagesData = new PreviewImagesData();
            JUtils.Log(requestPreviewImagesParams.getUrls().get(i));

            previewImagesData.setOriginUrl(requestPreviewImagesParams.getUrls().get(i));
            previewImagesData.setThumbnailUrl(requestPreviewImagesParams.getUrls().get(i));
            datas.add(previewImagesData);
        }
        PreviewImagesData current = new PreviewImagesData();
        current.setOriginUrl(requestPreviewImagesParams.getCurrent());
        current.setThumbnailUrl(requestPreviewImagesParams.getCurrent());

        int i=0;
        int currentNumber=0;
        List<Image> images = new ArrayList<>();
        for (PreviewImagesData d : datas) {
            Image image = new Image();
            image.setOriginUrl(d.getOriginUrl());
            image.setThumbnailUrl(d.getThumbnailUrl());
            images.add(image);
            i++;
            if(d.getOriginUrl().equals(current.getOriginUrl())){
                JUtils.Log("*** list 中找到了这个current image：是第"+i+"个");
                currentNumber = i-1;
            }
        }

        Preview.with(mContext)
                .builder()
                .load(images)
                .displayCount(true)
                .markPosition(currentNumber)
                .showDownload(false)
                .showOriginImage(false)
                .downloadLocalPath("Preview")
                .show();


    }

    private void handleSaveImageToPhotosAblum() {
        RequestSaveImg requestSaveImg = new RequestSaveImg();
        requestSaveImg = new Gson().fromJson(h5Data,RequestSaveImg.class);
        saveImage(requestSaveImg.getFilePath());
        //requestStorage(requestSaveImg.getFilePath(),RequestPermissonType.REQ_SAVE_IMAGE_TO_PHOTO_ALBUM);
    }

    private void handleChooseImage(){
        RequestChooseImagesParams requestChooseImagesParams = new RequestChooseImagesParams();
        requestChooseImagesParams = new Gson().fromJson(h5Data,RequestChooseImagesParams.class);

        int mCompress = 2;//0->origin; 1->compressed; 2->两者都有
        Boolean isCamera = true;
        if(requestChooseImagesParams.getSourceType().size()==2){
            mCompress = 2;
            JUtils.Log("mCompress = 2");
        }else if(requestChooseImagesParams.getSourceType().size()==1){
            if(requestChooseImagesParams.getSourceType().get(0).equals("original")){
                mCompress = 0;
                JUtils.Log(" mCompress = 0");
            }else if(requestChooseImagesParams.getSourceType().get(0).equals("compressed")){
                mCompress = 1;
                JUtils.Log("mCompress = 1");
            }
        }

        if(requestChooseImagesParams.getSizeType().size()==2){
            isCamera = true;
            JUtils.Log("isCamera is true 2");
        }else if(requestChooseImagesParams.getSizeType().size()==1){
            if(requestChooseImagesParams.getSizeType().get(0).equals("album")){
                isCamera = false;
                JUtils.Log("isCamera is false");
            }else if(requestChooseImagesParams.getSizeType().get(0).equals("camera")){
                isCamera = true;
                JUtils.Log("isCamera is true");
            }
        }

        PhotoPicker.builder()
                .setPhotoCount(requestChooseImagesParams.getCount())
                .setGridColumnCount(4)
                .setCompress(mCompress)
                .setShowCamera(isCamera)
                .start(mContext);
    }

    private void handleScan() {
        RequestScanParams scanEntity = new Gson().fromJson(h5Data,RequestScanParams.class);
        ResultScan resultScan = new ResultScan();
        Intent intent = new Intent(mContext, CaptureActivity.class);
        mContext.startActivity(intent);
    }


    /**
     * 判断网络是否可达
     * @param context
     * @return
     */
    public  String  isNetWorkReachable(Context context) {
        String netAddress = null;
        try
        {
            netAddress = new NetTask().execute("www.baidu.com").get();//"http://www.fsa111f.com/"
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        JUtils.Log("百度的IP:"+netAddress);
        return netAddress;
    }


    public class NetTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            InetAddress addr = null;
            try
            {
                addr = InetAddress.getByName(params[0]);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            if(addr != null){
                return  addr.getHostAddress();
            }
            return "" ;
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ContactEvent event) {
        ArrayList<ContactInfo> contactInfos = event.getContactInfos();
        ResultContacts resultContacts = new ResultContacts();
        resultContacts.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
        resultContacts.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_SUCCESS);
        resultContacts.setResponseResult(contactInfos);
        callBackFunction.onCallBack(new Gson().toJson(resultContacts));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TransScanEntity transScanEntity) {
        ResultScan resultScan = new ResultScan();
        ScanEntity scanEntity = new ScanEntity();
        if(transScanEntity ==null){
            resultScan.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
            resultScan.setResponseMsg("Scan:"+ com.jdjz.weexlib.weex.modle.ModleConfig.RES_FAIL);
            resultScan.setResponseResult(null);
            String str2 = new Gson().toJson(resultScan);
            callBackFunction.onCallBack(str2);

        }else{
            if(transScanEntity.isSuccess()){
                resultScan.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
                resultScan.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_SUCCESS);

                /*if(transScanEntity.isType()){  //true qcode
                    scanEntity.setCode(transScanEntity.getResult());
                    scanEntity.setBarCode("");
                    scanEntity.setQrCode(transScanEntity.getResult());
                    resultScan.setResponseResult(scanEntity);
                }else{
                    scanEntity.setCode(transScanEntity.getResult());
                    scanEntity.setBarCode(transScanEntity.getResult());
                    scanEntity.setQrCode("");
                    resultScan.setResponseResult(scanEntity);
                }*/
                scanEntity.setCode(transScanEntity.getResult());
                //scanEntity.setBarCode("");
                //scanEntity.setQrCode(transScanEntity.getResult());
                resultScan.setResponseResult(scanEntity);
                String str2 = new Gson().toJson(resultScan);
                callBackFunction.onCallBack(str2);

            }else{
                resultScan.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
                resultScan.setResponseMsg(transScanEntity.getResult());

                resultScan.setResponseResult(scanEntity);
                String str2 = new Gson().toJson(resultScan);
                callBackFunction.onCallBack(str2);
            }
        }
    }

    /**
     * 选择照片后返回值
     * @param file
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(List<File> file) {
        ResultChooseImages responseResult = new ResultChooseImages();
        ChooseImagesFileInfoEntity chooseImagesFileInfoEntity = new ChooseImagesFileInfoEntity();
        List<String> tempFilePaths = new ArrayList<>();
        List<TempFile> tempFiles = new ArrayList<>();
        for(int i=0; i<file.size();i++){
            TempFile tempFile = new TempFile();
            Log.i("tchl","selectedPhotos:"+file.get(i).getAbsolutePath());
            tempFilePaths.add(file.get(i).getAbsolutePath());

            tempFile.setPath(file.get(i).getAbsolutePath());
            tempFile.setSize(file.get(i).length());
            tempFiles.add(tempFile);

        }
        chooseImagesFileInfoEntity.setTempFilePaths(tempFilePaths);
        chooseImagesFileInfoEntity.setTempFiles(tempFiles);

        responseResult.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
        responseResult.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_SUCCESS);
        responseResult.setResponseResult(chooseImagesFileInfoEntity);
        String str2 = new Gson().toJson(responseResult);
        callBackFunction.onCallBack(str2);

    }

    /**
     * 申请地理位置权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始定位。
     * @param locationEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LocationEvent locationEvent) {
        handleGetLocation();
    }

    /**
     * 申请联系人权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始获取联系人。
     * @param contactsEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ContactsEvent contactsEvent) {
        handleSelectEnterpriseContact();
    }

    /**
     * 申请联系人权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始获取联系人。
     * @param phoneEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PhoneEvent phoneEvent) {
        handleMakePhoneCall();
    }

    /**
     * 申请自动获取地理位置权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始获取自动获取地理位置。
     * @param startAutoLBSEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StartAutoLBSEvent startAutoLBSEvent) {
        handleStartAutoLBS();
    }

    /**
     * 申请停止自动获取地理位置权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始停止自动获取地理位置。
     * @param stopAutoLBSEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StopAutoLBSEvent stopAutoLBSEvent) {
        handleStopAutoLBS();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PreviewImageEvent previewImageEvent) {
        handlePreviewImage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SaveImageToPhotosAlbumEvent saveImageToPhotosAlbumEvent) {
        handleSaveImageToPhotosAblum();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GetImageInfoEvent getImageInfoEvent) {
        handleGetImageInfo();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChooseImagesFileInfoEntity event) {
        handleChooseImage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ScanEvent event) {
        handleScan();
    }



    /**
     * 跳到拨号界面
     */
    private void startDialActivity(){
        RequestPhoneParams jsParams = new Gson().fromJson(h5Data,RequestPhoneParams.class);
        JUtils.Log("phone:"+jsParams.getPhone());
        Intent intentNO = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+jsParams.getPhone()));
        mContext.startActivity(intentNO);
    }

    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(mContext);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }


    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            ResultLBS resultLBS = new ResultLBS();
            LBSEntity lbsEntity = new LBSEntity();
            Street street = new Street();
            String str2="";
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    //sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

                    resultLBS.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
                    resultLBS.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_SUCCESS);


                    lbsEntity.setLongitude(location.getLongitude()+"" );  //经度
                    lbsEntity.setLatitude(location.getLatitude()+"");  //纬度

                    lbsEntity.setAccuracy(location.getAccuracy()+"");   //精确度，单位m
                    lbsEntity.setHorizontalAccuracy("");//水平精确度，单位m   高德地图不能获得

                    if(Integer.parseInt(lbs_type)>0){
                        lbsEntity.setCountry(location.getCountry());
                        lbsEntity.setCountryCode("");//国家码另外写代码
                        lbsEntity.setProvince(location.getProvince());
                        lbsEntity.setCity(location.getCity());
                        lbsEntity.setCityAdcode(location.getCityCode());
                        lbsEntity.setDistrict(location.getDistrict());
                        lbsEntity.setDistrictAdcode(location.getAdCode());
                    }

                    //设置街道信息
                    if(Integer.parseInt(lbs_type)>1) {
                        street.setNumber(location.getStreetNum());
                        street.setStreet(location.getStreet());
                        lbsEntity.setStreetObject(street);
                    }

                    //设置pois
                    if(Integer.parseInt(lbs_type)>2){
                        lbsEntity.setPois(location.getPoiName());
                    }
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");

                    if(responseTypeLBS==0) {  //返回给getLocation()
                        resultLBS.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
                        resultLBS.setResponseMsg("[A"+location.getErrorCode()+"]" + location.getLocationDetail());
                    }else if(responseTypeLBS==1){
                        resultLBS.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
                        resultLBS.setResponseMsg("[A"+location.getErrorCode()+"]" + location.getLocationDetail());
                        //NativeToJSstartAutoLBS();
                    }
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
                //sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                // sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
                JUtils.Log(sb.toString());
                //解析定位结果，
                if(responseTypeLBS==0){  //返回给getLocation()

                    String result = sb.toString();
                    stopLocation();
                    resultLBS.setResponseResult(lbsEntity);
                    str2 = new Gson().toJson(resultLBS);
                    callBackFunction.onCallBack(str2);
                }else if(responseTypeLBS==1){
                    JUtils.Log("call ativeToJSstartAutoLBS(resultLBS);");
                    resultLBS.setResponseResult(lbsEntity);
                    NativeToJSstartAutoLBS(resultLBS);
                }

            } else {
                if(responseTypeLBS==0) {  //返回给getLocation()
                    stopLocation();
                    JUtils.Log("定位失败，loc is null");
                    resultLBS.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
                    resultLBS.setResponseMsg("定位失败");
                    resultLBS.setResponseResult(null);
                    str2 = new Gson().toJson(resultLBS);
                    callBackFunction.onCallBack(str2);
                }else if(responseTypeLBS==1){
                    resultLBS.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
                    resultLBS.setResponseMsg("定位失败");
                    resultLBS.setResponseResult(null);
                    NativeToJSstartAutoLBS(resultLBS);
                }

            }

        }
    };



    /**
     * 回调 NativeToJSstartAutoLBS, 返回信息为html
     */
    private void NativeToJSstartAutoLBS(final ResultLBS resultLBS){
        mWebView.callHandler("NativeToJSstartAutoLBS", new Gson().toJson(resultLBS), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

                //Toast.makeText(mContext, "网页在获取你的位置!!!，"+ data, Toast.LENGTH_SHORT).show();
                JUtils.Log("NativeToJSstartAutoLBS"+data);
            }
        });
    }


    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    //重新设置定位参数
    private void resetOption(boolean isOnceLocation) {
        if(isOnceLocation){
            locationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        }else{
            locationOption.setOnceLocation(false);
        }
    }


    private  void  getImageInfo(String src){
        ResultGetImageInfo resultGetImageInfo = new ResultGetImageInfo();
        GetImageInfoEntity getImageInfoEntity = new GetImageInfoEntity();
        BitmapFactory.Options options = new BitmapFactory.Options();

        if(!isExist(src)){

            resultGetImageInfo.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES404);
            resultGetImageInfo.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_FAIL);
            resultGetImageInfo.setResponseResult(null);
        }else{
            /**
             * 最关键在此，把options.inJustDecodeBounds = true;
             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
             */

            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(src, options); // 此时返回的bitmap为null
            /**
             *options.outHeight为原始图片的高
             */
            //return new String[]{String.valueOf(options.outWidth) ,String.valueOf(options.outHeight),src};
            JUtils.Log("width:"+options.outWidth+"  height:"+ options.outHeight);
            getImageInfoEntity.setHeight(options.outHeight);
            getImageInfoEntity.setWidth(options.outWidth);
            getImageInfoEntity.setPath(src);
            resultGetImageInfo.setResponseCode(com.jdjz.weexlib.weex.modle.ModleConfig.RES200);
            resultGetImageInfo.setResponseMsg(com.jdjz.weexlib.weex.modle.ModleConfig.RES_SUCCESS);
            resultGetImageInfo.setResponseResult(getImageInfoEntity);
        }

        String str = new Gson().toJson(resultGetImageInfo);
        callBackFunction.onCallBack(str);
    }



    /**
     * 判断文件是否存在
     *
     * @param path
     */
    private Boolean isExist(String path) {
        File file = new File(path);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            return false;
        }
        return  true;
    }


    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                StringBuilder sb = new StringBuilder();
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                JUtils.getSharedPreference().edit().putString(StrUtil.BATTYER_LEVEL,String.valueOf(level)+"%").apply();
                sb.append("The phone");
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                    sb.append("'s battery feels very hot!");
                } else {
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            sb.append("no battery.");
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            sb.append("'s battery");
                            if (level <= 33)
                                sb.append(" is charging, battery level is low" + "[" + level + "]");
                            else if (level <= 84) sb.append(" is charging." + "[" + level + "]");
                            else sb.append(" will be fully charged.");
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            if (level == 0) sb.append(" needs charging right away.");
                            else if (level > 0 && level <= 33)
                                sb.append(" is about ready to be recharged, battery level is low" + "[" + level + "]");
                            else sb.append("'s battery level is" + "[" + level + "]");
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            sb.append(" is fully charged.");
                            break;
                        default:
                            sb.append("'s battery is indescribable!");
                            break;
                    }
                }
                sb.append(' ');
                JUtils.Log(sb.toString());
                //batterLevel.setText(sb.toString());


            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }
}
