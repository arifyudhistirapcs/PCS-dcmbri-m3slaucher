package id.co.pcsindonesia.ia.diagnostic.helper;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import id.co.pcsindonesia.ia.diagnostic.App;
import id.co.pcsindonesia.ia.diagnostic.PinActivity;
import id.co.pcsindonesia.ia.diagnostic.model.ConfigModel;
import id.co.pcsindonesia.ia.diagnostic.model.GeneralConfigModel;
import id.co.pcsindonesia.ia.diagnostic.model.LocationModel;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBConfig;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBHelper;
import id.co.pcsindonesia.ia.diagnostic.util.ENC;
import sunmi.sunmiui.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;



public class HttpHelper {
    private static final String TAG = "HTTP Helper";
    public static final int SERVER_PORT = 38300;

    //============ HTTP STAGING ========================
    public static final String HttpUrl_GetParams = "https://bbril.xt.pcsindonesia.co.id/brilink_api/api/Heartbeat/get_config_hb";
    public static final String URL_INSERT_PCSHB= "https://bbril.xt.pcsindonesia.co.id/brilink_api/api/Heartbeat/primaryHeartbeat_enc";
    public static final String URL_INSERT_secondary= "https://bbril.xt.pcsindonesia.co.id/brilink_api/api/Heartbeat/secondaryHeartbeat_enc";

    private  static DBHelper sqldb;
    private static Context context;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

    public HttpHelper(Context context) {
        this.context = context;
        sqldb = new DBHelper(context);
    }


    //GET DCM PARAMS
    static ConfigModel model;
    public ConfigModel getParams(final boolean isInit, final Context context, final Location location){
        Log.e("HttpHelper","getParams");
        model = new ConfigModel();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HttpUrl_GetParams,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject object = new JSONObject(response);
                            object.put("date_request",sdf.format(new Date()));
                            response = object.toString();
                            model = ConfigModel.convertToParamModel(response);
                            sqldb.updatingConfig(model);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        Log.e("ThreadService","getResponseParams: "+response);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(sqldb.rowsConfig() == 0){
                            DataHelper.setDefaultParams(context);
                        }

                        Log.e(TAG,"GetParams-Error: "+error.getMessage());
                    }
                });

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

//        final RequestQueue requestQueue;
//        requestQueue = Volley.newRequestQueue(context, getHurlStack(context));
        requestQueue.add(stringRequest);
//
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });

        return model;
    }

    //------ GENERAL HEARTBEAT ------------
    public void generalHeartbeat(final Context context, Location location){
        DBConfig config = new DBConfig(context);
        String longitude = "0";
        String latitude = "0";
        if(location != null){
            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
        }
        JSONObject postparams = new JSONObject();
        JSONObject objects = new JSONObject();
        try {
            postparams.put("sn", GlobalHelper.getSN());
            postparams.put("poi", "-");
            postparams.put("deco_version", id.co.pcsindonesia.ia.diagnostic.GlobalHelper.getAppVersionByPackege(context, id.co.pcsindonesia.ia.diagnostic.GlobalHelper.DECO_PACKAGE));
            postparams.put("brilink_version", id.co.pcsindonesia.ia.diagnostic.GlobalHelper.getAppVersionByPackege(context, id.co.pcsindonesia.ia.diagnostic.GlobalHelper.BRILINK_PACKAGE));
            if(config.getAllLocation().size() > 0){
                LocationModel locationModel = config.getAllLocation().get(0);
                postparams.put("longitude",locationModel.getLONGITUDE());
                postparams.put("latitude",locationModel.getLATITUDE());
            }else {
                postparams.put("longitude",longitude);
                postparams.put("latitude",latitude);
            }
            postparams.put("battery",GlobalHelper.getPercentBattery(context));
            postparams.put("wifi_signal", GlobalHelper.getWifiSignal(context));
            postparams.put("sim_signal", GlobalHelper.getSimSignal(context));
            postparams.put("version", App.getVersion());
            objects.put("token", ENC.encrypt(postparams.toString()));
            Log.e(TAG,"Send general heartbeat: "+GlobalHelper.getIMEI(context)+" ; "+GlobalHelper.getSN()+" ; "+App.getDeviceModel()+" ; "+App.getVersion());
        } catch (JSONException e) {
            Log.e(TAG,"Send general heartbeat-Error: "+e.getMessage());
        }

        LogUtil.e("TAG","Heartbeat obj " + objects);



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL_INSERT_PCSHB, objects,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG,"Post General Heartbeat: "+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Post General Heartbeat-error: "+error.getMessage());
                    }
                });

//        final RequestQueue requestQueue;
//        requestQueue = Volley.newRequestQueue(context, getHurlStack(context));
//        requestQueue = Volley.newRequestQueue(context);

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjReq);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }



    //------ SECONDARY HEARTBEAT ------------
    public void secondaryHeartbeat(final Context context, Location location){
        DBConfig config = new DBConfig(context);
        String longitude = "0";
        String latitude = "0";
        if(location != null){
            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
        }
        JSONObject postparams = new JSONObject();
        JSONObject objects = new JSONObject();
        try {
            postparams.put("imei",GlobalHelper.getIMEI(context));
            postparams.put("sn", GlobalHelper.getSN());
            postparams.put("device_type", App.getDeviceModel());
            postparams.put("poi", "-");
            postparams.put("mac",GlobalHelper.getMacAddress(context));
            postparams.put("ip_address",GlobalHelper.getIPAddress(context));
            postparams.put("type", App.getDeviceModel());
            if(config.getAllLocation().size() > 0){
                LocationModel locationModel = config.getAllLocation().get(0);
                postparams.put("longitude",locationModel.getLONGITUDE());
                postparams.put("latitude",locationModel.getLATITUDE());
            }else {
                postparams.put("longitude",longitude);
                postparams.put("latitude",latitude);
            }
            postparams.put("pot_version",GlobalHelper.getAppsVersion(context,GlobalHelper.POT_PACKAGE));
            postparams.put("connection",GlobalHelper.getConnectivityStatusString(context));
            postparams.put("battery",GlobalHelper.getPercentBattery(context));
            postparams.put("wifi_status",GlobalHelper.getWifiStatus(context));
            postparams.put("wifi_signal", GlobalHelper.getWifiSignal(context));
            postparams.put("sim_signal", GlobalHelper.getSimSignal(context));
            postparams.put("dcm_version", App.getVersion());
            postparams.put("iccid", GlobalHelper.getICCID(context));
            objects.put("token", ENC.encrypt(postparams.toString()));
            Log.e(TAG,"Send secondary heartbeat: "+GlobalHelper.getIMEI(context)+" ; "+GlobalHelper.getSN()+" ; "+App.getDeviceModel()+" ; "+App.getVersion());
        } catch (JSONException e) {
            Log.e(TAG,"Send secondary heartbeat-Error: "+e.getMessage());
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL_INSERT_secondary, objects,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG,"Post Secondary Heartbeat: "+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Post Secondary Heartbeat-error: "+error.getMessage());
                    }
                });
//        final RequestQueue requestQueue;
//        requestQueue = Volley.newRequestQueue(context, getHurlStack(context));
//        requestQueue = Volley.newRequestQueue(context);

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjReq);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }



    public void getGeneralParam(final Context context){
        final DBConfig config = new DBConfig(context);
        Log.e("HttpHelper","getAppConfig");
        String tag_json_obj = "get_general_config";


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "https://bbril.xt.pcsindonesia.co.id/brilink_api/api/Heartbeat/get_config_hb", null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("HttpHelper","response: "+response.toString());
                        GeneralConfigModel model = new GeneralConfigModel();
                        model = model.convertToGenConfigModel(response.toString());
                        config.insertGenConfig(model);
                        Log.e("HttpHelper","response: "+config.getGenConfigCount());
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Get General Heartbeat-error: "+error.getMessage());
                        System.out.println("Fail: ");
                    }
                });
//        RequestQueue requestQueue;
//        requestQueue = Volley.newRequestQueue(context, getHurlStack(context));
//        requestQueue = Volley.newRequestQueue(context);

        Volley.newRequestQueue(context).add(jsonObjReq);

//        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
//            @Override
//            public void onRequestFinished(Request<Object> request) {
//                requestQueue.getCache().clear();
//            }
//        });
    }

    //------------------------------------------------


}
