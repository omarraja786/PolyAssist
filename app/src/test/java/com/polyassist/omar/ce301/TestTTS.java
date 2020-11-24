package com.polyassist.omar.ce301;

import android.speech.tts.TextToSpeech;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestTTS {


    private ttsScreen myTTS;


    @Mock
    private TextToSpeech testTTS;

    @Before
    public void initiateTTS() throws InterruptedException {
        MockitoAnnotations.initMocks(this);
        when(testTTS.isLanguageAvailable(Locale.UK)).thenReturn(0);
        when(testTTS.setLanguage(Locale.UK)).thenReturn(0);
        when(testTTS.setSpeechRate(1.0f)).thenReturn(0);
        when(testTTS.speak("test", TextToSpeech.QUEUE_FLUSH, null, "Random")).thenReturn(0);
        myTTS = new ttsScreen(testTTS);


    }


    @After
    public void closeTTS() {
        myTTS.shutDown();
    }


    @Test
    public void testText() {
        boolean result = myTTS.testSpeech("Test", Locale.UK);
        assertTrue("TTS Should Return True", result);
    }

    @Test
    public void speechwithNullLocale() {
        boolean result = myTTS.testSpeech("Test", null);
        assertFalse("TTS Should Return False", result);
    }

    @Test
    public void speechWithNullInput() {
        boolean result = myTTS.testSpeech(null, Locale.UK);
        assertFalse("TTS Should Return False", result);
    }

    @Test
    public void speechWithUnavailableLocale() {
        when(testTTS.isLanguageAvailable(Locale.UK)).thenReturn(-2);
        boolean result = myTTS.testSpeech("Test", Locale.UK);
        assertFalse("Should return False for Locale's that are not supported", result);
    }


}
