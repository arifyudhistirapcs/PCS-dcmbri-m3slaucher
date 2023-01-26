package id.co.pcsindonesia.ia.diagnostic.launcher

import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import id.co.pcsindonesia.pcslauncher.GlobalHelper
import id.co.pcsindonesia.pcslauncher.GlobalHelper.Companion.getListApp
import id.co.pcsindonesia.pcslauncher.GlobalHelper.Companion.getToken
import id.co.pcsindonesia.pcslauncher.GlobalHelper.Companion.parseToken
import id.co.pcsindonesia.pcslauncher.HttpUrlHelper
import id.co.pcsindonesia.pcslauncher.SharedPreferencesHelper
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class HttpRequest {
    val TAG = "HttpRequest"
    lateinit var context: Context
    lateinit var sharedPreferences: SharedPreferencesHelper
    lateinit var notifInbox: NotificationCompat.Builder
    var lastLocation: Location? = null
    lateinit var notificationManager: NotificationManager

    fun initial(context: Context){
        this.context = context
        sharedPreferences = SharedPreferencesHelper(context)
        sharedPreferences.init()
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun sendPrimaryHB(activity:String? = "service") {

        val `object` = JSONObject()
        val reqObject = JSONObject()
        try {
            `object`.put("sn", GlobalHelper.getSN())
            `object`.put("activity", activity)
            `object`.put("location", GlobalHelper.getLocation(context))

            reqObject.put("token", getToken(`object`))
            LogUtil.e(TAG, "reqObject postPrimaryHbRaw: $`object`")
            LogUtil.e(TAG, "reqObject postPrimaryHb: $reqObject")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjReq = object : JsonObjectRequest(
            HttpUrlHelper.URL_SEND_HB_PRIMARY,
            reqObject, Response.Listener{ response ->
                try {
                    if (response.has("rc")) {
                        val rc = response.getString("rc")
                        if (rc.equals("00", ignoreCase = true)) {
                            val sdfFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                            val str = sdfFormat.format(Date())
                            GlobalHelper.updateFiles(
                                GlobalHelper.getCacheDirHeartbeat(context),
                                GlobalHelper.getResponseFilename(), str
                            )
                            LogUtil.e(TAG, "success")

                        } else {
                            LogUtil.e(TAG, "error rc = $rc")
                        }
                    }
                } catch (e: JSONException) {
                    LogUtil.e(TAG, "postPrimaryHb-error: " + e.message)
                }
            },
            Response.ErrorListener {
                LogUtil.e(TAG, "postPrimaryHb-error: ${it.message}")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(context)
            .add(jsonObjReq)
    }
    fun sendSecondaryHB() {

        val `object` = JSONObject()
        val reqObject = JSONObject()
        try {
            `object`.put("sn", GlobalHelper.getSN())
            `object`.put("device_type", GlobalHelper.getDevice())
            `object`.put("location", GlobalHelper.getLocation(context))
            `object`.put("battery", GlobalHelper.getBatteryBundle(context))
            `object`.put("ip_address", GlobalHelper.getIPAddress())
            `object`.put("mac", GlobalHelper.getMacAddress(context))
            `object`.put("imei", GlobalHelper.getIMEI(context))
            `object`.put("signal", GlobalHelper.getSignal(context))
            `object`.put("printer", GlobalHelper.getPrinterCondition(context))
            `object`.put("list_package", getListApp(context))

            reqObject.put("token", getToken(`object`))
            LogUtil.e(TAG, "reqObject postSecondaryHbRaw: $`object`")
            LogUtil.e(TAG, "reqObject postSecondaryHb: $reqObject")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjReq = object : JsonObjectRequest(
            HttpUrlHelper.URL_SEND_HB_SECONDARY,
            reqObject, Response.Listener{ response ->
                try {
                    if (response.has("rc")) {
                        val rc = response.getString("rc")
                        if (rc.equals("00", ignoreCase = true)) {
                            val sdfFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                            val str = sdfFormat.format(Date())
                            GlobalHelper.updateFiles(
                                GlobalHelper.getCacheDirHeartbeat(context),
                                GlobalHelper.getResponseFilename(), str
                            )
                            LogUtil.e(TAG, "success")

                        } else {
                            LogUtil.e(TAG, "error rc = $rc")
                        }
                    }
                } catch (e: JSONException) {
                    LogUtil.e(TAG, "postSecondaryHb-error: " + e.message)
                }
            },
            Response.ErrorListener {
                LogUtil.e(TAG, "postSecondaryHb-error: ${it.message}")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(context)
            .add(jsonObjReq)
    }

    fun getConfigHB() {
        val jsonObjReq = object : JsonObjectRequest(
            HttpUrlHelper.URL_GET_CONF_HB +"?sn=${GlobalHelper.getSN()}",
            null, Response.Listener{ response ->
                try {
                    if (response.has("rc")) {
                        val rc = response.getString("rc")
                        if (rc.equals("00", ignoreCase = true)) {
                            val sdfFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                            val str = sdfFormat.format(Date())
                            GlobalHelper.updateFiles(
                                GlobalHelper.getCacheDirHeartbeat(context),
                                GlobalHelper.getResponseFilename(), str
                            )
                            LogUtil.e(TAG, "success")


                            if (response.has("msg")){
                                val token = response.getString("msg")
                                val msg =  parseToken(token)
                                if (!msg.toString().equals("{}")){
                                    LogUtil.e("VALUE", msg.toString())
                                    LogUtil.e("get_config_time", msg!!.opt("get_config_time").toString())
                                    LogUtil.e("post_hb_primary", msg!!.opt("post_hb_primary").toString())
                                    LogUtil.e("post_hb_secondary", msg!!.opt("post_hb_secondary").toString())
                                    LogUtil.e("status", msg!!.opt("status").toString())

                                    msg.put("created_date", GlobalHelper.ymdh.format(Date()))
                                    sharedPreferences.insertHeartbeatConfig(msg.toString())
                                } else {
                                    context.toast("Token Expired!")
                                }
                            } else {
                                context.toast("No Msg!")
                            }


                        } else {
                            LogUtil.e(TAG, "error rc = $rc")
                        }
                    }
                } catch (e: JSONException) {
                    LogUtil.e(TAG, "postPrimaryHb-error: " + e.message)
                }
            },
            Response.ErrorListener {
                LogUtil.e(TAG, "postPrimaryHb-error: ")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val jsonSN = JSONObject()
                jsonSN.put("sn", GlobalHelper.getSN())
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["token"] = getToken(jsonSN)
                return headers
            }
        }

        Volley.newRequestQueue(context)
            .add(jsonObjReq)
    }


}