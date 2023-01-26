package id.co.pcsindonesia.ia.diagnostic.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigModel {
/**
 * hb_id : 1
 * merchant_id :
 * submerchant_id :
 * hb_time_schedule : 16:00:00
 * hb_day_schedule : {mon,wed}
 * hb_time_pcs_schedule : 16:00:00
 * hb_day_pcs_schedule : {mon,wed}
 * hb_time_get_param : 15:41:00
 * hb_day_get_param : {mon,wed}
 * hb_time_get_merchant : 11:30:00
 * max_hb_local : 5
 * status : 1
 */

private int hb_id;
    private int db_id;
    private String merchant_id;
    private String submerchant_id;
    private int hb_time_periode;
    private String hb_day_schedule;
    private String hb_time_get_param;
    private String hb_day_get_param;
    private String hb_time_get_merchant;
    private int max_hb_local;
    private int status;
    private int max_listview_trx;
    private String date_request;

    public String getDate_request() {
        return date_request;
    }

    public void setDate_request(String date_request) {
        this.date_request = date_request;
    }

    public int getHb_id() {
        return hb_id;
    }

    public void setHb_id(int hb_id) {
        this.hb_id = hb_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getSubmerchant_id() {
        return submerchant_id;
    }

    public void setSubmerchant_id(String submerchant_id) {
        this.submerchant_id = submerchant_id;
    }

    public String getHb_day_schedule() {
        return hb_day_schedule;
    }

    public void setHb_day_schedule(String hb_day_schedule) {
        this.hb_day_schedule = hb_day_schedule;
    }

    public String getHb_time_get_param() {
        return hb_time_get_param;
    }

    public void setHb_time_get_param(String hb_time_get_param) {
        this.hb_time_get_param = hb_time_get_param;
    }

    public String getHb_day_get_param() {
        return hb_day_get_param;
    }

    public void setHb_day_get_param(String hb_day_get_param) {
        this.hb_day_get_param = hb_day_get_param;
    }

    public String getHb_time_get_merchant() {
        return hb_time_get_merchant;
    }

    public void setHb_time_get_merchant(String hb_time_get_merchant) {
        this.hb_time_get_merchant = hb_time_get_merchant;
    }

    public int getMax_hb_local() {
        return max_hb_local;
    }

    public void setMax_hb_local(int max_hb_local) {
        this.max_hb_local = max_hb_local;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHb_time_periode() {
        return hb_time_periode;
    }

    public void setHb_time_periode(int hb_time_periode) {
        this.hb_time_periode = hb_time_periode;
    }

    public int getMax_listview_trx() {
        return max_listview_trx;
    }

    public void setMax_listview_trx(int max_listview_trx) {
        this.max_listview_trx = max_listview_trx;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }

    public static ConfigModel convertToParamModel(String data){
        ConfigModel model = new ConfigModel();
        try{
            JSONObject object = new JSONObject(data);
            int hb_id = object.getInt("hb_id");
            String merchant_id = object.getString("merchant_id");
            String submerchant_id = object.getString("submerchant_id");
            int hb_time_periode = object.getInt("hb_time_periode");
            String hb_day_schedule = object.getString("hb_day_schedule");
            String hb_time_get_param = object.getString("hb_time_get_param");
            String hb_day_get_param = object.getString("hb_day_get_param");
            String hb_time_get_merchant = object.getString("hb_time_get_merchant");
            int max_hb_local = object.getInt("max_hb_local");
            int status = object.getInt("status");
            int max_listview_trx = object.has("max_listview_trx")?object.getInt("max_listview_trx"):0;
            String date_request = object.getString("date_request");

            model.setHb_time_get_param(hb_time_get_param);
            model.setSubmerchant_id(submerchant_id);
            model.setMerchant_id(merchant_id);
            model.setHb_id(hb_id);
            model.setHb_time_periode(hb_time_periode);
            model.setHb_day_schedule(hb_day_schedule);
            model.setHb_day_get_param(hb_day_get_param);
            model.setHb_time_get_merchant(hb_time_get_merchant);
            model.setMax_hb_local(max_hb_local);
            model.setStatus(status);
            model.setMax_listview_trx(max_listview_trx);
            model.setDate_request(date_request);
        }catch (JSONException e){
            Log.e("GlobalHelper","convertToParamModel-Error JSON: "+e.getMessage());
        }
        return model;
    }

}
