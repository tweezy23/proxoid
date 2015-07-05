# Installation on Windows Vista #

Important 1 : Be sure to have correctly configure your phone before<br>
See <a href='installationPhone.md'>this page</a> if not.<br>
<br>
Important 2 : Il you have a Samsung Galaxy, Install Samsung PC Suite now before continuing.<br>
<br>
<br>
<h2>Overview</h2>

The steps will be :<br>
<ol><li>Download drivers<br>
</li><li>Extract drivers<br>
</li><li>Install drivers<br>
</li><li>Configure browser<br>
</li><li>Start tunnel<br>
</li><li>Start proxoid<br>
</li><li>Start browsing</li></ol>

Steps 1 to 3 are only for the first time.<br>
Next times, it will be more faster.<br>
<br>
Note :<br />
In the following documentation, screenshots are in french.<br />

<h2>Step 1 : Download drivers</h2>

The real Android SDK is near 200Mb.<br />
If you're not a developper, most of it will be unused.<br />
So I made a subset of it that is juste 2Mb and contains adb and the windows driver.<br>
<br>
Download it here :<br />
for 32 bits : <a href='http://www.baroukh.com/proxoid/proxoid-adb.zip'>http://www.baroukh.com/proxoid/proxoid-adb.zip</a><br />
for 64 bits : <a href='http://www.baroukh.com/proxoid/proxoid-adb64.zip'>http://www.baroukh.com/proxoid/proxoid-adb64.zip</a><br />
and save it on your disk.<br />
(if you don't know witch one, try 32bits first ...)<br>
<h2>Step 2 : Extract drivers</h2>

Right click on the file an choose "Extract all"<br>
Click Next then Next and Finish.<br>
There should be now a directory named "proxoid-adb".<br>
<br>
<h2>Step 3 : Install drivers</h2>

<h3>Step 3.1</h3>
Plug your phone now.<br />
Windows will work and ask you to use windows update to install drivers :<br />
<img src='http://www.baroukh.com/proxoid/proxoidvista-1.png' />

Select the second option : "ask me later".<br>
<br>
Explanation : we will not let Windows try to find the driver himself. Instead, we'll use Hardware Manager to install.<br>
<br>
<h3>Step 3.2</h3>

In "Start" menu, right click on "Computer" and select "Properties".<br />
Then click on Hardware Manager.<br />
You should find your phone at top, with a yellow warning.<br />
Right click on it and select "Update driver".<br />
<img src='http://www.baroukh.com/proxoid/proxoidvista-2.png' />

<h3>Step 3.3</h3>


Select the second option : "find a driver on my computer".<br />
<img src='http://www.baroukh.com/proxoid/proxoidvista-3.png' />

<h3>Step 3.4</h3>

Click on browse.<br>
Browse to the extracted "proxoid-adb" directory.<br />
Select subdirectory "usb_drivers" then "x86".<br />
Click Next<br />
<img src='http://www.baroukh.com/proxoid/proxoidvista-4.png' />

<h3>Step 3.5</h3>

Windows will show an alert.<br />
Don't care and select "Install anyway".<br>
<br>
<img src='http://www.baroukh.com/proxoid/proxoidvista-5.png' />

<h3>Step 3.6</h3>

Windows is now installing the driver<br />
When installation is finish you should see something like this.<br>
Click "Finish"<br />
<img src='http://www.baroukh.com/proxoid/proxoidvista-6.png' />

<h3>Step 3.7</h3>

In Harware Manager, now, the phone should be referenced in "ADB Interface" with "HTC Dream Composite ADB Interface" :<br />
<img src='http://www.baroukh.com/proxoid/proxoidvista-7.png' />

<h3>Step 3.8</h3>

We will now verify that all works.<br />
Your phone should be plugged.<br />
Browse to proxy-adb directory and execute "check.bat".<br />
You should see something like<br>
<pre><code>List of devices attached<br>
HT848GZ40121    device<br>
</code></pre>

The device number can be different.<br />
What is important is that there is something after "List of devices attached".<br>
<br>
If yes, you make the harder.<br />
If not, drop me a mail (mike_at_baroukh.com). I'll try to help you. If you can, provide me a screenshot.<br />

<h2>Step 4 : Configure browser</h2>

We have to configure the proxy on your browser.<br>
If you already know how to do, go directly to step 5 after ahving configured your browser with proxy "localhost" on port "8080".<br>
<br>
<h3>If you're using Internet Explorer</h3>

Launch your browser.<br />
Select menu "Tools" then "Internet Options"<br />
Go to "network" tab<br />
Click on "Network parameters"<br />
Check "Use a proxy server".<br />
Enter "localhost" for server and 8080 for port.<br />

Click Ok.<br />

<img src='http://www.baroukh.com/proxoid/proxoidxp-8.png' />

<h3>If you're using Firefox</h3>

Doc to come ...<br>
<br>
<h2>Step 5 : Start tunnel</h2>

Go now to directory "proxy-adb" and run script "start-tunnel".<br />

<img src='http://www.baroukh.com/proxoid/proxoidxp-6.png' />

<h2>Step 6 : Start Proxoid</h2>

On your phone, run Proxoid and check "Start Proxoid".<br />
You should see Proxoid's icon on notification bar saying that "Proxoid is running".<br />

<h2>Step 7 : Browse</h2>

Here it is.<br />
If I'm not wrong, you actually can browse using your phone. <br />
Enjoy !