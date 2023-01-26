package id.co.pcsindonesia.pcslauncher.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.pcsindonesia.ia.diagnostic.R
import id.co.pcsindonesia.ia.diagnostic.launcher.ui.BaseActivity
import id.co.pcsindonesia.pcslauncher.recycler_adapter.AdAdapter
import kotlinx.android.synthetic.main.fragment_ad.*

class AdFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listAd = createListIv()
        rvFragAd.adapter = AdAdapter(requireContext(), listAd)
        rvFragAd.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        ivAdNavigation.setOnClickListener {
            (context as BaseActivity).changeFragment(1)
        }
    }

    private fun createListIv(): ArrayList<ImageView> {
        return ArrayList<ImageView>().also {
            for(i in 0..3){
                it.add(ImageView(requireContext()).apply {
                    this.setImageResource(R.drawable.tutoriale)
                })
            }
        }
    }
}