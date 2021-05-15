package com.example.landorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements FRListener<Void> {
    Usuario usuario;
    boolean isManager=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onSuccess(Void result) {
        FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = result.getRaw();
                            String rol = json.getString("roles");
                            if(rol.contains("Manager")){ isManager=true;}
                            usuario = new Usuario(isManager,json.getString("name"),json.getString("sub"), json.getString("phone_number"),json.getString("email"));
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            intent.putExtra("sampleObject", usuario);
                            startActivity(intent);
                        } catch (JSONException e) {
                        }
                    }
                });

            }

            @Override
            public void onException(Exception e) {

            }
        });


    }

    @Override
    public void onException(Exception e) {
        System.out.println(e.getMessage());
    }
}