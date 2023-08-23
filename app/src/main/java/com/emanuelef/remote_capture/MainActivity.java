package com.emanuelef.remote_capture;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.emanuelef.remote_capture.model.CaptureSettings;
import com.emanuelef.remote_capture.model.Prefs;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    private Button button;
    private boolean isActive;
    private CaptureHelper captureHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureHelper = new CaptureHelper(this);

        captureHelper.setListener(success -> {
            isActive = true;
            button.setText("Stop tracking");

        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isActive){
                    button.setText("Stop tracking");
                    stopCapture();
                }else{
                    button.setText("Start tracking");
                    startCapture();

                }


            }
        });

    }

    private boolean showRemoteServerAlert() {
        if(mPrefs.getBoolean(Prefs.PREF_REMOTE_COLLECTOR_ACK, false))
            return false; // already acknowledged

        if(((Prefs.getDumpMode(mPrefs) == Prefs.DumpMode.UDP_EXPORTER) && !Utils.isLocalNetworkAddress(Prefs.getCollectorIp(mPrefs))) ||
                (Prefs.getSocks5Enabled(mPrefs) && !Utils.isLocalNetworkAddress(Prefs.getSocks5ProxyAddress(mPrefs)))) {
            Log.i("ttt", "Showing possible scan notice");

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.remote_collector_notice)
                    .setPositiveButton(R.string.ok, (d, whichButton) -> mPrefs.edit().putBoolean(Prefs.PREF_REMOTE_COLLECTOR_ACK, true).apply())
                    .show();
            dialog.setCanceledOnTouchOutside(false);
            return true;
        }

        return false;
    }

    public void startCapture() {
        if(showRemoteServerAlert())
            return;
        captureHelper.startCapture(new CaptureSettings(this, mPrefs));


    }
    public void stopCapture() {
        CaptureService.stopService();
        button.setText("Start tracking");
        isActive = false;
    }


}