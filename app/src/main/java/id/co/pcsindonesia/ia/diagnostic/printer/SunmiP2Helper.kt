package id.co.pcsindonesia.ia.diagnostic.printer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.RemoteException
import android.util.Log
import androidx.core.content.ContextCompat
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService
import id.co.pcsindonesia.ia.diagnostic.R
import id.co.pcsindonesia.ia.diagnostic.model.SummaryItemModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SunmiP2Helper {
    private  var woyouService: SunmiPrinterService? = null
    private val TAG = "P2PRINTERLAYOUT"
    private var context: Context? = null

   constructor(context: Context) {
       this.context = context
       try {
           InnerPrinterManager.getInstance().bindService(context, object : InnerPrinterCallback() {
               override fun onConnected(service: SunmiPrinterService) {
                   Log.e("P2", "SDK CONNECTED")
                   woyouService = service
               }

               override fun onDisconnected() {
                   Log.e("P2", "SDK DISCONNECT")
                   woyouService = null
               }
           })
       } catch (e: InnerPrinterException) {
           e.printStackTrace()
       }
   }



    @Throws(RemoteException::class)
    fun checkCondition(): Int {
        return try {
            InnerPrinterManager.getInstance().bindService(context, object : InnerPrinterCallback() {
                override fun onConnected(service: SunmiPrinterService) {
                    Log.e("P2", "SDK CONNECTED")
                    woyouService = service
                }

                override fun onDisconnected() {
                    Log.e("P2", "SDK DISCONNECT")
                    woyouService = null
                }
            })
            woyouService!!.updatePrinterState()
        } catch (e: Exception) {
            -1
        }
    }

    @Throws(RemoteException::class)
    fun checkVersion(): String {
        return try {
            InnerPrinterManager.getInstance().bindService(context, object : InnerPrinterCallback() {
                override fun onConnected(service: SunmiPrinterService) {
                    Log.e("P2", "SDK CONNECTED")
                    woyouService = service
                }

                override fun onDisconnected() {
                    Log.e("P2", "SDK DISCONNECT")
                    woyouService = null
                }
            })
            woyouService!!.printerVersion.replace(" ", "")
        } catch (e: java.lang.Exception) {
            ""
        }
    }

    fun printSummaryTrx(listSumamry: List<SummaryItemModel?>?) {
        val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy - HH:mm:ss")
        try {
            val d = ContextCompat.getDrawable(context!!, R.drawable.logo_bri_white_gray2)
            val bitmap = (d as BitmapDrawable?)!!.bitmap
            val b = Bitmap.createScaledBitmap(bitmap, 180, 155, false)
            woyouService!!.setAlignment(1, null)
            woyouService!!.printBitmap(b, null)
            woyouService!!.setAlignment(1, null)
            woyouService!!.printText("\n", null)
            woyouService!!.setFontSize(20f, null)
            woyouService!!.setAlignment(1, null)
            woyouService!!.printText("======================================\n", null)
            woyouService!!.setAlignment(1, null)
            woyouService!!.printText("***         DIAGNOSTIC APP         ***\n", null)
            woyouService!!.setAlignment(1, null)
            woyouService!!.printText("======================================\n", null)
            if (listSumamry != null) {
                for (sim in listSumamry) {
                    woyouService!!.setFontSize(18f, null)
                    woyouService!!.setAlignment(1, null)
                    woyouService!!.printText(
                        """
                            ${sim?.title}
                            
                            """.trimIndent(), null
                    )
                    val listItem = sim?.listSummaryTrx
                    if (listItem != null) {
                        for (stim in listItem) {
                            val right = stim.right
                            val left = stim.left
                            woyouService!!.setFontSize(15f, null)
                            woyouService!!.setAlignment(0, null)
                            woyouService!!.printText(left, null)
                            val spaceLeft = 48 - left.length
                            val spaceMidd = spaceLeft - right.length
                            for (i in 0 until spaceMidd) {
                                woyouService!!.printText(" ", null)
                            }
                            woyouService!!.printText(
                                """
                                                $right
                                                
                                                """.trimIndent(), null
                            )
                        }
                    }
                    woyouService!!.printText(
                        "---------------------------------------------\n",
                        null
                    )
                }
            }
            woyouService!!.setFontSize(18f, null)
            woyouService!!.printText("\n", null)
            woyouService!!.setAlignment(0, null)
            woyouService!!.printText(
                """
                Print Date: ${dateFormat.format(Date())}
                
                """.trimIndent(), null
            )
            woyouService!!.setAlignment(2, null)
            woyouService!!.setFontSize(15f, null)
            woyouService!!.printText("\nPowered by PCS", null)
            woyouService!!.printText("\n\n\n\n\n", null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}