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
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String URL_parkings = "http://192.168.1.144/landorWebServices/parkings.php";
    private ClusterManager<MyItem> clusterManager;
    private GoogleMap mMap;
    private Marker markerPrueba;
    private LocationManager ubicacion;
    EasyLocationMod easyLocationMod;
    double[] l;
    String lat, lon;
    CardView linearLayoutCustomView;
    boolean clickinicial;
    Marker prevMarker;
    List<Parking> parkingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        parkingList = new ArrayList<>();
        //cargo primero los nombre de empresa en el volley ya que no puedo hacer dos peticiones a la vez


        linearLayoutCustomView = findViewById(R.id.customLayout);
        linearLayoutCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write your function here.
               Intent intent = new Intent(MapsActivity.this, TestActivity.class);
                startActivity(intent);
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
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.lista:
                        startActivity(new Intent(getApplicationContext(), MostrarParkings.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.configuracion:
                        startActivity(new Intent(getApplicationContext(), UserinfoActivity.class));
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
        //
        // Add a marker in Sydney and move the camera
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Raku Detectado").draggable(true).snippet("hola es la descripcion").icon(BitmapDescriptorFactory.fromResource(R.drawable.mexico)));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng prueba = new LatLng(l[0], l[1]);
        Log.d("test", "onMapReady: prueb");
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18)); //con zoom
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(prueba)
                .zoom(14)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //DIALOG

        //mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMaxZoomPreference(19.0f);

        clusterManager = new ClusterManager<MyItem>(this, mMap);
        cargarParkings();
        clusterManager.setRenderer(new OwnIconRendered(getApplicationContext(), mMap, clusterManager));
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);


        //mMap.setMyLocationEnabled(true);
        // mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void addItems() {

        // Add ten cluster items in close proximity, for purposes of this example.
        //aqui se añaden los puntos
        Log.d("TAsdG", "addItems: "+ parkingList.size());

        for (int i = 0; i < parkingList.size(); i++) {
            Parking parking = parkingList.get(i);


        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // Toast.makeText(this, "mi evento2", Toast.LENGTH_SHORT).show();

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
            displayCustomeInfoWindow(marker);
        }

        return true; //movido de false a true para que no se ejecute lo que hay por defecto
    }
    //implement the onClusterItemClick interface


    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        //   TolucaFragment.newIntance(marker.getTitle(), "blablabla").show(getSupportFragmentManager(), null);

    }
    private void displayCustomeInfoWindow(Marker marker) {
        linearLayoutCustomView.setVisibility(View.VISIBLE);
        TextView textViewTitle = linearLayoutCustomView.findViewById(R.id.textViewTitle);
        TextView textViewTarifa = linearLayoutCustomView.findViewById(R.id.textViewTarifa);
        TextView textViewDireccion = linearLayoutCustomView.findViewById(R.id.textViewDireccion);
        // TextView textViewOtherDetails = linearLayoutCustomView.findViewById(R.id.textViewOtherDetails);
        textViewTitle.setText(marker.getTitle());
        textViewTarifa.setText(marker.getSnippet()+"€/min");
        textViewDireccion.setText(getCompleteAddressString(marker.getPosition().latitude, marker.getPosition().longitude));
        // textViewOtherDetails.setText("LatLong :: " + marker.getPosition().latitude + "," + marker.getPosition().longitude);

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

}