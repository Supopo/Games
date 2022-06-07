package com.xxx.games.app;

import android.content.Context;

import com.tencent.mmkv.MMKV;
import com.xxx.games.BuildConfig;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.utils.KLog;

public class MApplication extends BaseApplication {

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
    }
}
