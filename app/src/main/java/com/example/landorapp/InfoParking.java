package com.example.landorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoParking extends AppCompatActivity {
    boolean isManager = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_parking);
        //sacamos user
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
        bottomNavigationView.setSelectedItemId(R.id.lista);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapa:
                        startActivity(new Intent(InfoParking.this, MapsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.lista:
                        startActivity(new Intent(InfoParking.this, MostrarParkings.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.configuracion:
                        if(isManager){
                            startActivity(new Intent(InfoParking.this, ManagerSettings.class));
                        }
                        else {
                            startActivity(new Intent(InfoParking.this, UserSettings.class));
                        }
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        TextView infoNombre = findViewById(R.id.mostrarInfoNombre_label);
        TextView infoTarifa = findViewById(R.id.mostrarInfoTarifa_label);
        TextView infoEmpresa = findViewById(R.id.mostrarInfoEmpresa_label);
        TextView infoDireccion = findViewById(R.id.mostrarInfoDireccion_label);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        infoNombre.setText((String) b.get("MENSAJE_NOMBRE"));
        infoTarifa.setText((String) b.get("MENSAJE_TARIFA"));
        infoEmpresa.setText((String) b.get("MENSAJE_EMPRESA"));
        infoDireccion.setText((String) b.get("MENSAJE_DIRECCION"));
    }

}