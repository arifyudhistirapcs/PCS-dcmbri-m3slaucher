package id.co.pcsindonesia.pcslauncher

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.sunmi.pay.hardware.aidl.AidlConstants
import id.co.pcsindonesia.ia.diagnostic.launcher.App.Companion.isSunmiConnected
import id.co.pcsindonesia.ia.diagnostic.launcher.App.Companion.mBasicOptV2
import id.co.pcsindonesia.ia.diagnostic.launcher.LogUtil
import id.co.pcsindonesia.ia.diagnostic.printer.PrinterManagement
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.*

class GlobalHelper {

    companion object {
        var TAG = "GlobalHelper"

        val BRILINK = 1

        @SuppressLint("SimpleDateFormat")
        val ddMMyyyy = SimpleDateFormat("dd-MM-yyyy")

        @SuppressLint("SimpleDateFormat")
        val ymdh = SimpleDateFormat("yyyy-MM-dd HH")

        @JvmField
        var SUNMI_BASE_MODEL: String = "P1_4G"

        @JvmField
        var SUNMI_BASE_MODEL_P2: String = "P2"

        val JWT_KEY = "euOJUsxWZ1oa1uq1dvSYLMhRwDmwONasbyfUfKmR8WQlb5hwUWrYBUw8mjaRRG8R"

        fun getDeviceModel(): String? {
            return Build.MODEL
        }

        fun getTimeHBPrimary(): Int {
            val res = 300 //default (detik)`
            return res
        }

        fun getTimeHBSecondary(): Int {
            val res = 3600 //default (detik)`
            return res
        }



        fun getToken(data : JSONObject) : String {
            val claim = data.toMap()
            val mapHeader = mapOf<String,String>("typ" to "JWT")
            val key = Keys.hmacShaKeyFor(JWT_KEY.toByteArray())
            val jws = Jwts.builder()
                .setExpiration(getExpirationDate())
                .setIssuer("launcher.pcsindoensia.co.id")
//                .setAudience(ConstantsShared.jwtAudience)
//                .setSubject(ConstantsShared.jwtSubject)
//                .setNotBefore(Date())
                .setIssuedAt(Date())
                .setHeader(mapHeader)
                .signWith(key)
                .claim("msg", claim)
                .compact();
            return jws
        }

        private fun getExpirationDate(): Date? {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
            cal.time = Date()
            cal.add(Calendar.MINUTE, 10)

            LogUtil.e("DATE",cal.time.toString())

            return cal.time

        }

        fun parseToken(data: String): JSONObject? {
            return try {
                val key = Keys.hmacShaKeyFor(JWT_KEY.toByteArray())
                val jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(data)
                    .body["msg"]

                JSONObject(Gson().toJson(jws,LinkedHashMap::class.java))
            } catch (e : JwtException) {
                e.printStackTrace()
                LogUtil.e(TAG,e.message)
                JSONObject()
            }
        }

        fun getSN(): String? {
            var sn = ""
            if (getDeviceModel()!!.startsWith(SUNMI_BASE_MODEL) || getDeviceModel()!!.startsWith(
                    SUNMI_BASE_MODEL_P2
                )
            ) {
                if (isSunmiConnected) {
                    val basicOptV2 = mBasicOptV2
                    sn = try {
                        basicOptV2!!.getSysParam(AidlConstants.SysParam.SN)
                    } catch (e: java.lang.Exception) {
                        LogUtil.e(TAG, "getSN-Error: " + e.message)
                        ""
                    }
                }
            }
            return sn
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun openKeyboard(activity: Activity) {
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        fun getBatteryBundle(context: Context): JSONObject? {
            var batteryBundle = JSONObject()
            var batLevel = 0f
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toFloat()
            }
            if (batLevel == 0f) {
                val batteryIntent =
                    context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                // Error checking that probably isn't needed but I added just in case.
                if (level == -1 || scale == -1) {
                    batLevel = 50.0f
                }
                batLevel = level.toFloat() / scale.toFloat() * 100.0f
            }



            var batTemp = 0f
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

            batTemp = intent!!.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toFloat() / 10


            var batHealth = when (intent!!.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)){
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Overvoltage"
                BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified"
                else -> "Error"
            }




            batteryBundle.put("level", batLevel.toString())
            batteryBundle.put("temperature", batTemp.toString())
            batteryBundle.put("health", batHealth)

            return batteryBundle
        }

        fun getListApp (context: Context) : JSONObject? {
            val pManager = context.packageManager
            val i = Intent(Intent.ACTION_MAIN,null).addCategory(Intent.CATEGORY_LAUNCHER)
            val apps = pManager.getInstalledPackages(0)
            var listApp = JSONObject()


            for (app in apps){
                var ai = pManager.getApplicationInfo(app.packageName,0)
                if (ai.flags and ApplicationInfo.FLAG_SYSTEM === 0) {
                    var aa = JSONObject()
                    aa.put("version_name",app.versionName)
                    aa.put("version_code",app.versionCode)
                    aa.put("package_name",app.packageName)
                    listApp.put(app.applicationInfo.loadLabel(pManager).toString(),aa)
                }

            }
            return  listApp
        }

        fun getLocation (context: Context) : JSONObject? {
            val sharedPreferences = SharedPreferencesHelper(context)
            sharedPreferences.init()
            var location = JSONObject()
            var longitude: String? = "0"
            var latitude: String? = "0"

            if (sharedPreferences.getLocation() != null) {
                val strLoc = sharedPreferences.getLocation()
                try {
                    val obj = JSONObject(strLoc)
                    longitude = obj.getString("longitude")
                    latitude = obj.getString("latitude")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    LogUtil.e(TAG, "postPrimaryHb-Location convert error: " + e.message)
                }
            }
            location.put("longitude",longitude)
            location.put("latitude",latitude)

            return location
        }

        fun getSignal(context: Context) : JSONObject? {
            var signal = JSONObject()

            var wsignal = -100
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_WIFI_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val wimanager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wsignal = wimanager.connectionInfo.rssi
            } else {
                LogUtil.e(TAG, "WIFI STATE NOT GRANTED")
            }

            var ssignal = -127
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                var signals = ""
                try {
                    if (!tm.allCellInfo.isEmpty()) {
                        val cellInfoWcdma : CellInfoWcdma = tm.allCellInfo[0] as CellInfoWcdma
                        val cellSignalStrengthWcdma = cellInfoWcdma.cellSignalStrength
                        signals += cellSignalStrengthWcdma.dbm.toString()
                    }
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    LogUtil.e(TAG, "getSimSignal error : $e")
                    try {
                        if (!tm.allCellInfo.isEmpty()) {
                            val cellInfoGsm = tm.allCellInfo[0] as CellInfoGsm
                            val cellSignalStrengthGsm = cellInfoGsm.cellSignalStrength
                            signals += cellSignalStrengthGsm.dbm.toString()
                        }

                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                        LogUtil.e(TAG, "getSimSignal error : $e")
                        try {
                            if (!tm.allCellInfo.isEmpty()) {
                                val cellInfoLte = tm.allCellInfo[0] as CellInfoLte
                                val cellSignalStrengthLte = cellInfoLte.cellSignalStrength
                                signals += cellSignalStrengthLte.dbm.toString()
                            }
                        } catch (e: RuntimeException) {
                            e.printStackTrace()
                            LogUtil.e(TAG, "getSimSignal error : $e")
                        }
                    }
                }

                try {
                    ssignal = signals.toInt()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    LogUtil.e(TAG, "getSimSignal error : $e")
                }
            }

            signal.put("wifi",wsignal)
            signal.put("sim",ssignal)


            return signal
        }

        fun getPrinterCondition(context: Context) : JSONObject {
            var printer = JSONObject()
            var condition = ""
            var pm = PrinterManagement(context)

            var res = pm.checkCondition()
            var version = pm.checkVersion()

            when (res){
                1 -> condition = "Normal"
                2 -> condition = "Abnormal (2)"
                3 -> condition = "Abnormal (3)"
                4 -> condition = "Thermal paper kosong"
                5 -> condition = "Overheat"
                6 -> condition = "Cover terbuka"
                7 -> condition = "Cutter error"
                8 -> condition = "Cutter recovered"
                9 -> condition = "Black mark not detected"
                505 -> condition = "Printer not detected"
                507 -> condition = "Printer firmware update failed"
                else -> "null"
            }

            printer.put("version",version)
            printer.put("condition",condition)

            return printer
        }

        fun getDevice() : JSONObject? {
            var device = JSONObject()
            device.put("model", Build.MODEL)
            device.put("manufacturer", Build.MANUFACTURER)
            device.put("brand",Build.BRAND)

            return device
        }

        fun getIMEI(context: Context): String? {
            var imei = "-"
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tm.imei
                } else {
                    tm.deviceId
                }
            }
            return imei
        } fun getMacAddress(context: Context): String? {
            var macAddress = ""
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_WIFI_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val wimanager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                macAddress = wimanager.connectionInfo.macAddress
                if (macAddress == null) {
                    macAddress = ""
                }
            } else {
                LogUtil.e(TAG, "WIFI STATE NOT GRANTED")
            }
            return macAddress
        }

        fun getIPAddress(): String? {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return null
        }


        fun updateFiles(directory: String?, filename: String?, sBody: String?) {
            try {
                val file = File(directory, filename)
                val root = File(directory)
                if (!root.exists()) {
                    root.mkdirs()
                }
                if (file.exists()) {
                    file.delete()
                }
                val gpxfile = File(root, filename)
                val writer = FileWriter(gpxfile)
                writer.write(sBody)
                writer.flush()
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun getCacheDirHeartbeat(context: Context): String {
            val baseDir = context.cacheDir
            val path = "$baseDir/pcs_launcher/data/data/heartbeat"
            val file = File(path)
            if (!file.exists()) file.mkdirs()
            return file.absolutePath + "/"
        }

        fun getResponseFilename(): String {
            return "last_heartbeat.txt"
        }


        fun fullScreencall(activity: Activity) {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                val v: View = activity.window.decorView
                v.setSystemUiVisibility(View.GONE)
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                val decorView: View = activity.window.decorView
                val uiOptions: Int =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                decorView.setSystemUiVisibility(uiOptions)
            }
        }

        fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
            when (val value = this[it])
            {
                is JSONArray ->
                {
                    val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                    JSONObject(map).toMap().values.toList()
                }
                is JSONObject -> value.toMap()
                JSONObject.NULL -> null
                else            -> value
            }
        }

    }

}


