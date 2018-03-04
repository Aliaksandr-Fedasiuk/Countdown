# Countdown  

1.
$sudo vi /etc/systemd/system/bluetooth.target.wants/bluetooth.service  
change line:  
ExecStart=/usr/lib/bluetooth/bluetoothd  
to
ExecStart=/usr/lib/bluetooth/bluetoothd -C  
(simply adding the -C option)

2.
for ubuntu version  
sudo apt-get install libbluetooth-dev   

for fedora version  
sudo dnf install bluez-libs-devel  

should do the trick  
Source: http://bluecove.org/bluecove-gpl/  
Source: http://yasir03.online.fr/?p=267  

The package has a differant name in CentOS 5.5... it is  
bluez-libs-devel-3.7-1.1.x86_64  <---64-bit  
bluez-libs-devel-3.7-1.1.i386  <---32-bit  

3.
sudo vi /etc/dbus-1/system.d/bluetooth.conf  
```xml
<policy user="<username>">  
    <allow own="org.bluez"/>  
    <allow send_destination="org.bluez"/>  
    <allow send_interface="org.bluez.Agent1"/>  
    <allow send_interface="org.bluez.MediaEndpoint1"/>  
    <allow send_interface="org.bluez.MediaPlayer1"/>  
    <allow send_interface="org.bluez.ThermometerWatcher1"/>  
    <allow send_interface="org.bluez.AlertAgent1"/>  
    <allow send_interface="org.bluez.Profile1"/>  
    <allow send_interface="org.bluez.HeartRateWatcher1"/>  
    <allow send_interface="org.bluez.CyclingSpeedWatcher1"/>  
    <allow send_interface="org.bluez.GattCharacteristic1"/>  
    <allow send_interface="org.bluez.GattDescriptor1"/>  
    <allow send_interface="org.freedesktop.DBus.ObjectManager"/>  
    <allow send_interface="org.freedesktop.DBus.Properties"/>  
  </policy>  
```
$sudo systemctl daemon-reload  
$sudo systemctl restart bluetooth  


4.
$mvn clean compile assembly:single  
$sudo java -jar ./countdown-1.0-SNAPSHOT-jar-with-dependencies.jar <time in seconds>  

