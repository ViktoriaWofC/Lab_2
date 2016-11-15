package com.example.user.lab_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by User on 12.11.2016.
 */

public class AuthorizationActivity extends AppCompatActivity {

    private EditText editLogin;
    private EditText editPassword;
    private Button buttonLogin;
    private Button buttonSingIn;
    private Context context;

    private String login = "";
    private String password = "";
    private String firstName = "";
    private String lastName = "";
    private String middleName = "";
    private String post = "";
    View layoutView;

    Intent intent;
    AlertDialog al;
    AlertDialog.Builder ad;
    DatabaseReference databaseReference;
    ParticipantListener participantListener;
    List<String> logins = new ArrayList<>();
    List<String> passwords = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        context = AuthorizationActivity.this;

        getLoginPassword();

        editLogin = (EditText)findViewById(R.id.edit_login);
        editPassword = (EditText)findViewById(R.id.edit_password);

        buttonLogin = (Button)findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login = editLogin.getText().toString();
                password = editPassword.getText().toString();

                if(login.equals("")) Toast.makeText(context, "Login is NULL!",Toast.LENGTH_LONG).show();
                else if(password.equals("")) Toast.makeText(context, "Password is NULL!",Toast.LENGTH_LONG).show();
                else {
                    int k = logins.indexOf(login);

                    if(k>=0)
                        if (passwords.get(k).equals(password)) {
                            intent = new Intent(context, MainActivity.class);
                            intent.putExtra("login", login);
                            startActivity(intent);
                        } else Toast.makeText(AuthorizationActivity.this, "Password is not correct!", Toast.LENGTH_LONG).show();
                    else Toast.makeText(AuthorizationActivity.this, "Login is not correct!", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonSingIn = (Button)findViewById(R.id.button_sign_up);
        buttonSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                layoutView = li.inflate(R.layout.sing_up_layout, null);
                ad = new AlertDialog.Builder(context);
                ad.setView(layoutView);
                ad.setCancelable(true);

                ad.setPositiveButton(R.string.sign_up, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText edit = (EditText)layoutView.findViewById(R.id.edit_first_name);

                        if(edit.getText().toString().equals(""))
                           firstName = "Ivan";
                        else firstName = edit.getText().toString();

                        edit = (EditText)layoutView.findViewById(R.id.edit_last_name);
                        if(edit.getText().toString().equals(""))
                            lastName = "Ivanov";
                        else lastName = edit.getText().toString();

                        edit = (EditText)layoutView.findViewById(R.id.edit_middle_name);
                        if(edit.getText().toString().equals(""))
                            middleName = "Ivanovich";
                        else middleName = edit.getText().toString();

                        edit = (EditText)layoutView.findViewById(R.id.edit_post);
                        if(edit.getText().toString().equals(""))
                            post = "employee";
                        else post = edit.getText().toString();

                        edit = (EditText)layoutView.findViewById(R.id.edit_login);
                        login = edit.getText().toString();
                        edit = (EditText)layoutView.findViewById(R.id.edit_password);
                        password = edit.getText().toString();

                        if(login.equals("")) Toast.makeText(context, "Login is NULL!",Toast.LENGTH_LONG).show();
                        else if(password.equals("")) Toast.makeText(context, "Password is NULL!",Toast.LENGTH_LONG).show();
                        else {

                            if(!logins.contains(login)){
                                ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo ni = connMan.getActiveNetworkInfo();
                                if(ni!=null&&ni.isConnected()){

                                    //service?
                                    //intent = new Intent(context, FirebaseCheckLoginService.class);
                                    //intent.putExtra("login",login);

                                    editLogin.setText(login);
                                    editPassword.setText(password);
                                    intent = new Intent(context,FirebaseNewParticipantService.class);
                                    intent.putExtra("l", lastName);
                                    intent.putExtra("f", firstName);
                                    intent.putExtra("m", middleName);
                                    intent.putExtra("p", post);
                                    intent.putExtra("log",login);
                                    intent.putExtra("pas",password);
                                    startService(intent);

                                    getLoginPassword();
                                }
                                else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
                            }
                            else Toast.makeText(context, "This login is used!",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        al.cancel();
                    }
                });

                al = ad.create();
                al.show();
            }
        });
    }

    public void getLoginPassword(){

        intent = new Intent(context,FirebaseGetLoginPasswordService.class);
        startService(intent);

        //databaseReference = FirebaseDatabase.getInstance().getReference();
        //participantListener = new ParticipantListener();
        //databaseReference.child("participant").addValueEventListener(participantListener);
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
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        login = "";
        password = "";
        logins.clear();
        passwords.clear();
    }*/
}
