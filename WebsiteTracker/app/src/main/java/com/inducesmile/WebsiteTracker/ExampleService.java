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
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import static com.inducesmile.WebsiteTracker.App.CHANNEL_2_ID;

import static com.inducesmile.WebsiteTracker.App.CHANNEL_ID;


public class ExampleService extends Service {
    int x = 0; private NotificationManagerCompat notificationManager; private String readText; boolean Mismatch; int minutes; String website; Timer timer;
    @Override
    public void onCreate() {
        notificationManager = NotificationManagerCompat.from(this);
        Mismatch = false;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
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
        Toast.makeText(this, "Servics Started", Toast.LENGTH_SHORT).show();

        timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                x=x+1;
                Log.d("FUNCTION CALLED", "readWebpage() function called" + x);
                readWebpage();

                if(Mismatch == true){
                    timer.cancel();
                    timer.purge();
                    Log.d("SERVICE DESTROYED", "ON DESTROY() function called" + x);
                    onDestroy();
                }
            }
        };

        minutes = Integer.parseInt(MainActivity.editText.getText().toString());

        timer.schedule (hourlyTask, 0l, 1000*60*minutes);   // 1000*10*60 every 10 minut

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer.purge();
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendNotification(boolean Notify) {
        String title = "Website Changed";
        String message = "Maxar Technologies website has changed. Please check it.";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        if(Notify == true){
            notificationManager.notify(2, notification);
        }
    }

    public void write(String str){
        String filename = "myfile";
        String message = str;

        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(){
        try {
            String filename = "myfile";
            FileInputStream inputStream = openFileInput(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            readText = total.toString();
            MainActivity.counter = MainActivity.counter + 1;
            StringBuilder c = new StringBuilder();
            c = c.append("The counter is : " + MainActivity.counter + ". ");
            total = c.append(total);
            final String disp = total.toString();

            r.close();
            inputStream.close();
            Log.d("File", "File contents: " + total);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readWebpage() {
        website = MainActivity.editText2.getText().toString();
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[] { website});
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            response = response.replaceAll("\\<script.*?script\\>", "");

            response = response.replaceAll("\\<.*?\\>", "");
            response = response.replace("\n", "").replace("\t", "").replace("\r", "");
            response = response.trim().replaceAll(" +", " ");
            response = response.replaceAll("[^A-Za-z0-9 ]", "");

            if(MainActivity.firstDownload == true){
                write(response);
            }

            if(MainActivity.firstDownload == false){
                read();
                String a1,b1;
                a1 = response;
                b1 = readText;
                if (a1.equals(b1)) {
                    Log.d("LogSanket_MATCH", "Message of Sanket File contents.So it matches");

                }
                else {
                    Mismatch = true;
                    sendNotification(true);
                    Log.d("LogSanket_NO_MATCH", "Message of Sanket File contents.So it does not match");
                }
            }
            MainActivity.firstDownload = false;
            return response;
        }
    }
}