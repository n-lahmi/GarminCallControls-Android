//package com.nlahmi.garmincallcontrols;
//
//import android.Manifest;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.ActivityCompat;
//import android.telecom.TelecomManager;
//import android.telephony.PhoneStateListener;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//
//import com.garmin.android.connectiq.ConnectIQ;
//import com.garmin.android.connectiq.IQApp;
//import com.garmin.android.connectiq.IQDevice;
//
//import java.util.List;
//
//public class MainService extends Service {
////    private PhoneStateListener callStateListener;
//    CIQHelper helper;
//
//    public MainService() {
//    }
//
//    @Override
//    public void onCreate() {
//
//    }
//
//    public void initCIQ() {
//        helper = new CIQHelper(getApplicationContext());
//        helper.initialize(new ConnectIQ.IQApplicationEventListener() {
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onMessageReceived(IQDevice device, IQApp app, List<Object> messageData, ConnectIQ.IQMessageStatus status) {
//
//                if (status == ConnectIQ.IQMessageStatus.SUCCESS) {
//                    for (Object msgObj : messageData) {
//                        String msg = msgObj.toString();
////                                Log.i("", msg);
//
//                        switch (msg) {
//                            case "ACT_HANGUP":
//                                hangup();
//                                break;
//                            case "TEST":
//
//                                break;
//                            default:
//                                Log.i("", "Received: " + msg);
//                        }
//                    }
//
//                } else {
//                    Log.i("", status.toString());
//                }
//            }
//        });
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    void hangup() {
//        TelecomManager tm = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            Log.i("", "Hanging up");
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
//                Log.e("", "No Perms");
//                return;
//            }
//            tm.endCall();
//        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
////        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
////        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        initCIQ();
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
////        callHelper.stop();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // not supporting binding
//        return null;
//    }
//}