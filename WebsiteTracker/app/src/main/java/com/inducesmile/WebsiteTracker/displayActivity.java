package com.inducesmile.WebsiteTracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import java.net.URL;

public class displayActivity extends AppCompatActivity {
    static TextView changedwebsites, trackingtimes, activewebsites, checktimemssg, invalidwebsites; Date endtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        changedwebsites = (TextView) findViewById(R.id.changedwebsites);
        trackingtimes = (TextView) findViewById(R.id.trackingtimes);
        activewebsites = (TextView) findViewById(R.id.activewebsites);
        invalidwebsites = (TextView) findViewById(R.id.invalidwebsites);
        checktimemssg = (TextView) findViewById(R.id.checktimemssg);


        changedwebsites.setVisibility(View.GONE);
        trackingtimes.setVisibility(View.GONE);
        activewebsites.setVisibility(View.GONE);
        invalidwebsites.setVisibility(View.GONE);
        checktimemssg.setVisibility(View.GONE);

        Button StopService = findViewById(R.id.stopservice);
        StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.serviceactive == false){
                    Toast.makeText(getApplicationContext(), "No website is being tracked", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ExampleService.class);
                stopService(intent);
            }
        });
    }

    public void activetrackedwebsites(){
        int urlcount = 0; String activeWebsites = "Following websites are being tracked:\n";
        try{
            for (String url : ExampleService.list)
            {
                urlcount = urlcount + 1;
                URL urlobj = new URL(url);
                activeWebsites = activeWebsites + urlcount + ". " + urlobj.getHost() + "\n";
            }
            if(urlcount==0) {
                activewebsites.setText(activeWebsites + " None");
            }
            else {
                activewebsites.setText(activeWebsites);
            }

            activewebsites.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changedwebsites(View v){
        String checkedmsg = "The websites have been checked " + ExampleService.readcount + " times";
        trackingtimes.setText(checkedmsg);
        trackingtimes.setVisibility(View.VISIBLE);

        if(ExampleService.changedWebsitecount == 0){
            changedwebsites.setText(ExampleService.changedWebsites + "\n  None");
        }
        else{
            changedwebsites.setText(ExampleService.changedWebsites);
        }

        if(MainActivity.serviceactive == true){
            changedwebsites.setVisibility(View.VISIBLE);
            trackingtimes.setVisibility(View.VISIBLE);
            activetrackedwebsites();
            if(ExampleService.invalidcount != 0){
                invalidwebsites.setText(ExampleService.msg);
                invalidwebsites.setVisibility(View.VISIBLE);
            }

        }
        else{
            if(ExampleService.changedWebsitecount != 0){
                changedwebsites.setVisibility(View.VISIBLE);
                trackingtimes.setVisibility(View.VISIBLE);
                activetrackedwebsites();
            }
            else{
                changedwebsites.setVisibility(View.GONE);
                activewebsites.setVisibility(View.GONE);
                trackingtimes.setVisibility(View.GONE);
                invalidwebsites.setVisibility(View.GONE);
            }
            Toast.makeText(this, "No website is being tracked", Toast.LENGTH_SHORT).show();
        }

    }

    public void checktime(View v){
        if(MainActivity.serviceactive == false){
            checktimemssg.setVisibility(View.GONE);
            Toast.makeText(this, "No website is being tracked", Toast.LENGTH_SHORT).show();
            return;
        }
        endtime = new Date();
        long millisec = ExampleService.starttime.getTime() - endtime.getTime() + Integer.parseInt(MainActivity.Interval.getText().toString()) * 1000*60;
        int min = (int)(millisec/(1000*60));
        int sec = (int)(millisec/1000)%60;
        String mssg = "Next check will be in " + min + " min " + sec + " sec.";

        try{
            checktimemssg.setText(mssg);
            checktimemssg.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goback(View v){
        startActivity(new Intent(this, MainActivity.class));
    }



}
