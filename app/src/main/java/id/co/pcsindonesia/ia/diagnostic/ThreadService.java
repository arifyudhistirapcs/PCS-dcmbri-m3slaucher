package id.co.pcsindonesia.ia.diagnostic;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import id.co.pcsindonesia.ia.diagnostic.helper.DataHelper;
import id.co.pcsindonesia.ia.diagnostic.helper.GlobalHelper;
import id.co.pcsindonesia.ia.diagnostic.helper.HttpHelper;
import id.co.pcsindonesia.ia.diagnostic.model.CommandModel;
import id.co.pcsindonesia.ia.diagnostic.model.ConfigModel;
import id.co.pcsindonesia.ia.diagnostic.model.DataModel;
import id.co.pcsindonesia.ia.diagnostic.model.GeneralConfigModel;
import id.co.pcsindonesia.ia.diagnostic.model.LocationModel;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBCommands;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBConfig;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBHelper;
import id.co.pcsindonesia.ia.diagnostic.util.CustomThreadExecutor;
import id.co.pcsindonesia.ia.diagnostic.util.ServiceBroadcastReceiver;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Sys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import sunmi.paylib.SunmiPayKernel;

public class ThreadService extends Service {

    private static final String TAG = "ThreadService";
    private final String KEY_CHECK_UPDATE = "check_update";


    protected LocationManager locationManager;


    public CustomThreadExecutor executor;
    private ServerSocket serverSocket;
    private Socket socket;

    private Context context;
    private MainActivity activity;
    private Thread thread1;

    private PrintWriter output;
    private BufferedReader input;


    private DBHelper sqldb;
    private DBCommands dbcomand;
    private DBConfig dbConfig;
    private HttpHelper httpHelper;

    public static Location mLastLocation;
    private DownloadManager downloadManager;
    NotificationManager notificationManager;
    private DriverManager mDriverManager = App.sDriverManager;
    private Sys zcsBase;

    private SunmiPayKernel mSMPayKernel;


    private SharedPreferences sPreferences;
    private SharedPreferences.Editor editor;
    private SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");

    public ThreadService(Context context) {
        this.context = context;
    }

    public ThreadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    boolean isGPSEnabled, isNetworkEnabled;

    @Override
    public void onCreate() {
        if(App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_A920) ||
                App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90) ||
                App.getDeviceModel().startsWith(GlobalHelper.DEVICE_SUNMI_P1)) {
            try {
                serverSocket = new ServerSocket(httpHelper.SERVER_PORT);
            } catch (IOException e) {
                thread1 = new Thread(new Thread1());
                thread1.start();
            }
            thread1 = new Thread(new Thread1());
            thread1.start();
        }

        initializeLocationManager();

        try {
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
            }else if(isNetworkEnabled){
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[1]);
            }else{
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
            }
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED )
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListeners[0]);
            mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        File file = new File(GlobalHelper.DIR_CONFIG, GlobalHelper.PARAMS_FILENAME);
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        Intent broadcastIntent = new Intent(this, ServiceBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);

        if(App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_A920) ||
                App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90) ||
                App.getDeviceModel().startsWith(GlobalHelper.DEVICE_SUNMI_P1)) {
            thread1.interrupt();
            try {
                if (socket != null) {
                    socket.close();
                    serverSocket.close();
                }
            } catch (IOException e) {

            }
        }

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        if(App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_A920) ||
                App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90)) {

        }
        activity = MainActivity.instance;
        sqldb = new DBHelper(this);
        dbcomand = new DBCommands(this);
        dbConfig = new DBConfig(this);
        httpHelper = new HttpHelper(ThreadService.this);

        sPreferences = ThreadService.this.getSharedPreferences("service",MODE_PRIVATE);
        editor = sPreferences.edit();
        String last_check = sPreferences.getString(KEY_CHECK_UPDATE,"init");
        Log.e("ThreadService","Check spreference udpate: "+last_check);
        if(sPreferences.contains(KEY_CHECK_UPDATE)){
            editor.remove(KEY_CHECK_UPDATE);
            editor.commit();
            Log.e("ThreadService","Check spreference udpate-after: "+last_check);
        }


        if(App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90)) {

            zcsBase = mDriverManager.getBaseSysDevice();
        }

        if(App.getDeviceModel().startsWith(GlobalHelper.DEVICE_SUNMI_P1)){
            connectPayService();
        }


        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(50);
        executor = new CustomThreadExecutor(1,
                40, 5000, TimeUnit.MILLISECONDS, blockingQueue);

        if(App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_A920) ||
                App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90)){
            DataHelper.setDefaultParams(this);
        }


        new Thread(new ThreadDaily()).start();

        runHB();

        if (intent != null)
            if (intent.getAction() == Intent.ACTION_SEND) {
                output.println(intent.getStringExtra("message"));
                output.flush();
                thread1 = new Thread(new Thread1());
                thread1.start();

                Intent intent1 = new Intent("dcm_activity_finish");
                sendBroadcast(intent1);
            }else if (intent.getAction() == Intent.ACTION_ANSWER) {
                if(intent.hasExtra("message")){
                    String msg = intent.getStringExtra("message");
                    if(msg.equalsIgnoreCase("resume")){
                        executor.resume();
                    }
                }
            }


        return START_STICKY;
    }

    class Thread1 implements Runnable {
        @Override
        public void run() {
            try {
                if (serverSocket != null) {
                    serverSocket.setReuseAddress(true);
                    if (!serverSocket.isBound()) {
                        serverSocket.bind(new InetSocketAddress(httpHelper.SERVER_PORT));
                    }
                } else {
                    serverSocket = new ServerSocket(httpHelper.SERVER_PORT);
                }
                socket = serverSocket.accept();
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                new Thread(new Thread2()).start();
            } catch (IOException e) {
                try {
                    if (socket != null) {
                        socket.close();
                        serverSocket.close();
                    }
                } catch (IOException ex) {

                }
            }
        }
    }

    private class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    if (message != null) {
                        GlobalHelper.turnScreenOn(ThreadService.this);
                        try {
                            JSONObject object = new JSONObject(message);
                            Boolean ispaid = object.getBoolean("isPaid");
                            if (ispaid) {
                                DataModel model = DataModel.convertToDataModel(message);
                                output.println(GlobalHelper.makeResponse("00", "Success", "Data transaction received"));
                                output.flush();
                                thread1 = new Thread(new Thread1());
                                thread1.start();
                                if(model.getFlag().toLowerCase().equals("ondemand")){
                                    boolean issave = sqldb.insertTransaction(model);
                                    if(issave){

                                    }
                                }else{

                                }

                            } else {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Payment Application in development", Toast.LENGTH_SHORT).show();
                                        output.println(GlobalHelper.makeResponse("00", "Success", "Application in evelopment"));
                                        output.flush();
                                        thread1 = new Thread(new Thread1());
                                        thread1.start();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            Log.e("ThreadService", "Thread2-JSONException: " + e.getMessage());
                            output.println(GlobalHelper.makeResponse("30", "Error", e.getMessage()));
                            output.flush();
                            thread1 = new Thread(new Thread1());
                            thread1.start();
                        }
                    }
                } catch (IOException e) {
                    Log.e("ThreadService", "Thread2-IOExcepion: " + e.getMessage());
                    output.println(GlobalHelper.makeResponse("30", "Error", e.getMessage()));
                    output.flush();
                    thread1 = new Thread(new Thread1());
                    thread1.start();
                }
            }
        }
    }



    private ConfigModel configModel = null;
    private boolean isInitCommand = false;
    private class ThreadDaily implements Runnable {
        @Override
        public void run() {
            //--- ONE TIME RUNNING ---
            httpHelper.getGeneralParam(ThreadService.this);
            //------------------------
            while(true){
                boolean serviceReady = true;
                if(App.getDeviceModel().startsWith(GlobalHelper.DEVICE_SUNMI_P1)){
                    serviceReady = App.isSunmiConnected;
                }
                if(App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_A920) ||
                        App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90)) {
                    if(!isInitCommand){
                        httpHelper.getParams(true, ThreadService.this, mLastLocation);
                        isInitCommand = true;
                    }
                    if (configModel == null) {
                        if (sqldb.getAllHBConfig().size() > 0) {
                            configModel = sqldb.getHBConfig();
                        } else {
                            httpHelper.getParams(false, ThreadService.this, mLastLocation);
                        }
                    } else {
                        String[] actionTime = configModel.getHb_time_get_param().split(":");
                        String appTime = actionTime[0] + ":" + actionTime[1];
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        Log.e("ThreadDaily", "ThreadDaily; Time: " + appTime + "; Current " + sdf.format(new Date()));
                        if (sdf.format(new Date()).equalsIgnoreCase(appTime)) {
                            GlobalHelper.turnScreenOn(ThreadService.this);
                            httpHelper.getParams(false, ThreadService.this, mLastLocation);
                        }

                        if (dbcomand.getCountCommand() > 0) {
                            List<CommandModel> listCommand = dbcomand.getAllCommand();
                            Log.e(TAG, "ThreadDaily; List Command: " + listCommand.size());
                            if (listCommand.size() > 0) {
                                CommandModel model = listCommand.get(0);

                            }
                        }

                        configModel = null;
                    }
                }else{
                    if(dbConfig.getGenConfigCount() > 0){
                        GeneralConfigModel gConfig = dbConfig.getAllGenConfig().get(0);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        if(!gConfig.getCreated_date().equalsIgnoreCase(sdf.format(new Date()))){
                            httpHelper.getGeneralParam(ThreadService.this);
                        }
                    }else{
                        httpHelper.getGeneralParam(ThreadService.this);
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                int hours = Integer.parseInt(sdf.format(new Date()));
                Log.e("ThreadService", "Service log active - "+ hours + " - "+ dbConfig.getLocationCount());
                if(hours >= 9) {
                    String last_check = sPreferences.getString(KEY_CHECK_UPDATE, "init");
                    if (last_check.equalsIgnoreCase("init")) {
                        Log.e("ThreadService", "Thread Daily; check update - init: " + last_check);

                        editor.putString(KEY_CHECK_UPDATE, date_format.format(new Date()));
                        editor.commit();
                    } else if (!last_check.equalsIgnoreCase(date_format.format(new Date()))) {
                        Log.e("ThreadService", "Thread Daily; check update - new: " + last_check);

                        editor.putString(KEY_CHECK_UPDATE, date_format.format(new Date()));
                        editor.commit();
                    }
                }

                try {
                    Thread.sleep(32000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //============================= HEARTBEAT ============================================
    Handler mHandler;
    private void runHB() {
        mHandler = new Handler();
        HBPrimaryRunnable.run();
        HBSecondaryRunnable.run();
    }

    Runnable miniHBHandlerTask = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(miniHBHandlerTask, 300000);
        }
    };


    Runnable HBTaskRunnable = new Runnable() {
        @Override
        public void run() {
            if (App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_Z90) ||
                    App.getDeviceModel().equalsIgnoreCase(GlobalHelper.DEVICE_A920)) {
                ConfigModel dparam = sqldb.getHBConfig();
                int delay = dparam.getHb_time_periode();

                mHandler.postDelayed(HBTaskRunnable, delay * 1000);
            } else {
                if (dbConfig.getGenConfigCount() > 0) {
                    GeneralConfigModel gConfig = dbConfig.getAllGenConfig().get(0);
                    if (gConfig.getStatus() == 1) {

                    }
                    if (gConfig.getPost_hb_period() != null) {
                        mHandler.postDelayed(HBTaskRunnable, Integer.parseInt(gConfig.getPost_hb_period()) * 1000);
                    } else {
                        mHandler.postDelayed(HBTaskRunnable, 1800 * 1000);
                    }
                } else {
                    httpHelper.getGeneralParam(ThreadService.this);
                    mHandler.postDelayed(HBTaskRunnable, 600 * 1000);
                }
            }
        }
    };

    Runnable HBPrimaryRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Start HBPrimaryRunnable");
            if (dbConfig.getGenConfigCount() > 0) {
                GeneralConfigModel gConfig = dbConfig.getAllGenConfig().get(0);
                if (gConfig.getStatus() == 1) {
                    new Thread(new ThreadPrimaryHB()).start();
                }
                if (gConfig.getPost_hb_period() != null) {
                    mHandler.postDelayed(HBPrimaryRunnable, Integer.parseInt(gConfig.getPost_hb_period()) * 1000 * 60);
                } else {
                    mHandler.postDelayed(HBPrimaryRunnable, 60 * 1000);
                }
            } else {
                httpHelper.getGeneralParam(ThreadService.this);
                mHandler.postDelayed(HBPrimaryRunnable, 60 * 1000);
            }
        }
    };

    Runnable HBSecondaryRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Start HBSecondaryRunnable");
            if (dbConfig.getGenConfigCount() > 0) {
                GeneralConfigModel gConfig = dbConfig.getAllGenConfig().get(0);
                if (gConfig.getStatus() == 1) {
                    new Thread(new ThreadSecondaryHB()).start();
                }
                if (gConfig.getSecondary_hb_period() != null) {
                    mHandler.postDelayed(HBSecondaryRunnable, Integer.parseInt(gConfig.getSecondary_hb_period()) * 1000 * 60);
                } else {
                    mHandler.postDelayed(HBSecondaryRunnable, 1800 * 1000);
                }
            } else {
                httpHelper.getGeneralParam(ThreadService.this);
                mHandler.postDelayed(HBSecondaryRunnable, 300 * 1000);
            }
        }
    };


    private class ThreadPrimaryHB implements Runnable {
        @Override
        public void run() {
            GlobalHelper.turnScreenOn(ThreadService.this);
            Log.e(TAG, "Start send heartbeat-ThreadPrimaryHB ");
            httpHelper.generalHeartbeat(ThreadService.this, mLastLocation);
        }
    }

    private class ThreadSecondaryHB implements Runnable {
        @Override
        public void run() {
            GlobalHelper.turnScreenOn(ThreadService.this);
            Log.e(TAG, "Start send heartbeat-ThreadSecondaryHB ");
            httpHelper.secondaryHeartbeat(ThreadService.this, mLastLocation);
        }
    }

    //-------------- END OF HEARTBEAT --------------



    //============================= LOCATION LISTENER ========================
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            if(location.getLongitude() > 0 && location.getLatitude() > 0){
                LocationModel locationModel = new LocationModel();
                locationModel.setLATITUDE(location.getLatitude());
                locationModel.setLONGITUDE(location.getLongitude());
                dbConfig.insertLocation(locationModel);
            }else {
                if(dbConfig.getLocationCount() == 0){
                    LocationModel locationModel = new LocationModel();
                    locationModel.setLATITUDE(location.getLatitude());
                    locationModel.setLONGITUDE(location.getLongitude());
                    dbConfig.insertLocation(locationModel);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private void connectPayService() {
        mSMPayKernel = SunmiPayKernel.getInstance();
        mSMPayKernel.initPaySDK(this, mConnectCallback);
    }

    private SunmiPayKernel.ConnectCallback mConnectCallback = new SunmiPayKernel.ConnectCallback() {

        @Override
        public void onConnectPaySDK() {
            Log.e(TAG, "onConnectPaySDK");
            try {
                App.mEMVOptV2 = mSMPayKernel.mEMVOptV2;
                App.mBasicOptV2 = mSMPayKernel.mBasicOptV2;
                App.mPinPadOptV2 = mSMPayKernel.mPinPadOptV2;
                App.mReadCardOptV2 = mSMPayKernel.mReadCardOptV2;
                App.mSecurityOptV2 = mSMPayKernel.mSecurityOptV2;
                App.mTaxOptV2 = mSMPayKernel.mTaxOptV2;
                App.isSunmiConnected = true;
                activity = MainActivity.instance;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnectPaySDK() {
            Log.e(TAG, "onDisconnectPaySDK");
            App.isSunmiConnected = false;
        }

    };
}
