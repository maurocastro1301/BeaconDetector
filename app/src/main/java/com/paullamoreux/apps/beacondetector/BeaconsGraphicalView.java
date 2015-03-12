package com.paullamoreux.apps.beacondetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 3/10/15.
 */
public class BeaconsGraphicalView extends View {
    private static final String TAG = "BeaconsGraphicalView";

    private int screenWidth = -1;
    private int screenHeight = -1;
    private int targetXOffset = -1;
    private int targetYOffset = -1;
    private int numXPoints = 12;
    private int numYPoints = 12;
    private int xStep = 75;
    private int yStep = 70;

    private int animationMarker = 0;

    private Paint drawPaintTargets;
    private Paint drawPaintLines;
    private Paint drawPaintBreadcrumbs;
    private Paint drawPaintTargetsFound;

    private List<Point> targets;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner scanner;

    HashMap<String, BeaconInfo> devicesByAddress = new HashMap<String, BeaconInfo>();


    public BeaconsGraphicalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
        setupTargets();

        // Initializes Bluetooth adapter.
        bluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();

        startAnimationTimer();

        //bleAdapter = BluetoothAdapter.getDefaultAdapter();
        scanner = bleAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            ScanFilter filter = null; //new ScanFilter.Builder().setDeviceAddress(result.getDevice().getAddress()).build();
            ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
            filters.add(filter);
            ScanSettings settings = new ScanSettings.Builder()
                    .setReportDelay(0)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            scanner.startScan(null, settings, scanCallback);
//            scanner.startScan(scanCallback);


        }
    }


    Handler handler = new Handler();

    private void startAnimationTimer() {
        handler.post(runnableCode);
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here
            animationMarker += 1;
            if (animationMarker > numXPoints - 1) animationMarker = 0;
            postInvalidate();
            // Repeat this runnable code block again every 2 seconds
            handler.postDelayed(runnableCode, 100);
        }
    };

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaintTargets = new Paint();
        drawPaintTargets.setColor(Color.BLACK);
        drawPaintTargets.setAntiAlias(true);
        drawPaintTargets.setStrokeWidth(2);
        drawPaintTargets.setStyle(Paint.Style.STROKE);
        drawPaintTargets.setStrokeJoin(Paint.Join.ROUND);
        drawPaintTargets.setStrokeCap(Paint.Cap.ROUND);

        drawPaintTargetsFound = new Paint();
        drawPaintTargetsFound.setColor(Color.BLUE);
        drawPaintTargetsFound.setAntiAlias(true);
        drawPaintTargetsFound.setAlpha(225);
        drawPaintTargetsFound.setStrokeWidth(5);
        drawPaintTargetsFound.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaintTargetsFound.setStrokeJoin(Paint.Join.ROUND);
        drawPaintTargetsFound.setStrokeCap(Paint.Cap.ROUND);

        drawPaintLines = new Paint();
        drawPaintLines.setColor(Color.BLUE);
        drawPaintLines.setAntiAlias(true);
        drawPaintLines.setStrokeWidth(5);
        drawPaintLines.setStyle(Paint.Style.STROKE);
        drawPaintLines.setStrokeJoin(Paint.Join.ROUND);
        drawPaintLines.setStrokeCap(Paint.Cap.ROUND);

        drawPaintBreadcrumbs = new Paint();
        drawPaintBreadcrumbs.setColor(Color.DKGRAY);
        drawPaintBreadcrumbs.setAntiAlias(true);
        drawPaintBreadcrumbs.setStrokeWidth(5);
        drawPaintBreadcrumbs.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaintBreadcrumbs.setStrokeJoin(Paint.Join.ROUND);
        drawPaintBreadcrumbs.setStrokeCap(Paint.Cap.ROUND);
    }


    private void setupTargets() {
        int xPosition;
        int yPosition;

        targets = new ArrayList<Point>();

        // lay out the 9 dots on the screen
        for (int i = 0; i < numXPoints; i++) {
            for (int j = 0; j < numYPoints; j++) {
                xPosition = (i * xStep);
                yPosition = (j * yStep);
                targets.add(new Point(xPosition, yPosition));
            }
        }
    }




    // BLE

    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            if (result == null) {
                return;
            }

            BluetoothDevice device = result.getDevice();
            String address = device.getAddress();
            String name = device.getName();
            int value = result.getRssi();

//            if (name != null && name.toLowerCase().contains("zuli")) {
//            if (name != null) {
            boolean isZuli = (name != null && name.toLowerCase().contains("zuli"));
            if (!devicesByAddress.containsKey(address)) {
                devicesByAddress.put(address, new BeaconInfo(address, value, isZuli));
            } else {
                BeaconInfo info = devicesByAddress.get(address);
                if (info != null) {
                    info.setRssi(value);
                }
            }

            postInvalidate();
//            }
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
            Log.i(TAG, message);
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





    // DRAWING


    @Override
    protected void onDraw(Canvas canvas) {
        if (screenWidth < 0 || screenHeight < 0){
            screenWidth = getMeasuredWidth();
            screenHeight = getMeasuredHeight();
            targetXOffset = (screenWidth - ((numXPoints - 1) * xStep)) / 2;
            targetYOffset = (screenHeight - ((numYPoints - 1) * yStep)) / 2;
        }

        drawTargets(canvas);
        drawValues(canvas);

    }


    private void drawTargets(Canvas canvas) {
        float width = 7;
        for (Point p : targets) {
//            canvas.drawCircle(targetXOffset + p.x, targetYOffset + p.y, 1, drawPaintTargets);
            canvas.drawLine(targetXOffset + p.x - width, targetYOffset + p.y, targetXOffset + p.x + width, targetYOffset + p.y, drawPaintTargets);
        }
    }

    private void drawValues(Canvas canvas) {
        float width = 25;
        int i = 0;
        int rssi;
        float xScale= (numXPoints - 1) * xStep / 11;
        float yScale = (numYPoints - 1) * yStep / (100);
        float x;
        float y;

        Iterator it = devicesByAddress.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            BeaconInfo info = (BeaconInfo) pair.getValue();
            float value = info.getAvgRssi() - 20;
            x = targetXOffset + i * xScale;
            y = targetYOffset + (value * yScale);
            //Log.i(TAG, pair.getKey() + " = " + pair.getValue());
//            canvas.drawLine(x - width, y, x + width, y, drawPaintTargetsFound);
            if (info.getIsZuli() == true) {
                canvas.drawCircle(x, y, width, drawPaintTargetsFound);
            } else {
                canvas.drawCircle(x, y, width, drawPaintBreadcrumbs);
            }
            i++;
            if (i >= numXPoints) {
                break;
            }
        }

        int value = Math.abs(animationMarker);
        x = targetXOffset + (value * xScale);
        y = 20; //targetYOffset + ((numYPoints - 1) * yStep) + 20;
        //Log.i(TAG, pair.getKey() + " = " + pair.getValue());
        canvas.drawCircle(x, y, 10, drawPaintBreadcrumbs);
    }

//    private void logToDisplay(final String line) {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                messages.add(line);
//                aMessages.notifyDataSetChanged();
//                aResults.notifyDataSetChanged();
//            }
//        });
//    }




    // INPUT MOTION PROCESSING


    private Point isNearTarget(Point pt) {
        int x = pt.x - targetXOffset;
        int y = pt.y - targetYOffset;
        int padding = 40;

//        for (Point t : targets) {
//            if (x > t.x - padding
//                    && x < t.x + padding
//                    && y > t.y - padding
//                    && y < t.y + padding) {
//                targetsFound.add(t);
//                targets.remove(t);
//                return t;
//            }
//        }

        return null;
    }


    // Append new circle each time user presses on screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                break;

            default:
                return false;
        }


        // indicate view should be redrawn
        postInvalidate();

        return true;
    }


    private void stopScanning() {
        if (scanner != null) {
            scanner.stopScan(scanCallback);
            Toast.makeText(getContext(), "Scanning Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void startScanning() {
        if (scanner != null) {
            scanner.startScan(scanCallback);
            Toast.makeText(getContext(), "Scanning Started", Toast.LENGTH_SHORT).show();
        }
    }



}




