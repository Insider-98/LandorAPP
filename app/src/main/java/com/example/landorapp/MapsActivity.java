package com.example.landorapp;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import github.nisrulz.easydeviceinfo.base.EasyLocationMod;

public class MapsActivity extends FragmentActivity implements  GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String URL_parkings = "http://192.168.1.144/landorWebServices/parkings.php";
    private ClusterManager<MyItem> clusterManager;
    private GoogleMap mMap;
    double[] l;
    String lat, lon;
    CardView linearLayoutCustomView;
    boolean clickinicial;
    Marker prevMarker;
    List<Parking> parkingList;
    boolean isManager=false;
    HashMap<String, String> empresas = new HashMap<String, String>();
    Usuario usuario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //consigo el user
        Intent i = getIntent();
        usuario = (Usuario)i.getSerializableExtra("sampleObject");
        isManager=usuario.isManager();

        getNombreEmpresas();
        parkingList = new ArrayList<>();
        //cargo primero los nombre de empresa en el volley ya que no puedo hacer dos peticiones a la vez


        linearLayoutCustomView = findViewById(R.id.customLayout);
        linearLayoutCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parking parkingSeleccionado = buscarParking();
                Intent intent = new Intent(MapsActivity.this, InfoParking.class);
                intent.putExtra("MENSAJE_NOMBRE", parkingSeleccionado.getNombreParking());
                intent.putExtra("MENSAJE_EMPRESA", parkingSeleccionado.getNombreEmpresa());
                intent.putExtra("MENSAJE_TARIFA", parkingSeleccionado.getTarifa());
                intent.putExtra("MENSAJE_DIRECCION", parkingSeleccionado.getDireccion());
                MapsActivity.this.startActivity(intent);
            }
        });
        linearLayoutCustomView.setVisibility(View.GONE);
        clickinicial=false;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        EasyLocationMod easyLocationMod = new EasyLocationMod(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        l = easyLocationMod.getLatLong();
        lat = String.valueOf(l[0]);
        lon = String.valueOf(l[1]);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mapa);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapa:
                        Intent a = new Intent(MapsActivity.this,MapsActivity.class);
                        a.putExtra("sampleObject", usuario);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.lista:
                        Intent b = new Intent(MapsActivity.this,MostrarParkings.class);
                        b.putExtra("sampleObject", usuario);
                        startActivity(b);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.configuracion:
                        if(isManager){
                            Intent c = new Intent(MapsActivity.this,ManagerSettings.class);
                            c.putExtra("sampleObject", usuario);
                            startActivity(c);
                        }
                        else {
                            Intent d = new Intent(MapsActivity.this,UserSettings.class);
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ubicacionActual = new LatLng(l[0], l[1]);
        Log.d("test", "onMapReady: prueb");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ubicacionActual)
                .zoom(15)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMaxZoomPreference(19.0f);

        clusterManager = new ClusterManager<MyItem>(this, mMap);
        cargarParkings();
        clusterManager.setRenderer(new OwnIconRendered(getApplicationContext(), mMap, clusterManager));
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marker.getPosition())
                .zoom(mMap.getCameraPosition().zoom * 1.2f) //cada vez un poco mas de zoom
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (!clickinicial) {
            linearLayoutCustomView.setVisibility(View.GONE);
            clickinicial=true;
        }
        else{
            if (prevMarker != null) {
                //Set prevMarker back to default color
                prevMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.googlemarker));
            }

            //leave Marker default color if re-click current Marker
            if (!marker.equals(prevMarker)) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selectedgooglemarkerv3));
                prevMarker = marker;
            }
            prevMarker = marker;
            displayCustomInfoWindow(marker);
        }

        return true; //movido de false a true para que no se ejecute lo que hay por defecto
    }


    private void displayCustomInfoWindow(Marker marker) {
        linearLayoutCustomView.setVisibility(View.VISIBLE);
        TextView textViewTitle = linearLayoutCustomView.findViewById(R.id.textViewTitle);
        TextView textViewTarifa = linearLayoutCustomView.findViewById(R.id.textViewTarifa);
        TextView textViewDireccion = linearLayoutCustomView.findViewById(R.id.textViewDireccion);
        textViewTitle.setText(marker.getTitle());
        textViewTarifa.setText(marker.getSnippet()+"€/min");
        textViewDireccion.setText(getCompleteAddressString(marker.getPosition().latitude, marker.getPosition().longitude));
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
            } else {
                //   Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }


    private void cargarParkings() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_parkings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject parking = array.getJSONObject(i);
                        Double lat = Double.parseDouble(parking.getString("lat"));
                        Double lng = Double.parseDouble(parking.getString("longt"));
                        Log.d("TAsdG", "addItems: "+ lat+" "+lng);
                        parkingList.add(new Parking(
                                parking.getString("nombre"),
                                parking.getString("lat"),
                                parking.getString("longt"),
                                parking.getString("tarifa")+"€/min",
                                parking.getString("id_Empresa"),
                                getCompleteAddressString(Double.parseDouble(parking.getString("lat")), Double.parseDouble(parking.getString("longt"))),
                                empresas.get(parking.getString("id_Empresa"))

                        ));
                        MyItem infoWindowItem = new MyItem(lat,lng,parking.getString("nombre"), parking.getString("tarifa"));
                        clusterManager.addItem(infoWindowItem);
                    }
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

    private Parking buscarParking(){
        Parking parking = null;
        for (int i = 0; i<parkingList.size();i++){
            if(parkingList.get(i).getNombreParking().equals(prevMarker.getTitle())){
                 return parkingList.get(i);
            }
        }
        return parking;

    }

    private void getNombreEmpresas(){
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

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }
}