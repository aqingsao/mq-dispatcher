androidpn-mq
============

AndroidPN with message queue

How to setup and run:
====================
1. Download AndroidPN from https://github.com/dannytiehui/androidpn, which includes server and client code.
2. In androidpn folder, run 'cd androidpn-server-bin-jetty/bin && ./run.sh' to start androidPN server.
3. Open "http://localhost:7070/" in browser to check AndroidPN is running.
4. Follow 'http://www.petefreitag.com/item/763.cfm' to setup android emulator.
5. In androidpn folder, open 'androidpn-client' in android studio(http://developer.android.com/sdk/installing/studio.html), and run the application on emulator.
6. Run mq-dispatcher from com.thoughtworks.i1.mq.MQApplication.class.main.
7. Go to 'http://localhost:7070/user.do' to check the device id in 'username' colume.
8. Run 'curl --data "message={message}" localhost:8052/mq/api/device/{device id}', and you should see message delivered to device.
