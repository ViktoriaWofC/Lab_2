package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    @Override
    protected void onHandleIntent(Intent intent) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        participantListener = new ParticipantListener();
        databaseReference.child("participant").addValueEventListener(participantListener);

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

            //Intent intent = new Intent();
            //startService(intent);
            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
