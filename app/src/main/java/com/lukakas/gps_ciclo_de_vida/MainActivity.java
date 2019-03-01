package com.lukakas.gps_ciclo_de_vida;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latAtual, longAtual;

    private TextView localizacaoTextView;

    private static final int REQUEST_PERMISSSION_GPS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localizacaoTextView =
                findViewById(R.id.localizacaoTextView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri =
                        Uri.parse(
                                String.format(Locale.getDefault(),
                                "geo:%f,%f?q=restaurantes",
                                latAtual,
                                longAtual
                                )
                        );
                Intent intent =
                        new Intent(
                                Intent.ACTION_VIEW,
                                uri
                        );
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationListener =
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latAtual = location.getLatitude();
                        longAtual = location.getLongitude();
                        String exibir =
                                String.format(
                                        Locale.getDefault(),
                                        "Lat%f, Long:%f",
                                        latAtual,
                                        longAtual
                                );
                        localizacaoTextView.setText(exibir);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    5,
                    locationListener
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_PERMISSSION_GPS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSSION_GPS){
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            2000,
                            5,
                            locationListener
                    );
                }
            } else {
                Toast.makeText(this,
                        getString(R.string.no_gps_no_app),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }
}
