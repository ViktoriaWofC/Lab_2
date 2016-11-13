package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.DatePicker;
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
    private TextView dateToday;
    private Button updateButton;
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
    String today = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = MainActivity.this;
        today = getTodayDate();
        login = getIntent().getStringExtra("login");

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
        dateToday = (TextView)findViewById(R.id.today);
        dateToday.setText("Today: "+getTodayDate());

        updateButton = (Button)findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meetingListener = new MeetingListener(mSimpleFirebaseDatabaseReference);
                mSimpleFirebaseDatabaseReference.child("meetings").addValueEventListener(meetingListener);
            }
        });

        mSimpleFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //mMeetingRecyclerView.setLayoutManager(mLinearLayoutManager);
        //mMeetingRecyclerView.setAdapter(mFirebaseAdapter);

        t = (TextView)findViewById(R.id.testText);

        participantListener = new ParticipantListener();
        //meetingListener = new MeetingListener();


        t.setText(getTodayDate());
        ///////////////////////////////////////////////////////////


        //получаем логин из интента
        mSimpleFirebaseDatabaseReference.child("participant").addValueEventListener(participantListener);

        //startService(new Intent(this,FirebaseOnceUpdateMeetingService.class));
        //startService(new Intent(context,FirebaseUpdateMeetingService.class));

        meetingListener = new MeetingListener(mSimpleFirebaseDatabaseReference);
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

    public String getTodayDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);
        return dateString;
    }

    class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameTextView;
        private CardView cv;

        public MeetingViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);

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
            MeetingViewHolder viewHolder = new MeetingViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MeetingViewHolder holder, int position) {
            holder.nameTextView.setText(meetings.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return meetings.size();
        }
    }

    public class MeetingListener implements ValueEventListener {

        private DatabaseReference ref;

        public MeetingListener(DatabaseReference r){
            ref = r;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Meeting meet;
            String start,end;
            meetings.clear();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                meet = (Meeting) child.getValue(Meeting.class);
                start = meet.getDateStart();
                end = meet.getDateEnd();
                if(compareDate(start,today)==-1||compareDate(start,today)==0)
                    if(compareDate(end,today)==1||compareDate(end,today)==0)
                        meetings.add(meet);
            }
            //mSimpleFirebaseDatabaseReference.child("meetings").removeEventListener(meetingListener);
            ref.child("meetings").removeEventListener(this);
            //
            recyclerMeeting.setAdapter(new MeetingAdaper());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public int compareDate(String d1, String d2){
        //d1>d2 - 1
        //d1=d2 - 0
        //d1<d2 - -1
        int month1 = Integer.valueOf(d1.substring(3, 5));
        int day1 = Integer.valueOf(d1.substring(0, 2));
        int year1 = Integer.valueOf(d1.substring(6));
        int month2 = Integer.valueOf(d2.substring(3, 5));
        int day2 = Integer.valueOf(d2.substring(0, 2));
        int year2 = Integer.valueOf(d2.substring(6));

        if(year1>year2) return 1;
        else if(year1<year2) return -1;
        else if(month1>month2) return 1;
        else if(month1<month2) return -1;
        else if(day1>day2) return 1;
        else if(day1<day2) return -1;
        else return 0;
    }

    public class ParticipantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Participant pat;
            String s = "";
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                pat = (Participant) child.getValue(Participant.class);//Participant.class);
                if(login.equals(pat.getLogin()))
                {
                    participant = pat;
                    stringFIO = pat.getLastName()+ " "+ pat.getFirstName()+" "+pat.getMiddleName();
                    userName.setText("User: "+stringFIO);
                }
            }
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
        Participant pat = new Participant("2n","1n","3n","direct","2n","2n");

        mSimpleFirebaseDatabaseReference.child("participant").push().setValue(pat);

    }

    public void onClickTest(View v){
        //checkParticipant("Slava");
        //b = true;

        //mSimpleFirebaseDatabaseReference.child("participant").addValueEventListener(participantListener);

        //mSimpleFirebaseDatabaseReference.child("meetings").addValueEventListener(meetingListener);

        createMeeting();

        //Date d = new Date();

    }

    public void attachAudio(){
        String audioStr = AudioAttach.getStringFromAudio(MainActivity.this, savedUri);
        //AudioAttach.playAudio(MainActivity.this, savedUri);
    }

    public void createMeeting(){

        String audioStr = "";
        ////////////////////////////////////////////////////

        Meeting meeting = new
                Meeting("meeting", "text",getTodayDate(),getTodayDate(),"Planned",audioStr);
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
