package id.co.pcsindonesia.ia.diagnostic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.WIFI_SERVICE;


public class GlobalHelper {

    public static final String BRILINK_PACKAGE = "id.co.bri.brilinkmobile";
    public static final String DECO_PACKAGE = "id.co.pcsindonesia.ia.dcm";
    public static final String APPSTORE_PACKAGE = "woyou.market";

    public static final SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd-MM-yyyy");

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final String NETWORK_STATUS_NOT_CONNECTED = "Not Connected";
    public static final String NETWORK_STATUS_WIFI = "Wifi";
    public static final String NETWORK_STATUS_MOBILE = "Mobile";



    public static String getAppVersion(Context context){
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getAppVersionByPackege(Context context, String packageName){
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getDeviceModel(){
        return Build.MODEL;
    }

    public static String getAppPackageName(Context context){
        return context.getPackageName();
    }

    public static String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getSN(){
        String sn = "";
        if(App.isSunmiConnected) {
            BasicOptV2 basicOptV2 = App.mBasicOptV2;
            try {
                sn = basicOptV2.getSysParam(AidlConstantsV2.SysParam.SN);
            } catch (Exception e) {
                sn = "";
            }
        }
        return sn;
    }

    public static String getAppsVersion(Context context, String packageName){
        String version = "-";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return version;
    }

    public static long getAppsVersionCode(Context context, String packageName){
        long version = 0;
        String temp = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                temp = pInfo.versionName; // avoid huge version numbers and you will be ok
            } else {
                //noinspection deprecation
                temp = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = Long.parseLong(temp.replace(".", ""));

        return version;
    }

    public static int getWifiStatus(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean macAddress = wimanager.isWifiEnabled();
        if (macAddress) {
            return 1;
        }else{
            return 0;
        }
    }

    public static String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "";
        }
        return macAddress;
    }

    public static int getWifiSignal(Context context){
        int signal = -100;
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        signal = wimanager.getConnectionInfo().getRssi();
        return signal;
    }

    public static int getSimSignal(Context context){
        int signal = -127;
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED ) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String signals = "";
            try{
                CellInfoGsm cellInfoGsm = (CellInfoGsm) tm.getAllCellInfo().get(0);
                CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                signals += String.valueOf(cellSignalStrengthGsm.getDbm());
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            try{
                CellInfoWcdma cellInfoGsm = (CellInfoWcdma) tm.getAllCellInfo().get(0);
                CellSignalStrengthWcdma cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                signals += String.valueOf(cellSignalStrengthGsm.getDbm());
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            try{
                CellInfoLte cellInfoGsm = (CellInfoLte) tm.getAllCellInfo().get(0);
                CellSignalStrengthLte cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                signals += String.valueOf(cellSignalStrengthGsm.getDbm());
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            try {
                signal = Integer.parseInt(signals);
            }catch (Exception e){
                e.printStackTrace();
            }
            return signal;
        }
        return -127;
    }

    @SuppressWarnings("deprecation")
    public String getIPAddress(Context context) {
        if(getConnectivityStatus(context) == id.co.pcsindonesia.ia.diagnostic.GlobalHelper.TYPE_MOBILE) {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            } catch (Exception ex) {
            } // for now eat exceptions
            return "";
        }else{
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return  Formatter.formatIpAddress(wifiInfo.getIpAddress());
        }
    }

    public static String getIMEI(Context context){
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED ) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        }
        return null;
    }

    public static String getICCID(Context context){
        String result = "-";
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED ) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            result = tm.getSimSerialNumber();
        }
        if(result == null) result = "-";
        return result;
    }

    public static String getPercentBattery(Context context){
        float batLevel = 0;
        BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        if(batLevel == 0){
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Error checking that probably isn't needed but I added just in case.
            if(level == -1 || scale == -1) {
                batLevel =  50.0f;
            }

            batLevel =  ((float)level / (float)scale) * 100.0f;
        }
        return batLevel+"";
    }

    public static void turnScreenOn(Context context){
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!manager.isInteractive()) {
                Log.e("ThreadService", "Wakelock: " + manager.isInteractive());
                PowerManager.WakeLock fullWakeLock = manager.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "myapp:mywakelocktag");
                fullWakeLock.acquire(); // turn on
                fullWakeLock.release();
            }
        }

    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_NETWORK_STATE ) == PackageManager.PERMISSION_GRANTED ) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = "";
        if (conn == TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI;
        } else if (conn == TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    public static SpannableString fileSizeShorter(String fileSize, boolean withSuffix){
        DecimalFormat decimalFormat = new DecimalFormat("#,###.#");
        Double fileSizeLong = Double.parseDouble(fileSize);
        Double finalSize=0.0;
        String suffix = "B";
        if(fileSizeLong >= 1024){
            if(fileSizeLong>= 1024*1024){
                if(fileSizeLong >=1024*1024*1024){
                    finalSize = fileSizeLong / (1024*1024*1024);
                    suffix = "GB";
                }else{
                    finalSize = fileSizeLong / (1024*1024);
                    suffix = "MB";
                }
            }else{
                finalSize = fileSizeLong / 1024;
                suffix = "KB";
            }
        }else{
            finalSize = fileSizeLong;
            suffix = "B";
        }
        if(withSuffix){
            SpannableString string = new SpannableString(decimalFormat.format(finalSize) + suffix);
            switch (suffix){
                case "GB":
                    string.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")),string.toString().indexOf(suffix),
                            string.toString().indexOf(suffix)+suffix.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case "MB":
                    string.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")),string.toString().indexOf(suffix),
                            string.toString().indexOf(suffix)+suffix.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case "KB":
                    string.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")),string.toString().indexOf(suffix),
                            string.toString().indexOf(suffix)+suffix.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    break;
                case "B":
                    string.setSpan(new ForegroundColorSpan(Color.parseColor("#0000FF")),string.toString().indexOf(suffix),
                            string.toString().indexOf(suffix)+suffix.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    break;

            }
            return string;
        }else{
            SpannableString string = new SpannableString(decimalFormat.format(finalSize));
            return string;
        }

    }

    public static boolean isPackageInstalled(String packageName, Context context) {
        boolean cek;
        PackageManager manager = context.getPackageManager();
        try {
            manager.getApplicationInfo(packageName, 0);
            cek = true;
        } catch (PackageManager.NameNotFoundException e) {
            cek = false;
        }
        return cek;
    }

    public static void deleteRecursive(String dir_path){
        File dir = new File(dir_path);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
            Log.e("GlobalHelper", "deleteRecursive; "+ children.length+" ; "+dir_path);
        }
    }

}
