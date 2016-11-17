package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 17.11.2016.
 */

public class UpdateValueService extends IntentService {
    public UpdateValueService() {
        super("UpdateValueService");
    }

    DatabaseReference databaseReference;

    List<String> parts = new ArrayList<>();
    List<String> music = new ArrayList<>();
    String key = "";

    public static final String ACTION_MYINTENTSERVICE = "com.example.user.lab_2val.RESPONSE";
    public static final String PARTS = "PARTS";
    public static final String FILES = "FILES";
    public static final String KEY = "KEY";
    public static final String NETWORK = "NETWORK";

    @Override
    protected void onHandleIntent(Intent intent) {

        parts = intent.getStringArrayListExtra(PARTS);
        music = intent.getStringArrayListExtra(FILES);
        key = intent.getStringExtra(KEY);

        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            Map<String,Object> m = new HashMap<String,Object>();
            m.put("participants",parts);
            databaseReference.child("meetings").child(key).updateChildren(m);
            m.put("file",music);
            databaseReference.child("meetings").child(key).updateChildren(m);

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
