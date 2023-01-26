package id.co.pcsindonesia.ia.diagnostic;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import id.co.pcsindonesia.ia.diagnostic.R;
import id.co.pcsindonesia.ia.diagnostic.model.ConfigModel;
import id.co.pcsindonesia.ia.diagnostic.model.SummaryItemModel;
import id.co.pcsindonesia.ia.diagnostic.model.SummaryTrxItemModel;
import id.co.pcsindonesia.ia.diagnostic.printer.PrinterManagement;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBHelper;
import id.co.pcsindonesia.ia.diagnostic.helper.HttpsTrustManager;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.co.pcsindonesia.ia.diagnostic.util.Restarter;
import sunmi.paylib.SunmiPayKernel;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    ThreadService service;
    static MainActivity instance;
    Intent mServiceIntent;

    public Toolbar toolbar;
    public NavigationView navigationView = null;
    private TextView tvVersion;

    private VelocityTracker mVelocityTracker = null;

    int position = 0;

    Button button;

    private TextView tvBRIlink, tvDiagnostic, tvDeco, tvAppsStore;
    private TextView tvSN, tvImei, lblimei, tvDeviceType, tvOS, tvFirmware, tvAndroid, tvBuildNumber, tvBrand;
    private TextView tvConnection, tvIPAddress, tvIccid;
    private Button btnPrint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        tvBRIlink = findViewById(R.id.tv_brilink);
        tvDiagnostic = findViewById(R.id.tv_diagnostic);
        tvDeco = findViewById(R.id.tv_deco);
        tvAppsStore = findViewById(R.id.tv_appsstore);
        tvSN = findViewById(R.id.tv_sn);
        tvImei = findViewById(R.id.tv_imei);
        lblimei = findViewById(R.id.lblimei);
        tvDeviceType = findViewById(R.id.tv_devicetype);
        tvOS = findViewById(R.id.tv_osversion);
        tvFirmware = findViewById(R.id.tv_firmwareversion);
        tvAndroid = findViewById(R.id.tv_android_version);
        tvBuildNumber = findViewById(R.id.tv_buildnumber);
        tvBrand = findViewById(R.id.tv_brand);

        tvConnection = findViewById(R.id.tv_connection);
        tvIPAddress = findViewById(R.id.tv_ipaddress);
        tvIccid = findViewById(R.id.tv_iccid);
        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setEnabled(false);

        permissionManager();
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        ArrayList<ConfigModel> listConfig = dbHelper.getAllHBConfig();


        registerReceiver(mBroadcastReceiver, new IntentFilter("dcm_activity_finish"));

        service = new ThreadService(MainActivity.this);
        mServiceIntent = new Intent(MainActivity.this, service.getClass());
        if(!isMyServiceRunning(service.getClass())){
            startService(mServiceIntent);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Print Diag");

        button = (Button) findViewById(R.id.set_up);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });


        new GetConnection().execute();
        connectPayService();

        new CountDownTimer(2000, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                setup();
            }
        }.start();
    }

    private void setup(){
        tvBRIlink.setText(GlobalHelper.getAppVersionByPackege(getApplicationContext(), GlobalHelper.BRILINK_PACKAGE));
        tvDeco.setText(GlobalHelper.getAppVersionByPackege(getApplicationContext(), GlobalHelper.DECO_PACKAGE));
        tvDiagnostic.setText(GlobalHelper.getAppVersion(getApplicationContext()));
        tvAppsStore.setText(GlobalHelper.getAppVersionByPackege(getApplicationContext(), GlobalHelper.APPSTORE_PACKAGE));


        tvSN.setText(GlobalHelper.getSN());
        if(GlobalHelper.getIMEI(getApplicationContext()) != null) {
            tvImei.setText(GlobalHelper.getIMEI(getApplicationContext()));
        }else{
            lblimei.setVisibility(View.GONE);
            tvImei.setVisibility(View.GONE);
        }
        tvDeviceType.setText(GlobalHelper.getDeviceModel());
        tvAndroid.setText(android.os.Build.VERSION.RELEASE);
        tvBuildNumber.setText(String.valueOf(Build.VERSION.SDK_INT));
        tvBrand.setText(Build.BRAND);
        tvOS.setText(System.getProperty("os.version"));

        tvConnection.setText(String.valueOf(GlobalHelper.getConnectivityStatusString(getApplicationContext())));
        tvIPAddress.setText(GlobalHelper.getIPAddress());
        tvIccid.setText(GlobalHelper.getICCID(getApplicationContext()));


        btnPrint.setEnabled(true);
        final PrinterManagement printerManagement = new PrinterManagement(getApplicationContext());
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<SummaryItemModel> listItemModel = new ArrayList<>();
                SummaryItemModel itemModel = new SummaryItemModel();
                itemModel.setTitle("Application Version");
                List<SummaryTrxItemModel> listTrx1 = new ArrayList<>();
                listTrx1.add(new SummaryTrxItemModel("BRILink", tvBRIlink.getText().toString()));
                listTrx1.add(new SummaryTrxItemModel("Diagnostic App", tvDiagnostic.getText().toString()));
                listTrx1.add(new SummaryTrxItemModel("Deco", tvDeco.getText().toString()));
                listTrx1.add(new SummaryTrxItemModel("Apps Store", tvAppsStore.getText().toString()));
                itemModel.setListSummaryTrx(listTrx1);
                listItemModel.add(itemModel);

                SummaryItemModel itemModel2 = new SummaryItemModel();
                itemModel2.setTitle("System Information");
                List<SummaryTrxItemModel> listTrx2 = new ArrayList<>();
                listTrx2.add(new SummaryTrxItemModel("Serial Number", tvSN.getText().toString()));
                if(tvImei.getText().toString().length() > 0) {
                    listTrx2.add(new SummaryTrxItemModel("IMEI", tvImei.getText().toString()));
                }
                listTrx2.add(new SummaryTrxItemModel("Brand", tvBrand.getText().toString()));
                listTrx2.add(new SummaryTrxItemModel("Device Type", tvDeviceType.getText().toString()));
                listTrx2.add(new SummaryTrxItemModel("Kernel Version", tvOS.getText().toString()));
                listTrx2.add(new SummaryTrxItemModel("Android Version", tvAndroid.getText().toString()));
                listTrx2.add(new SummaryTrxItemModel("Android API Level", tvBuildNumber.getText().toString()));
                itemModel2.setListSummaryTrx(listTrx2);
                listItemModel.add(itemModel2);

                SummaryItemModel itemModel3 = new SummaryItemModel();
                itemModel3.setTitle("Network and Location");
                List<SummaryTrxItemModel> listTrx3 = new ArrayList<>();
                listTrx3.add(new SummaryTrxItemModel("Connection TYpe", tvConnection.getText().toString()));
                listTrx3.add(new SummaryTrxItemModel("IP Address", tvIPAddress.getText().toString()));
                listTrx3.add(new SummaryTrxItemModel("ICCID", tvIccid.getText().toString()));
                itemModel3.setListSummaryTrx(listTrx3);
                listItemModel.add(itemModel3);

                printerManagement.printSummaryTrx(listItemModel);
            }
        });
    }

    private SunmiPayKernel mSMPayKernel;
    private void connectPayService() {
        mSMPayKernel = SunmiPayKernel.getInstance();
        mSMPayKernel.initPaySDK(MainActivity.this, mConnectCallback);
    }

    private SunmiPayKernel.ConnectCallback mConnectCallback = new SunmiPayKernel.ConnectCallback() {

        @Override
        public void onConnectPaySDK() {
            try {
                Log.e(TAG, "onConnectPaySDK");
                App.mEMVOptV2 = mSMPayKernel.mEMVOptV2;
                App.mBasicOptV2 = mSMPayKernel.mBasicOptV2;
                App.mPinPadOptV2 = mSMPayKernel.mPinPadOptV2;
                App.mReadCardOptV2 = mSMPayKernel.mReadCardOptV2;
                App.mSecurityOptV2 = mSMPayKernel.mSecurityOptV2;
                App.mTaxOptV2 = mSMPayKernel.mTaxOptV2;
                App.isSunmiConnected = true;

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

    private class GetConnection extends AsyncTask<URL, Integer, Boolean> {
        HttpsURLConnection httpsUrlConnection;
        boolean checkPinnedStatus= false;

        protected Boolean doInBackground(URL... urls) {
            URL url = null;
            try {
                url = new URL("https://bbril.xt.pcsindonesia.co.id/");
                httpsUrlConnection = (HttpsURLConnection) url.openConnection();
                httpsUrlConnection.connect();

                for (Certificate crt : httpsUrlConnection.getServerCertificates()) {
                    checkPinnedStatus = checkPinned(httpsUrlConnection, crt);
                    Log.e("cekpinned", String.valueOf(checkPinned(httpsUrlConnection, crt)));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onProgressUpdate(Integer... progress) {

        }
        protected void onPostExecute(Boolean result) {
            if(checkPinnedStatus){
                cekDataOut("1", "1", 1, 1);
                checkPinnedStatus= false;
            }

        }
    }

    public static final String BASE_URL = "https://staging.pcsindonesia.co.id/index.php/api/";
    public static final  String log_url = BASE_URL+ "log";

    private void cekDataOut(String sn, String dm, double lat, double lng){
        StringRequest postRequest = new StringRequest(Request.Method.POST, log_url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("SN", "1");
                params.put("DM", "1" );
                params.put("lat", String.valueOf(1));
                params.put("lng", String.valueOf(1));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences prefs= getSharedPreferences("myPrefs",Context.MODE_PRIVATE);

                HashMap<String, String> header = new HashMap<>();
                header.put("token",  prefs.getString("token", ""));
                return header;
            }

        };
        RequestQueue requestQueue;
//        requestQueue = Volley.newRequestQueue(this, HttpsTrustManager.getHurlStack(this));
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);
    }


    private boolean checkPinned(HttpsURLConnection httpsUrlConnection, Certificate pinnedCertificate) throws IOException {
        try {
            InputStream inputStream = getApplicationContext().getAssets().open("staging.pcsindonesia.co.id.cer");
            Certificate pinnedCertificateFile = CertificateFactory.getInstance("X.509")
                    .generateCertificate(inputStream);
//            Log.e("cek cert pinned", String.valueOf(pinnedCertificate.getPublicKey()));
//
//            Log.e("cek cert file", String.valueOf(pinnedCertificateFile.getPublicKey()));

            if (pinnedCertificate.getPublicKey().equals(pinnedCertificateFile.getPublicKey())) {

                // Open stream
                httpsUrlConnection.getInputStream();
                Log.e("Pinning", "Server certificates validation successful");
                return true;
            } else {
                Log.e("Pinning", "Server certificates validation failed");
                throw new SSLException("Server certificates validation failed");
            }
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
            Log.e("error cek cert", String.valueOf(e.toString()));
        }

        return false;
    }

    public void openNewActivity(){
        Intent intent = new Intent(this, SetUpActivity.class);
        startActivity(intent);
    }

    public void openWebActivity(){
        Intent intent = new Intent(this, WebViewActivity.class);
        startActivity(intent);
    }

    public void openAboutPCSActivity(){
        Intent intent = new Intent(this, AboutPcsActivity.class);
        startActivity(intent);
    }

    public void openAboutAppSActivity(){
        Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("dcm_activity_finish")){
                finish();
            }
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(service != null && mServiceIntent != null) {
            if (!isMyServiceRunning(service.getClass())) {
                startService(mServiceIntent);
            }
        }else {
            service = new ThreadService(MainActivity.this);
            mServiceIntent = new Intent(MainActivity.this, service.getClass());
            if(!isMyServiceRunning(service.getClass())){
                startService(mServiceIntent);
            }
        }
    }


    @Override
    protected void onDestroy() {
        if(service != null && mServiceIntent != null) {
            if (!isMyServiceRunning(service.getClass())) {
                startService(mServiceIntent);
            }
        }else {
            service = new ThreadService(MainActivity.this);
            mServiceIntent = new Intent(MainActivity.this, service.getClass());
            if(!isMyServiceRunning(service.getClass())){
                startService(mServiceIntent);
            }
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);

        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(service != null && mServiceIntent != null) {
            if (!isMyServiceRunning(service.getClass())) {
                startService(mServiceIntent);
            }
        }else {
            service = new ThreadService(MainActivity.this);
            mServiceIntent = new Intent(MainActivity.this, service.getClass());
            if(!isMyServiceRunning(service.getClass())){
                startService(mServiceIntent);
            }
        }
    }

    private void  permissionManager(){
        ArrayList<String> permissionList = new ArrayList<>();

        if (!checkPermission(Manifest.permission.INTERNET))
            permissionList.add(Manifest.permission.INTERNET);
        if (!checkPermission(Manifest.permission.ACCESS_NETWORK_STATE))
            permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        if (!checkPermission(Manifest.permission.ACCESS_WIFI_STATE))
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        if (!checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!checkPermission(Manifest.permission.READ_PHONE_STATE))
            permissionList.add(Manifest.permission.READ_PHONE_STATE);

        String[] listperm = new String[permissionList.size()];
        for(int i=0; i < permissionList.size(); i++){
            listperm[i] = permissionList.get(i);
        }
        if(listperm.length > 0) {
            requestPermission(listperm, 1);
        }
    }

    private boolean checkPermission(String permission){
        int result = ContextCompat.checkSelfPermission(this,permission);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    private void requestPermission(String[] permission,int reqcode){
        ActivityCompat.requestPermissions(this,permission,reqcode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isPermissionOK = true;
        for(int codeResult:grantResults){
            if(codeResult== PackageManager.PERMISSION_GRANTED){
                isPermissionOK = true;
            }else{
                isPermissionOK = false;
            }
        }
        if(isPermissionOK){

        }else{

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_app) {
            openAboutAppSActivity();
            return true;
        }
        if (id == R.id.about_pcs) {
            openAboutPCSActivity();
            return true;
        }

        if (id == R.id.web_view) {
            openWebActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}