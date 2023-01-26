package id.co.pcsindonesia.ia.diagnostic.printer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.co.pcsindonesia.ia.diagnostic.R;
import id.co.pcsindonesia.ia.diagnostic.launcher.LogUtil;
import id.co.pcsindonesia.ia.diagnostic.model.SummaryItemModel;
import id.co.pcsindonesia.ia.diagnostic.model.SummaryTrxItemModel;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class SunmiP1Helper {

    private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
    private static final String SERVICE＿ACTION = "woyou.aidlservice.jiuiv5.IWoyouService";

    private IWoyouService woyouService;
    private ICallback callback;
    private Context context;

    public SunmiP1Helper(Context context) {
        this.context = context;
        initPrinter(context);
    }

    public SunmiP1Helper() { }


    private void initPrinter(Context context) {

        this.context = context.getApplicationContext();
        Intent intents=new Intent();
        intents.setPackage("woyou.aidlservice.jiuiv5");
        intents.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        this.context.startService(intents);
        this.context.bindService(intents, connService,
                Context.BIND_AUTO_CREATE);

        callback = new ICallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {
                Log.e("SunmiP1Helper","Printer Callback onRunResult: "+isSuccess);
            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.e("SunmiP1Helper","Printer Callback onReturnString: "+result);
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {
                Log.e("SunmiP1Helper","Printer Callback onRaiseException: "+msg);
            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {
                Log.e("SunmiP1Helper","Printer Callback onPrintResult: "+msg);
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        };
    }

    public void disconnectPrinterService() {
        try {
            if (woyouService != null) {
                context.unbindService(connService);
                woyouService = null;
            }
        }catch (RuntimeException e){
            Log.e("SunmiP1Helper","Disconnecting sunmi printer: "+e.getMessage());
        }
    }

    public boolean isConnect() {
        return woyouService != null;
    }

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e("SunmiP1Helper","Disconnected");
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.e("SunmiP1Helper","Connected");
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };


    public void printSummaryTrx(List<SummaryItemModel> listSumamry){
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy - HH:mm:ss");
        try {
            Drawable d = ContextCompat.getDrawable(context, R.drawable.logo_bri_white_gray2);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            Bitmap b = Bitmap.createScaledBitmap(bitmap, 180, 155, false);
            woyouService.setAlignment(1, callback);
            woyouService.printBitmap(b, callback);
            woyouService.setAlignment(1, callback);
            woyouService.printText("\n", callback);
            woyouService.setFontSize(20, callback);
            woyouService.setAlignment(1, callback);
            woyouService.printText("======================================\n",callback);
            woyouService.setAlignment(1, callback);
            woyouService.printText("***         DIAGNOSTIC APP         ***\n",callback);
            woyouService.setAlignment(1, callback);
            woyouService.printText("======================================\n",callback);

            for(SummaryItemModel sim : listSumamry){
                woyouService.setFontSize(18,callback);
                woyouService.setAlignment(1, callback);
                woyouService.printText(sim.getTitle()+"\n",callback);
                List<SummaryTrxItemModel> listItem = sim.getListSummaryTrx();
                for(SummaryTrxItemModel stim : listItem){
                    String right = stim.getRight();
                    String left = stim.getLeft();

                    woyouService.setFontSize(15,callback);
                    woyouService.setAlignment(0, callback);
                    woyouService.printText(left,callback);

                    int spaceLeft = 48 - left.length();
                    int spaceMidd = spaceLeft - right.length();
                    for(int i=0; i < spaceMidd; i++){
                        woyouService.printText(" ",callback);
                    }
                    woyouService.printText(right+"\n",callback);
                }
                woyouService.printText("---------------------------------------------\n",callback);
            }
            woyouService.setFontSize(18, callback);
            woyouService.printText("\n",callback);
            woyouService.setAlignment(0, callback);
            woyouService.printText("Print Date: "+dateFormat.format(new Date())+"\n",callback);

            woyouService.setAlignment(2, callback);
            woyouService.setFontSize(15,callback);
            woyouService.printText("\nPowered by PCS", callback);
            woyouService.printText("\n\n\n\n\n",callback);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int checkCondition() throws RemoteException {
        try {
            return woyouService.updatePrinterState();
        } catch (Exception e){
            return -1;
        }

    }

    public String checkVersion() throws RemoteException {
        try {
            return woyouService.getPrinterVersion().replace("\n","");
        } catch (Exception e){
            return "";
        }

    }
}
