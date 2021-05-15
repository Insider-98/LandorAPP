package com.example.landorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddParking extends AppCompatActivity {

    String username;
    RequestQueue requestQueue;
    String idEmpresa;
    TextView nombre;
    TextView tarifa;
    TextView direccion;
    TextView ciudad;
    TextView codigoPostal;
    TextView provincia;
    TextView pais;
    Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);
        nombre = findViewById(R.id.name_text_nombreParking);
        tarifa = findViewById(R.id.name_text_tarifa);
        direccion = findViewById(R.id.name_text_direccion);
        ciudad= findViewById(R.id.address_text_ciudad);
        codigoPostal= findViewById(R.id.address_text_postal);
        provincia= findViewById(R.id.address_text_provincia);
        pais= findViewById(R.id.address_text_pais);

        Intent i = getIntent();
        usuario = (Usuario)i.getSerializableExtra("sampleObject");
        username = usuario.getUsername();
    }

    public void GuardarParking(View view) {
        String direccionCompleta = direccion.getText().toString() + ", " + codigoPostal.getText().toString() + " "+ ciudad.getText().toString()+", "+ provincia.getText().toString()+", " +pais.getText().toString();
        LatLng posicion = getLocationFromAddress(this, direccionCompleta);
        Double latitud = posicion.latitude;
        Double longitud = posicion.longitude;
        //LA EMPRESA y el usuario ***CREADA DE ANTEMANO YA QUE ESTA INCORPORADA EL REGISTRO**
        sacarEmpresaYGuardarParking(latitud,longitud);


    }
    private void sacarEmpresaYGuardarParking(Double latitud,Double longitud){
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest("http://192.168.1.144/landorWebServices/buscarEmpresaUsuario.php?username="+username+"", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Log.d("TAG1", "JSONOBJECT: " + jsonObject.toString());

                        if (i==0) idEmpresa = jsonObject.getString("id_Empresa");
                        Log.d("TAG2", "SACADO :" + jsonObject.getString("id_Empresa"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.144:80/landorWebServices/insertarParking.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Parking guardado correctamente", Toast.LENGTH_LONG).show();
                        Intent a = new Intent(AddParking.this,ManagerSettings.class);
                        a.putExtra("sampleObject", usuario);
                        startActivity(a);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("nombre", nombre.getText().toString());
                        parametros.put("lat", Double.toString(latitud));
                        parametros.put("longt", Double.toString(longitud));
                        parametros.put("tarifa", tarifa.getText().toString());
                        parametros.put("id_Empresa", idEmpresa);
                        return parametros;
                    }
                };
                requestQueue = Volley.newRequestQueue(AddParking.this);
                requestQueue.add(stringRequest);
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error de conexion", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}