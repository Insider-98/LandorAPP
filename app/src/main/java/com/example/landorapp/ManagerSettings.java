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

public class ManagerSettings extends AppCompatActivity {
    boolean isManager;
    Usuario usuario;
    String nombreEmpresa_Query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_settings);

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
                        Intent a = new Intent(ManagerSettings.this,MapsActivity.class);
                        a.putExtra("sampleObject", usuario);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.lista:
                        Intent b = new Intent(ManagerSettings.this,MostrarParkings.class);
                        b.putExtra("sampleObject", usuario);
                        startActivity(b);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.configuracion:
                        if(isManager){
                            Intent c = new Intent(ManagerSettings.this,ManagerSettings.class);
                            c.putExtra("sampleObject", usuario);
                            startActivity(c);
                        }
                        else {
                            Intent d = new Intent(ManagerSettings.this,UserSettings.class);
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
                Intent intent = new Intent(ManagerSettings.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    private void InicializarComponentes(){
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest("http://192.168.1.144/landorWebServices/getEmpresaFromUserUsername.php?username="+usuario.getUsername()+"", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        nombreEmpresa_Query=jsonObject.getString("nombre");

                        Log.d("nombrempresa2", "onCreate: " + nombreEmpresa_Query);
                    } catch (JSONException e) {
                        // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                TextView nombreCompleto = findViewById(R.id.nombreCompleto_Manager);
                TextView nombreEmpresa = findViewById(R.id.nombreEmpresa_Manager);
                TextView userName = findViewById(R.id.username_manager);
                TextView telefono = findViewById(R.id.telefono_manager);
                TextView correo = findViewById(R.id.correo_manager);

                nombreCompleto.setText("Empleado: " + usuario.getNombreCompleto());
                nombreEmpresa.setText(nombreEmpresa_Query);
                userName.setText("Usuario: "+ usuario.getUsername() );
                telefono.setText("Telefono: " + usuario.getTelefono());
                correo.setText("Correo electronico: "+ usuario.getEmail());
                Log.d("nombrempresa", "onCreate: " + nombreEmpresa_Query);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(), "error de conexion", Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void AñadirParking(View view) {
        Intent a = new Intent(ManagerSettings.this,AddParking.class);
        a.putExtra("sampleObject", usuario);
        startActivity(a);
    }
}