package com.xxx.games.app;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.tencent.mmkv.MMKV;
import com.xxx.games.BuildConfig;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.utils.KLog;

public class MApplication extends BaseApplication {
    private final String TAG = "MApplication";
    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化MMKV
        MMKV.initialize(this);

        //代码位ID:949298664
        TTAdConfig ttAdConfig = new TTAdConfig.Builder()
                .appId(Constant.AD_APPID)
                .appName("玩什么")
                .useTextureView(true) //默认使用SurfaceView播放视频广告,当有SurfaceView冲突的场景，可以使用TextureView
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_NO_TITLE_BAR)//落地页主题
                .allowShowNotify(true) //是否允许sdk展示通知栏提示,若设置为false则会导致通知栏不显示下载进度
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                .supportMultiProcess(false) //是否支持多进程，true支持
                .asyncInit(true) //是否异步初始化sdk,设置为true可以减少SDK初始化耗时。3450版本开始废弃~~
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build();
        TTAdSdk.init(this, ttAdConfig, new TTAdSdk.InitCallback() {
            @Override
            public void success() {
                Log.e(TAG, "initCallback --> success！ ");
            }

            @Override
            public void fail(int i, String s) {
                Log.e(TAG, "initCallback --> onError: " + i + ", " + String.valueOf(s));
            }
        });
    }
}
