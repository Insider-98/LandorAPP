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