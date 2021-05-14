package com.example.landorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class InfoParking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_parking);
        TextView infoNombre = findViewById(R.id.mostrarInfoNombre_label);
        TextView infoTarifa = findViewById(R.id.mostrarInfoTarifa_label);
        TextView infoEmpresa = findViewById(R.id.mostrarInfoEmpresa_label);
        TextView infoDireccion = findViewById(R.id.mostrarInfoDireccion_label);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        infoNombre.setText((String) b.get("MENSAJE_NOMBRE"));
        infoTarifa.setText((String) b.get("MENSAJE_EMPRESA"));
        infoEmpresa.setText((String) b.get("MENSAJE_TARIFA"));
        infoDireccion.setText((String) b.get("MENSAJE_DIRECCION"));
    }

    //meter metodos para sacar la informacion a partir del nombre del parking
}