package com.example.user.lab_2;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 08.11.2016.
 */

public class AudioAttach {

    public static byte[] getByteArrFromAudio(Context context, Uri uri){
        String path = getRealPathFromURI(context ,uri);

        File file = new File(path);
        int audioLength = (int)(file.length());
        byte[] audio = new byte[audioLength];

        try {
            InputStream is = new FileInputStream(file);
            is.read(audio);
            is.close();
        } catch (Throwable t) {
            Toast.makeText(context, "err 1!", Toast.LENGTH_LONG).show();
            Log.e("teg","errrr 1!",t);
        }

        return  audio;
    }

    public static List<String> getStringArrFromAudio(Context context, Uri uri){
        String path = getRealPathFromURI(context ,uri);

        File file = new File(path);
        int audioLength = (int)(file.length());
        byte[] audio = new byte[audioLength];
        List<String> audioStr = new ArrayList<>();

        try {
            InputStream is = new FileInputStream(file);
            is.read(audio);
            is.close();
        } catch (Throwable t) {
            Toast.makeText(context, "err 1!",
                    Toast.LENGTH_LONG).show();
            Log.e("teg","errrr 1!",t);
        }

        for(int i = 0; i<audio.length;i++) {
            audioStr.add(Byte.toString(audio[i]));
        }

        return  audioStr;

    }

    public static String getStringFromAudio(Context context, Uri uri){
        String path = getRealPathFromURI(context ,uri);

        File file = new File(path);
        int audioLength = (int)(file.length());
        byte[] audio = new byte[audioLength];
        String audioStr = "";

        try {
            InputStream is = new FileInputStream(file);
            is.read(audio);
            is.close();
        } catch (Throwable t) {
            Toast.makeText(context, "err 1!",
                    Toast.LENGTH_LONG).show();
            Log.e("teg","errrr 1!",t);
        }

        audioStr = Byte.toString(audio[0]);
        for(int i = 1; i<audio.length;i++)
            audioStr +=  ","+ Byte.toString(audio[i]);

        return  audioStr;
    }

    public static Uri getAudioFromString(Context context, String name, String arr){
        Uri uri = null;
        String[] audioStr = arr.split(",");
        int audioLength = audioStr.length;
        byte[] audio = new byte[audioLength];

        for(int i = 0; i<audio.length;i++) {
            audio[i] = Byte.valueOf(audioStr[i]);
        }

        String exts = Environment.getExternalStorageDirectory().getAbsolutePath()+"/record";
        if(isExternalStorageReadable()&& isExternalStorageWritable())
        {
            File f = new File(exts);
            if(!f.exists())
                f.mkdirs();

            try {
                OutputStream os = new FileOutputStream(exts + name);
                os.write(audio, 0, audio.length);
                os.close();

                uri = Uri.fromFile(new File(exts + name));

            } catch (Throwable t) {
                Toast.makeText(context, "err 2!",
                        Toast.LENGTH_LONG).show();
                Log.e("teg","errr 2!",t);
            }
            return uri;
        }
        else {
            Toast.makeText(context, R.string.noSD, Toast.LENGTH_LONG).show();
            return uri;
        }
    }

    public static Uri getAudioFromString(Context context, List<String> audioStr){
        Uri uri = null;
        String name = audioStr.get(audioStr.size()-1);
        int audioLength = audioStr.size()-1;
        byte[] audio = new byte[audioLength];

        for(int i = 0; i<audio.length;i++) {
            audio[i] = Byte.valueOf(audioStr.get(i));
        }

        String exts = Environment.getExternalStorageDirectory().getAbsolutePath()+"/record";
        if(isExternalStorageReadable()&& isExternalStorageWritable())
        {
            File f = new File(exts);
            if(!f.exists())
                f.mkdirs();

            try {
                OutputStream os = new FileOutputStream(exts + name);
                os.write(audio, 0, audio.length);
                os.close();

                uri = Uri.fromFile(new File(exts + name));

            } catch (Throwable t) {
                Toast.makeText(context, "err 2!",
                        Toast.LENGTH_LONG).show();
                Log.e("teg","errr 2!",t);
            }
            return uri;
        }
        else {
            Toast.makeText(context, R.string.noSD, Toast.LENGTH_LONG).show();
            return uri;
        }
    }

    public static void playAudio(Context context, Uri uri){
        MediaPlayer mp = MediaPlayer.create(context,uri);
        mp.start();
    }

    public static String getNameAudio(Context context, Uri uri){
        String path = getRealPathFromURI(context ,uri);
        String[] s = path.split("/");

        return s[s.length-1];
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
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

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
