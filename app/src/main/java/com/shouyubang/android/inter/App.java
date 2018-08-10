package com.shouyubang.android.inter;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.shouyubang.android.inter.presenters.InitBusinessHelper;

import java.util.List;

/**
 * Created by KunLiu on 2017/7/20.
 */

public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Context context = getApplicationContext();

        if (shouldInit()) {
            //初始化APP
            InitBusinessHelper.initApp(context);
        }

    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();

        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static Context getContext() {
        return app.getApplicationContext();
    }

    public static App getInstance() {
        return app;
    }
}
