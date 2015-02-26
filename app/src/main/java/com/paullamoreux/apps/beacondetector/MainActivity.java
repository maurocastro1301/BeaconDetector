package com.paullamoreux.apps.beacondetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity implements BeaconConsumer {
    private static final String TAG = "BeaconDetectorMainActivity";
    private final static int REQUEST_ENABLE_BT = 1;

    private BluetoothManager bluetoothManager;
    private BeaconManager beaconManager;
    private ArrayList<String> messages;
    private ArrayAdapter<String> aMessages;
    private ListView lvMessages;

    private ArrayList<ScanResult> results;
    private ResultAdapter aResults;

    BluetoothAdapter bleAdapter;
    BluetoothLeScanner scanner;
    HashMap<String, BluetoothGatt> gatts = new HashMap<String, BluetoothGatt>();
    HashMap<String, ScanResult> resultsByAddress = new HashMap<String, ScanResult>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messages = new ArrayList<String>();
        results = new ArrayList<ScanResult>();

        lvMessages = (ListView) findViewById(R.id.lvMessages);
        aMessages = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        aResults = new ResultAdapter(this, results);
        lvMessages.setAdapter(aResults);

        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult result = results.get(position);
                goToDetailActivity(result);
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported", Toast.LENGTH_LONG).show();
            return;
        }

        // Initializes Bluetooth adapter.
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bleAdapter == null || !bleAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

//        beaconManager = BeaconManager.getInstanceForApplication(this);
//        beaconManager.bind(this);

        //bleAdapter = BluetoothAdapter.getDefaultAdapter();
        scanner = bleAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            Toast.makeText(this, "scanner created", Toast.LENGTH_SHORT).show();
            scanner.startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {

                    if (result == null) {
                        return;
                    }

                    ScanRecord record = result.getScanRecord();
                    BluetoothDevice device = result.getDevice();
                    int value = result.getRssi();

                    String name = record.getDeviceName(); // device.getName();
                    String address = device.getAddress();
                    String message = name + ":" + address + ":" + String.valueOf(value);

                    if (!resultsByAddress.containsKey(address)) {
                        resultsByAddress.put(address, result);
                        results.add(result);
                        logToDisplay(message);
                    }


                    // If this is the first time we've seen this device, crank up a conversation
                    // with it.

//                    if (!gatts.containsKey(address)) {
//                        BluetoothGatt gatt = device.connectGatt(MainActivity.this, true, new BluetoothGattCallback() {
//                            @Override
//                            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                                super.onConnectionStateChange(gatt, status, newState);
//                                logToDisplay("onConnectionStateChange");
//                            }
//
//                            @Override
//                            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//                                super.onCharacteristicChanged(gatt, characteristic);
//                                logToDisplay("onCharacteristicChanged");
//                            }
//
//                            @Override
//                            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                                super.onServicesDiscovered(gatt, status);
//                                logToDisplay("onCharacteristicChanged");
//                            }
//                        });
//                        gatts.put(address, gatt);
//                        logToDisplay("Got a GATT for: " + address);
//                    }

                    //Toast.makeText(MainActivity.this, name + ":" + address + ":" + String.valueOf(value), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    String message = "Got a batch scan result";
                    logToDisplay(message);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    String message = "unspecified failure";
                    switch(errorCode) {
                        case SCAN_FAILED_ALREADY_STARTED:
                            message = "SCAN_FAILED_ALREADY_STARTED";
                            break;

                        case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                            message = "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED";
                            break;

                        case SCAN_FAILED_FEATURE_UNSUPPORTED:
                            message = "SCAN_FAILED_FEATURE_UNSUPPORTED";
                            break;

                        case SCAN_FAILED_INTERNAL_ERROR:
                            message = "SCAN_FAILED_INTERNAL_ERROR";
                            break;
                    }
                    logToDisplay(message);
                    //Toast.makeText(MainActivity.this, "onScanResult: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void goToDetailActivity(ScanResult result) {
//        Intent i = new Intent(this, BeaconDetailActivity.class);
//        startActivity(i);

        //ScanResult dataToSend = new MyParcelable();
        Intent i = new Intent(this, BeaconDetailActivity.class);
        i.putExtra("result", result); // using the (String name, Parcelable value) overload!
        startActivity(i); // dataToSend is now passed to the new Activity
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }
        if (scanner != null) {
            //scanner.stopScan();
        }
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
                messages.add(line);
                aMessages.notifyDataSetChanged();
                aResults.notifyDataSetChanged();
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
