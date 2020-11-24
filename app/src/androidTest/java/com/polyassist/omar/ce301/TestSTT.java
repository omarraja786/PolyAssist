package com.polyassist.omar.ce301;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestSTT {

    @Test
    public void testSpeechIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        if(intent != null) {
            assert(true);
        }
        else {
            assert(false);
        }
    }

    @Test
    public void testRecognitionAvailable(){
        Context context = InstrumentationRegistry.getContext();
        if(SpeechRecognizer.isRecognitionAvailable(context)) {
            assert(true);
        }
        else{
            assert(false);
        }
    }
}
