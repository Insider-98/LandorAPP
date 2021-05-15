package com.example.landorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.forgerock.android.auth.FRUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserSettings extends AppCompatActivity {

    boolean isManager;
    Usuario usuario;
    String nombreEmpresa_Query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        Intent i = getIntent();
        usuario = (Usuario)i.getSerializableExtra("sampleObject");
        isManager=usuario.isManager();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.configuracion);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapa:
                        Intent a = new Intent(UserSettings.this,MapsActivity.class);
                        a.putExtra("sampleObject", usuario);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.lista:
                        Intent b = new Intent(UserSettings.this,MostrarParkings.class);
                        b.putExtra("sampleObject", usuario);
                        startActivity(b);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.configuracion:
                        if(isManager){
                            Intent c = new Intent(UserSettings.this,ManagerSettings.class);
                            c.putExtra("sampleObject", usuario);
                            startActivity(c);
                        }
                        else {
                            Intent d = new Intent(UserSettings.this,UserSettings.class);
                            d.putExtra("sampleObject", usuario);
                            startActivity(d);
                        }
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        InicializarComponentes();

        FloatingActionButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FRUser.getCurrentUser().logout();
                Intent intent = new Intent(UserSettings.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    private void InicializarComponentes(){
        TextView nombreCompleto = findViewById(R.id.nombreCompleto_user);
        TextView userName = findViewById(R.id.username_user);
        TextView telefono = findViewById(R.id.telefono_user);
        TextView correo = findViewById(R.id.correo_user);
        nombreCompleto.setText("Nombre completo: " + usuario.getNombreCompleto());
        userName.setText("Usuario: "+ usuario.getUsername() );
        telefono.setText("Telefono: " + usuario.getTelefono());
        correo.setText("Correo electronico: "+ usuario.getEmail());

    }


}