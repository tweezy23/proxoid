# Installation on Windows #

## Step 1 : SDK download and extraction ##

You first need to download Android's SDK.
Go to http://developer.android.com/sdk/1.5_r1/index.html and download Window's zip (filename is android-sdk-windows-1.5\_r1.zip as I'm writing this help).

Then, extract to your disk.
Let say it will be located in c:\android-sdk-windows-1.5\_r1.

## Step 2 : Android's usb driver installation ##

- Plug your phone.
- Windows will ask you for the driver. Browse to c:\android-sdk-windows-1.5\_r1\usb\_driver\x86

To check if your phone is correctly detected :
- launch a dos console : [Start](Start.md)/[Run](Run.md)/cmd.exe
- go to sdk's tool subdirectory :
c:
cd \android-sdk-windows-1.5\_r1\tools
- type
adb.exe devices
the output must look like
List of devices attached
HT848GZ40121    device


## Step 3 : Run adb ##

Now we can launch adb to forward internet traffic to the phone :
c:
cd \android-sdk-windows-1.5\_r1\tools
adb.exe forward tcp:8080 tcp:8080

## Step 4 : Configure browser ##

Your browser must now user localhost:8080 for proxy.

## Step 5 : Finally run Proxoid ##

On your phone, run Proxoid and touch Start.

That's all. You can now start Browsing using your phone's Internet Access.