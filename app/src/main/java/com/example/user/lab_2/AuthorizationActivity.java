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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    static final String F = "f";
    static final String L = "l";
    static final String M = "m";
    static final String P = "p";

    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        context = AuthorizationActivity.this;

        intent = new Intent(this,FirebaseNewParticipantService.class);

        editLogin = (EditText)findViewById(R.id.edit_login);
        editPassword = (EditText)findViewById(R.id.edit_password);

        buttonLogin = (Button)findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AuthorizationActivity.this, "click LOG IN!",Toast.LENGTH_LONG).show();
            }
        });

        buttonSingIn = (Button)findViewById(R.id.button_sign_up);
        buttonSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                layoutView = li.inflate(R.layout.sing_up_layout, null);
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
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
                            ConnectivityManager connMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo ni = connMan.getActiveNetworkInfo();
                            if(ni!=null&&ni.isConnected()){
                                editLogin.setText(login);
                                editPassword.setText(password);


                                intent.putExtra(L, lastName);
                                intent.putExtra(F, firstName);
                                intent.putExtra(M, middleName);
                                intent.putExtra(P, post);
                                startService(intent);


                            }
                            else Toast.makeText(context, "Network not found!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                AlertDialog al = ad.create();
                al.show();
            }
        });
    }
}
