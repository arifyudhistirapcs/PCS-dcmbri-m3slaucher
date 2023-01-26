package id.co.pcsindonesia.ia.diagnostic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import id.co.pcsindonesia.ia.diagnostic.helper.GlobalHelper;
import id.co.pcsindonesia.ia.diagnostic.helper.HttpsTrustManager;

import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;
import com.sunmi.pay.hardware.aidlv2.tax.TaxOptV2;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.card.CardInfoEntity;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private static boolean isInterestingActivityVisible;
    private static boolean isActivityPaused;

    public static DriverManager sDriverManager;
    public  static CardInfoEntity cardInfoEntity;
    private static Context context;
    private static App mInstance;
    public static final String TAG = App.class.getSimpleName();
    private RequestQueue mRequestQueue;
    public static IDAL idal = null;

    public static BasicOptV2 mBasicOptV2;       // 获取基础操作模块
    public static ReadCardOptV2 mReadCardOptV2; // 获取读卡模块
    public static PinPadOptV2 mPinPadOptV2;     // 获取PinPad操作模块
    public static SecurityOptV2 mSecurityOptV2; // 获取安全操作模块
    public static EMVOptV2 mEMVOptV2;           // 获取EMV操作模块
    public static TaxOptV2 mTaxOptV2;           // 获取税控操作模块
    public static boolean isSunmiConnected = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance  = this;
        context = getApplicationContext();
        registerActivityLifecycleCallbacks(this);

        if(getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_A920) ||
                getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90)){
            sDriverManager = DriverManager.getInstance();
            cardInfoEntity = new CardInfoEntity();
            try {
                idal = NeptuneLiteUser.getInstance().getDal(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HttpsTrustManager.handleSSLHandshake();
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public static Context getContext(){
        return context;
    }

    public static String getVersion(){
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getDeviceModel(){
        return android.os.Build.MODEL;
    }

    public static String getAppPackageName(){
        return context.getPackageName();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static IDAL getIdal() {
        return idal;
    }

    public static boolean isActivityVisible() {
        return isInterestingActivityVisible;
    }

    public static boolean isIsActivityPaused(){
        return isActivityPaused;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e("APP","Activity Resumed");
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = true;
            isActivityPaused = false;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("APP","Activity Stopped");
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = false;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.e("APP","Activity Created");
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = true;
            isActivityPaused = false;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("APP","Activity Destroyed");
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = false;
            isActivityPaused = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("APP","Activity Paused");
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = false;
            isActivityPaused = true;
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("APP","Activity Started");
        if (activity instanceof MainActivity) {
            isInterestingActivityVisible = true;
            isActivityPaused = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }


}

