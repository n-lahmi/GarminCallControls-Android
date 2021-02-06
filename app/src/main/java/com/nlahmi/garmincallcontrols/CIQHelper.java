package com.nlahmi.garmincallcontrols;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.IQOpenApplicationStatus;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.List;

import static com.garmin.android.connectiq.ConnectIQ.IQOpenApplicationStatus.APP_IS_NOT_INSTALLED;

public class CIQHelper {

    ConnectIQ connectIQ;
    private static final String appId = "cdf4d946250a48ca81666ced25d9493e";
    private final IQApp appObj;
    Context ctx;

    public CIQHelper(Context ctx) {
        this.ctx = ctx;
        appObj = new IQApp(appId);
        connectIQ = ConnectIQ.getInstance(this.ctx, ConnectIQ.IQConnectType.WIRELESS);
//        connectIQ = ConnectIQ.getInstance(this.ctx, ConnectIQ.IQConnectType.TETHERED);

    }

    public void initialize(final ConnectIQ.IQApplicationEventListener listener) {
        connectIQ.initialize(ctx, true, new ConnectIQ.ConnectIQListener() {
            // Called when the SDK has been successfully initialized
            @Override
            public void onSdkReady() {
                Log.d("", "SDK ready");

                if (listener != null) {
                    try {
                        register(listener);
                    } catch (InvalidStateException e) {
                        e.printStackTrace();
                    } catch (ServiceUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onInitializeError(ConnectIQ.IQSdkErrorStatus iqSdkErrorStatus) {
                Log.e("", iqSdkErrorStatus.toString());
            }

            // Called when the SDK has been shut down
            @Override
            public void onSdkShutDown() {
                // Take care of any post shutdown requirements
            }

        });
    }

    public void register(ConnectIQ.IQApplicationEventListener listener) throws InvalidStateException, ServiceUnavailableException {
        for (IQDevice dev : connectIQ.getKnownDevices()) {
            connectIQ.registerForAppEvents(dev, appObj, listener);
        }
    }

    public IQDevice getActiveDevice() {
        List<IQDevice> paired = null;
        try {
            paired = connectIQ.getConnectedDevices();
        } catch (InvalidStateException e) {
            e.printStackTrace();
        } catch (ServiceUnavailableException e) {
            e.printStackTrace();
        }
        if (paired.size() == 1) {
            return paired.get(0);
        } else {
            try {
                throw new Exception("Only one watch at a time is supported. Found " + paired.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sendBanana(String msg) throws InvalidStateException, ServiceUnavailableException {
        IQDevice device = getActiveDevice();

        connectIQ.sendMessage(device, appObj, msg, new ConnectIQ.IQSendMessageListener() {

            @Override
            public void onMessageStatus(IQDevice device, IQApp app, ConnectIQ.IQMessageStatus status) {
                if (status != ConnectIQ.IQMessageStatus.SUCCESS) {
                    Log.i("", status.name());

                }
            }
        });
    }

    public void sendBananaAsync(final String msg) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CIQHelper.this.sendBanana(msg);
                } catch (InvalidStateException e) {
                    e.printStackTrace();
                } catch (ServiceUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void updateMicStatus(Boolean micStatus) {
        sendBananaAsync("MUTE|" + (micStatus ? "1" : "0"));
    }

    public void updateOutputDevice(int outputDevice) {
        sendBananaAsync("OUTPUT|" + outputDevice);
    }

    public void openApp() {
        try {
            connectIQ.openApplication(getActiveDevice(), appObj, new ConnectIQ.IQOpenApplicationListener() {

                @Override
                public void onOpenApplicationResponse(IQDevice device, IQApp app, IQOpenApplicationStatus status) {
                    if (status == APP_IS_NOT_INSTALLED) {
                        try {
                            connectIQ.openStore("2702e5a7-40d0-4858-8b03-d1d6a3ce1bef");
                        } catch (InvalidStateException e) {
                            e.printStackTrace();
                        } catch (ServiceUnavailableException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (InvalidStateException e) {
            e.printStackTrace();
        } catch (ServiceUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void keepLastMessage(String msg) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_pref_messages), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ctx.getString(R.string.shared_pref_lastmsg_key), msg);
        editor.apply();
    }

    public String getLastMessage() {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_pref_messages), Context.MODE_PRIVATE);
        Log.d("", "SharedPref: " + sharedPref.getString(ctx.getString(R.string.shared_pref_lastmsg_key), "HANGUP"));
        String msg = sharedPref.getString(ctx.getString(R.string.shared_pref_lastmsg_key), "HANGUP");
        keepLastMessage("");
        return msg;
    }
}
