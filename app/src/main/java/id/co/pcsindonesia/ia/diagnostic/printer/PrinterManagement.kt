package id.co.pcsindonesia.ia.diagnostic.printer

import android.content.Context
import id.co.pcsindonesia.ia.diagnostic.launcher.App
import id.co.pcsindonesia.ia.diagnostic.model.SummaryItemModel
import id.co.pcsindonesia.pcslauncher.GlobalHelper

class PrinterManagement(context: Context) {
    private var sunmiP1Helper: SunmiP1Helper
    private var  sunmiP2Helper: SunmiP2Helper
    private var context : Context

    init {
        this.context = context
        sunmiP1Helper = SunmiP1Helper(context)
        sunmiP2Helper = SunmiP2Helper(context)
    }






    fun printSummaryTrx(listSumamry: List<SummaryItemModel?>?) {
        if (GlobalHelper.getDeviceModel()!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL)) {
            sunmiP1Helper?.printSummaryTrx(listSumamry)
        } else {
            sunmiP2Helper?.printSummaryTrx(listSumamry)
        }
    }

    fun checkCondition() : Int {
        var res = 505
        if (GlobalHelper.getDeviceModel()!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL)) {
            res = sunmiP1Helper!!.checkCondition()
        }
        if (GlobalHelper.getDeviceModel()!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL_P2)) {
            res = App.printerCondition
        }
        return res
    }

    fun checkVersion() : String {
        var res = "-"
        if (GlobalHelper.getDeviceModel()!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL)) {
            res = sunmiP1Helper!!.checkVersion()
        }
        if (GlobalHelper.getDeviceModel()!!.startsWith(GlobalHelper.SUNMI_BASE_MODEL_P2)) {
            res = App.printerVersion
        }
        return res
    }


}