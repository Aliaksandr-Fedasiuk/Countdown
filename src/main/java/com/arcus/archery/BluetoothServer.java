package com.arcus.archery;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BluetoothServer {

    boolean run = true;

    // start server
    public void startServer(UIFrame uiFrame) throws IOException {
        // Create a UUID for SPP
        UUID uuid = new UUID("0000110100001000800000805F9B34FB", false);
        // Create the servicve url
        String connectionString = "btspp://localhost:" + uuid + ";name=Sample Archery Bluetooth Server";

        // open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

        // Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        StreamConnection connection = streamConnNotifier.acceptAndOpen();

        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: " + dev.getBluetoothAddress());
        System.out.println("Remote device name: " + dev.getFriendlyName(true));

        // read string from spp client
        InputStream inStream = connection.openInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
        while (run) {
            String lineRead = bReader.readLine();
            if (lineRead != null && !lineRead.isEmpty()) {
                if (lineRead.equals("i")) {
                    uiFrame.init();
                } else if (lineRead.equals("1")) {
                    uiFrame.setTeamAndStartTimer(1);
                } else if (lineRead.equals("2")) {
                    uiFrame.setTeamAndStartTimer(2);
                }
                System.out.println("Message from mobile device: " + lineRead);
            }
        }
        streamConnNotifier.close();
    }

}