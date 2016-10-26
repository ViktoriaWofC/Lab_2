package com.example.user.lab_2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 26.10.2016.
 */

public class TestClass extends AppCompatActivity {

    private TextView tv;
    private Button tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        tv = (TextView)findViewById(R.id.testTV);
        tv.setText("o ho ho");

        //////////////////////////////////////////

        tb = (Button)findViewById(R.id.testBut);
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                try{
                    JSONObject jsonObject = new JSONObject("{\"dollar\":54.48}");
                    double dollar = jsonObject.getDouble("dollar");
                    Log.d("json", "json");
                    Log.d("json", "Курс доллара: " + dollar);
                } catch (JSONException e){}*/

                /*try{
                    JSONObject jsonObject = new JSONObject("{\"pupils\":[\"Василий Ломоносов\",\"Александр Пушкин\",\"Сергей Есенин\",\"Агния Барто\",\"Владимир Маяковский\"]}");
                JSONArray jsonArray = jsonObject.getJSONArray("pupils");
                    String str = jsonArray.getString(0)+" "+jsonArray.getString(1)+" "+jsonArray.getString(2)+" "+jsonArray.getString(3);
                    Log.d("json", str);
            } catch (JSONException e){}*/

                /*try{
                    JSONObject jsonObject = new JSONObject("{\"lada_car\":{\"type\":\"седан\",\"model\":\"Kalina\",\"manufacturer\":\"Lada\"},\"toyota_car\":{\"type\":\"седан\",\"model\":\"Camry\",\"manufacturer\":\"Toyota\"}}");

                    JSONObject kalina = jsonObject.getJSONObject("lada_car");

                    Log.d("json", kalina.getString("manufacturer"));
                    Log.d("json", kalina.getString("model"));
                    Log.d("json", kalina.getString("type"));

                    JSONObject camry = jsonObject.getJSONObject("toyota_car");

                    Log.d("json", camry.getString("manufacturer"));
                    Log.d("json", camry.getString("model"));
                    Log.d("json", camry.getString("type"));

                    String json = jsonObject.toString();
                    Log.d("json", json);
                    Log.d("json", "tt");

                } catch (JSONException e){}*/

                JSONObject jsonObject = new JSONObject();
                try{
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    String dateString = sdf.format(date);
                    jsonObject.put("name", date);
                    String json = sdf.format(jsonObject.get("name"));
                    boolean nameExists = jsonObject.has("name");
                    boolean surnameExists = jsonObject.has("surname");
                    Log.d("json", json);
                    Log.d("json", "Имя:"+ nameExists + ", Фамилия:" + surnameExists);
                } catch (JSONException e){}

            }
        });



    }

}
