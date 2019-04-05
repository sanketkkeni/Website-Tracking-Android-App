package com.inducesmile.WebsiteTracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static boolean firstDownload, firsttimecheckwebsites; static EditText Interval, website1, website2, website3, website4, website5, website6, website7, website8, website9, website10; static List<Integer> skipurl = new ArrayList<>(); static SharedPreferences myPrefs; static boolean serviceactive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Interval = (EditText) findViewById(R.id.Interval);
        website1 = (EditText) findViewById(R.id.website1);
        website2 = (EditText) findViewById(R.id.website2);
        website3 = (EditText) findViewById(R.id.website3);
        website4 = (EditText) findViewById(R.id.website4);
        website5 = (EditText) findViewById(R.id.website5);
        website6 = (EditText) findViewById(R.id.website6);
        website7 = (EditText) findViewById(R.id.website7);
        website8 = (EditText) findViewById(R.id.website8);
        website9 = (EditText) findViewById(R.id.website9);
        website10 = (EditText) findViewById(R.id.website10);


        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);

        website1.setText(myPrefs.getString("website1",""));
        website2.setText(myPrefs.getString("website2",""));
        website3.setText(myPrefs.getString("website3",""));
        website4.setText(myPrefs.getString("website4",""));
        website5.setText(myPrefs.getString("website5",""));
        website6.setText(myPrefs.getString("website6",""));
        website7.setText(myPrefs.getString("website7",""));
        website8.setText(myPrefs.getString("website8",""));
        website9.setText(myPrefs.getString("website9",""));
        website10.setText(myPrefs.getString("website10",""));
        Interval.setText(myPrefs.getString("Interval2",""));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void startService(View v) {

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();

        editor.putString("website1", website1.getText().toString());
        editor.putString("website2", website2.getText().toString());
        editor.putString("website3", website3.getText().toString());
        editor.putString("website4", website4.getText().toString());
        editor.putString("website5", website5.getText().toString());
        editor.putString("website6", website6.getText().toString());
        editor.putString("website7", website7.getText().toString());
        editor.putString("website8", website8.getText().toString());
        editor.putString("website9", website9.getText().toString());
        editor.putString("website10", website10.getText().toString());
        editor.putString("Interval2", Interval.getText().toString());

        editor.apply();

        MainActivity.firstDownload = true;
        firsttimecheckwebsites = true;

        Intent serviceIntent = new Intent(getApplicationContext(), ExampleService.class);

        ContextCompat.startForegroundService(this, serviceIntent);

        Intent secondDisplay = new Intent(MainActivity.this, displayActivity.class);
        startActivity(secondDisplay);
    }

    public void goforward(View v){
        startActivity(new Intent(this, displayActivity.class));
    }

    public void help(View v){
        Intent helpDisplay = new Intent(this, helpactivity.class);
        startActivity(helpDisplay);
    }
}
