package id.co.pcsindonesia.ia.diagnostic.helper;

import android.content.Context;

import id.co.pcsindonesia.ia.diagnostic.model.ConfigModel;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataHelper {

    private static String TAG = "DataHelper";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private  static DBHelper sqldb;

    public static void deleteFile(Context context, String directory){
        sqldb = new DBHelper(context);
        ConfigModel model = sqldb.getHBConfig();
        if(sqldb.rowsConfig() > 0) {
            int maxFile = model.getMax_hb_local();

            File file = new File(directory);
            File[] files = file.listFiles();
            try {
                if (files != null) {
                    if (files.length >= maxFile) {
                        for (File x : files) {
                            x.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void setDefaultParams(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        sqldb = new DBHelper(context);
        if(sqldb.rowsConfig() == 0) {
            try {
                JSONObject object = new JSONObject();
                object.put("hb_id", "1");
                object.put("merchant_id", "");
                object.put("submerchant_id", "");
                object.put("hb_time_periode", "60");
                object.put("hb_day_schedule", "{mon,tue,wed,thu,fri,sat,sun}");
                object.put("hb_time_get_param", "10:30:00");
                object.put("hb_day_get_param", "{mon,tue,wed,thu,fri,sat,sun}");
                object.put("hb_time_get_merchant", "10:30:00");
                object.put("hb_day_get_merchant", "{mon,tue,wed,thu,fri,sat,sun}");
                object.put("max_hb_local", "7");
                object.put("status", "1");
                object.put("max_listview_trx","3");
                object.put("date_request",sdf.format(date));

                sqldb = new DBHelper(context);
                ConfigModel model = ConfigModel.convertToParamModel(object.toString());
                sqldb.updatingConfig(model);
            } catch (JSONException e) {

            }
        }
    }
}

