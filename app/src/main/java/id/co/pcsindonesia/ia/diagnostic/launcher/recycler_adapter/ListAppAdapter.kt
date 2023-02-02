package id.co.pcsindonesia.ia.diagnostic.launcher.recycler_adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.co.pcsindonesia.ia.diagnostic.MainActivity
import id.co.pcsindonesia.ia.diagnostic.R
import id.co.pcsindonesia.pcslauncher.GlobalHelper
import id.co.pcsindonesia.pcslauncher.model.AppInfo
import id.co.pcsindonesia.ia.diagnostic.launcher.ui.BaseActivity


class ListAppAdapter(val context: Context, val listApp: ArrayList<AppInfo>): RecyclerView.Adapter<ListAppAdapter.ListAppVH>() {
    class ListAppVH (view: View): RecyclerView.ViewHolder(view) {
        val icon = view.findViewById<ImageView>(R.id.iv_item_list_app)
        val name = view.findViewById<TextView>(R.id.tv_item_list_app)
        val ll = view.findViewById<LinearLayout>(R.id.ll_item_list_app)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAppVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_app, parent, false)
        return ListAppVH(view)
    }

    override fun onBindViewHolder(holder: ListAppVH, position: Int) {
        val item = listApp[position]
        holder.icon.setImageDrawable(item.icon)
        holder.name.text = item.label
        holder.ll.setOnClickListener {

        if (item.packageName.toString() == "printdiag") {
                context.startActivity(Intent(context, MainActivity::class.java))
        } else {
             val intent = context.packageManager.getLaunchIntentForPackage(item.packageName.toString())
             context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return listApp.size
    }
}