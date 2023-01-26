package id.co.pcsindonesia.ia.diagnostic;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import id.co.pcsindonesia.ia.diagnostic.R;

import androidx.appcompat.app.AppCompatActivity;

public class AboutPcsActivity extends AppCompatActivity {

    //Define WebView
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("About PCS");

        setContentView(R.layout.activity_about_pcs);
        webView = (WebView)findViewById(R.id.aboutpcs); //get webView

        WebSettings webSettings = webView.getSettings();// initiate webView settings
        webSettings.setJavaScriptEnabled(true); //allow webView perform javascript
        webSettings.setBuiltInZoomControls(true); //show zoom control


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("Error", description);
            }   });

        webView.loadUrl("https://pcsindonesia.co.id/?page_id=4117"); //load URL

    }
}
