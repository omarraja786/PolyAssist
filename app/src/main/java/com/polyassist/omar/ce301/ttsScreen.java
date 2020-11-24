package com.polyassist.omar.ce301;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class ttsScreen extends AppCompatActivity implements OnClickListener, OnInitListener {

    //https://www.sitepoint.com/using-android-text-to-speech-to-create-a-smart-assistant/

    private Button pdfReader;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    Context context;
    private boolean isLoaded = false;

    public ttsScreen() {

    }

    public ttsScreen(TextToSpeech tts) {
        this.myTTS = tts;
        isLoaded = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_screen);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        Button speakButton = (Button)findViewById(R.id.speak);
        speakButton.setOnClickListener(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        pdfReader = (Button) findViewById(R.id.pdfReader);
        pdfReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, pdfPage.class));
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    public boolean testSpeech(String text, Locale locale) {
        if (isLoaded && text != null && locale != null) {
            int available = myTTS.isLanguageAvailable(locale);
            if (available >= TextToSpeech.LANG_AVAILABLE) {
                myTTS.setLanguage(locale);
                myTTS.setSpeechRate(1.0f);
                if (Build.VERSION.SDK_INT >= 21) {
                    myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Random");
                } else {
                    myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
                return true;
            } else {
                Log.e("TTS", "Can't play text = " + text + " for locale = " + locale.toString());
                return false;
            }
        } else {
            Log.e("TTS", "TTS Not Initialized");
            return false;
        }
    }



    @Override
    public void onClick(View v) {
        EditText enteredText = (EditText)findViewById(R.id.enter);
        String words = enteredText.getText().toString();
        speakWords(words);
    }

    public void speakWords(String speech) {

        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }


    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.UK);
            speakWords("You have entered the text to speech page.");
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Text to speech not installed on device.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
            Log.d("TTS", "TTS Destroyed");
        }
    }

    public void shutDown() {
        myTTS.shutdown();
    }

}
