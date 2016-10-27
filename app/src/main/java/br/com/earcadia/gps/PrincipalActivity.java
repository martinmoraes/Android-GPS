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
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    TextView longitudeGpsTextView, latitudeGpsTextView, alturaGpsTextView, precisaoGpsTextView,
            longitudeNetworkTextView, latitudeNetworkTextView, alturaNetworkTextView, precisaoNetworkTextView,
            networkProviderTextView, gpsProviderTextView,
            horarioUltimaLeituraGpsTextView, horarioUltimaLeituraNetworkTextView;
    Button calculaDistanciaButton;
    LocationManager locationManager;
    LocationListener locationListener = null;
    Double longitudeGpsUltima, latitudeGpsUltima, longitudeNetworkUltima, latitudeNetworkUltima,
            longitudeGpsGuardada, latitudeGpsGuardada, longitudeNetworkGuardada, latitudeNetworkGuardada;
    boolean leituraGps = false, leituraNetwork = false;
    boolean temPrimeiraTomada = false;
    DateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Log.d("MEUAPP", "onCreate()");
        calculaDistanciaButton = (Button) findViewById(R.id.calculaDistanciaButton);

        longitudeGpsTextView = (TextView) findViewById(R.id.longitudeGpsTextView);
        latitudeGpsTextView = (TextView) findViewById(R.id.latitudeGpsTextView);
        precisaoGpsTextView = (TextView) findViewById(R.id.precisaoGpsTextView);
        alturaGpsTextView = (TextView) findViewById(R.id.alturaGpsTextView);

        longitudeNetworkTextView = (TextView) findViewById(R.id.longitudeNetworkTextView);
        latitudeNetworkTextView = (TextView) findViewById(R.id.latitudeNetworkTextView);
        precisaoNetworkTextView  = (TextView) findViewById(R.id.precisaoNetworkTextView);
        alturaNetworkTextView = (TextView) findViewById(R.id.alturaNetworkTextView);

        networkProviderTextView = (TextView) findViewById(R.id.networkProviderTextView);
        gpsProviderTextView = (TextView) findViewById(R.id.gpsProviderTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        horarioUltimaLeituraGpsTextView = (TextView) findViewById(R.id.horarioUltimaLeituraGpsTextTive);
        horarioUltimaLeituraNetworkTextView = (TextView) findViewById(R.id.horarioUltimaLeituraNetworkTextTive);
        dateFormat = DateFormat.getTimeInstance(DateFormat.LONG);
    }


    private void identificandoProvider() {
        List<String> providerLista = locationManager.getAllProviders();
        for (String provider : providerLista) {
            Log.d("MEUAPP", "Provider: " + provider + " -- Ativo: " + locationManager.isProviderEnabled(provider));
        }
    }

    public void iniciaGerenciadoresDeLocalizacao() {
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
            Toast.makeText(this, "Gps não ativo", Toast.LENGTH_SHORT).show();
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
                        Log.d("MEUAPP", "onLocationChanged: LocationManager.Gps_PROVIDER");
                        longitudeGpsUltima = location.getLongitude();
                        latitudeGpsUltima = location.getLatitude();
                        longitudeGpsTextView.setText(String.valueOf(longitudeGpsUltima));
                        latitudeGpsTextView.setText(String.valueOf(latitudeGpsUltima));
                        precisaoGpsTextView.setText(String.valueOf(location.getAccuracy()));
                        alturaGpsTextView.setText(String.valueOf(location.getAltitude()));
                        leituraGps = true;
                        horarioUltimaLeituraGpsTextView.setText(dateFormat.format(Calendar.getInstance().getTime()));
                        break;
                    case "network":
                        Log.d("MEUAPP", "onLocationChanged: LocationManager.Network_PROVIDER");
                        longitudeNetworkUltima = location.getLongitude();
                        latitudeNetworkUltima = location.getLatitude();
                        longitudeNetworkTextView.setText(String.valueOf(longitudeNetworkUltima));
                        latitudeNetworkTextView.setText(String.valueOf(latitudeNetworkUltima));
                        precisaoNetworkTextView.setText(String.valueOf(location.getAccuracy()));
                        alturaNetworkTextView.setText(String.valueOf(location.getAltitude()));
                        leituraNetwork = true;
                        horarioUltimaLeituraNetworkTextView.setText(dateFormat.format(Calendar.getInstance().getTime()));
                        break;
                }

                if (!calculaDistanciaButton.isEnabled() && leituraGps && leituraNetwork) {
                    calculaDistanciaButton.setEnabled(true);
                    if (!temPrimeiraTomada) {
                        calculaDistanciaButton.setText(getString(R.string.daqui));
                    } else {
                        calculaDistanciaButton.setText(getString(R.string.ate));
                    }
                }
            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("MEUAPP", "onStatusChanged: " + provider);
                switch (provider) {
                    case "gps":
                        gpsProviderTextView.setText(getString(R.string.gps_provider) + " - " + condicaoDoProvedor(status));
                        break;
                    case "network":
                        networkProviderTextView.setText(getString(R.string.network_provider) + " - " + condicaoDoProvedor(status));
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
                return "";//"Disponível";
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationListener();
        identificandoProvider();
//        verificandoLocalizadorAtivo();
        iniciaGerenciadoresDeLocalizacao();
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


    public void calculaDistancia(View view) {

        if (temPrimeiraTomada) {
            TableRow distanciaGpsTableRow = (TableRow) findViewById(R.id.distanciaGpsTableRow);
            TableRow distanciaNetworkTableRow = (TableRow) findViewById(R.id.distanciaNetworkTableRow);
            distanciaGpsTableRow.setVisibility(View.VISIBLE);
            distanciaNetworkTableRow.setVisibility(View.VISIBLE);
            float[] distanciaCalculada = new float[3];
            Location.distanceBetween(latitudeGpsGuardada, longitudeGpsGuardada, latitudeGpsUltima, longitudeGpsUltima, distanciaCalculada);
            ((TextView) findViewById(R.id.distanciaGpsTextView)).setText(String.valueOf(distanciaCalculada[0]));
            Location.distanceBetween(latitudeNetworkGuardada, longitudeNetworkGuardada, latitudeNetworkUltima, longitudeNetworkUltima, distanciaCalculada);
            ((TextView) findViewById(R.id.distanciaNetworkTextView)).setText(String.valueOf(distanciaCalculada[0]));
            calculaDistanciaButton.setText(getString(R.string.daqui));
            temPrimeiraTomada = false;

            for (int x = 0; x < distanciaCalculada.length; x++) {
                Log.d("MEUAPP", "Posição " + x + ":" + distanciaCalculada[x]);
            }
        } else {
            longitudeGpsGuardada = longitudeGpsUltima;
            latitudeGpsGuardada = latitudeGpsUltima;
            longitudeNetworkGuardada = longitudeNetworkUltima;
            latitudeNetworkGuardada = latitudeNetworkUltima;
            calculaDistanciaButton.setText(getString(R.string.fazendo_leitura));
            temPrimeiraTomada = true;
            calculaDistanciaButton.setEnabled(false);
            leituraGps = false;
            leituraNetwork = false;
        }
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