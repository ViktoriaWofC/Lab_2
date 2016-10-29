package com.example.user.lab_2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.user.lab_2.TestClass.RQS_RECORDING;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /////
    private DatabaseReference mSimpleFirechatDatabaseReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMsgEditText;
    /////
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirechatUser;
    private String mUsername;

    /////
    private static final int READ_BLOCK_SIZE = 100;
    Uri savedUri;


    public static class FirechatMsgViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView userTextView;


        public FirechatMsgViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
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

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMsgEditText = (EditText)findViewById(R.id.editText);

        mSimpleFirechatDatabaseReference = FirebaseDatabase.getInstance().getReference();


        mFirebaseAdapter = new FirebaseRecyclerAdapter(
                Meeting.class,
                R.layout.chat_message,
                FirechatMsgViewHolder.class,
                mSimpleFirechatDatabaseReference.child("meetings")) {

            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {

                FirechatMsgViewHolder fviewHolder = (FirechatMsgViewHolder)viewHolder;
                Meeting meeting = (Meeting)model;

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                fviewHolder.nameTextView.setText(meeting.getName());
                fviewHolder.userTextView.setText(meeting.getNames());

            }

            //@Override
            protected void tpopulateViewHolder(FirechatMsgViewHolder viewHolder, Meeting meeting, int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.nameTextView.setText(meeting.getName());
                viewHolder.userTextView.setText(meeting.getNames());
            }

        };


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

                super.onItemRangeInserted(positionStart, itemCount);
                int chatMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||  (positionStart >= (chatMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        ////////////////////////////////////////////
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirechatUser = mFirebaseAuth.getCurrentUser();
        if (mFirechatUser == null) {
            startActivity(new Intent(this, AuthorizationActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirechatUser.getDisplayName();
        }

        //////////////////////////////

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);


        TextView t = (TextView)findViewById(R.id.testText);
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
                //mr.stop();
                TextView t = (TextView)findViewById(R.id.testText);
                t.setText(savedUri.getPath());
                MediaPlayer mp = MediaPlayer.create(MainActivity.this,savedUri);
                mp.start();


                int frequency = 11025/2;
                int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
                int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
                String str = getRealPathFromURI(MainActivity.this,savedUri);
                String[] s = str.split("/");

                String ss = "";
                for(int i = 0; i< (s.length-1);i++)
                    ss += s[i]+ "/";

                //String transferFile = "transferimage.jpg";
                File extDir = getExternalFilesDir(null);
                File file = new File(extDir, s[s.length-1]);
                //File file = new File(Environment.getExternalStorageDirectory(), s[s.length-1]);
                //@NonNull
                //File file = new File(savedUri);// File(str);//ss,s[s.length-1]);

                t.setText(s[s.length-1]);

                // Массив типа short для хранения аудиоданных (звук 16-битный,
                // поэтому выделяем по 2 байта на значение)
                int audioLength = (int)(file.length()/2);
                short[] audio = new short[audioLength];

                //InputStream is = null;
                try {

                    InputStream is = new FileInputStream(file);//new BufferedInputStream(new FileInputStream(file));//new FileInputStream(file);
                    /*BufferedInputStream bis = new BufferedInputStream(is);
                    DataInputStream dis = new DataInputStream(bis);
                    int i = 0;
                    while (dis.available() > 0) {
                        audio[audioLength] = dis.readShort(); i++;
                    }
                    // Закрытие входящих потоков. dis.close();
                    // Создание объекта AudioTrack и проигрывание звука с его помощью

                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding,
                            audioLength,   AudioTrack.MODE_STREAM);
                    audioTrack.play();
                    audioTrack.write(audio, 0, audioLength);*/
                    is.close();
                    Toast.makeText(getBaseContext(), "!!!!!!",
                            Toast.LENGTH_LONG).show();

                } catch (Throwable ee) {
                    Toast.makeText(getBaseContext(), "errrrrrrrrrrr!",
                            Toast.LENGTH_LONG).show();
                } /*finally
                {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }*/


            }
        });

    }

    private void uploadFile(Uri fileUri) {
        // create upload service client
        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java


        String str = getRealPathFromURI(MainActivity.this,savedUri);
        String[] s = str.split("/");

        String ss = "";
        for(int i = 0; i< (s.length-1);i++)
            ss += s[i];
        //File file = new File(Environment.getExternalStorageDirectory(), s[s.length-1]);
        File file = new File(ss,s[s.length-1]);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void onClickSend(View v){

        String str = getRealPathFromURI(MainActivity.this,savedUri);
        String[] s = str.split("/");

        String ss = "";
        for(int i = 0; i< (s.length-1);i++)
            ss += s[i];
        //File file = new File(Environment.getExternalStorageDirectory(), s[s.length-1]);
        File file = new File(ss,s[s.length-1]);

        Meeting meeting = new
                Meeting(mMsgEditText.getText().toString(), mUsername,file);
        mSimpleFirechatDatabaseReference.child("meetings")
                .push().setValue(meeting);
        mMsgEditText.setText("");

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
