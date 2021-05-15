package com.example.landorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MostrarParkings extends AppCompatActivity {

    private static final String URL_parkings = "http://192.168.1.144/landorWebServices/parkings.php";
    List<Parking> parkingList;
    RecyclerView recyclerView;
    HashMap<String, String> empresas = new HashMap<String, String>();
    boolean isManager;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_parkings);
        //sacamos user
        Intent i = getIntent();
        usuario = (Usuario)i.getSerializableExtra("sampleObject");
        isManager=usuario.isManager();

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        parkingList = new ArrayList<>();
        //cargo primero los nombre de empresa en el volley ya que no puedo hacer dos peticiones a la vez
        InicializarComponentes();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.lista);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapa:
                        Intent a = new Intent(MostrarParkings.this,MapsActivity.class);
                        a.putExtra("sampleObject", usuario);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.lista:
                        Intent b = new Intent(MostrarParkings.this,MostrarParkings.class);
                        b.putExtra("sampleObject", usuario);
                        startActivity(b);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.configuracion:
                        if(isManager){
                            Intent c = new Intent(MostrarParkings.this,ManagerSettings.class);
                            c.putExtra("sampleObject", usuario);
                            startActivity(c);
                        }
                        else {
                            Intent d = new Intent(MostrarParkings.this,UserSettings.class);
                            d.putExtra("sampleObject", usuario);
                            startActivity(d);
                        }
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


    }

    private void cargarParkings() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_parkings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject parking = array.getJSONObject(i);
                        parkingList.add(new Parking(
                                parking.getString("nombre"),
                                parking.getString("lat"),
                                parking.getString("longt"),
                                parking.getString("tarifa")+"€/min",
                                parking.getString("id_Empresa"),
                                getCompleteAddressString(Double.parseDouble(parking.getString("lat")), Double.parseDouble(parking.getString("longt"))),
                                empresas.get(parking.getString("id_Empresa"))

                        ));
                    }

                    MyAdapter adapter = new MyAdapter(MostrarParkings.this, parkingList);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                //Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                //   Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private void InicializarComponentes(){
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest("http://192.168.1.144/landorWebServices/getEmpresas.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        empresas.put(jsonObject.getString("id_Empresa"), jsonObject.getString("nombre"));
                    } catch (JSONException e) {
                        // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                cargarParkings();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(), "error de conexion", Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}