package com.acme.push.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.acme.push.Contract;

public class MyGcmReceiver extends com.google.android.gms.gcm.GcmReceiver {

    private static final String TAG = MyGcmReceiver.class.getName();
    public static final String MESSAGE_DATA = "messageData";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG, "onReceive");

        // Process message
        Intent broadcast = new Intent(Contract.REGISTRATION.MESSAGE_RECEIVED);
        broadcast.putExtra(MESSAGE_DATA, bundle2string(intent.getExtras()));
        context.sendBroadcast(broadcast);
    }

    public static String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";\n";
        }
        string += " }Bundle";
        return string;
    }
}