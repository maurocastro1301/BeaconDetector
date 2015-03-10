package com.paullamoreux.apps.beacondetector;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


// where to put?
//  bluetoothGatt.disconnect();
//  bluetoothGatt.close();


@TargetApi(21)
public class BeaconDetailActivity extends ActionBarActivity {
    private static final String TAG = "BeaconDetailActivity";

    private TextView tvName;
    private TextView tvUUID;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner scanner;

    private int numAds = 0;


    private void logToDisplay(final String txt) {
        runOnUiThread(new Runnable() {
            public void run() {
                tvUUID.setText(txt);
            }
        });
    }


    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            if (result == null) {
                return;
            }

            numAds++;
            logToDisplay(String.valueOf(numAds));
//
//            ScanRecord record = result.getScanRecord();
//
//            SparseArray<byte[]> bytes =  record.getManufacturerSpecificData();
//            String s = record.toString();
//
//            // need to parse the AltBeacon advertisement here!
//
//            BluetoothDevice device = result.getDevice();
//            int value = result.getRssi();
//
//            String name = record.getDeviceName(); // device.getName();
//            String address = device.getAddress();
//            String message = name + ":" + address + ":" + String.valueOf(value);

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            String message = "Got a batch scan result";
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
            Log.i(TAG, message);
        }

    };




    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation

        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    gatt.discoverServices();
                }
            }

        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a            BluetoothGatt.discoverServices() call
            Log.i(TAG, "onServicesDiscovered");
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                Log.i(TAG, service.toString());
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    Log.i(TAG, characteristic.toString());
//                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
//                    //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
//                    // and then call setValue on that descriptor
//
//                    descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                    bluetoothGatt.writeDescriptor(descriptor);
//                }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detail);

        ScanResult result = (ScanResult) getIntent().getParcelableExtra("result");
        Log.i(TAG, result.toString());

        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(result.getDevice().getAddress());

        tvUUID = (TextView) findViewById(R.id.tvUUID);

        // Initializes Bluetooth adapter.
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();

        scanner = bleAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(result.getDevice().getAddress()).build();
            ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
            filters.add(filter);
            ScanSettings settings = new ScanSettings.Builder()
                    .setReportDelay(0)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            scanner.startScan(filters, settings, scanCallback);
//            scanner.startScan(scanCallback);
        }


//        BluetoothGatt gatt;
//        BluetoothDevice device = result.getDevice();
//        if (device != null ) {
//            gatt = device.connectGatt(this, true, btleGattCallback);
//        }
    }


    @Override
    protected void onDestroy() {
        scanner.stopScan(scanCallback);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_detail, menu);
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
