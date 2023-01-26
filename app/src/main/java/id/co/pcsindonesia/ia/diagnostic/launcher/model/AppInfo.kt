package id.co.pcsindonesia.pcslauncher.model

import android.graphics.drawable.Drawable

data class AppInfo(
    var label: CharSequence,
    val packageName: CharSequence,
    var icon: Drawable
)