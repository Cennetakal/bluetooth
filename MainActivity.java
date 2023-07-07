package com.bluetoothchat.btc;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import com.bluetoothchat.btchat.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Toolbar actionbar;
    private ViewPager vpMain;
    private TabLayout tabsMain;
    private Set<BluetoothDevice> pairedDevices;


    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    public static String EXTRA_ADRESS = "device_adress";
    ArrayAdapter<String>adapter;
    BluetoothAdapter bluetoothAdapter;
    Button btnbonded_devices;
    Button btnconnectable_devices;
    OpenActivityClass openActivityClass;
    int requestCode=0;

    ListView pairedlist;
    Button pair_button,toggle_button;


    public void init(){
        actionbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionbar);
        getSupportActionBar().setTitle(R.string.app_namee);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        btnconnectable_devices=(Button) findViewById(R.id.btnconnectable_devices);
        btnbonded_devices=(Button) findViewById(R.id.btnbonded_devices);



        btnconnectable_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityClass = new OpenActivityClass(MainActivity.this, ConnectableDevicesActivity.class);
                openActivityClass.openActivityWithoutSendingAddress();
            }
        });

        btnbonded_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityClass = new OpenActivityClass(MainActivity.this, BondedDevicesActivity.class);
                openActivityClass.openActivityWithoutSendingAddress();
            }
        });
        init();

    }



    @Override
    protected void onStart() {
        if(currentUser ==null){
            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
        }
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;

    }
//Çıkış işlemi
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.mainLogout){

            auth.signOut();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();

        }
        if(item.getItemId()==R.id.hakkında){

            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);

        }

        return true;
    }



}