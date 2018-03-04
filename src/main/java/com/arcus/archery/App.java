package com.arcus.archery;

import javax.bluetooth.LocalDevice;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {

        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: " + localDevice.getBluetoothAddress());
        System.out.println("Name: " + localDevice.getFriendlyName());
        BluetoothServer sampleSPPServer = new BluetoothServer();
        sampleSPPServer.startServer(new CountDownFrame(new Countdown("30")));

    }

}
