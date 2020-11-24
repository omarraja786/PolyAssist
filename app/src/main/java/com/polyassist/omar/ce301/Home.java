package com.polyassist.omar.ce301;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Home extends AppCompatActivity implements View.OnClickListener{
    private CardView ttsCard, sttCard, cbbCard, ocrCard;
    private TextToSpeech tts;
    private int NOTIFICATION_ID = 234;
    private int shakeCount=0;
    private Runnable runnable ;
    private Handler handler = new Handler();

    Context context;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    public static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions= new String[]{
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };


    //https://stackoverflow.com/questions/48468230/settings-button-with-icon-in-action-bar-android-studio
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_app_bg_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        showRecordingNotification();
        context = this;

        checkPermissions();

        //https://developer.android.com/reference/android/content/SharedPreferences
        //https://stackoverflow.com/questions/5950043/how-to-use-getsharedpreferences-in-android
        SharedPreferences sharedPrefs = getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initialisation Failed!");
                }
            }
        });

        if (!prefs.getBoolean("firstTime", false)) {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(Locale.getDefault());
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "This Language is not supported");
                        }
                        speak("Welcome to Poly Assist. Shake your phone once to view text to speech features. Shake  your phone twice to view speech to text features. Shake your phone three times to view colour blind mode features. Shake your phone four times to view optical character recognition features. Or Shake your phone five times to go to the settings page");

                    } else {
                        Log.e("TTS", "Initialisation Failed!");
                    }
                }
            });


            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }


        //https://stackoverflow.com/questions/38633936/detect-shake-in-android
        //https://www.youtube.com/watch?v=Dh_IgQ4bQyQ&t=273s
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();

        runnable =new Runnable() {
            public void run() {
                if( shakeCount == 1 ) {
                    final Intent tts = new Intent(context, ttsScreen.class);
                    startActivity(tts);
                }
                else if (shakeCount == 2) {
                    final Intent stt = new Intent(context, sttScreen.class);
                    startActivity(stt);
                }
                else if (shakeCount == 3) {
                    final Intent cbb = new Intent(context, cbbScreen.class);
                    startActivity(cbb);
                }
                else if (shakeCount == 4) {
                    final Intent ocr = new Intent(context, ocrScreen.class);
                    startActivity(ocr);
                }
                else if (shakeCount == 5) {
                    final Intent settings = new Intent(context, SMSReader.class);
                    startActivity(settings);
                }
                shakeCount=0;
            }
        };

        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(final int count) {
                speak(count + "shake");
                shakeCount = count;
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 1800);
            }

            });


        ttsCard = (CardView) findViewById(R.id.tts_card);
        sttCard = (CardView) findViewById(R.id.stt_card);
        cbbCard = (CardView) findViewById(R.id.cbb_card);
        ocrCard = (CardView) findViewById(R.id.ocr_card);

        ttsCard.setOnClickListener(this);
        sttCard.setOnClickListener(this);
        cbbCard.setOnClickListener(this);
        ocrCard.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sms = new Intent(context, SMSReader.class);
        Intent tutorial = new Intent(context, tutorial.class);
        if (item.getItemId() == R.id.settings) {
            startActivity(sms);
            return true;
        }
        if(item.getItemId() == R.id.tutorial) {
            startActivity(tutorial);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {
            case R.id.tts_card : i = new Intent(this, ttsScreen.class); startActivity(i); break;
            case R.id.stt_card : i = new Intent(this, sttScreen.class); startActivity(i); break;
            case R.id.cbb_card : i = new Intent(this, cbbScreen.class); startActivity(i); break;
            case R.id.ocr_card : i = new Intent(this, ocrScreen.class); startActivity(i); break;
            default:break;

        }
    }



    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }



    //https://developer.android.com/training/notify-user/channels
    public void showRecordingNotification(){

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "channelOne";
        CharSequence name = "notiChannel";
        String Description = "Channel for notifications";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notiChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notiChannel.setDescription(Description);
            notiChannel.enableLights(true);
            notiChannel.setLightColor(Color.RED);
            notiChannel.enableVibration(true);
            notiChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notiChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notiChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("PolyAssist")
                .setContentText("This program is running in the background. Tap here to open the menu");



        Intent resultIntent = new Intent(this, Home.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Home.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);


        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    //https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions/35495855#35495855
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            permissionsDenied += "\n" + per;

                        }

                    }
                }
                return;
            }
        }
    }
}
