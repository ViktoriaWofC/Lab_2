package com.example.user.lab_2;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by User on 13.11.2016.
 */

public class FirebaseCheckLoginService extends IntentService {
    public FirebaseCheckLoginService() {
        super("FirebaseCheckLoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // check logon- не существует ли уже такого?
    }
}
