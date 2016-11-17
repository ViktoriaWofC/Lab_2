package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by User on 17.11.2016.
 */

public class PlayAudioService extends IntentService {
    public PlayAudioService() {
        super("PlayAudioService");
    }

    DatabaseReference databaseReference;
    Uri uri;
    Context context;

    public static final String ACTION_MYINTENTSERVICE = "com.example.user.lab_2uri.RESPONSE";
    public static final String URI = "URI";
    public static final String CONT = "CONT";

    @Override
    protected void onHandleIntent(Intent intent) {

        uri = (Uri) intent.getSerializableExtra(URI);
        context = (Context)intent.getSerializableExtra(CONT);
        AudioAttach.playAudio(this,uri);
        //stopSelf();
    }
}
