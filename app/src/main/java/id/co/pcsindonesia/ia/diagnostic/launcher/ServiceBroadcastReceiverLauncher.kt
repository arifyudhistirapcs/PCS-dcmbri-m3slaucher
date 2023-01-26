package id.co.pcsindonesia.pcslauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import id.co.pcsindonesia.ia.diagnostic.launcher.App
import id.co.pcsindonesia.ia.diagnostic.launcher.AppServices
import java.util.*
class ServiceBroadcastReceiverLauncher  : BroadcastReceiver() {

    val connectionReceiverListener: ConnectionReceiverListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("Broadcast receiver", "intent data: " + intent.action)
        if (intent.action != null) if (intent.action.equals("android.net.conn.CONNECTIVITY_CHANGE", ignoreCase = true)) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = (activeNetwork != null
                    && activeNetwork.isConnected)
            connectionReceiverListener?.onNetworkConnectionChanged(isConnected)
        } else if (intent.action.equals("android.intent.action.BOOT_COMPLETED", ignoreCase = true)) {
            val intStartService = Intent(context, AppServices::class.java)
            intStartService.action = "android.intent.action.BOOT_COMPLETED"
            context.startService(intStartService)
        } else {
            context.startService(Intent(context, AppServices::class.java))
        }
    }

    companion object{
        fun isConnected(): Boolean {
            val cm = App.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return (activeNetwork != null
                    && activeNetwork.isConnected)
        }

    }

    interface ConnectionReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

}
