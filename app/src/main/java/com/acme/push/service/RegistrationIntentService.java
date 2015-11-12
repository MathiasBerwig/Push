package com.acme.push.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.acme.push.Config;
import com.acme.push.Contract;
import com.acme.push.model.ClientRegistation;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getName();

    public RegistrationIntentService() {
        super(TAG);
    }

    public static final String ACTION = "action";
    public static final int ACTION_REGISTER = 0;
    public static final int ACTION_UNREGISTER = 1;

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                if (!intent.hasExtra(ACTION)) throw new Exception("Extra ACTION not found.");
                int actionCode = intent.getIntExtra(ACTION, -1);

                // Initially this call goes out to the network to retrieve the token, subsequent calls are local.
                InstanceID instanceID = InstanceID.getInstance(this);
                String user = intent.getStringExtra(Contract.REGISTRATION.USERNAME);
                String token = instanceID.getToken(Config.GCM.DEFAULT_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                ClientRegistation registration = new ClientRegistation(user, token);

                boolean commitSuccessful = commitToServer(registration, actionCode);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putString(Contract.REGISTRATION.TOKEN, token).apply();
                sharedPreferences.edit().putString(Contract.REGISTRATION.USERNAME, user).apply();
                sharedPreferences.edit().putBoolean(Contract.REGISTRATION.SENT_TOKEN_TO_SERVER, commitSuccessful).apply();
            }
        } catch (Exception e) {
            Log.i(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Contract.REGISTRATION.SENT_TOKEN_TO_SERVER, false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Contract.REGISTRATION.REGISTRATION_COMPLETE);
        sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account maintained by your application.
     */
    private boolean commitToServer(ClientRegistation registration, int action) throws IOException {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, new Gson().toJson(registration));
        Request request = new Request.Builder()
                .url(action == ACTION_UNREGISTER ? Config.ADRESSES.DELETE_USER_URL : Config.ADRESSES.INSERT_USER_URL)
                .post(body)
                .build();
        return client.newCall(request).execute().isSuccessful();
    }
}
