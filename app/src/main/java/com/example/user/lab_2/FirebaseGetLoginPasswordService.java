package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 13.11.2016.
 */

public class FirebaseGetLoginPasswordService extends IntentService {
    public FirebaseGetLoginPasswordService() {
        super("FirebaseCheckLoginService");
    }

    List<String> logins = new ArrayList<>();
    List<String> passwords = new ArrayList<>();
    DatabaseReference databaseReference;
    ParticipantListener participantListener;
    public static final String ACTION_MYINTENTSERVICE = "com.example.user.lab_2.RESPONSE";
    public static final String LOGIN = "LOGIN";
    public static final String PASS = "PASS";

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()){
            databaseReference = FirebaseDatabase.getInstance().getReference();
            participantListener = new ParticipantListener();
            databaseReference.child("participant").addValueEventListener(participantListener);
        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(LOGIN, " ");
            sendBroadcast(responseIntent);
            stopSelf();
        }


        // check logon- не существует ли уже такого?
    }

    public class ParticipantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Participant pat;

            for (DataSnapshot child : dataSnapshot.getChildren()) {
                pat = (Participant) child.getValue(Participant.class);
                logins.add(pat.getLogin());
                passwords.add(pat.getPassword());
            }
            databaseReference.child("participant").removeEventListener(participantListener);

            Intent responseIntent = new Intent();
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(LOGIN, (Serializable) logins);
            responseIntent.putExtra(PASS, (Serializable) passwords);
            sendBroadcast(responseIntent);
            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
