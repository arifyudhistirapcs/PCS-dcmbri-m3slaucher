package id.co.pcsindonesia.ia.diagnostic.launcher

import android.content.Context
import android.location.Location
import android.os.Handler
import id.co.pcsindonesia.pcslauncher.GlobalHelper
import id.co.pcsindonesia.pcslauncher.SharedPreferencesHelper
import org.json.JSONException
import org.json.JSONObject

class ThreadHBServices {

    val TAG = "ThreadHBServices"
    val httpRequest = HttpRequest()
    lateinit var context: Context
    lateinit var sharedPreferences: SharedPreferencesHelper

    fun initial(context: Context){
        this.context = context
        httpRequest.initial(context)
        sharedPreferences = SharedPreferencesHelper(context)
        sharedPreferences.init()
    }


    //============================= HEARTBEAT =============================
    var mHandlerPrimary: Handler? = null
    var mHandlerSecondary: Handler? = null
    lateinit var lastLocation: Location
    fun runHB(lastLocation: Location) {
        this.lastLocation = lastLocation
        mHandlerPrimary = Handler()
        mHandlerSecondary = Handler()
        HBPrimaryRunnable.run()
        HBSecondaryRunnable.run()
    }

    private var HBPrimaryRunnable: Runnable = object : Runnable {
        override fun run() {
            Thread(ThreadHBPrimary()).start()
            val strConfig = sharedPreferences.getHeartbeatConfig()
            if (strConfig != null) {
                try{
                    val obj = JSONObject(strConfig)
                    val hbperiod = obj.getString("post_hb_primary")
                    mHandlerPrimary!!.postDelayed( this,hbperiod.toInt() * 1000.toLong())

                }catch (e: JSONException){
                    e.printStackTrace()
                    mHandlerPrimary!!.postDelayed(this, GlobalHelper.getTimeHBPrimary() * 1000.toLong())
                }catch (e: NullPointerException){
                    e.printStackTrace()
                    mHandlerPrimary!!.postDelayed(this, GlobalHelper.getTimeHBPrimary() * 1000.toLong())
                }

            } else {
                httpRequest.getConfigHB()
                mHandlerPrimary!!.postDelayed(this, GlobalHelper.getTimeHBPrimary() * 1000.toLong())
            }
        }
    }

    private var HBSecondaryRunnable: Runnable = object : Runnable {
        override fun run() {
            Thread(ThreadHBSecondary()).start()
            val strConfig = sharedPreferences.getHeartbeatConfig()
            if (strConfig != null) {
                try{
                    val obj = JSONObject(strConfig)
                    val hbperiod = obj.getString("post_hb_secondary")
                    mHandlerSecondary!!.postDelayed( this,hbperiod.toInt() * 1000.toLong())

                }catch (e: JSONException){
                    e.printStackTrace()
                    mHandlerSecondary!!.postDelayed(this, GlobalHelper.getTimeHBSecondary() * 1000.toLong())
                }catch (e: NullPointerException){
                    e.printStackTrace()
                    mHandlerSecondary!!.postDelayed(this, GlobalHelper.getTimeHBSecondary() * 1000.toLong())
                }

            } else {
                httpRequest.getConfigHB()
                mHandlerSecondary!!.postDelayed(this, GlobalHelper.getTimeHBSecondary() * 1000.toLong())
            }
        }
    }



    inner class ThreadHBPrimary : Runnable {
        override fun run() {
            httpRequest.sendPrimaryHB()
        }
    }

    inner class ThreadHBSecondary : Runnable {
        override fun run() {
            httpRequest.sendSecondaryHB()
        }
    }


}
