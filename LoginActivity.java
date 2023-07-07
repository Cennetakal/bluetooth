package com.bluetoothchat.btc;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.bluetoothchat.btchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private Toolbar actionbarLogin;
    private EditText txtEmail,txtPassword;
    private Button btnLogin;


    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    BluetoothAdapter myBluetooth;
    Button toggle_button;



    public void init(){
        actionbarLogin = (Toolbar) findViewById(R.id.actionbarLogin);
        setSupportActionBar(actionbarLogin);
        getSupportActionBar().setTitle("Giriş Yap");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        txtEmail = (EditText) findViewById(R.id.txtEmailLogin);
        txtPassword = (EditText) findViewById(R.id.txtPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Intent intent=new Intent(getApplicationContext(), WelcomeActivity.class);
            NavUtils.navigateUpTo(this,intent);
        }

        if (item.getItemId()==android.R.id.home)
        {
            this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();

            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myBluetooth= BluetoothAdapter.getDefaultAdapter();
        toggle_button=(Button) findViewById(R.id.btnLogin);
        toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton();
            }


        });

    }

    private void loginUser() {

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this,"Email Alanı Boş Olamaz",Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Şifre Alanı Boş Olamaz",Toast.LENGTH_LONG).show();
        }else{
            btnLogin.setEnabled(false);
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"Giriş Başarılı",Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this,"Giriş Başarısız",Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                    }
                }
            });

        }

    }
    private void toggleButton() {
        if (myBluetooth==null){
            Toast.makeText(getApplicationContext(),"Telefonunuzda Bluetooth bulunmamaktadır",Toast.LENGTH_SHORT).show();
        }
        if(!myBluetooth.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
        }
        if (myBluetooth.isEnabled()){
            loginUser();
          //  myBluetooth.disable();
        }


    }

}