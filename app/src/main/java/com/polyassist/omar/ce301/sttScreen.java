package com.polyassist.omar.ce301;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//https://www.sitepoint.com/using-android-text-to-speech-to-create-a-smart-assistant/
//https://www.youtube.com/watch?v=AnNJPf-4T70

public class sttScreen extends AppCompatActivity {

    private TextToSpeech tts;
    private SpeechRecognizer mySpeechRecognizer;
    private ImageButton btnSpeak;
    private TextView txtSpeechInput;
    private TextView minSpeechInput;
    private TextView hourSpeechInput;
    public TextView smsSpeechInput;
    public String num;
    public String newNum;
    public int hour;
    public int mins;
    public String sms;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_CODE_SPEECH_NUM = 104;
    private final int REQ_CODE_SPEECH_INPUT_ALARM = 101;
    private final int REQ_CODE_SPEECH_INPUT_MINS = 102;
    private final int REQ_CODE_SPEECH_INPUT_SMS = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt_screen);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialiseTextToSpeech();
        initialiseSpeechRecognizer();

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        minSpeechInput = (TextView) findViewById(R.id.minSpeechInput);
        hourSpeechInput = (TextView) findViewById(R.id.hourSpeechInput);
        smsSpeechInput = (TextView) findViewById(R.id.smsSpeechInput);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mySpeechRecognizer.startListening(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void initialiseSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> bundle = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(bundle.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void promptSpeechInputSMS() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_SMS); //SMS Contents
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_NUM); //SMS Contact/Number
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void promptSpeechInputAlarm() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_MINS);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_ALARM);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    newNum = getPhoneNumber(result.get(0), this);
                    txtSpeechInput.setText(result.get(0).replaceAll("\\s+", ""));
                    num = txtSpeechInput.getText().toString();
                    dialPhoneNumber(newNum);
                    try {
                        Thread.sleep(18000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            case REQ_CODE_SPEECH_NUM: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    newNum = getPhoneNumber(result.get(0), this);
                    txtSpeechInput.setText(result.get(0).replaceAll("\\s+", ""));
                    num = txtSpeechInput.getText().toString();
                    try {
                        Thread.sleep(18000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            case REQ_CODE_SPEECH_INPUT_SMS: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    smsSpeechInput.setText(result.get(0));
                    sms = smsSpeechInput.getText().toString();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(newNum, null, sms, null, null);
                    speak("Text message sent");
                    try {
                        Thread.sleep(18000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case REQ_CODE_SPEECH_INPUT_ALARM: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    hourSpeechInput.setText(result.get(0));
                    hour = Integer.parseInt(hourSpeechInput.getText().toString());
                }
                break;
            }
            case REQ_CODE_SPEECH_INPUT_MINS: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    minSpeechInput.setText(result.get(0));
                    mins = Integer.parseInt(minSpeechInput.getText().toString());
                    speak("Alarm set for " + hour + " " + mins);
                    createAlarm("Omar's CE301 App",hour,mins);
                }
            }
            break;
        }
    }



    public void dialPhoneNumber(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
           return ;
        }
    }

    public String getPhoneNumber(String name, Context context) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if(ret==null)
            ret = name;
        return ret;
    }


    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    //https://developer.android.com/guide/components/intents-common
    private void processResult(String command) {
        command = command.toLowerCase();

        if(command.indexOf("time") != -1) {
            Date now = new Date();
            String time = DateUtils.formatDateTime(this, now.getTime(), DateUtils.FORMAT_SHOW_TIME);
            speak("The time is " + time);
        }
        if(command.indexOf("date") != -1) {
            String date = DateFormat.getDateInstance().format(new Date());
            speak("The date is " + date);
        }
        else if (command.indexOf("open") != -1) {
            if(command.indexOf("browser") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.uk/"));
                startActivity(intent);
            }
        }
        if(command.indexOf("call") != -1) {
            speak("Who would you like to call? Speak a number or contact name after the beep");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promptSpeechInput();
        }

        if(command.indexOf("text") != -1) {
            speak("Who would you like to send a text to? Speak a number or contact name after the beep");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                promptSpeechInputSMS();
        }

        if(command.indexOf("alarm") != -1) {
            speak("What time would you like to set the alarm for? First speak the hour after the first beep then the minutes after the second beep.");
            try {
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promptSpeechInputAlarm();
        }
    }

    private void initialiseTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(tts.getEngines().size() == 0) {
                    Toast.makeText(sttScreen.this, "There is no TTS engine installed", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    tts.setLanguage(Locale.getDefault());

                    speak("You have entered the speech to text page. Tap the microphone in the centre of the screen to speak your command");
                }
            }
        });
    }

    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null,null);
        }
        else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
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


    @Override
    protected void onPause() {
        super.onPause();
    }

}
