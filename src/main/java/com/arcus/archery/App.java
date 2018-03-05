package com.arcus.archery;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.io.IOException;

public class App {

    public static void main(String[] args) {
        int time = 20;
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            System.out.println("Address: " + localDevice.getBluetoothAddress());
            System.out.println("Name: " + localDevice.getFriendlyName());
            BluetoothServer sampleSPPServer = new BluetoothServer();
            sampleSPPServer.startServer(new CountDownFrame(new Countdown(time)));
        } catch (BluetoothStateException be) {
            System.out.println(String.format("ERROR: %s", be.getStackTrace()));
            new CountDownFrame(new Countdown(time));
        } catch (Exception e) {
            System.out.println(String.format("ERROR: %s", e.getStackTrace()));
        }
    }

}
