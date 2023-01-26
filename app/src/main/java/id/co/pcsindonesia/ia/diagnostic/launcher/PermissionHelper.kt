package id.co.pcsindonesia.ia.diagnostic.launcher

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import java.util.*

class PermissionHelper(var activity: Activity) {
    var TAG = "PermissionHelper"
    var REQUEST_PERMISSION_KEY = 2
    fun permissionManager() {
        val permissionList = ArrayList<String>()
        if (!checkPermission(activity, Manifest.permission.INTERNET)) permissionList.add(Manifest.permission.INTERNET)
        if (!checkPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE)) permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE)
        if (!checkPermission(activity, Manifest.permission.ACCESS_WIFI_STATE)) permissionList.add(Manifest.permission.ACCESS_WIFI_STATE)
        if (!checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION))  permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!checkPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION))  permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!checkPermission(activity, Manifest.permission.ACCESS_WIFI_STATE)) permissionList.add(Manifest.permission.ACCESS_WIFI_STATE)
        if (!checkPermission(activity, Manifest.permission.READ_PHONE_STATE)) permissionList.add(Manifest.permission.READ_PHONE_STATE)
        if (!checkPermission(activity, Manifest.permission.REORDER_TASKS)) permissionList.add(Manifest.permission.REORDER_TASKS)
        val listperm = arrayOfNulls<String>(permissionList.size)
        for (i in permissionList.indices) {
            listperm[i] = permissionList[i]
            checkPermission(activity, permissionList[i])
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionList[i])) {
            }
            LogUtil.e(TAG, "listperm : ${  listperm[i]}")
        }

        if (listperm.isNotEmpty()) {
            requestPermission(listperm, REQUEST_PERMISSION_KEY)
        }
        LogUtil.e(TAG, "permissionList : $permissionList")

    }

    private fun requestPermission(permissions: Array<String?>, reqcode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, reqcode)
        for (permission in permissions) {
            checkPermission(permission!!)
        }
    }


    fun checkPermission(permissionName: String): Boolean {

        val granted =  if (Build.VERSION.SDK_INT >= 23) {
                ContextCompat.checkSelfPermission(activity, permissionName) == PackageManager.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(activity, permissionName) == PermissionChecker.PERMISSION_GRANTED
        }
        LogUtil.e(TAG, "granted $permissionName : $granted")

        return granted
    }

    companion object {
        fun checkPermission(context: Context?, permission: String?): Boolean {
            val result = ContextCompat.checkSelfPermission(context!!, permission!!)
            return result == PackageManager.PERMISSION_GRANTED
        }
    }
}
