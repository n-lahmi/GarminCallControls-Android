package com.nlahmi.garmincallcontrols;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.util.Log;

import java.util.Arrays;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.FLAG_ALLOW_RINGER_MODES;
import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.GET_DEVICES_ALL;
import static android.media.AudioManager.GET_DEVICES_OUTPUTS;

public class CallController {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void hangup(Context context) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.i("", "Hanging up");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("", "No Perms");
                return;
            }
            tm.endCall();
        }
    }

    public static boolean toggleMic(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setMicrophoneMute(!am.isMicrophoneMute());
        return am.isMicrophoneMute();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int shiftAudioSource(Context context) {
        Log.d("", "Shifting audio source");
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_CALL);
//        am.setSpeakerphoneOn(!am.isSpeakerphoneOn());
//        int status = getCurrentAudioOutput(am);
        setCurrentAudioSource(am, getCurrentAudioOutput(am) + 1);
//        for (int i = 0; i <= 3; i++) {
//            setCurrentAudioSource(am, status + i);
//            int newStatus = getCurrentAudioOutput(am);
//            if (status != newStatus)
//                return newStatus;
//        }
        return getCurrentAudioOutput(am);
    }

    public static void volumeDown(Context context) {
        Log.d("", "Volume Down");
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, ADJUST_LOWER, FLAG_SHOW_UI);
//        Log.d("", String.valueOf(am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)));
    }

    public static void volumeUp(Context context) {
        Log.d("", "Volume Up");
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, ADJUST_RAISE, FLAG_SHOW_UI);
//        Log.d("", String.valueOf(am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)));
    }

    private static int getCurrentAudioOutput(AudioManager am) {
        int status;
        if (am.isSpeakerphoneOn())
            status = 1;
        else if (am.isBluetoothScoOn())
            status = 2;
        else if (am.isWiredHeadsetOn())
            status = 3;
        else
            status = 0;

        Log.d("", String.valueOf(status));
        return status;
    }

    private static void setCurrentAudioSource(AudioManager am, int targetOutput) {
        if (targetOutput > 3)
            targetOutput = 0;

        switch (targetOutput) {
            case 0:
                am.setBluetoothScoOn(false);
                am.setSpeakerphoneOn(false);
                am.setWiredHeadsetOn(false);
                break;
            case 1:
                am.setBluetoothScoOn(false);
                am.setSpeakerphoneOn(true);
                am.setWiredHeadsetOn(false);
                break;
            case 2:
                am.setBluetoothScoOn(true);
                am.setSpeakerphoneOn(false);
                am.setWiredHeadsetOn(false);
                break;
            case 3:
                am.setBluetoothScoOn(false);
                am.setSpeakerphoneOn(false);
                am.setWiredHeadsetOn(true);
                break;
        }
    }
}
