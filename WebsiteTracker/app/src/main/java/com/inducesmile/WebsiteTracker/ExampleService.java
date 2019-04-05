package com.inducesmile.WebsiteTracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import static com.inducesmile.WebsiteTracker.App.CHANNEL_2_ID;

import static com.inducesmile.WebsiteTracker.App.CHANNEL_ID;

public class ExampleService extends Service {
    private NotificationManagerCompat notificationManager; private String readText; boolean Mismatch; int minutes; Timer timer; int listcount; static String changedWebsites, msg; static int changedWebsitecount; static List<String> list = new ArrayList<String>(); List<String> removefromlist = new ArrayList<String>(); static Date starttime; static int readcount, invalidcount;
    @Override
    public void onCreate() {
        notificationManager = NotificationManagerCompat.from(this);
        Mismatch = false;
        list.clear();
        if(!TextUtils.isEmpty(MainActivity.website1.getText().toString())){
            list.add(MainActivity.website1.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website2.getText().toString())){
            list.add(MainActivity.website2.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website3.getText().toString())){
            list.add(MainActivity.website3.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website4.getText().toString())){
            list.add(MainActivity.website4.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website5.getText().toString())){
            list.add(MainActivity.website5.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website6.getText().toString())){
            list.add(MainActivity.website6.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website7.getText().toString())){
            list.add(MainActivity.website7.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website8.getText().toString())){
            list.add(MainActivity.website8.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website9.getText().toString())){
            list.add(MainActivity.website9.getText().toString());
        }
        if(!TextUtils.isEmpty(MainActivity.website10.getText().toString())){
            list.add(MainActivity.website10.getText().toString());
        }

        listcount = list.size();



        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MainActivity.serviceactive = true;
        changedWebsitecount = 0;
        readcount = 0;

        changedWebsites = "Following websites have changed:\n";
        Intent notificationIntent = new Intent(this, displayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("Website tracking service is running!")
                .setSmallIcon(R.drawable.ic_one)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();
        Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();

        timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                starttime = new Date();
                Log.d("FUNCTION CALLED", "readWebpage() function called");
                readWebpage();

                if(Mismatch == true){
                    timer.cancel();
                    timer.purge();
                    Log.d("SERVICE DESTROYED", "SERVICE DESTROYED");
                    MainActivity.skipurl.clear();
                    stopSelf();
                    MainActivity.serviceactive = false;
                }
            }
        };

        minutes = Integer.parseInt(MainActivity.Interval.getText().toString());

        timer.schedule (hourlyTask, 0l, 1000*60*minutes);   // 1000*10*60 every 10 minutes

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        String tempfilename;
        timer.cancel();
        timer.purge();
        MainActivity.serviceactive = false;
        invalidcount=0;
        MainActivity.skipurl.clear();

        try{
            for (int i = 0; i < list.size(); i++){
                tempfilename = "myfile" + i;
                deleteFile(tempfilename);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendNotification(String title, String message) {

        Intent notificationIntent = new Intent(this, displayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;



        notificationManager.notify(2, notification);

    }

    public void write(String str, int i){
        String filename = "myfile" + i;
        String message = str;

        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(int i){
        try {
            String filename = "myfile" + i;
            FileInputStream inputStream = openFileInput(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            readText = total.toString();
            r.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void readWebpage() {
        readcount++;
        DownloadWebPageTask task = new DownloadWebPageTask();

        if(MainActivity.firsttimecheckwebsites == true){
            task.checkwebsites();
            MainActivity.firsttimecheckwebsites = false;
        }


        String[] arr = list.toArray(new String[list.size()]);

        task.execute(arr);
    }

    public void setatend(){
        MainActivity.serviceactive = false;
        invalidcount=0;
        Mismatch = false;
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        public void checkwebsites() {
            String url; invalidcount=0; msg = "The following websites are invalid and cannot be tracked:";
            String[] urls = list.toArray(new String[list.size()]);
            for (int i = 0; i < urls.length; i++){
                url = urls[i];
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                }
                catch (Exception e) {
                    invalidcount++;
                    removefromlist.add(url);
                    listcount--;
                    msg += "\n " + invalidcount + ") " + url;

                }
            }
            if(invalidcount != 0){
                sendNotification("Invalid Webaites!",msg);
                list.removeAll(removefromlist);
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = ""; String url;

            for (int i = 0; i < urls.length; i++){
                if (MainActivity.skipurl.contains(i)){
                    continue;
                }
                response = "";
                url = urls[i];
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    TimeUnit.SECONDS.sleep(5);

                        HttpResponse execute = client.execute(httpGet);
                        InputStream content = execute.getEntity().getContent();
                        BufferedReader buffer = new BufferedReader(
                                new InputStreamReader(content));
                        String s = "";
                        while ((s = buffer.readLine()) != null) {
                            response += s;
                        }
                    response = response.replaceAll("\\<script.*?script\\>", "");

                    response = response.replaceAll("\\<.*?\\>", "");
                    response = response.replace("\n", "").replace("\t", "").replace("\r", "");
                    response = response.trim().replaceAll(" +", " ");
                    response = response.replaceAll("[^A-Za-z0-9 ]", "");

                    if(MainActivity.firstDownload == true){
                        write(response, i);
                    }

                    if(MainActivity.firstDownload == false){
                        read(i);
                        String a1,b1;
                        a1 = response;
                        b1 = readText;
                        if (a1.equals(b1)) {
                            Log.d("LogSanket_MATCH", "Message of Sanket File contents for url " + url + " matches");
                        }
                        else {
                            changedWebsitecount = changedWebsitecount + 1;
                            MainActivity.skipurl.add(i); // skipurl is used when multiple websites
                            int d = urls.length;
                            int h = MainActivity.skipurl.size();
                            if (urls.length == MainActivity.skipurl.size()){
                                Mismatch = true;
                                MainActivity.skipurl.clear();
                                //stopSelf();
                                Intent intent = new Intent(getApplicationContext(), ExampleService.class);
                                stopService(intent);
                            }


                            URL bbb = new URL(url);
                            changedWebsites = changedWebsites + changedWebsitecount + ") " + bbb.getHost() + "\n";
                            //displayActivity.changedwebsites.setText(changedWebsites);

                            sendNotification("Website change detected!", changedWebsites);
                            Log.d("LogSanket_NO_MATCH", "Message of Sanket File contents for website " + bbb.getHost() + "  does not match");
                            list.remove(url);
                        }
                    }
                    if (i == urls.length-1){
                        MainActivity.firstDownload = false;
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return response;

        }

    }

}
