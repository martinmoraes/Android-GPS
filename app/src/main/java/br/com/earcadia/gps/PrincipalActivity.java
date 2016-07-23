package br.com.earcadia.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    TextView longitudeGPSTextView, latitudeGPSTextView, alturaGPSTextView,
            longitudeNETWORKTextView, latitudeNETWORKTextView, alturaNETWORKTextView,
            networkProviderTextView, gpsProviderTextView;
    LocationManager locationManager;
    LocationListener locationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Log.d("MEUAPP", "onCreate()");
        longitudeGPSTextView = (TextView) findViewById(R.id.longitudeGPSTextView);
        latitudeGPSTextView = (TextView) findViewById(R.id.latitudeGPSTextView);
        alturaGPSTextView = (TextView) findViewById(R.id.alturaGPSTextView);

        longitudeNETWORKTextView = (TextView) findViewById(R.id.longitudeNETWORKTextView);
        latitudeNETWORKTextView = (TextView) findViewById(R.id.latitudeNETWORKTextView);
        alturaNETWORKTextView = (TextView) findViewById(R.id.alturaNETWORKTextView);

        networkProviderTextView = (TextView) findViewById(R.id.networkProviderTextView);
        gpsProviderTextView = (TextView) findViewById(R.id.gpsProviderTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }


    private void identificandoProvider() {
        List<String> providerLista = locationManager.getAllProviders();
        for (String provider : providerLista) {
            Log.d("MEUAPP", "Provider: " + provider + " -- Ativo: " + locationManager.isProviderEnabled(provider));
        }
    }

    public void startGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) { // Determina se precisa de um EXPLANAÇÃO
                //Depois do usuáiro vêr a explicação, solicitar novamente a permissão.
            } else {                    // Não necessita de explanação, pode requerer a permissão.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MINHA_SOLICITACAO_DE_PERMICAO_DE_LOCALIZACAO);
            }
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.5f, locationListener);
        } else {
            Toast.makeText(this, "GPS não ativo", Toast.LENGTH_SHORT).show();
        }
    }


    public void startNETWORK() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) { // Determina se precisa de um EXPLANAÇÃO
                //Depois do usuáiro vêr a explicação, solicitar novamente a permissão.
            } else {                    // Não necessita de explanação, pode requerer a permissão.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MINHA_SOLICITACAO_DE_PERMICAO_DE_LOCALIZACAO);
            }
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.5f, locationListener);
        } else {
            Toast.makeText(this, "Modo de localização não ativo", Toast.LENGTH_SHORT).show();
        }
    }

    private void locationListener() {
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                String provider = location.getProvider();

                switch (provider) {
                    case "gps":
                        Log.d("MEUAPP", "onLocationChanged: LocationManager.GPS_PROVIDER");
                        longitudeGPSTextView.setText(String.valueOf(location.getLongitude()));
                        latitudeGPSTextView.setText(String.valueOf(location.getLatitude()));
                        alturaGPSTextView.setText(String.valueOf(location.getAltitude()));
                        break;
                    case "network":
                        Log.d("MEUAPP", "onLocationChanged: LocationManager.NETWORK_PROVIDER");
                        longitudeNETWORKTextView.setText(String.valueOf(location.getLongitude()));
                        latitudeNETWORKTextView.setText(String.valueOf(location.getLatitude()));
                        alturaNETWORKTextView.setText(String.valueOf(location.getAltitude()));
                        break;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("MEUAPP", "onStatusChanged: " + provider);
                switch (provider) {
                    case "gps":
                        gpsProviderTextView.setText("GPS PROVIDER - " + condicaoDoProvedor(status));
                        break;
                    case "network":
                        networkProviderTextView.setText("NETWORK PROVIDER - " + condicaoDoProvedor(status));
                        break;
                }

            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    private String condicaoDoProvedor(int status) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                return "Fora de serviço";
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                return "Temporariamente indisponível";
            case LocationProvider.AVAILABLE:
                return "Disponível";
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationListener();
        identificandoProvider();
//        verificandoLocalizadorAtivo();
        startGPS();
        startNETWORK();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MEUAPP", "onPause()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    private void verificandoLocalizadorAtivo() {
        //TODO não está funcionando corretamente - Verificar
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_MODE);
        if (provider == null) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1);
        }
    }

    int MINHA_SOLICITACAO_DE_PERMICAO_DE_LOCALIZACAO = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MINHA_SOLICITACAO_DE_PERMICAO_DE_LOCALIZACAO)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.d("MEUAPP", "Permissão garantida");
    }


}


/*

LocacionManeger
addProximityAlert (latitude dupla, dupla longitude, raio float, longa validade, PendingIntent intenção)
Definir um alerta de proximidade para o local determinado pela posição (latitude, longitude) eo raio dado.




Location
distanceBetween(double startLatitude, double startLongitude, double endLatitude, double endLongitude, float[] results)
Computes the approximate distance in meters between two locations, and optionally the initial and final bearings of the shortest path between them.


getSpeed()
Get the speed if it is available, in meters/second over ground.
 */