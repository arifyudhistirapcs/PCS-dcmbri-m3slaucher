package id.co.pcsindonesia.ia.diagnostic;

import android.content.Intent;
import android.os.Bundle;
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
import id.co.pcsindonesia.ia.diagnostic.helper.HttpHelper;
import id.co.pcsindonesia.ia.diagnostic.util.ENC;
import id.co.pcsindonesia.ia.diagnostic.helper.HttpsTrustManager;
import sunmi.sunmiui.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class PinActivity extends AppCompatActivity {

    private HttpHelper httpHelper;
    private static final String TAG = "HTTP Helper";
    private  int isPINTrue = 0;
    private  int isSetup = 0;

    Button button;
    EditText pin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);


        httpHelper = new HttpHelper(this);

        pin = (EditText) findViewById(R.id.pin);
        button = (Button) findViewById(R.id.submitPIN);

        getSupportActionBar().setTitle("DCM BRI");



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject postparams = new JSONObject();
                JSONObject reqObject = new JSONObject();
                try {
                    postparams.put("pin", pin.getText().toString());
                    reqObject.put("token", ENC.encrypt(postparams.toString()));
                    Log.e(TAG,"Send data PIN edc: ");
                } catch (JSONException e) {
                    Log.e(TAG,"Send secondary data PIN-Error: "+e.getMessage());
                }

                LogUtil.e("TAG", "PIN OBJ " + reqObject);


                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        "https://bbril.xt.pcsindonesia.co.id/brilink_api/api/edc/checkPIN", reqObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e(TAG,"Post PIN EDC: "+response);
//                                Toast.makeText(getApplicationContext(),"Success".toString(),Toast.LENGTH_SHORT).show();
                                sendData();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG,"Post PIN EDC-error: "+error);
                                Toast.makeText(getApplicationContext(),"Wrong PIN Number, please try again".toString(),Toast.LENGTH_SHORT).show();
                            }
                        })
                {
                };
                final RequestQueue requestQueue;
//                requestQueue = Volley.newRequestQueue(PinActivity.this.getBaseContext(), HttpsTrustManager.getHurlStack(PinActivity.this.getBaseContext()));
                requestQueue = Volley.newRequestQueue(PinActivity.this.getBaseContext());
                requestQueue.add(jsonObjReq);

                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        requestQueue.getCache().clear();
                    }
                });


            }
        });


    }

    public void sendData(){
        Bundle extras = getIntent().getExtras();

        JSONObject postparams = new JSONObject();
        JSONObject object = new JSONObject();
                try {
                    postparams.put("sn", extras.getString("sn"));
                    postparams.put("merchant_name", extras.getString("merchant_name"));
                    postparams.put("merchant_code",extras.getString("merchant_code"));
                    postparams.put("address", extras.getString("address"));
                    postparams.put("email", extras.getString("email"));
                    postparams.put("phone", extras.getString("phone"));
                    postparams.put("long", extras.getString("longitude"));
                    postparams.put("lat", extras.getString("latitude"));

                    object.put("token", ENC.encrypt(postparams.toString()));
                    Log.e(TAG,"Send data set up edc: "+GlobalHelper.getIMEI(PinActivity.this)+" ; "+GlobalHelper.getSN()+" ; "+App.getDeviceModel()+" ; "+App.getVersion());
                } catch (JSONException e) {
                    Log.e(TAG,"Send secondary data set up edc-Error: "+e.getMessage());
                }
                LogUtil.e("TAG","intnet " + extras.getString("latitude") );
                LogUtil.e("TAG","postparams " + postparams );
                LogUtil.e("TAG","JSON OBJCT " + object );


                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        "https://bbril.xt.pcsindonesia.co.id/brilink_api/api/edc/setEDC_enc", object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e(TAG,"Post Setup EDC: "+response);
                                Toast.makeText(getApplicationContext(),"Success Setup EDC",Toast.LENGTH_SHORT).show();
                                mainActivity();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG,"Post Setup EDC-error: "+ String.valueOf(error));
                                Toast.makeText(getApplicationContext(),"Something wrong, please try again",Toast.LENGTH_SHORT).show();

                            }
                        })
                {
                };

                final RequestQueue requestQueue;
//                requestQueue = Volley.newRequestQueue(PinActivity.this.getBaseContext(), HttpsTrustManager.getHurlStack(PinActivity.this.getBaseContext()));
                requestQueue = Volley.newRequestQueue(PinActivity.this.getBaseContext());
                requestQueue.add(jsonObjReq);

                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        requestQueue.getCache().clear();

                    }
                });

    }


    public void mainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
