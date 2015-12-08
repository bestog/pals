Privacy Aware Location Service
===============
For more information: [http://bestog.github.io/pals](http://bestog.github.io/pals)

*Tested with Android Version 4.4.2 (KitKat) / Motorola Razr i - XT890*

## Get the application
* Download the latest release: [**pals_v1.0.apk**](https://github.com/bestog/pals/releases)

* Install App  on your Smartphone.

*and use the code below in your own app*

## Use in external application
```java
final Intent serviceIntent = new Intent("com.bestog.pals.Service");
final ServiceConnection serviceConnection = new ServiceConnection() { ... };
BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle bundle = intent.getExtras();
    float lat = bundle.getFloat("lat");
    float lon = bundle.getFloat("lon");
    // ...
    // your code
    // ...
    activity.unregisterReceiver(this);
    activity.stopService(serviceIntent);
    activity.unbindService(serviceConnection);
  }
};
activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
activity.registerReceiver(broadcastReceiver, new IntentFilter("com.bestog.pals.receiver"));
```

### License
<pre>
LGPL v3.0
</pre>
