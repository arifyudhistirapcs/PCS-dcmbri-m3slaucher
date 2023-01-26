package id.co.pcsindonesia.ia.diagnostic;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import id.co.pcsindonesia.ia.diagnostic.R;
import id.co.pcsindonesia.ia.diagnostic.helper.GlobalHelper;

import androidx.appcompat.app.AppCompatActivity;

public class AboutAppActivity extends AppCompatActivity {


    TextView about_sn, about_imei, about_iccid, about_type,about_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("About App");

        setContentView(R.layout.activity_about_app);

        about_sn = (TextView) findViewById(R.id.about_sn);
        about_iccid = (TextView) findViewById(R.id.about_iccid);
        about_imei = (TextView) findViewById(R.id.about_imei);
        about_type = (TextView) findViewById(R.id.about_type);
        about_version = (TextView) findViewById(R.id.about_version);

        about_sn.setText("SN: "+ GlobalHelper.getSN());
        String iccid = GlobalHelper.getICCID(this);
        if(iccid == null){
            about_iccid.setText("ICCID: ");
        }else{
            about_iccid.setText("ICCID: "+iccid);
        }

        about_imei.setText("IMEI: "+GlobalHelper.getIMEI(this));
        about_type.setText("Device Type: "+App.getDeviceModel());

        try {
            PackageInfo pInfo = AboutAppActivity.this.getPackageManager().getPackageInfo(AboutAppActivity.this.getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            about_version.setText(version+" "+verCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }
}
