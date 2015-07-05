#This page describes how to use Proxoid with Linux.
# Installation on Linux #
The guide below describes how to install and use proxoid with Linux. This guide has been tested with Ubuntu 9.04.
## Step 0 : Download and install Proxoid on your phone ##
See [this page](http://code.google.com/p/proxoid/wiki/installationPhone) for instructions.
## Step 1 : Configure udev to recognize your phone ##
First, plug in your Android phone _via_ usb. Then, create a file
```
/etc/udev/rules.d/09-android.rules 
```
containing the two lines
```
SUBSYSTEM=="usb", ATTR{idVendor}=="22b8", MODE="0666", GROUP="plugdev"
SUBSYSTEM=="usb", ATTR{idVendor}=="0bb4", MODE="0666", GROUP="plugdev"
SUBSYSTEM=="usb", SYSFS{idVendor}=="04e8", MODE="0666", GROUP="plugdev"
SUBSYSTEM=="usb", SYSFS{idVendor}=="18d1", MODE="0666", GROUP="plugdev"
```
Be sure to place yourself in "plugdev" group.
Those lines works for all phone I tested : HTCs (G1, Tatoo, Nexus One), Motorola Milestone and Samsung Galaxy (I7500).
```
lsusb
```
Mine returns
```
...
Bus 001 Device 005: ID 0bb4:0c02 High Tech Computer Corp.
...
```
Then, restart udev by issuing the command
```
sudo /etc/init.d/udev restart
```
## Step 2 : Download SDK ##
Download the Android SDK from [here](http://developer.android.com/intl/fr/sdk/index.html).
Extract the file
```
unzip <downloadfile.zip>
```
## Step 3 : Tunneling ##
Now, we can start tunneling betwwen your computer and your phone.
Change directory to the tools subdirectory of the extracted Android SDK directory. Issue the command
```
./adb forward tcp:8080 tcp:8080
```
## Step 3 : configure your browser ##
Your browser must use the proxy server running at localhost:8080.
### Firefox ###
In firefox, go to Tools->Options->Advanced->Network->Settings. Here, enter the settings shown in the image below

![http://lh4.ggpht.com/_3FwLAmI01qI/Sl79XIYBfxI/AAAAAAAAAuc/hryEM_JIDgY/s400/firefox_proxoid_proxy_settings.png](http://lh4.ggpht.com/_3FwLAmI01qI/Sl79XIYBfxI/AAAAAAAAAuc/hryEM_JIDgY/s400/firefox_proxoid_proxy_settings.png)

Finally, untick the work offline check button in the file menu.
### (Planned) Epiphany ###
## Step 4 : Run proxoid on your phone ##
Finally, run Proxoid on your Android phone, click "Start" and start browsing ...
# Advanced topics #
## Using Proxoid system-wide (Gnome only so far, Untested) ##
Start network preferences by running
```
gnome-network-preferences
```
or
```
gnome-network-properties
```
Click **Manual Proxy Configuration** and check **Use the same proxy for all protocols**. Then fill out
  * HTTP Proxy:
> > localhost Port: 8080
Finally, click the button **Apply System-wide**.
## Configure Evolution (Untested) ##
To use Evolution with Proxoid, go to Edit->Preferences->Network Preferences. Fill out the fields
  * HTTP Proxy:
> > localhost Port: 8080
  * Secure HTTP Proxy:
> > localhost Port: 8080
  * SOCKS Proxy:
> > localhost Port: 8080
## Configure SSH ##
To use SSH with proxoid, you need to download the latest version of [corkscrew](http://www.agroman.net/corkscrew/), a tool for tunneling SSH through HTTP proxies. Unpack the archive, change to the directory and compile the program
```
./configure
make
sudo make install
```
To use SSH through proxoid, add the following line to your ~/.ssh/config file
```
ProxyCommand /usr/local/bin/corkscrew localhost 8080 %h %p 
```
If you keep beeing logged out from the server, add the following line to the /etc/ssh/ssh\_config file
```
ServerAliveInterval = 2
```
# Troubleshooting #
To check whether the device is recogniced properly by adb, try issuing the command (from the tools dir of the android SDK)
```
./adb devices
```
This should give you output of the form
```
* daemon not running. starting it now *
* daemon started successfully *
List of devices attached 
HTXXNKFXXXXX    device
```
If your devices is still not found, try to reboot (power off/power on) your computer with the phone plugged in via usb.