package id.co.pcsindonesia.ia.diagnostic.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import id.co.pcsindonesia.ia.diagnostic.ThreadService;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        context.startService(new Intent(context, ThreadService.class));
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //we double check here for only boot complete event
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            //here we start the service  again.
            Intent serviceIntent = new Intent(context, ThreadService.class);
            context.startService(serviceIntent);
        }
    }
}

