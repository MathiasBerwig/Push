package com.acme.push.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.acme.push.Contract;
import com.acme.push.R;
import com.acme.push.service.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = MainActivity.class.getName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // GUI
    @Bind(R.id.edtUsername) TextView edtUsername;
    @Bind(R.id.edtLog) TextView edtLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind ButterKnife
        ButterKnife.bind(this);

        // Get the last username and set the editText
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString(Contract.REGISTRATION.USERNAME, "");
        edtUsername.setText(username);

        // Register Receivers
        registerReceiver(regCompleteReceiver, regCompleteFilter);
        registerReceiver(messageReceivedReceiver, messageReceivedFilter);

        // Get extras from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            printBundle(extras);
        } else {
            Toast.makeText(this, "intent extras is null", Toast.LENGTH_LONG).show();
        }
    }

    private IntentFilter regCompleteFilter = new IntentFilter(Contract.REGISTRATION.REGISTRATION_COMPLETE);
    private BroadcastReceiver regCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean sentToken = sharedPreferences.getBoolean(Contract.REGISTRATION.SENT_TOKEN_TO_SERVER, false);
            String message = sentToken ? "Success" : "Error";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    };

    private IntentFilter messageReceivedFilter = new IntentFilter(Contract.REGISTRATION.MESSAGE_RECEIVED);
    private BroadcastReceiver messageReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            printBundle(intent.getExtras());
        }
    };

    @Override
    protected void onResume() {
        registerReceiver(regCompleteReceiver, regCompleteFilter);
        registerReceiver(messageReceivedReceiver, messageReceivedFilter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(regCompleteReceiver);
        unregisterReceiver(messageReceivedReceiver);

        super.onPause();
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        if (checkPlayServices() && checkUsername()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
            intent.putExtra(RegistrationIntentService.ACTION, RegistrationIntentService.ACTION_REGISTER);
            intent.putExtra(Contract.REGISTRATION.USERNAME, edtUsername.getText().toString().trim());
            startService(intent);
        }
    }

    @OnClick(R.id.btnUnregister)
    public void unregister() {
        if (checkUsername()) {
            // Start IntentService to unregister this application with GCM.
            Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
            intent.putExtra(RegistrationIntentService.ACTION, RegistrationIntentService.ACTION_UNREGISTER);
            intent.putExtra(Contract.REGISTRATION.USERNAME, edtUsername.getText().toString().trim());
            startService(intent);
        }
    }

    @OnClick(R.id.btnClear)
    public void clear() {
        edtLog.setText("");
    }

    private boolean checkUsername() {
        String username = edtUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Invalid username");
            return false;
        }
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "This device does not support Google Play Services.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();

        if (bundle.containsKey("JSON")) {
            String messageData = bundle.getString("JSON");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(messageData);
            sb.append(gson.toJson(je));
        } else {
            for (String key: bundle.keySet()) {
                sb.append(key).append(":").append(bundle.get(key)).append("\n");
            }
        }
        edtLog.setText(sb.toString());
    }
}
