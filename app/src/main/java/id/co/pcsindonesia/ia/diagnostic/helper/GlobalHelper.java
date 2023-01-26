package id.co.pcsindonesia.ia.diagnostic.helper;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
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

import id.co.pcsindonesia.ia.diagnostic.App;
import com.pax.dal.ISys;
import com.pax.dal.entity.ETermInfoKey;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;
import com.zcs.sdk.Sys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.core.content.ContextCompat;

import static android.content.Context.BATTERY_SERVICE;

public class GlobalHelper {
    public static final String PASSWORD_ENCRYPT = "PCSIndonesiaForFutureTechnology";

    public static final String DEVICE_Z90 = "Z90";
    public static final String DEVICE_A920 = "A920";
    public static final String DEVICE_SUNMI_P1 = "P1_4G";

    public static final String REQUEST_PERTAMINA = "pertamina";
    public static final String REQUEST_PARAMS = "params";


    public static final SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd-MM-yyyy");

    public static final String PERTAMINA_FIELD_CONFIG = "config_content";
    public static final String PERTAMINA_FIELD_PARAM = "param_content";

    public static final String MESS_STATUS_SUCCESS = "Success";
    public static final String MESS_STATUS_ERROR = "Error";
    public static final String MESS_ERROR_GENERAL = "General error";
    public static final String MESS_ERROR_INPUT_DATABASE = "Error input to database";
    public static final String MESS_ERROR_FORMAT = "Format error";
    public static final String MESS_CANCEL_PAYMENT = "Cancel payment";
    public static final String MESS_PAPER_OUT = "Paper out";

    public static final String CODE_STATUS_SUCCESS = "00";
    public static final String CODE_ERROR_GENERAL = "01";
    public static final String CODE_ERROR_INPUT_DATABASE = "02";
    public static final String CODE_ERROR_FORMAT = "03";
    public static final String CODE_CANCEL_PAYMENT = "04";
    public static final String CODE_PAPER_OUT = "05";

    public static final String EXTERNAL_PCS_DIR = Environment.getExternalStorageDirectory().toString() + "/PCSApps/";
    public static final String EXTERNAL_DIR_FILES = Environment.getExternalStorageDirectory().toString() + "/PCSApps/files/";
    public static final String EXTERNAL_DIR_APPFILES = Environment.getExternalStorageDirectory().toString() + "/PCSApps/apps/";
    public static final String DIR_CONFIG = EXTERNAL_DIR_FILES+"/config/";
    public static final String DIR_PERTAMINA = EXTERNAL_DIR_FILES+"/pertamina/";
    public static final String DIR_APP_DATA = EXTERNAL_DIR_APPFILES+"/appdata/";
    public static final String APPDATA_FILENAME = "appdata.txt";
    public static final String DIR_LOCAL = EXTERNAL_DIR_FILES+"/pcs/";
    public static final String PARAMS_FILENAME = "params.txt";
    public static final String POT_PACKAGE = "com.pinsyst.outdoorterminal";

    public static final String DIR_TYPE_MEDIA = "media/";

    public static final String APPCONFIG_FILENAME= "app_config.txt";

    public static final int REQUEST_INSTALL = 700;
    public static final int REQUEST_UNINSTALL = 800;

    public static String getIconPath(){
        String path = EXTERNAL_DIR_FILES+DIR_TYPE_MEDIA;
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();
        return EXTERNAL_DIR_FILES+DIR_TYPE_MEDIA;
    }

    public static String makeResponse(String status, String detail, String message){
        try{
            JSONObject object = new JSONObject();
            object.put("status_code",status);
            object.put("status_detail",detail);
            object.put("message",message);
            return object.toString();
        }catch (JSONException e){
            return "Error Response: "+e.getMessage();
        }
    }

    public static String printGeneralCurrency(int amount){
        NumberFormat kurensiJepang = NumberFormat.getCurrencyInstance();
        Log.e("GlobalHelper",String.format("Harga Yen: %s %n", kurensiJepang.format(amount)));
        return kurensiJepang.format(amount);
    }

    public static String printCurrencyInt(double amount){
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(amount);
    }

    public static String printCurrency(double amount){
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(amount);
    }

    private String printCurrencyInt(String myAmount){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(Double.parseDouble(myAmount));
    }

    public static void killAppBypackage(Context context, String packageName){
        List<ApplicationInfo> packages;
        PackageManager pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = context.getPackageName();

        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1) {
                continue;
            }
            if(packageInfo.packageName.equals(myPackage)) {
                continue;
            }
            if(packageInfo.packageName.equals(packageName)) {
                mActivityManager.killBackgroundProcesses(packageInfo.packageName);
            }
        }
    }

    public static void generateFiles(Context context,String directory, String filename, String sBody) {
        try {
            if(directory.equalsIgnoreCase(GlobalHelper.DIR_CONFIG)) {
                DataHelper.deleteFile(context,directory);
            }
            File file = new File(directory,filename);
            if(file.exists())
                file.delete();
            File root = new File(directory);
            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root,filename);
            FileWriter writer = new FileWriter(gpxfile);
            writer.write(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateFiles(Context context,String directory, String filename, String sBody) {
        try {
            File file = new File(directory,filename);
            File root = new File(directory);
            if (!root.exists()) {
                root.mkdirs();
            }

            if(file.exists()){
                FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                writer.append(sBody);
                writer.close();
                fileOutputStream.close();
            }else {
                DataHelper.deleteFile(context,directory);
                File gpxfile = new File(root,filename);
                FileWriter writer = new FileWriter(gpxfile);
                writer.write(sBody);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStringFile(String directory, String filename){
        File file = new File(directory, filename);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=bufferedReader.readLine()) !=null){
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkFilesExist(String directory, String fileName){
        File file = new File(directory,fileName);
        return file.exists();
    }

    public static String getPertaminaFilename(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date())+".txt";
    }

    public static String getResponseFilename(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date())+".txt";
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
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                version = pInfo.getLongVersionCode(); // avoid huge version numbers and you will be ok
            } else {
                //noinspection deprecation
                version = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
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

    public static String getIPAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wimanager.getConnectionInfo().getIpAddress());
        if (ipAddress == null) {
            ipAddress = "";
        }
        return ipAddress;
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
                Log.e("GlobalHelper","Error parse signal: "+e.getMessage());
            }
            return signal;
        }
        return -127;
    }

    public static String getOtherInfo(Context context){
        String info = "";
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED ) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            info += "Signal getRssi : "+wimanager.getConnectionInfo().getRssi()+"; ";
            info += "Signal getLinkSpeed : "+wimanager.getConnectionInfo().getLinkSpeed()+"; ";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                info += "Signal getFrequency : " + wimanager.getConnectionInfo().getFrequency() + "; ";
            }
            info += "Signal getSSID : "+wimanager.getConnectionInfo().getSSID()+"; ";

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

            info += "Signal getSignalStrength : "+signals+"; ";
            return info;
        }
        return null;
    }

    public static String getIMEI(Context context){
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED ) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        }
        return null;
    }

    public static String getICCID(Context context){
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED ) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        }
        return null;
    }

    public static String getPercentBattery(Context context){
        float batLevel = 0;
        BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!manager.isInteractive()) {
                Log.e("ThreadService", "Wakelock: " + manager.isInteractive());
                PowerManager.WakeLock fullWakeLock = manager.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "myapp:mywakelocktag");
                fullWakeLock.acquire(); // turn on
                fullWakeLock.release();
            }
        }

    }

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final String NETWORK_STATUS_NOT_CONNECTED = "Not Connected";
    public static final String NETWORK_STATUS_WIFI = "Wifi";
    public static final String NETWORK_STATUS_MOBILE = "Mobile";
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
        PackageManager manager = context.getPackageManager();
        try {
            manager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void startApplication(Context context, String packageName) {
        try {
            GlobalHelper.killAppBypackage(context, packageName);
            Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(i);
        } catch (Exception e) {
            Log.e("ThreadService", "Error Open Apps: " + e.getMessage());
        }
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
