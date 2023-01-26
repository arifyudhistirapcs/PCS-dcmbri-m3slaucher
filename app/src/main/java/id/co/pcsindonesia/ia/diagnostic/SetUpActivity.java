package id.co.pcsindonesia.ia.diagnostic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import id.co.pcsindonesia.ia.diagnostic.R;
import id.co.pcsindonesia.ia.diagnostic.helper.GlobalHelper;
import id.co.pcsindonesia.ia.diagnostic.helper.HttpHelper;
import id.co.pcsindonesia.ia.diagnostic.model.LocationModel;
import id.co.pcsindonesia.ia.diagnostic.sqlite.DBConfig;
import id.co.pcsindonesia.ia.diagnostic.util.ENC;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.co.pcsindonesia.ia.diagnostic.helper.HttpsTrustManager;
import sunmi.sunmiui.utils.LogUtil;

public class SetUpActivity extends AppCompatActivity {

    private HttpHelper httpHelper;
    private static final String TAG = "HTTP Helper";

    EditText sn,longitude_view,latitude_view,type,imei,merch_name,merch_code,address,email,phone,input_long,input_lat;


    Button button,refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        DBConfig config = new DBConfig(this);
        String longitude = "Cannot get longitude";
        String latitude = "Cannot get latitude";

        merch_name = (EditText) findViewById(R.id.merch_name);
        merch_code = (EditText) findViewById(R.id.merch_code);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.address);
        input_long = (EditText) findViewById(R.id.input_long);
        input_lat = (EditText) findViewById(R.id.input_lat);

        sn = (EditText) findViewById(R.id.sn);
        sn.setText(GlobalHelper.getSN());

        type = (EditText) findViewById(R.id.type);
        type.setText(App.getDeviceModel());

        imei = (EditText) findViewById(R.id.imei);
        imei.setText(GlobalHelper.getIMEI(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setTitle("Setup EDC");

        longitude_view = (EditText) findViewById(R.id.longitude);
        latitude_view = (EditText) findViewById(R.id.latitude);

        httpHelper = new HttpHelper(this);

        if(config.getAllLocation().size() > 0){
            LocationModel locationModel = config.getAllLocation().get(0);

            longitude_view.setText(""+locationModel.getLONGITUDE());
            latitude_view.setText(""+locationModel.getLATITUDE());
        }else {

            longitude_view.setText(""+longitude);
            latitude_view.setText(""+latitude);
        }

        button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openNewActivity();

            }
        });

        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

    }

    public void openNewActivity(){
        DBConfig config = new DBConfig(SetUpActivity.this);
        String longitude = "0";
        String latitude = "0";

        if (input_long.getText().toString().equals("") || input_lat.getText().toString().equals("") ) {
            if(config.getAllLocation().size() > 0){
                LocationModel locationModel = config.getAllLocation().get(0);
                longitude = String.valueOf(locationModel.getLONGITUDE()) ;
                latitude =  String.valueOf(locationModel.getLATITUDE());
            }
        }else{
            longitude = input_long.getText().toString();
            latitude = input_lat.getText().toString();
        }

        Intent intent = new Intent(this, PinActivity.class);

        intent.putExtra("sn",  GlobalHelper.getSN());
        intent.putExtra("merchant_name",  merch_name.getText().toString());
        intent.putExtra("merchant_code",  merch_code.getText().toString());
        intent.putExtra("address",  address.getText().toString());
        intent.putExtra("email",  email.getText().toString());
        intent.putExtra("phone",  phone.getText().toString());
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude",  latitude);

        startActivity(intent);
    }




}
