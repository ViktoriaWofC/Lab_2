package com.example.user.lab_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.example.user.lab_2.TestClass.RQS_RECORDING;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private TextView userName;
    private TextView dateToday;
    private Button updateButton;
    private EditText editSearch;
    private Button searchButton;
    RecyclerView recyclerMeeting;

    List<Meeting> meetings = new ArrayList<>();
    List<String> keys = new ArrayList<>();
    int key = 0;
    Participant participant;
    List<String> parts = new ArrayList<>();
    List<String> music = new ArrayList<>();

    /////
    Uri savedUri;

    private meetingBroadcastReceiver meetingBroadcast;
    private meetingTenBroadcastReceiver meetingTenBroadcast;
    private meetingSearchBroadcastReceiver searchBroadcast;
    private participantBroadcastReceiver participantBroadcast;
    private valueBroadcastReceiver valueBroadcast;
    private deleteBroadcastReceiver deleteBroadcast;
    private createBroadcastReceiver createBroadcast;
    private notificationBroadcastReceiver notificationBroadcast;

    String login = "";
    String stringFIO = "";
    Context context;
    String today = "";
    String search = "";

    AlertDialog al;
    AlertDialog.Builder ad;
    View layoutView;

    public static final String SEARCH = "SEARCH";
    private static final int NOTIFY_ID = 101;
    public static final String NETWORK = "NETWORK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = MainActivity.this;
        today = getTodayDate();
        login = getIntent().getStringExtra("login");
        search = "";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFab(view);
            }
        });

        //////////////////////////////////////////////////////////////////////////
        userName = (TextView)findViewById(R.id.userName);
        dateToday = (TextView)findViewById(R.id.today);
        dateToday.setText("Today: "+getTodayDate());

        updateButton = (Button)findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMeetings();
                editSearch.setText("");
            }
        });

        editSearch = (EditText)findViewById(R.id.edit_search);

        searchButton = (Button)findViewById(R.id.search_buttonn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = editSearch.getText().toString();
                searchMeetings();
            }
        });

        getParticipant();

        // регистрируем BroadcastReceiver
        meetingBroadcast = new meetingBroadcastReceiver();
        IntentFilter intentFilter1 = new IntentFilter(
                UpdateMeetingService.ACTION_MYINTENTSERVICE);
        intentFilter1.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingBroadcast, intentFilter1);

        meetingTenBroadcast = new meetingTenBroadcastReceiver();
        IntentFilter intentFilter2 = new IntentFilter(
                UpdateMeetingTenService.ACTION_MYINTENTSERVICE);
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingTenBroadcast, intentFilter2);

        searchBroadcast = new meetingSearchBroadcastReceiver();
        IntentFilter intentFilter3 = new IntentFilter(
                SearchMeetingService.ACTION_MYINTENTSERVICE);
        intentFilter3.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(searchBroadcast, intentFilter3);

        participantBroadcast = new participantBroadcastReceiver();
        IntentFilter intentFilter4 = new IntentFilter(
                GetParticipantService.ACTION_MYINTENTSERVICE);
        intentFilter4.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(participantBroadcast, intentFilter4);

        valueBroadcast = new valueBroadcastReceiver();
        IntentFilter intentFilter5 = new IntentFilter(
                UpdateValueService.ACTION_MYINTENTSERVICE);
        intentFilter5.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(valueBroadcast, intentFilter5);

        deleteBroadcast = new deleteBroadcastReceiver();
        IntentFilter intentFilter6 = new IntentFilter(
                DeleteMeetingService.ACTION_MYINTENTSERVICE);
        intentFilter6.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(deleteBroadcast, intentFilter6);

        createBroadcast = new createBroadcastReceiver();
        IntentFilter intentFilter7 = new IntentFilter(
                CreateMeetingService.ACTION_MYINTENTSERVICE);
        intentFilter7.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(createBroadcast, intentFilter7);

        notificationBroadcast = new notificationBroadcastReceiver();
        IntentFilter intentFilter8 = new IntentFilter(
                UpdateMeetingTenService.ACTION_MY);
        intentFilter8.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(notificationBroadcast, intentFilter8);

        ///////////////////////////////////////////////////////////////////

        updateTenMeetings();

        recyclerMeeting = (RecyclerView)findViewById(R.id.recyclerMeeting);
        recyclerMeeting.setLayoutManager(new LinearLayoutManager(this));

    }

    public void updateMeetings(){
        Intent intent = new Intent(context,UpdateMeetingService.class);
        startService(intent);
    }

    public void updateTenMeetings(){
        Intent intent = new Intent(context,UpdateMeetingTenService.class);
        startService(intent);
    }

    public void searchMeetings(){
        Intent intent = new Intent(context,SearchMeetingService.class);
        intent.putExtra(SEARCH,search);
        startService(intent);
    }

    public void getParticipant(){
        Intent intent = new Intent(context,GetParticipantService.class);
        intent.putExtra(GetParticipantService.LOGIN,login);
        startService(intent);
    }

    public void updateValueMeetings(){
        Intent intent = new Intent(context,UpdateValueService.class);
        intent.putExtra(UpdateValueService.PARTS, (Serializable) parts);
        intent.putExtra(UpdateValueService.FILES, (Serializable) music);
        intent.putExtra(UpdateValueService.KEY, keys.get(key));
        startService(intent);
    }

    public void deleteMeetings(){
        Intent intent = new Intent(context,DeleteMeetingService.class);
        intent.putExtra(DeleteMeetingService.KEY, keys.get(key));
        startService(intent);
    }

    public void createMeeting(Meeting m){
        Intent intent = new Intent(context,CreateMeetingService.class);
        intent.putExtra(CreateMeetingService.MEET, m);
        startService(intent);
    }

    public class meetingBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(NETWORK).equals("1")){
                meetings = (List<Meeting>) intent.getSerializableExtra(UpdateMeetingService.MEETING);
                keys = intent.getStringArrayListExtra(UpdateMeetingService.KEYS);
                recyclerMeeting.setAdapter(new MainActivity.MeetingAdaper());
                Toast.makeText(MainActivity.this, "Meetings update", Toast.LENGTH_LONG).show();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
        }
    }

    public class meetingTenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(NETWORK).equals("1")){
                meetings = (List<Meeting>) intent.getSerializableExtra(UpdateMeetingTenService.MEETING);
                keys = intent.getStringArrayListExtra(UpdateMeetingTenService.KEYS);
                recyclerMeeting.setAdapter(new MainActivity.MeetingAdaper());
                Toast.makeText(MainActivity.this, "Meetings update", Toast.LENGTH_LONG).show();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
        }
    }

    public class meetingSearchBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getStringExtra(NETWORK).equals("1")){
                meetings = (List<Meeting>) intent.getSerializableExtra(SearchMeetingService.MEETING);
                keys = intent.getStringArrayListExtra(SearchMeetingService.KEYS);
                recyclerMeeting.setAdapter(new MainActivity.MeetingAdaper());
                Toast.makeText(MainActivity.this, "Meetings search", Toast.LENGTH_LONG).show();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();

        }
    }

    public class participantBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(NETWORK).equals("1")){
                participant = (Participant) intent.getSerializableExtra(GetParticipantService.PART);
                stringFIO = participant.getLastName()+ " "+ participant.getFirstName()+" "+participant.getMiddleName();
                userName.setText("User: "+stringFIO);
                //Toast.makeText(MainActivity.this, "Participant search", Toast.LENGTH_LONG).show();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
        }
    }

    public class valueBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(NETWORK).equals("1")){
                String k;
                k = intent.getStringExtra(UpdateValueService.KEY);

                updateMeetings();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
        }
    }

    public class deleteBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(NETWORK).equals("1")){
                Toast.makeText(context, "Meeting is deleted",Toast.LENGTH_LONG).show();
                updateMeetings();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
        }
    }

    public class createBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(NETWORK).equals("1")){
                Toast.makeText(context, "Meeting is created",Toast.LENGTH_LONG).show();
                updateMeetings();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
        }
    }

    public class notificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(NETWORK).equals("1")){
                createNotification();
            }else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
        }
    }

    public void createNotification(){
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent();//context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        //Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_account_circle_black_36dp)
                .setTicker("Meeting list is changed")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Notification")
                .setContentText("Meeting list was changed"); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
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
            LayoutInflater li = LayoutInflater.from(context);
            layoutView = li.inflate(R.layout.meeting_layout, null);
            TextView tv;
            int position = this.getLayoutPosition();
            key = position;

            ad = new AlertDialog.Builder(context);
            ad.setView(layoutView);
            ad.setCancelable(false);

            tv = (TextView)layoutView.findViewById(R.id.text_name);
            tv.setText(meetings.get(position).getName());

            tv = (TextView)layoutView.findViewById(R.id.text_description);
            tv.setText(meetings.get(position).getDescription());

            tv = (TextView)layoutView.findViewById(R.id.text_date_start);
            tv.setText(meetings.get(position).getDateStart());

            tv = (TextView)layoutView.findViewById(R.id.text_date_end);
            tv.setText(meetings.get(position).getDateEnd());

            tv = (TextView)layoutView.findViewById(R.id.text_priority);
            tv.setText(meetings.get(position).getPriority());

            tv = (TextView)layoutView.findViewById(R.id.text_participants);
            List<String> parts = new ArrayList<>();
            parts = meetings.get(position).getParticipants();
            if(!(parts.size()==1&&parts.get(0).equals(""))){
                String part = parts.get(0);
                for(int i = 1; i< parts.size();i++)
                    part += ", "+parts.get(i);

                tv.setText(part);
            }
            else tv.setText("No participants");

            Switch sw = (Switch)layoutView.findViewById(R.id.switchs);
            String fio = participant.getLastName()+ " "+ participant.getFirstName()+" "+participant.getMiddleName();
            if(parts.contains(fio))
                sw.setChecked(true);
            else sw.setChecked(false);
            //

            savedUri = null;
            tv = (TextView)layoutView.findViewById(R.id.text_music);
            Button play = (Button)layoutView.findViewById(R.id.button_play);
            Button delete = (Button)layoutView.findViewById(R.id.button_delete_record);
            if(meetings.get(position).getFile().size()==1) {
                play.setEnabled(false);
                delete.setEnabled(false);
                tv.setText("");
            }else {
                tv.setText("Record");
                List<String> audioStr = meetings.get(key).getFile();
                //audioStr - from json
                savedUri = AudioAttach.getAudioFromString(context,audioStr);
            }

            al = ad.create();
            al.show();
        }

        /*public MeetingViewHolder(ViewGroup parent) {
            super(getLayoutInflater().inflate(R.layout.meeting_item, parent, false));
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            stringTextView = (TextView) itemView.findViewById(R.id.stringTextView);
        }*/
    }

    class MeetingAdaper extends RecyclerView.Adapter<MeetingViewHolder> {


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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void onClickSave(View view){

        Switch sw = (Switch)layoutView.findViewById(R.id.switchs);
        String fio = participant.getLastName()+" "+participant.getFirstName()+" "+participant.getMiddleName();

        parts = meetings.get(key).getParticipants();
        if(!(parts.size()==1&&parts.get(0).equals(""))){
            if(!parts.contains(fio)&&sw.isChecked()) {
                parts.add(fio);
            }
            else if(parts.contains(fio)&&!sw.isChecked()){
                parts.remove(fio);
                if(parts.size()==0)
                    parts.add("");
            }
        }
        else if(sw.isChecked()) {
            parts.remove("");
            parts.add(fio);
        }
        //вызвать сервис
        music = new ArrayList<>();

            if(savedUri==null)
                music.add("");
            else{
                music = AudioAttach.getStringArrFromAudio(context,savedUri);
            }

        al.cancel();
        savedUri = null;

        updateValueMeetings();
    }

    public void onClickCancel(View view){
        al.cancel();
        savedUri = null;
    }

    public void onClickDelete(View view){
        deleteMeetings();
        al.cancel();
        savedUri = null;
    }

    public void onClickPlay(View view){
        Intent intent = new Intent(context,PlayAudioService.class);
        if(savedUri!=null){
            AudioAttach.playAudio(MainActivity.this,savedUri);
            //intent.putExtra(PlayAudioService.URI, savedUri);
            //intent.putExtra(PlayAudioService.CONT, (Serializable) context);
            //startService(intent);
        }
        else{
            List<String> audioStr = meetings.get(key).getFile();
            //String name  = AudioAttach.getNameAudio(MainActivity.this,savedUri);
            AudioAttach.playAudio(MainActivity.this,savedUri);
            savedUri = AudioAttach.getAudioFromString(context,audioStr);
            //intent.putExtra(PlayAudioService.URI, savedUri);
            //startService(intent);
        }
    }

    public void onClickAddAttach(View view){
        Button play = (Button)layoutView.findViewById(R.id.button_play);
        Button delete = (Button)layoutView.findViewById(R.id.button_delete_record);
        play.setEnabled(true);
        delete.setEnabled(true);

        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, RQS_RECORDING);

        TextView tv = (TextView)layoutView.findViewById(R.id.text_music);
        tv.setText("Record");
    }

    public void onClickDeleteAttach(View view){
        TextView tv = (TextView)layoutView.findViewById(R.id.text_music);
        tv.setText("");
        Button play = (Button)layoutView.findViewById(R.id.button_play);
        Button delete = (Button)layoutView.findViewById(R.id.button_delete_record);
        play.setEnabled(false);
        delete.setEnabled(false);
        savedUri = null;
    }

    public void onClickFab(View view){
        LayoutInflater li = LayoutInflater.from(context);
        layoutView = li.inflate(R.layout.new_meeting_layout, null);

        ad = new AlertDialog.Builder(context);
        ad.setView(layoutView);
        ad.setCancelable(false);

        Button play = (Button)layoutView.findViewById(R.id.button_play);
        Button delete = (Button)layoutView.findViewById(R.id.button_delete_record);
        play.setEnabled(false);
        delete.setEnabled(false);

        al = ad.create();
        al.show();
    }

    public void onClickCreate(View view){
        Meeting meet = new Meeting();
        EditText et;

        et = (EditText)layoutView.findViewById(R.id.edit_name);
        meet.setName(et.getText().toString());

        et = (EditText)layoutView.findViewById(R.id.edit_description);
        meet.setDescription(et.getText().toString());

        DatePicker dp = (DatePicker)layoutView.findViewById(R.id.picker_date_start);
        String month,day,year,dateStart,dateEnd;

        if (String.valueOf(dp.getMonth() + 1).length() == 1)
            month = "0" + String.valueOf(dp.getMonth() + 1);
        else month = String.valueOf(dp.getMonth() + 1);
        if (String.valueOf(dp.getDayOfMonth()).length() == 1)
            day = "0" + String.valueOf(dp.getDayOfMonth());
        else day = String.valueOf(dp.getDayOfMonth());
        year = String.valueOf(dp.getYear());
        dateStart = String.valueOf(day + "." + month + "." + year);
        meet.setDateStart(dateStart);

        dp = (DatePicker)layoutView.findViewById(R.id.picker_date_end);
        if (String.valueOf(dp.getMonth() + 1).length() == 1)
            month = "0" + String.valueOf(dp.getMonth() + 1);
        else month = String.valueOf(dp.getMonth() + 1);
        if (String.valueOf(dp.getDayOfMonth()).length() == 1)
            day = "0" + String.valueOf(dp.getDayOfMonth());
        else day = String.valueOf(dp.getDayOfMonth());
        year = String.valueOf(dp.getYear());
        dateEnd = String.valueOf(day + "." + month + "." + year);
        meet.setDateEnd(dateEnd);

        Spinner spinner = (Spinner)layoutView.findViewById(R.id.spinner_priority);
        if(spinner.getSelectedItemPosition()==0)
            meet.setPriority("Urgent");
        else if(spinner.getSelectedItemPosition()==1)
            meet.setPriority("Planned");
        else meet.setPriority("Possible");

        //устанавливаем свитч
        List<String> p = new ArrayList<>();
        p.add(participant.getLastName()+" "+participant.getFirstName()+" "+participant.getMiddleName());
        meet.setParticipants(p);

        //прикрепляем файл если есть
        List<String> music = new ArrayList<>();
        if(savedUri==null)
            music.add("");
        else {
            music = AudioAttach.getStringArrFromAudio(context,savedUri);
        }

        meet.setFile(music);

        if(meet.getName().equals(""))
            meet.setName("Meeting");

        if(compareDate(dateStart,dateEnd)==1)
            Toast.makeText(context, "Date start more than date end!", Toast.LENGTH_LONG).show();
        else{
            createMeeting(meet);
            al.cancel();
            savedUri = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_RECORDING) {
            savedUri = data.getData();
            //Toast.makeText(MainActivity.this, "Saved: " + savedUri.getPath(), Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(this, AuthorizationActivity.class));
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
