package id.co.pcsindonesia.ia.diagnostic.launcher.ui


import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import id.co.pcsindonesia.ia.diagnostic.R
import id.co.pcsindonesia.ia.diagnostic.launcher.AppServices
import id.co.pcsindonesia.ia.diagnostic.launcher.HttpRequest
import id.co.pcsindonesia.ia.diagnostic.launcher.LocationsListener
import id.co.pcsindonesia.ia.diagnostic.launcher.PermissionHelper
import id.co.pcsindonesia.pcslauncher.*
import id.co.pcsindonesia.pcslauncher.fragment_adapter.ListAppVpAdapter
import kotlinx.android.synthetic.main.activity_base.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.toast
import sunmi.sunmiui.utils.LogUtil


class BaseActivity : AppCompatActivity() {
    var TAG = "BASE ACTIVITY"
     private lateinit var appsServices: AppServices
    private  var locationManager: LocationManager? = null
    private val dbProfiles by lazy {
        DBProfiles(this)
    }
    private val profileModel = ProfileModel()
    val httpRequest by lazy {
        HttpRequest()
    }
    private lateinit var locationListener: LocationsListener
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
//        supportFragmentManager.beginTransaction().replace(R.id.llBase, ListAppFragment()).commit()
        val permission = PermissionHelper(this)
        permission.permissionManager()
        appsServices = AppServices()
        appsServices.initial(this)

        val mServiceIntent = Intent(this@BaseActivity, appsServices::class.java)
        if (!isMyServiceRunning(appsServices::class.java)) {
                LogUtil.e(TAG, "mServiceIntent: start")
                val cd = object : CountDownTimer(10000,1000){
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        startService(mServiceIntent)
                    }
                }
                cd.start()
        }else{
            LogUtil.e(TAG, "mServiceIntent: can't started")
        }

        profileModel.menu = "menu"
        profileModel.version = "1"

        dbProfiles.insertProfile(profileModel)
        checkEDCType()
        val test = ListAppVpAdapter(this)
        vp2BaseApp.adapter = test
        changeFragment(1)

        locationListener = LocationsListener()
        locationListener!!.initial(LocationManager.GPS_PROVIDER, this)
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        } catch (e : Exception) {
            LogUtil.e(TAG,e.toString())
        }

//        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

    }

    private fun checkEDCType() {
        LogUtil.e(TAG, "device type: " + GlobalHelper.getDeviceModel())
        if (GlobalHelper.getDeviceModel()!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL_P2)) {
            llBase.backgroundResource = R.drawable.background_p22
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
        

    override fun onResume() {
        super.onResume()
//        GlobalHelper.fullScreencall(this)
    }

    fun changeFragment(position: Int){
        vp2BaseApp.currentItem = position
    }

    fun goPassword(from:Int) {
        val intentPIN = Intent(this, PinActivity::class.java).also {
            it.putExtra("from",from)
        }
        startActivityForResult(intentPIN, 1)
    }

    fun openBrilink() {
        try {
            httpRequest!!.initial(this)
            httpRequest.sendPrimaryHB("Open Brilink")
            val intent = packageManager.getLaunchIntentForPackage("id.co.bri.brilinkmobile")
            startActivity(intent)
        } catch (e: Exception) {
            LogUtil.e(TAG, e.toString())
            e.printStackTrace()
            toast("Brilink not installed")
        }
    }

    fun location() {
        try {
            // Request location updates
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            var lastLocation =
                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            id.co.pcsindonesia.ia.diagnostic.launcher.LogUtil.e(
                TAG,
                "lastLocation  lat : ${lastLocation?.latitude}   long : ${lastLocation?.longitude}"
            )
            if (lastLocation != null) {
                locationListener!!.setLastLocation(lastLocation)
                locationListener!!.saveLocation()
            } else {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0L,0F,locationListener)
            }

        } catch(ex: Exception) {
            LogUtil.e(TAG, ex.message)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GlobalHelper.BRILINK) {
            if (resultCode == 1) {
//                try {
//                    httpRequest.sendHB("Open Brilink")
//                    val intent = packageManager.getLaunchIntentForPackage("id.co.bri.brilinkmobile")
//                    startActivity(intent)
//                }
//                catch (e:Exception){
//                    LogUtil.e(TAG,e.toString())
//                    e.printStackTrace()
//                    toast("Brilink not installed")
//                }
            } else {
                toast("Wrong password")
            }
        }
    }


}