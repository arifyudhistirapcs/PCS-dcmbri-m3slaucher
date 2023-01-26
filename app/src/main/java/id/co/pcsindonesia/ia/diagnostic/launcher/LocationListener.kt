package id.co.pcsindonesia.ia.diagnostic.launcher

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import id.co.pcsindonesia.pcslauncher.SharedPreferencesHelper
import org.json.JSONException
import org.json.JSONObject

class LocationsListener : LocationListener {
    val TAG = "LocationListener"
    lateinit var mLastLocation: Location
    private var sharedPreferences: SharedPreferencesHelper? = null

    fun initial(provider:String, context: Context) {
        mLastLocation = Location(provider)
        sharedPreferences = SharedPreferencesHelper(context)
        sharedPreferences!!.init()
    }

    fun setLastLocation(mLastLocation  : Location) {
        this.mLastLocation = mLastLocation
    }

    fun getLastLocation() : Location {
        return mLastLocation
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation.set(location)
        LogUtil.e(
            TAG,
            "Location Changed: onLocationChanged: "+mLastLocation.latitude+" ; "+mLastLocation.longitude
        )
        saveLocation()
    }

    fun saveLocation(){
        getLastLocation()
        LogUtil.e(TAG, "saveLocation; "+mLastLocation.latitude+" ; "+mLastLocation.longitude)
        if (mLastLocation.longitude.toString() != "0.0" && mLastLocation.latitude.toString() != "0.0") {
            try{
                val obj = JSONObject()
                obj.put("longitude", mLastLocation.longitude.toString())
                obj.put("latitude", mLastLocation.latitude.toString())
                sharedPreferences!!.insertLocation(obj.toString())
            }catch (e: JSONException){
                e.printStackTrace()
                LogUtil.e(TAG, "Location Changed: insert location error1: "+e.message)
            }
        } else {
            try {
                if (sharedPreferences!!.getLocation() == null) {
                    try{
                        val obj = JSONObject()
                        obj.put("longitude", mLastLocation.longitude.toString())
                        obj.put("latitude", mLastLocation.latitude.toString())
                        sharedPreferences!!.insertLocation(obj.toString())
                    }catch (e: JSONException){
                        e.printStackTrace()
                        Log.e(TAG, "Location Changed: insert location error2: "+e.message)
                    }
                }
            } catch (e: NullPointerException) {
                LogUtil.e(TAG, "M Location Changed-error: " + e.message)
                try{
                    val obj = JSONObject()
                    obj.put("longitude", mLastLocation.longitude.toString())
                    obj.put("latitude", mLastLocation.latitude.toString())
                    sharedPreferences!!.insertLocation(obj.toString())
                }catch (e: JSONException){
                    e.printStackTrace()
                    LogUtil.e(TAG, "Location Changed: insert location error3: "+e.message)
                }
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }




}