package com.polyassist.omar.ce301;

import android.content.pm.PackageManager;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;


@RunWith(AndroidJUnit4.class)
public class TestPermissions {

    int checkReadSMS = getContext().checkCallingOrSelfPermission(android.Manifest.permission.READ_SMS);
    int checkReceiveSMS = getContext().checkCallingOrSelfPermission(android.Manifest.permission.RECEIVE_SMS);
    int checkReadContacts = getContext().checkCallingOrSelfPermission(android.Manifest.permission.READ_CONTACTS);
    int checkRecordAudio = getContext().checkCallingOrSelfPermission(android.Manifest.permission.RECORD_AUDIO);
    int checkCallPhone = getContext().checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE);
    int checkSendSMS = getContext().checkCallingOrSelfPermission(android.Manifest.permission.SEND_SMS);
    int checkReadStorage = getContext().checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
    int checkWriteStorage = getContext().checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int checkCamera = getContext().checkCallingOrSelfPermission(android.Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionReadSMS = GrantPermissionRule.grant(android.Manifest.permission.READ_SMS);

    @Rule
    public GrantPermissionRule permissionReceiveSMS = GrantPermissionRule.grant(android.Manifest.permission.RECEIVE_SMS);

    @Rule
    public GrantPermissionRule permissionReadContacts = GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS);

    @Rule
    public GrantPermissionRule permissionRecordAudio = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO);

    @Rule
    public GrantPermissionRule permissionCallPhone = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);

    @Rule
    public GrantPermissionRule permissionSendSMS = GrantPermissionRule.grant(android.Manifest.permission.SEND_SMS);

    @Rule
    public GrantPermissionRule permissionReadStorage = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);

    @Rule
    public GrantPermissionRule permissionWriteStorage = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);


    @Test
    public void testReadSMS() {
        if (checkReadSMS== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }
    @Test
    public void testReceiveSMS() {
        if (checkReceiveSMS== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void testReadContacts() {
        if (checkReadContacts== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void testRecordAudio() {
        if (checkRecordAudio== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void testCallPhone() {
        if (checkCallPhone== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void testSendSMS() {
        if (checkSendSMS== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void testReadStorage() {
        if (checkReadStorage== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void testWriteStorage() {
        if (checkWriteStorage== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void testCamera() {
        if (checkCamera== PackageManager.PERMISSION_GRANTED){
            assert(true);
        }
        else{
            assert(false);
        }
    }

}
