package id.co.pcsindonesia.pcslauncher.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import id.co.pcsindonesia.ia.diagnostic.R

class AdAdapter (val context: Context, val listAd: ArrayList<ImageView>): RecyclerView.Adapter<AdAdapter.AdVH>() {
    class AdVH(view: View): RecyclerView.ViewHolder(view) {
        val iv = view.findViewById<ImageView>(R.id.iv_item_ad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad, parent, false)
        return AdVH(view)
    }

    override fun onBindViewHolder(holder: AdVH, position: Int) {
        holder.iv.setImageDrawable(listAd[position].drawable)
    }

    override fun getItemCount(): Int {
        return listAd.size
    }
}