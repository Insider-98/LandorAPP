package com.example.landorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class UserinfoActivity extends AppCompatActivity {
    boolean isManager=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        TextView userInfoTxt = findViewById(R.id.textViewUserinfo);

        FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = result.getRaw();
                            String rol = json.getString("roles");
                            if(rol.contains("Manager")) isManager=true;
                        } catch (JSONException e) {
                        }
                    }
                });
            }

            @Override
            public void onException(Exception e) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.configuracion);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapa:
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.lista:
                        startActivity(new Intent(getApplicationContext(), UserConfigTest.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.configuracion:
                        if(isManager){
                            startActivity(new Intent(getApplicationContext(), ManagerSettings.class));
                            overridePendingTransition(0,0);
                        }
                        else {
                            startActivity(new Intent(getApplicationContext(), UserSettings.class));
                            overridePendingTransition(0, 0);
                        }
                        return true;
                }
                return false;
            }
        });



        FloatingActionButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FRUser.getCurrentUser().logout();
                Intent intent = new Intent(UserinfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}