package com.paullamoreux.apps.beacondetector;

import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;


public class MainActivity extends ActionBarActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";

    private BeaconManager beaconManager;
    private TextView tvDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDebug = (TextView) findViewById(R.id.tvDebug);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    String str;

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                str = "Saw a beacon for the first time!\n";
                Log.i(TAG, str);
//                tvDebug.setText(tvDebug.getText().toString() + str);
                //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didExitRegion(Region region) {
                str = "No longer see some beacon\n";
                Log.i(TAG, str);
//                tvDebug.setText(tvDebug.getText().toString() + str);
//                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                str = "Just switched from seeing/not seeing beacons: " + state + "\n";
                Log.i(TAG, str);
//                tvDebug.setText(tvDebug.getText().toString() + str);
//                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
