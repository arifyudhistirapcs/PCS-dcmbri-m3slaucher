package id.co.pcsindonesia.ia.diagnostic.launcher.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.co.pcsindonesia.ia.diagnostic.R
import id.co.pcsindonesia.ia.diagnostic.launcher.HttpRequest
import id.co.pcsindonesia.pcslauncher.GlobalHelper
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val baseActivity = (context as BaseActivity)
        ivMainNavigationLeft.setOnClickListener {
            baseActivity.changeFragment(0)
        }
        ivMainNavigationRight.setOnClickListener {
            baseActivity.changeFragment(2)
        }
        llFragBriLink.setOnClickListener {
            baseActivity.openBrilink()
        }
        llFragPrintDiag.setOnClickListener {
            baseActivity.location()
        }
    }

}