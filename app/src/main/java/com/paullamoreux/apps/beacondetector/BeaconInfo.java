package com.paullamoreux.apps.beacondetector;

import java.util.ArrayList;

/**
 * Created by paul on 3/10/15.
 */
public class BeaconInfo {
    private String address;
    private int currentRssi;
    private float avgRssi;
    private ArrayList<Integer> values;
    private int bufferSize = 25;
    private boolean isZuli = false;

    public BeaconInfo(String address, int currentRssi, boolean isZuli) {
        this.address = address;
        this.currentRssi = currentRssi;
        this.isZuli = isZuli;
        values = new ArrayList<Integer>();
    }

    public void setRssi(int rssi) {

        values.add(rssi);

        if (values.size() > bufferSize) {
            values.remove(0);
        }

        if (values.size() == 0)
        {
            return;
        }

        float sum = 0;
        for (Integer i : values) {
            sum += i;
        }

        avgRssi = sum / values.size();

    }

    public float getAvgRssi() {
        return -avgRssi;
    }


    public boolean getIsZuli() {
        return isZuli;
    }
}
