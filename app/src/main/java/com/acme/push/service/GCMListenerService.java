package com.acme.push.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.acme.push.Contract;
import com.google.android.gms.gcm.GcmListenerService;

public class GCMListenerService extends GcmListenerService {

    private static final String TAG = GCMListenerService.class.getName();
    public static final String MESSAGE_DATA = "messageData";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i(TAG, "onMessageReceived");
        // Process message
        Intent intent = new Intent(Contract.REGISTRATION.MESSAGE_RECEIVED);
//        intent.putExtra(MESSAGE_DATA, data.getString(Contract.appId));
        intent.putExtras(data);
        sendBroadcast(intent);
    }
}