package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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

public class CapturasActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    ControladoraFachadaSingleton cg = ControladoraFachadaSingleton.getOurInstance();
    private GoogleMap mapa;
    private Toolbar toolbar;
    private List<PokemomCapturado> lc = null;
    private Pokemon p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturas);

        Intent it = getIntent();
        p = (Pokemon) it.getSerializableExtra("pokemon");
        if(p.getNumero() != 0) {
            lc = cg.getUser().getPokemons().get(p);
        }

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mapas das Capturas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
        MapFragment map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment2));
        map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        Marker marker = null;
        if (p.getNumero() != 0) {
            getSupportActionBar().setTitle("Mapas das Capturas - "+p.getNome());
            if (lc != null) {
                for(PokemomCapturado pc : lc) {
                    LatLng aux = new LatLng(pc.getLatitude(), pc.getLongitude());
                    marker = mapa.addMarker(new MarkerOptions().title(p.getNome()).snippet(pc.getDtCaptura()).position(aux).icon(BitmapDescriptorFactory.fromResource(p.getIcone())));
                }
            }
        } else {
            for(Pokemon p : cg.getPokemon()) {
                if (cg.getUser().getPokemons() != null) {
                    for (PokemomCapturado pc : cg.getUser().getPokemons().get(p)) {
                        LatLng aux = new LatLng(pc.getLatitude(), pc.getLongitude());
                        marker = mapa.addMarker(new MarkerOptions().title(p.getNome()).snippet(pc.getDtCaptura()).position(aux).icon(BitmapDescriptorFactory.fromResource(p.getIcone())));
                    }
                }
            }
        }
        mapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.marker, null);

                TextView info= (TextView) v.findViewById(R.id.info);

                info.setText(marker.getTitle()+"\n"+marker.getSnippet());

                return v;
            }
        });

        mapa.setOnMarkerClickListener(this);
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (marker != null){
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),18));
        }
    }

    @Override
    public boolean onMarkerClick(Marker _marker) {
        _marker.showInfoWindow();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
