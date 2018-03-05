package com.arcus.archery;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.io.IOException;

public class App {

    public static void main(String[] args) {
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            System.out.println("Address: " + localDevice.getBluetoothAddress());
            System.out.println("Name: " + localDevice.getFriendlyName());
            BluetoothServer sampleSPPServer = new BluetoothServer();
            sampleSPPServer.startServer(new UIFrame());
        } catch (BluetoothStateException be) {
            System.out.println(String.format("ERROR: %s", be.getStackTrace()));
            new UIFrame();
        } catch (Exception e) {
            System.out.println(String.format("ERROR: %s", e.getStackTrace()));
        }
    }

}
