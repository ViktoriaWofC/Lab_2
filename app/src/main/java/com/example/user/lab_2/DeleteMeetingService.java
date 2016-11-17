package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



/**
 * Created by User on 17.11.2016.
 */

public class DeleteMeetingService extends IntentService {
    public DeleteMeetingService() {
        super("DeleteMeetingService");
    }

    DatabaseReference databaseReference;
    String key = "";

    public static final String ACTION_MYINTENTSERVICE = "com.example.user.lab_2del.RESPONSE";
    public static final String KEY = "KEY";
    public static final String NETWORK = "NETWORK";

    @Override
    protected void onHandleIntent(Intent intent) {

        key = intent.getStringExtra(KEY);

        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("meetings").child(key).removeValue();

            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"1");
            sendBroadcast(responseIntent);
            stopSelf();
        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"0");
            sendBroadcast(responseIntent);
            stopSelf();
        }
    }
}
