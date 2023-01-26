package id.co.pcsindonesia.pcslauncher.fragment_adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.co.pcsindonesia.pcslauncher.ui.AdFragment
import id.co.pcsindonesia.pcslauncher.ui.ListAppFragment
import id.co.pcsindonesia.ia.diagnostic.launcher.ui.MainFragment

class ListAppVpAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> AdFragment()
            1 -> MainFragment()
            else -> ListAppFragment()
        }
    }
}