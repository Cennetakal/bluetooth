package com.bluetoothchat.btc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.bluetoothchat.btchat.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ConnectableDevicesActivity extends AppCompatActivity {

    public static final String TAG = "AvailableDevicesActvty";
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice clickedDevice;
    ListView connectableDevicesListView;
    MyDeviceAdapterClass deviceAdapter;
    ArrayList<MyDeviceClass> connectableDevicesList;
    String deviceName;
    String deviceAddress;
    OpenActivityClass openActivityClass;
    int deviceDuplication = 0;
    private FirebaseAuth auth;
    private Toolbar actionbarConnectable;


    public void init(){
        actionbarConnectable = (Toolbar) findViewById(R.id.actionbarConnectable);
        setSupportActionBar(actionbarConnectable);
        getSupportActionBar().setTitle("Bağlanılabilir Cihazlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){

            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            NavUtils.navigateUpTo(this,intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectable_devices);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectableDevicesListView = (ListView) findViewById(R.id.connectableDevicesListview);
        connectableDevicesList = new ArrayList<MyDeviceClass>();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        versionControl();

        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        deviceAdapter = new MyDeviceAdapterClass(this, connectableDevicesList);
        deviceAdapter.notifyDataSetChanged();
        connectableDevicesListView.setAdapter(deviceAdapter);

        connectableDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "onItemClick: Clicked on a device.");
                deviceName = connectableDevicesList.get(position).name;
                deviceAddress = connectableDevicesList.get(position).address;
                clickedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
                Log.d(TAG, "deviceName = " + deviceName);
                Log.d(TAG, "deviceAddress = " + deviceAddress);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Log.d(TAG, "Trying to connect to " + deviceName);
                }
                openActivityClass = new OpenActivityClass(ConnectableDevicesActivity.this, ConnectedActivity.class);
                openActivityClass.openActivityWithSendingAddress(deviceAddress);
            }
        });
        init();
    }

    protected final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(ConnectableDevicesActivity.this, "Arama başlatıldı.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Arama başlatıldı.");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d(TAG, "OnReceive:ACTION_FOUND");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                MyDeviceClass dvc = new MyDeviceClass();

                if (device.getName() == null) {
                    dvc.name = "UNDEFINED NAME";
                } else {
                    dvc.name = device.getName();
                }
                dvc.address = device.getAddress();
                Log.d("Device Name:", dvc.name);
                Log.d("Device Address:", dvc.address);

                //To prevent any device to shown twice
                for (int i = 0; i < connectableDevicesList.size(); i++) {
                    if (connectableDevicesList.get(i).address.equals(dvc.address)) {
                        deviceDuplication = 1;
                        break;
                    }
                }
                if (deviceDuplication == 0) {
                    connectableDevicesList.add(dvc);
                    deviceAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(ConnectableDevicesActivity.this, "Arama tamamlandı.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Arama tamamlandı");
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy is called.");
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void versionControl () {
        int permissionCheck=0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck += this.checkSelfPermission("Manifeest.permission.ACCESS_COARSE_LOCATION");
        }
        if (permissionCheck != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        }
    }

    }
