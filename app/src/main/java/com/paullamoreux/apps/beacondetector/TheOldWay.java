package com.paullamoreux.apps.beacondetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

/**
 * Created by paul on 3/5/15.
 */
public class TheOldWay {

    private static final String TAG = "TheOldWay";
    private static final int SCAN_INTERVAL_MS = 250;

    private Handler scanHandler = new Handler();
    private boolean isScanning = false;

    public void beginScanning() {
        scanHandler.post(scanRunnable);
    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

            if (isScanning) {
                adapter.stopLeScan(leScanCallback);
            } else if (!adapter.startLeScan(leScanCallback)) {
                // an error occurred during startLeScan
            }

            isScanning = !isScanning;

            scanHandler.postDelayed(this, SCAN_INTERVAL_MS);
        }
    };

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // use the RSSI value
            Log.i(TAG, "got rssi: " + String.valueOf(rssi));
        }
    };

}

