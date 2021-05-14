package com.example.landorapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Parking extends AppCompatActivity {
    private String nombreParking;
    private String latitud;
    private String longitud;
    private String tarifa;
    private String id_Empresa;
    private String direccion;
    private String nombreEmpresa;

    public Parking(String nombreParking, String latitud, String longitud, String tarifa, String id_Empresa, String direccion, String nombreEmpresa) {
        this.nombreParking = nombreParking;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tarifa = tarifa;
        this.id_Empresa = id_Empresa;
        this.direccion=direccion;
        this.nombreEmpresa=nombreEmpresa;

    }

    public String getNombreParking() {
        return nombreParking;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getTarifa() {
        return tarifa;
    }

    public String getId_Empresa() {
        return id_Empresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreParking(String nombreParking) {
        this.nombreParking = nombreParking;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public void setTarifa(String tarifa) {
        this.tarifa = tarifa;
    }

    public void setId_Empresa(String id_Empresa) {
        this.id_Empresa = id_Empresa;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }





}
