package com.nlahmi.garmincallcontrols;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    CIQHelper helper;
    static final String[] neededPerms = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] ungivenPerms = askPerms();
            if (ungivenPerms.length != 0) {
                Log.e("", "Permissions not sufficient");
            }
        }

        helper = new CIQHelper(getApplicationContext());

//        Button button = findViewById(R.id.btn123);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                helper.sendBananaAsync("test|123|456");
//            }
//        });

//        try {
//            main();
//        } catch (InvalidStateException e) {
//            e.printStackTrace();
//        } catch (ServiceUnavailableException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    String[] askPerms() {
        String[] neededPerms = checkPerms();

        // Don't try to request if there's nothing to request, you greedy
        if (neededPerms.length > 0) {
            ActivityCompat.requestPermissions(this, neededPerms, 0);
            return checkPerms();
        } else {
            return neededPerms;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    String[] checkPerms() {
        List<String> askPerms = new ArrayList<String>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (String perm : neededPerms) {
                if (getApplicationContext().checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED)
                    askPerms.add(perm);
            }
        }

        return askPerms.toArray(new String[0]);
    }
}
