package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by User on 12.11.2016.
 */

public class FirebaseNewParticipantService extends IntentService {
    public FirebaseNewParticipantService() {
        super("FirebaseNewParticipantService");
    }

    static final String F = "f";
    static final String L = "l";
    static final String M = "m";
    static final String P = "p";

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("!!!!!!!","FirebaseNewParticipantService");
        Bundle bundle = intent.getExtras();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Participant pat = new Participant("r","r","r","r");
        Participant pat = new Participant(intent.getStringExtra(L),intent.getStringExtra(F),intent.getStringExtra(M),intent.getStringExtra(P));
        //Participant pat = new Participant(bundle.getString(L),bundle.getString(F),bundle.getString(M),bundle.getString(P));
        databaseReference.child("participant").push().setValue(pat);
        //stopSelf();
    }
}
