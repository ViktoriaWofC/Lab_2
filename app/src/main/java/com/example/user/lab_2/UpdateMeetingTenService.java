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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 17.11.2016.
 */

public class UpdateMeetingTenService extends IntentService {
    public UpdateMeetingTenService() {
        super("UpdateMeetingTenService");
    }

    DatabaseReference databaseReference;

    List<Meeting> meetings = new ArrayList<>();
    List<String> keys = new ArrayList<>();
    String today = "";
    MeetingListener meetingListener;

    public static final String ACTION_MYINTENTSERVICE = "com.example.user.lab_2upten.RESPONSE";
    public static final String MEETING = "MEETING";
    public static final String KEYS = "KEYS";

    @Override
    protected void onHandleIntent(Intent intent) {
        today = getTodayDate();

        Intent responseIntent = new Intent();

        ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMan.getActiveNetworkInfo();
        while(true) {
            if (ni != null && ni.isConnected()) {
                databaseReference = FirebaseDatabase.getInstance().getReference();
                meetingListener = new MeetingListener(databaseReference);
                databaseReference.child("meetings").addValueEventListener(meetingListener);
            } else {
                responseIntent.setAction(ACTION_MYINTENTSERVICE);
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                responseIntent.putExtra(KEYS, " ");
                sendBroadcast(responseIntent);
            }
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                    if(compareDate(end,today)==1||compareDate(end,today)==0) {
                        meetings.add(meet);
                        keys.add(child.getKey());
                    }
            }
            //mSimpleFirebaseDatabaseReference.child("meetings").removeEventListener(meetingListener);
            ref.child("meetings").removeEventListener(this);

            Intent responseIntent = new Intent();
            responseIntent.setAction(ACTION_MYINTENTSERVICE);
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra(MEETING, (Serializable) meetings);
            responseIntent.putExtra(KEYS, (Serializable) keys);
            sendBroadcast(responseIntent);
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

    public String getTodayDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);
        return dateString;
    }
}
