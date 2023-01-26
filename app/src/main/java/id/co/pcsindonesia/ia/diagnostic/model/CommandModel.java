package id.co.pcsindonesia.ia.diagnostic.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandModel {
    private String push_app_package;
    private String push_command_code;
    private String app_url;
    private String app_name;
    private String app_filename;
    private String app_icon_url;
    private String app_version;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_filename() {
        return app_filename;
    }

    public void setApp_filename(String app_filename) {
        this.app_filename = app_filename;
    }

    public String getApp_icon_url() {
        return app_icon_url;
    }

    public void setApp_icon_url(String app_icon_url) {
        this.app_icon_url = app_icon_url;
    }

    public String getPush_app_package() {
        return push_app_package;
    }

    public void setPush_app_package(String push_app_package) {
        this.push_app_package = push_app_package;
    }

    public String getPush_command_code() {
        return push_command_code;
    }

    public void setPush_command_code(String push_command_code) {
        this.push_command_code = push_command_code;
    }

    public static CommandModel convertToCommandModel(String message){
        CommandModel model = new CommandModel();
        try{
            JSONObject object = new JSONObject(message);
            model.setPush_app_package(object.getString("push_app_package"));
            model.setPush_command_code(object.getString("push_command_code"));
            model.setApp_url(object.getString("app_url"));
            model.setApp_name(object.getString("app_name"));
            model.setApp_filename(object.getString("app_filename"));
            model.setApp_icon_url(object.getString("app_icon_url"));
            model.setApp_version(object.getString("app_version"));
        }catch (JSONException e){
            e.printStackTrace();
            Log.e("CommandModel","Error Convert CommandModel: "+e.getMessage());
        }
        return model;
    }
}
