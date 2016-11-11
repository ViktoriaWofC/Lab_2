package com.example.user.lab_2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import static com.example.user.lab_2.TestClass.RQS_RECORDING;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /////
    private DatabaseReference mSimpleFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mMeetingRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMeetingEditText;
    /////
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;

    private TextView t;

    /////
    private static final int READ_BLOCK_SIZE = 100;
    Uri savedUri;

    private boolean b = true;
    private ValueEventListener listener;
    private ParticipantListener participantListener;
    private MeetingListener meetingListener;


    public static class FirechatMeetingViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView userTextView;
        public TextView stringTextView;

        public FirechatMeetingViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            stringTextView = (TextView) itemView.findViewById(R.id.stringTextView);
        }

    }

    /////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //////////////////////////////////////////////////////////////////////////
        mMeetingEditText = (EditText)findViewById(R.id.editText);

        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        //mMeetingRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        //mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setStackFromEnd(true);
        //mMeetingRecyclerView.setLayoutManager(mLinearLayoutManager);


        mSimpleFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //mMeetingRecyclerView.setLayoutManager(mLinearLayoutManager);
        //mMeetingRecyclerView.setAdapter(mFirebaseAdapter);

        t = (TextView)findViewById(R.id.testText);

        participantListener = new ParticipantListener();
        meetingListener = new MeetingListener();



        ////////////////////////////////////////////
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, AuthorizationActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }

        //////////////////////////////

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);



        t.setText(dateString);

        ////////////////////////////////////////////////
        Button tb = (Button)findViewById(R.id.testBut);
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, RQS_RECORDING);
            }
        });

        Button tb2 = (Button)findViewById(R.id.testBut2);
        tb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(MainActivity.this);

            }
        });

    }

    public class MeetingListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Meeting meet;
            String s = "";
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                meet = (Meeting) child.getValue(Meeting.class);//Participant.class);
                s += meet.getName() + " ";
            }
            t.setText("yes " + s);
            mSimpleFirebaseDatabaseReference.child("meetings").removeEventListener(meetingListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public class ParticipantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Participant pat;
            String s = "";
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                pat = (Participant) child.getValue(Participant.class);//Participant.class);
                s += pat.getFirstName() + " ";
            }
            t.setText("yes " + s);
            mSimpleFirebaseDatabaseReference.child("participant").removeEventListener(participantListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void playAudio(Context context){
        String name  = AudioAttach.getNameAudio(MainActivity.this,savedUri);
        String audioStr = AudioAttach.getStringFromAudio(MainActivity.this, savedUri);
        Uri uri = AudioAttach.getAudioFromString(context,name,audioStr);
        AudioAttach.playAudio(MainActivity.this,uri);
    }

    public void checkParticipant(String name){
        Participant pat = new Participant("2n","1n","3n","direct");
        //GsonBuilder builder = new GsonBuilder();
        //Gson gson = builder.create();
        //mSimpleFirebaseDatabaseReference.child("participant").push().setValue(gson.toJson(pat));

        mSimpleFirebaseDatabaseReference.child("participant").push().setValue(pat);

    }

    public void onClickTest(View v){
        //checkParticipant("Slava");
        //b = true;
        //mSimpleFirebaseDatabaseReference.child("participant").addValueEventListener(participantListener);
        mSimpleFirebaseDatabaseReference.child("meetings").addValueEventListener(meetingListener);
    }

    public void attachAudio(){
        String audioStr = AudioAttach.getStringFromAudio(MainActivity.this, savedUri);
        //AudioAttach.playAudio(MainActivity.this, savedUri);
    }

    public void createMeeting(){

        String audioStr = "";
        ////////////////////////////////////////////////////

        Meeting meeting = new
                Meeting(mMeetingEditText.getText().toString(), mUsername,audioStr);
        mSimpleFirebaseDatabaseReference.child("meetings")
                .push().setValue(meeting);
        mMeetingEditText.setText("");
    }

    public void onClickSend(View v){
        createMeeting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_RECORDING) {
            savedUri = data.getData();
            Toast.makeText(MainActivity.this,
                    "Saved: " + savedUri.getPath(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out_menu:

                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = "";//DEFAULT_NAME;
                startActivity(new Intent(this, AuthorizationActivity.class));
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
