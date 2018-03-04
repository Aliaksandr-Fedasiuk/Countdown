package com.arcus.archery;

import javax.bluetooth.LocalDevice;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {

        int time = 180;
        if (args.length > 0) {
            try {
                int _time = Integer.valueOf(args[0]);
                if (_time > 500) {
                    _time = time;
                }
                time = _time;
            } catch (NumberFormatException nfe) {

            }
        }

        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: " + localDevice.getBluetoothAddress());
        System.out.println("Name: " + localDevice.getFriendlyName());
        BluetoothServer sampleSPPServer = new BluetoothServer();
        sampleSPPServer.startServer(new CountDownFrame(new Countdown(time)));

    }

}
