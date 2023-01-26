package id.co.pcsindonesia.pcslauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.alimuzaffar.lib.pin.PinEntryEditText
import id.co.pcsindonesia.ia.diagnostic.R
import id.co.pcsindonesia.pcslauncher.GlobalHelper.Companion.hideKeyboard

class PinActivity : AppCompatActivity() {
    var TAG = "PinActivity"
    lateinit var pinview: PinEntryEditText
    lateinit var mainLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinpad)
        pinview = findViewById(R.id.pinpad)
        mainLayout = findViewById(R.id.layoutpin)
        pinview.isFocusable = true
        pinview.callOnClick()

        pinview.setOnPinEnteredListener { str ->
            hideKeyboard(this@PinActivity)
            val getpin = str.toString()
            verifyPin(getpin)
        }

        GlobalHelper.openKeyboard(this)
    }

    private fun verifyPin(getpin: String) {
        if (getpin == "111111"){
            setResult(1)
        } else {
            setResult(2)
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        GlobalHelper.fullScreencall(this)
    }
}