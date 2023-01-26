package id.co.pcsindonesia.ia.diagnostic.model;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationModel {
    private double LONGITUDE;
    private double LATITUDE;

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(double LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(double LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public static LocationModel convertToLocationModel(String location){
        LocationModel model = new LocationModel();
        try{
            JSONObject object = new JSONObject(location);
            model.setLATITUDE(object.getDouble("latitude"));
            model.setLONGITUDE(object.getDouble("longitude"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return model;
    }
}

