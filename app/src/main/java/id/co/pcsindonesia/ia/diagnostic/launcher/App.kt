package id.co.pcsindonesia.ia.diagnostic.launcher

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
import com.sunmi.pay.hardware.aidlv2.tax.TaxOptV2
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService
import id.co.pcsindonesia.pcslauncher.GlobalHelper
import sunmi.paylib.SunmiPayKernel

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        context = applicationContext

        if (!isSunmiConnected) {
            connectPayService()
        }

        if (GlobalHelper.getDeviceModel()!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL_P2)) {
            bindPrintService()
        }

    }
    private var mSMPayKernel: SunmiPayKernel? = null
    private fun connectPayService() {
        mSMPayKernel = SunmiPayKernel.getInstance()
        mSMPayKernel!!.initPaySDK(this, mConnectCallback)
    }

    private val mConnectCallback: SunmiPayKernel.ConnectCallback = object :
        SunmiPayKernel.ConnectCallback {
        override fun onConnectPaySDK() {
            sunmi.sunmiui.utils.LogUtil.e(TAG, "onConnectPaySDK")
            try {
                mEMVOptV2 = mSMPayKernel!!.mEMVOptV2
                mBasicOptV2 = mSMPayKernel!!.mBasicOptV2
                mPinPadOptV2 = mSMPayKernel!!.mPinPadOptV2
                mReadCardOptV2 = mSMPayKernel!!.mReadCardOptV2
                mSecurityOptV2 = mSMPayKernel!!.mSecurityOptV2
                mTaxOptV2 = mSMPayKernel!!.mTaxOptV2
                isSunmiConnected = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onDisconnectPaySDK() {
            sunmi.sunmiui.utils.LogUtil.e(TAG, "onDisconnectPaySDK")
        }
    }

    private fun bindPrintService() {
        try {
            InnerPrinterManager.getInstance().bindService(this, object : InnerPrinterCallback() {
                override fun onConnected(service: SunmiPrinterService) {
                    LogUtil.e("SunmiP2Helper", "Connected")
                    sunmiPrinterService = service
                }

                override fun onDisconnected() {
                    LogUtil.e("SunmiP2Helper", "Disconnected")
                    sunmiPrinterService = null
                }
            })
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }


    companion object {
        var isSunmiConnected = false
        var context: Context? = null
        private var mInstance: App? = null
        var sunmiPrinterService: SunmiPrinterService? = null
        val TAG: String = "App"
        var mBasicOptV2 // 获取基础操作模块
                : BasicOptV2? = null
        var mReadCardOptV2 // 获取读卡模块
                : ReadCardOptV2? = null
        var mPinPadOptV2 // 获取PinPad操作模块
                : PinPadOptV2? = null
        var mSecurityOptV2 // 获取安全操作模块
                : SecurityOptV2? = null
        var mEMVOptV2 // 获取EMV操作模块
                : EMVOptV2? = null
        var mTaxOptV2 // 获取税控操作模块
                : TaxOptV2? = null


        @get:Synchronized
        val instance: App?
            get() = mInstance
        val versionName: String
            get() {
                var version = ""
                try {
                    val pInfo = context!!.packageManager.getPackageInfo(
                        context!!.packageName, 0
                    )
                    version = pInfo.versionName
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                return version
            }
        val versionCode: String
            get() {
                var version = ""
                try {
                    val pInfo = context!!.packageManager.getPackageInfo(
                        context!!.packageName, 0
                    )
                    version = pInfo.versionCode.toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                return version
            }
        val appPackageName: String
            get() = context!!.packageName

        val printerCondition : Int
            get() {
                return try {
                sunmiPrinterService!!.updatePrinterState()
            } catch (e : NullPointerException){
                -1
                }
            }
        val printerVersion : String
            get() {
                return try {
                    sunmiPrinterService!!.printerVersion.replace(" ","")
                }catch (e : NullPointerException){
                    "-"
                }
            }
    }

}