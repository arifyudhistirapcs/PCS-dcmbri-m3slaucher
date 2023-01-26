package id.co.pcsindonesia.ia.diagnostic.launcher

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import id.co.pcsindonesia.pcslauncher.*
import org.json.JSONException
import org.json.JSONObject
import sunmi.paylib.SunmiPayKernel
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

class AppServices : Service() {
    private var isStartLocationService = false
    private val TAG = "AppsServices"
    private lateinit var lastLocation: Location
    private lateinit var locationListener: LocationsListener
    private lateinit var httpRequest: HttpRequest
    lateinit var context: Context
    private lateinit var sharedPreferences : SharedPreferencesHelper
    private var threadHBServices = ThreadHBServices()
    protected var locationManager: LocationManager? = null
    lateinit var threadPers: Thread
    private var mSMPayKernel: SunmiPayKernel? = null


    fun initial(context: Context) {
        this.context = context
    }

    override fun onCreate() {

        var currentDevice = GlobalHelper.getDeviceModel()
        if (currentDevice!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL) || currentDevice.startsWith(
                GlobalHelper.SUNMI_BASE_MODEL_P2
            )
        ) {
            if (!App.isSunmiConnected)
                connectPayService()
        }
        sharedPreferences = SharedPreferencesHelper(this)
        sharedPreferences.init()


    }

    private fun connectPayService() {
        mSMPayKernel = SunmiPayKernel.getInstance()
        mSMPayKernel!!.initPaySDK(this@AppServices, mConnectCallback)
    }

    private val mConnectCallback: SunmiPayKernel.ConnectCallback = object :
        SunmiPayKernel.ConnectCallback {
        override fun onConnectPaySDK() {
            try {
                App.mEMVOptV2 = mSMPayKernel!!.mEMVOptV2
                App.mBasicOptV2 = mSMPayKernel!!.mBasicOptV2
                App.mPinPadOptV2 = mSMPayKernel!!.mPinPadOptV2
                App.mReadCardOptV2 = mSMPayKernel!!.mReadCardOptV2
                App.mSecurityOptV2 = mSMPayKernel!!.mSecurityOptV2
                App.mTaxOptV2 = mSMPayKernel!!.mTaxOptV2
                App.isSunmiConnected = true
                Log.e("AppsServices", "onConnectPaySDK; SN: " + GlobalHelper.getSN())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onDisconnectPaySDK() {
            LogUtil.e(TAG, "onDisconnectPaySDK")
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        httpRequest = HttpRequest()
        httpRequest!!.initial(this)
        threadHBServices.initial(this)
        locationListener = LocationsListener()
        locationListener!!.initial(LocationManager.GPS_PROVIDER, this)
        lastLocation = locationListener!!.getLastLocation()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isStartLocationService = true
            locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager?
            if (locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                lastLocation =
                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
                LogUtil.e(
                    TAG,
                    "lastLocation  lat : ${lastLocation.latitude}   long : ${lastLocation.longitude}"
                )
                locationListener!!.setLastLocation(lastLocation)
            }
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0f, locationListener)
            locationListener!!.saveLocation()
        } else {
            LogUtil.e(TAG, "ACCESS_FINE_LOCATION NOT GRANTED")
        }

        //AFTER RESTART
        if (intent != null) {
            if (intent.action != null) if (intent.action.equals(
                    "android.intent.action.BOOT_COMPLETED",
                    ignoreCase = true
                )
            ) {
                LogUtil.e(TAG, "onStartCommand - android.intent.action.BOOT_COMPLETED")
                object : CountDownTimer(14000, 1000) {
                    override fun onTick(l: Long) {
                        if (l in 1000..1999) {
//                            httpRequest!!.getConfigHB()
                        }
                    }

                    override fun onFinish() {
                        httpRequest!!.sendPrimaryHB()
                        httpRequest!!.sendSecondaryHB()
                    }
                }.start()
            }
        }

        object : CountDownTimer(2000, 1000) {
            override fun onTick(l: Long) {
            }

            override fun onFinish() {
                threadHBServices.runHB(lastLocation)
                threadPers = Thread(ThreadPersistence())
                threadPers.start()
            }
        }.start()

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        LogUtil.e(TAG, "TASK REMOVED - START SERVICE")

        val service = PendingIntent.getService(
            applicationContext,
            1001,
            Intent(applicationContext, AppServices::class.java),
            PendingIntent.FLAG_ONE_SHOT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000] = service
    }

    override fun onDestroy() {
        threadPers.interrupt()
        LogUtil.e(TAG, "onDestroy - START SERVICE")

        val service = PendingIntent.getService(
            applicationContext,
            1001,
            Intent(applicationContext, AppServices::class.java),
            PendingIntent.FLAG_ONE_SHOT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000] = service
    }

    inner class ThreadPersistence : Runnable {
        override fun run() {
            //--- ONE TIME RUNNING ---
            while (true) {
                if (!isStartLocationService) {
                    if (ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            isStartLocationService = true
                            locationManager =
                                applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager?

                            try {
                                locationManager!!.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    60000,
                                    0f,
                                    locationListener as android.location.LocationListener
                                )
                            } catch (e: java.lang.Exception) {
                                LogUtil.e(TAG, "ThreadPersistence-error1: " + e.message)
                            }
                            try {
                                locationManager!!.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    60000,
                                    0f,
                                    locationListener as android.location.LocationListener
                                )
                            } catch (e: java.lang.Exception) {
                                LogUtil.e(TAG, "ThreadPersistence-error2: " + e.message)
                            }
                            lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
                            LogUtil.e(
                                TAG,
                                "lastLocation  lat : ${lastLocation.latitude}   long : ${lastLocation.longitude}"
                            )
                            locationListener!!.saveLocation()
                        })
                    }
                } else {
                    lastLocation = locationListener!!.getLastLocation()
                }

                if (sharedPreferences.getHeartbeatConfig() == null) {
                    httpRequest!!.getConfigHB()
                }
                else {

                val gConfig = sharedPreferences.getHeartbeatConfig()
                var strdate = ""
                try {
                    val obj = JSONObject(gConfig)
                    strdate = obj.getString("get_config_time")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    LogUtil.e(TAG, "ThreadPersistence-error get date: " + e.message)
                }
                catch (e: NullPointerException) {
                    e.printStackTrace()
                    LogUtil.e(TAG, "ThreadPersistence-error get date: " + e.message)
                }

                val sdf = SimpleDateFormat("yyyy-MM-dd HH")
                val sama = GlobalHelper.ymdh.format(Date())

                val dateConfig: Date = sdf.parse(strdate)
                val dateNow: Date = sdf.parse(sama)

                LogUtil.e(TAG,"datenow = $dateNow")
                LogUtil.e(TAG,"dateconfig = $dateConfig")
                when {
                    dateConfig.before(dateNow) -> {
                        httpRequest.getConfigHB()
                    }
                    dateConfig == dateNow  -> {
                        httpRequest.getConfigHB()
                    }
                    else -> {

                    }
                }

            }

                try {
                    Thread.sleep(60000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

    }
}


