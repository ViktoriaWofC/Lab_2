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


/**
 * Created by User on 17.11.2016.
 */

public class GetParticipantService extends IntentService {
    public GetParticipantService() {
        super("GetParticiantService");
    }

    DatabaseReference databaseReference;

    Participant part;
    ParticipantListener listener;
    String login = "";

    public static final String ACTION_MYINTENTSERVICE = "com.example.user.lab_2part.RESPONSE";
    public static final String PART = "PART";
    public static final String LOGIN = "LOGIN";
    public static final String NETWORK = "NETWORK";

    @Override
    protected void onHandleIntent(Intent intent) {

        login = intent.getStringExtra(LOGIN);

        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        if(ni!=null&&ni.isConnected()) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            listener = new ParticipantListener();
            databaseReference.child("participant").addValueEventListener(listener);
        }
        else {
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(NETWORK,"0");
            sendBroadcast(responseIntent);
            stopSelf();
        }
    }

    public class ParticipantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Participant patt;
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                patt = (Participant) child.getValue(Participant.class);
                if(login.equals(patt.getLogin()))
                {
                    part = patt;
                }
            }
            databaseReference.child("participant").removeEventListener(listener);

            Intent responseIntent = new Intent();
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(PART, (Serializable) part);
            responseIntent.putExtra(NETWORK,"1");
            sendBroadcast(responseIntent);
            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


}
