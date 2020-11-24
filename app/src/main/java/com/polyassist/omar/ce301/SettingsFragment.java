package com.polyassist.omar.ce301;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

//https://google-developer-training.gitbooks.io/android-developer-fundamentals-course-practicals/content/en/Unit%204/92_p_adding_settings_to_an_app.html

public class SettingsFragment extends PreferenceFragment {

    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;

    private Speaker speaker;

    private BroadcastReceiver smsReceiver;
    private SharedPreferences sharedPrefs;

    private TextToSpeech tts;

    private static final Intent sSettingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


        sharedPrefs = this.getActivity().getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE);

        Preference preferenceBoot = findPreference("bootup");
        preferenceBoot.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    speaker.allow(true);
                    speaker.speak("App on device start-up enabled");
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE).edit();
                    editor.putBoolean("toggleState", true);
                    editor.commit();
                } else {
                    speaker.allow(true);
                    speaker.speak("App on device start-up disabled");
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE).edit();
                    editor.putBoolean("toggleState", false);
                    editor.commit();
                }
                return true;
            }
        });

        Preference preference = findPreference("sms_reader");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    speaker.allow(true);
                    speaker.speak(getString(R.string.start_speaking));
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE).edit();
                    editor.putBoolean("smsState", true);
                    editor.commit();
                } else {
                    speaker.speak(getString(R.string.stop_speaking));
                    speaker.allow(false);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE).edit();
                    editor.putBoolean("smsState", false);
                    editor.commit();
                }
                return true;
            }
        });

        //https://developer.android.com/training/run-background-service/create-service
        Preference preferenceBackground = findPreference("backgroundService");
        preferenceBackground.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    speaker.allow(true);
                    speaker.speak("Background Service Started");
                    Intent startServiceIntent = new Intent(getActivity(), backgroundService.class);
                    getActivity().startService(startServiceIntent);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE).edit();
                    editor.putBoolean("backgroundState", true);
                    editor.commit();
                } else {
                    speaker.allow(true);
                    speaker.speak("Background Service Stopped");
                    Intent stopServiceIntent = new Intent(getActivity(), backgroundService.class);
                    getActivity().stopService(stopServiceIntent);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE).edit();
                    editor.putBoolean("backgroundState", false);
                    editor.commit();
                }
                return true;
            }
        });

        Preference preferenceSetting = findPreference("settingsButton");
        preferenceSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(sSettingsIntent);
                return false;
            }
        });

        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("You have entered the settings page.");

                } else {
                    Log.e("TTS", "Initialisation Failed!");
                }
            }
        });

        checkTTS();
        initializeSMSReceiver();
        registerSMSReceiver();
    }


    private void checkTTS() {
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speaker = new Speaker(getActivity());
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    private void initializeSMSReceiver() {

        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdus.length; i++) {
                        byte[] pdu = (byte[]) pdus[i];
                        SmsMessage message = SmsMessage.createFromPdu(pdu);
                        String text = message.getDisplayMessageBody();
                        String sender = getContactName(message.getOriginatingAddress());
                        speaker.pause(LONG_DURATION);
                        speaker.speak("You have a new message from" + sender + "!");
                        speaker.pause(SHORT_DURATION);
                        speaker.speak(text);
                    }
                }

            }
        };
    }

    private String getContactName(String phone) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        String projection[] = new String[]{ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return "Number isn't in your contacts list";
        }
    }

    private void registerSMSReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        speaker.destroy();
    }
}
