package id.co.pcsindonesia.pcslauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class SharedPreferencesHelper(var context: Context) {

    var sharedPreferences:SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    @SuppressLint("CommitPrefEdits")
    fun init(){
        sharedPreferences = context.getSharedPreferences("PCS_Launcher_Preferences", Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
    }

    fun insertLocation(langlot: String){
        editor!!.putString("location", langlot)
        editor!!.apply()

    }

    fun getLocation() : String?{
        return sharedPreferences!!.getString("location", null)
    }

    fun insertHeartbeatConfig(config: String){
        editor!!.putString("heartbeat_config", config)
        editor!!.apply()

    }

    fun getHeartbeatConfig() : String?{
        return sharedPreferences!!.getString("heartbeat_config", null)
    }


}