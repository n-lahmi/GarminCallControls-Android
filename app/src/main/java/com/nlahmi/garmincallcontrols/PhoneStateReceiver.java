package com.nlahmi.garmincallcontrols;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;

import java.util.List;
import java.util.Objects;

import static com.nlahmi.garmincallcontrols.CallController.hangup;
import static com.nlahmi.garmincallcontrols.CallController.shiftAudioSource;
import static com.nlahmi.garmincallcontrols.CallController.toggleMic;
import static com.nlahmi.garmincallcontrols.CallController.volumeDown;
import static com.nlahmi.garmincallcontrols.CallController.volumeUp;

public class PhoneStateReceiver extends BroadcastReceiver {
    CIQHelper helper;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
//        context.startService(new Intent(context, MainService.class));
        this.context = context;
        initCIQ();
//        helper = new CIQHelper(context.getApplicationContext());

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        long currentTime = System.currentTimeMillis()/1000;

        // This receiver is called twice every time, but the first time the number is null
        if (number != null) {
//            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                Log.d("", "Ringing");
//            }
            String out = "";
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d("", "On Call with: " + number);
//                helper.openApp();
                out = "CALL|" + number + "|" + currentTime;
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.d("", "Call Hung up " + number);
                out = "HANGUP|" + number;
            }

            helper.sendBananaAsync(out);
            helper.keepLastMessage(out);
        }
    }

    public void initCIQ() {
        helper = new CIQHelper(context.getApplicationContext());
        helper.initialize(new ConnectIQ.IQApplicationEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onMessageReceived(IQDevice device, IQApp app, List<Object> messageData, ConnectIQ.IQMessageStatus status) {

                if (status == ConnectIQ.IQMessageStatus.SUCCESS) {
                    for (Object msgObj : messageData) {
                        String msg = msgObj.toString();
//                                Log.i("", msg);

                        switch (msg) {
                            case "GET_LAST":
                                String lastMessage = helper.getLastMessage();
                                if (!Objects.equals(lastMessage, ""))
                                    helper.sendBananaAsync(lastMessage);
                                break;
                            case "ACT_HANGUP":
                                hangup(context);
                                break;
                            case "ACT_MUTE":
                                helper.updateMicStatus(toggleMic(context));
                                break;
                            case "ACT_AUDSRC":
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    helper.updateOutputDevice(shiftAudioSource(context));
                                }
                                break;
                            case "ACT_VOLDN":
                                volumeDown(context);
                                break;
                            case "ACT_VOLUP":
                                volumeUp(context);
                                break;
                            default:
                                Log.w("", "Received: " + msg);
                        }
                    }

                } else {
                    Log.i("", status.toString());
                }
            }
        });
    }

}
