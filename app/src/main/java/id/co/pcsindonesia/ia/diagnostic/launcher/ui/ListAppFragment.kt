package id.co.pcsindonesia.pcslauncher.ui

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import id.co.pcsindonesia.ia.diagnostic.R
import id.co.pcsindonesia.ia.diagnostic.launcher.ui.BaseActivity
import id.co.pcsindonesia.pcslauncher.model.AppInfo
import id.co.pcsindonesia.ia.diagnostic.launcher.recycler_adapter.ListAppAdapter
import kotlinx.android.synthetic.main.fragment_list_app.*


class ListAppFragment : Fragment() {
    private val TAG = "ListAppFragment"
    private var listApp = ArrayList<AppInfo>()
    private val rvAdapter by lazy { ListAppAdapter(requireContext(), listApp) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ListAppThread(requireContext()).execute()
        return inflater.inflate(R.layout.fragment_list_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvListApp.layoutManager = GridLayoutManager(requireContext(), 3)
        rvListApp.adapter = rvAdapter
        ivListAppNavigation.setOnClickListener {
            (context as BaseActivity).changeFragment(1)
        }
    }

    private fun updateListApp() {
        rvAdapter.notifyDataSetChanged()
    }

    inner class ListAppThread(val context: Context): AsyncTask<Void, Void, ArrayList<AppInfo>>() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun doInBackground(vararg param: Void?): ArrayList<AppInfo> {
            val pManager = context.packageManager
            val i = Intent(Intent.ACTION_MAIN,null).addCategory(Intent.CATEGORY_LAUNCHER)
            val apps = pManager.queryIntentActivities(i, 0)
            val pd = context.getDrawable(R.drawable.ic_sn_imei)
            pd?.let {
                AppInfo(
                    "PrintDiag",
                    "printdiag",
                    it
                )
            }?.let {
                listApp.add(
                    it

                )
            }
            for(app in apps){
                if(app.loadLabel(pManager) != "PrintDiag") {
                    listApp.add(
                        AppInfo(
                            app.loadLabel(pManager),
                            app.activityInfo.packageName,
                            app.activityInfo.loadIcon(pManager)
                        )
                    )
                    Log.d(TAG, app.activityInfo.packageName)
                }
            }
            return listApp
        }

        override fun onPostExecute(result: ArrayList<AppInfo>?) {
            super.onPostExecute(result)
            updateListApp()
        }
    }



}