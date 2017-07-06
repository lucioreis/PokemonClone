package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.Aparecimento;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;
import com.trabalhopratico.grupo.pokemongoclone.model.Usuario;
import com.trabalhopratico.grupo.pokemongoclone.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapActivity extends Activity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    Handler handler = new Handler();
    MediaPlayer mp;
    Marker marker;
    private BitmapDescriptor bmp;
    private GoogleMap mapa;
    private Location playerPosition;
    private LatLng aux;
    public LocationManager lm;
    public Criteria criteria;
    public String provider;
    public int TEMPO_REQUISICAO_LATLONG = 5000;
    public int DISTANCIA_MIN_METROS = 0;
    private boolean taRodando = true;
    private ControladoraFachadaSingleton cg = ControladoraFachadaSingleton.getOurInstance();
    List<Marker> lMO = new ArrayList<Marker>();
    Runnable sorteador = new Runnable() {
        @Override
        public void run() {

            Log.i("THREAD", "GALINHA");
            Log.i("THREAD", "OI");
            double latMin = aux.latitude - 0.0003;
            double latMax = aux.latitude + 0.0003;
            double longMin = aux.longitude - 0.0003;
            double longMax = aux.longitude + 0.0003;
            Log.i("THREAD", "OI");
           // ControladoraFachadaSingleton cg = ControladoraFachadaSingleton.getOurInstance();
            cg.sorteaAparecimentos(latMin, latMax, longMin, longMax);
            Log.i("THREAD", "OI");
            if (lMO.size() > 0) {
                for (Marker mark : lMO) {
                    mark.remove();
                }
            }
            for (Aparecimento a : cg.getAparecimentos()) {
                LatLng l = new LatLng(a.getLatitude(), a.getLongitude());
                lMO.add(mapa.addMarker(new MarkerOptions().position(l).title(a.getPokemon().getNome()).icon(BitmapDescriptorFactory.fromResource(a.getPokemon().getIcone())))); // a.getPokemon().getIcone()
            }
            handler.postDelayed(sorteador, 180000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //geral
        super.onCreate(savedInstanceState);
        mp = MediaPlayer.create(this, R.raw.tema_rota_1);
        mp.setLooping(true);
        setContentView(R.layout.activity_map);
        Log.i("VERIFICANDO", "TESTE");
        Log.i("VERIFICANDO", ControladoraFachadaSingleton.getOurInstance().getUser().getNome());

        //escreve o nome do usuario na tela
        Usuario user = ControladoraFachadaSingleton.getOurInstance().getUser();
        TextView nomePerfil = ((TextView) findViewById(R.id.textViewProfileName));
        nomePerfil.setText(user.getNome());

        //define as imagens dos botoes e qual o icone do perfil
        ImageButton fotoPerfil = ((ImageButton) findViewById((R.id.imageButtonProfilePic)));
        ImageButton fotoMapa = ((ImageButton) findViewById((R.id.imageButtonMap)));
        fotoMapa.setImageResource(R.drawable.mapa_captura);
        ImageButton fotoDex = ((ImageButton) findViewById((R.id.imageButtonPokedex)));
        fotoDex.setImageResource(R.drawable.pokedex);
        if (user.getSexo().equals("homem")) {
            fotoPerfil.setImageResource(R.drawable.male_profile);
        } else {
            fotoPerfil.setImageResource(R.drawable.female_profile);
        }

        //pega o fragment do mapa
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment));
        mapFragment.getMapAsync(this);

        //faz a geolocalizacao
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        PackageManager pm = getPackageManager();
        boolean hasGPS = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if (hasGPS) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            Log.i("LOCATION", "Usando GPS");
        } else {
            Log.i("LOCATION", "Usando servicos de internet");
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }

        provider = lm.getBestProvider(criteria, true);
        //provider = LocationManager.GPS_PROVIDER;
        if (provider != null) {
            Log.e("PROVEDOR", "Nenhum provedor encontrado");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            lm.requestLocationUpdates(provider, TEMPO_REQUISICAO_LATLONG, DISTANCIA_MIN_METROS, this);
            aux = new LatLng(-20.752946,-42.879097);
            playerPosition = new Location(provider);
            playerPosition.setLongitude(aux.longitude);
            playerPosition.setLatitude(aux.latitude);
        } else {
            Log.i("PROVEDOR", "Provedor utilizado: " + provider);

        }
        if (user.getSexo().equals("homem")){
            bmp = BitmapDescriptorFactory.fromResource(R.drawable.male);
        } else {
            bmp = BitmapDescriptorFactory.fromResource(R.drawable.female);
        }
    }


    public void perfil(View v) {
        Intent it = new Intent(this, PerfilActivity.class);

        startActivity(it);
    }

    public void mapaCapturas(View v) {
        Intent it = new Intent(getBaseContext(), CapturasActivity.class);
        Pokemon pokemon = new Pokemon();
        pokemon.setNumero(0);
        it.putExtra("pokemon", pokemon);
        startActivity(it);
    }

    public void pokedex(View v) {
        Intent it = new Intent(this, PokedexActivity.class);
        startActivity(it);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mp.start();
        /*provider = lm.getBestProvider(criteria, true);
        provider = LocationManager.GPS_PROVIDER;
        if (provider != null) {
            Log.e("PROVEDOR", "Nenhum provedor encontrado");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            lm.requestLocationUpdates(provider, TEMPO_REQUISICAO_LATLONG, DISTANCIA_MIN_METROS, this);
        } else {
            Log.i("PROVEDOR", "Provedor utilizado: " + provider);

        }*/
    }
    @Override
    protected void onPause(){
        super.onPause();
        mp.pause();
    }
    @Override
    protected void onDestroy() {
        lm.removeUpdates(this);
        Log.w("PROVEDOR","Provedor " + provider + " parado");
        taRodando = false;
        mp.release();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("999", "99999");
        if(marker != null) marker.remove();
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        playerPosition = new Location(provider);
        aux = new LatLng(playerPosition.getLatitude(),playerPosition.getLongitude());
        playerPosition.setLongitude(aux.longitude);
        playerPosition.setLatitude(aux.latitude);
        if(playerPosition != null) {
            aux = new LatLng(playerPosition.getLatitude(),playerPosition.getLongitude());
            playerPosition.setLongitude(aux.longitude);
            playerPosition.setLatitude(aux.latitude);
        } else {
            aux = new LatLng(-20.752946,-42.879097);
            playerPosition = new Location(provider);
            playerPosition.setLongitude(aux.longitude);
            playerPosition.setLatitude(aux.latitude);
        }

        Usuario user = ControladoraFachadaSingleton.getOurInstance().getUser();
        handler.post(sorteador);
        mapa.setIndoorEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mapa.setMyLocationEnabled(true);
        mapa.setOnMarkerClickListener(this);

        marker = mapa.addMarker(new MarkerOptions().position(aux).title("Voce").icon(bmp));
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(aux,18));
    }

    @Override
    public void onLocationChanged(Location location) {
        /*if(location != null) {
            playerPosition = location;
        }
        ControladoraFachadaSingleton cg = ControladoraFachadaSingleton.getOurInstance();
        BitmapDescriptor bmp;
        if (cg.getUser().getSexo().equals("homem")){
            bmp = BitmapDescriptorFactory.fromResource(R.drawable.male);
        } else {
            bmp = BitmapDescriptorFactory.fromResource(R.drawable.female);
        }
        mapa.addMarker(new MarkerOptions().position(aux).title("Voce").icon(bmp));
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(aux,18));*/
        if(marker != null)
            marker.remove();

        double latitude = location.getLatitude();

        double longitude = location.getLongitude();

        Log.i("COORD", latitude + " "+ longitude );

        LatLng latLng = new LatLng(latitude, longitude);
        Location newPosition = new Location(provider);
        playerPosition.setLatitude(latitude);
        playerPosition.setLongitude(longitude);
        handler.post(sorteador);
        aux = new LatLng(playerPosition.getLatitude(),playerPosition.getLongitude());
        marker = mapa.addMarker(new MarkerOptions().position(latLng).icon(bmp));

        mapa.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mapa.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("LOCATION","Provedor mudou de estado");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("LOCATION","Provedor habilitado");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LOCATION","Provedor desabilitado");
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!VerificaConexao(getBaseContext())) {
            AlertDialog alerta;
            Log.i("PRE_VERIFICACOES", "SEM CONEXAO");
            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //define o titulo
            builder.setTitle("Sem conexão");
            //define a mensagem
            builder.setMessage("É necessário ter uma conexão com a internet");
            //define um botão como positivo
            //cria o AlertDialog
            alerta = builder.create();
            //Exibe
            alerta.show();
        }
        if (!marker.getTitle().equals("Voce")) {
            final Location pontoAtual = new Location(provider);
            pontoAtual.setLatitude(aux.latitude); pontoAtual.setLongitude(aux.longitude);
            final Location pontoPokemon = new Location(provider);
            pontoPokemon.setLatitude(marker.getPosition().latitude); pontoPokemon.setLongitude(marker.getPosition().longitude);

            Log.i("THREAD", aux.latitude+" "+aux.longitude + " " + marker.getPosition().latitude + " " + marker.getPosition().longitude + " " + pontoAtual.distanceTo(pontoPokemon));
            if (pontoAtual.distanceTo(pontoPokemon)/1000000 > 0.000004*10) {
                Toast.makeText(this,"Aproxime-se " + (pontoAtual.distanceTo(pontoAtual)/1000000 - 40) + " metros para batalhar",Toast.LENGTH_SHORT).show();
                Log.i("THREAD", " NAO PASSOU");
            } else {
                Log.i("THREAD", "PASSOU");
                Intent it = new Intent(this,CapturarActivity.class);
                for (Aparecimento a : cg.getAparecimentos()) {
                    if (marker.getTitle().equals(a.getPokemon().getNome())) {
                        Log.i("Apareceu", a.getPokemon().getNome());
                        it.putExtra("Apar", a);
                        //marker.remove();
                        break;
                    }
                }
                startActivity(it);
            }
            return true;
        }else {
            return false;
        }
    }

    public static boolean VerificaConexao(Context _context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }
}