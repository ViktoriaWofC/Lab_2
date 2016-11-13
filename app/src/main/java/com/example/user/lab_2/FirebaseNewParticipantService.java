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

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Participant pat = new Participant(intent.getStringExtra("l"),intent.getStringExtra("f"),intent.getStringExtra("m"),intent.getStringExtra("p"));
        databaseReference.child("participant").push().setValue(pat);
        stopSelf();
    }
}
