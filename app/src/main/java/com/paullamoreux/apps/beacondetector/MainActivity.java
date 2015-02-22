package com.paullamoreux.apps.beacondetector;

import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";

    private BeaconManager beaconManager;
    private ArrayList<String> messages;
    private ArrayAdapter<String> aMessages;
    private ListView lvMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messages = new ArrayList<String>();

        lvMessages = (ListView) findViewById(R.id.lvMessages);
        aMessages = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        lvMessages.setAdapter(aMessages);

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
                messages.add(str);
                logToDisplay(str);
                Log.i(TAG, str);
//                tvDebug.setText(tvDebug.getText().toString() + str);
                //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didExitRegion(Region region) {
                str = "No longer see some beacon\n";
                messages.add(str);
                logToDisplay(str);
                Log.i(TAG, str);
//                tvDebug.setText(tvDebug.getText().toString() + str);
//                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                str = "Just switched from seeing/not seeing beacons: " + state + "\n";
                messages.add(str);
                logToDisplay(str);
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


    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                aMessages.notifyDataSetChanged();
            }
        });
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


    public void onClickRefresh (View v) {
        aMessages.notifyDataSetChanged();
    }
}
