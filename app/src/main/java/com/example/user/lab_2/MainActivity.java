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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.example.user.lab_2.TestClass.RQS_RECORDING;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /////
    private DatabaseReference mSimpleFirebaseDatabaseReference;
    //private FirebaseRecyclerAdapter mFirebaseAdapter;
    //private RecyclerView mMeetingRecyclerView;
    //private LinearLayoutManager mLinearLayoutManager;
    private EditText mMeetingEditText;
    private TextView userName;
    RecyclerView recyclerMeeting;
    /////
    //private GoogleApiClient mGoogleApiClient;
    //private FirebaseAuth mFirebaseAuth;
    //private FirebaseUser mFirebaseUser;
    private String mUsername;

    private TextView t;

    List<Meeting> meetings = new ArrayList<>();
    //List<Participant> participants = new ArrayList<>();
    Participant participant;

    /////
    private static final int READ_BLOCK_SIZE = 100;
    Uri savedUri;

    private boolean b = true;
    private ValueEventListener listener;
    private ParticipantListener participantListener;
    private MeetingListener meetingListener;

    String login = "";
    String stringFIO = "";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = MainActivity.this;

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
        userName = (TextView)findViewById(R.id.userName);


        mSimpleFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //mMeetingRecyclerView.setLayoutManager(mLinearLayoutManager);
        //mMeetingRecyclerView.setAdapter(mFirebaseAdapter);

        t = (TextView)findViewById(R.id.testText);

        participantListener = new ParticipantListener();
        meetingListener = new MeetingListener();


        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);

        t.setText(dateString);
        ///////////////////////////////////////////////////////////


        //получаем логин из интента
        mSimpleFirebaseDatabaseReference.child("participant").addValueEventListener(participantListener);

        mSimpleFirebaseDatabaseReference.child("meetings").addValueEventListener(meetingListener);

        recyclerMeeting = (RecyclerView)findViewById(R.id.recyclerMeeting);
        recyclerMeeting.setLayoutManager(new LinearLayoutManager(this));


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

    class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameTextView;
        public TextView userTextView;
        public TextView stringTextView;
        private CardView cv;

        public MeetingViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            stringTextView = (TextView) itemView.findViewById(R.id.stringTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }

        /*public MeetingViewHolder(ViewGroup parent) {
            super(getLayoutInflater().inflate(R.layout.meeting_item, parent, false));
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            stringTextView = (TextView) itemView.findViewById(R.id.stringTextView);
        }*/
    }

    class MeetingAdaper extends RecyclerView.Adapter<MeetingViewHolder> {

       /* private List<Meeting> meetings;

        public MeetingAdaper(List<Meeting> meet){
            meetings = meet;
        }*/

        @Override
        public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.meeting_item, parent, false);
// create ViewHolder
            MeetingViewHolder viewHolder = new MeetingViewHolder(itemLayoutView);
            return viewHolder;
            //return new MeetingViewHolder(View.inflate(context, R.layout.meeting_item, null));
        }

        @Override
        public void onBindViewHolder(MeetingViewHolder holder, int position) {
            holder.nameTextView.setText(meetings.get(position).getName());
            holder.userTextView.setText(meetings.get(position).getNames());
        }

        @Override
        public int getItemCount() {
            return meetings.size();
        }
    }

    public class MeetingListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                meetings.add((Meeting) child.getValue(Meeting.class));
            }
            mSimpleFirebaseDatabaseReference.child("meetings").removeEventListener(meetingListener);
            //
            recyclerMeeting.setAdapter(new MeetingAdaper());
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
                //if(login.equals(pat.getLogin()))
                {
                    participant = pat;
                    stringFIO = "Ivaaaan";
                    //stringFIO = pat.getLastName()+ " "+ pat.getFirstName()+" "+pat.getMiddleName();
                    userName.setText(stringFIO);
                }
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
        //audioStr - from json
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

        //mSimpleFirebaseDatabaseReference.child("meetings").addValueEventListener(meetingListener);
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

                //mFirebaseAuth.signOut();
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = "";//DEFAULT_NAME;
                startActivity(new Intent(this, AuthorizationActivity.class));
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
