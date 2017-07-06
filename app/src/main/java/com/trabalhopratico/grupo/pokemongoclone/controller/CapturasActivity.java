package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.PokemomCapturado;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;

import java.util.List;
import java.util.Map;

public class CapturasActivity extends AppCompatActivity implements OnMapReadyCallback {

    ControladoraFachadaSingleton cg = ControladoraFachadaSingleton.getOurInstance();
    private GoogleMap mapa;

    List<PokemomCapturado> lc = null;
    Pokemon p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturas);

        Intent it = getIntent();
        p = (Pokemon) it.getSerializableExtra("pokemon");
        if(p.getNumero() != 0) {
            lc = cg.getUser().getPokemons().get(p);
        }
        MapFragment map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment2));
        map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        Marker marker = null;
        if (p.getNumero() != 0) {
            if (lc != null) {
                for(PokemomCapturado pc : lc) {
                    LatLng aux = new LatLng(pc.getLatitude(), pc.getLongitude());
                    marker = mapa.addMarker(new MarkerOptions().position(aux).icon(BitmapDescriptorFactory.fromResource(p.getIcone())));
                }
            }
        } else {
            for(Pokemon p : cg.getPokemon()) {
                if (cg.getUser().getPokemons() != null) {
                    for (PokemomCapturado pc : cg.getUser().getPokemons().get(p)) {
                        LatLng aux = new LatLng(pc.getLatitude(), pc.getLongitude());
                        marker = mapa.addMarker(new MarkerOptions().position(aux).icon(BitmapDescriptorFactory.fromResource(p.getIcone())));
                    }
                }
            }
        }
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (marker != null){
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),18));
        }
    }
}
