package id.co.pcsindonesia.ia.diagnostic.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralConfigModel {
    private String get_config_time;
    private String post_hb_period;
    private String secondary_hb_period;
    private int status;
    private String created_date;

    public String getGet_config_time() {
        return get_config_time;
    }

    public void setGet_config_time(String get_config_time) {
        this.get_config_time = get_config_time;
    }

    public String getPost_hb_period() {
        return post_hb_period;
    }

    public void setPost_hb_period(String post_hb_period) {
        this.post_hb_period = post_hb_period;
    }

    public String getSecondary_hb_period() {
        return secondary_hb_period;
    }

    public void setSecondary_hb_period(String secondary_hb_period) {
        this.secondary_hb_period = secondary_hb_period;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public GeneralConfigModel convertToGenConfigModel(String json){
        GeneralConfigModel model = new GeneralConfigModel();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try{
            JSONObject object = new JSONObject(json);
            model.setGet_config_time(object.getString("get_config_time"));
            model.setPost_hb_period(object.getString("post_hb_period"));
            model.setSecondary_hb_period(object.getString("secondary_hb_period"));
            model.setStatus(object.getInt("status"));
            model.setCreated_date(sdf.format(new Date()));
        }catch (JSONException e){
            Log.e("GeneralConfigModel","JSONException-"+e.getMessage());
        }
        return model;
    }
}
