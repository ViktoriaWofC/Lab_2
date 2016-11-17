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

public class CreateMeetingService extends IntentService {
    public CreateMeetingService() {
        super("CreateMeetingService");
    }

    DatabaseReference databaseReference;
    Meeting meet;

    public static final String ACTION_MYINTENTSERVICE = "com.example.user.lab_2cre.RESPONSE";
    public static final String MEET = "MEET";
    public static final String NETWORK = "NETWORK";

    @Override
    protected void onHandleIntent(Intent intent) {

        meet = (Meeting) intent.getSerializableExtra(MEET);

        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("meetings").push().setValue(meet);

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
