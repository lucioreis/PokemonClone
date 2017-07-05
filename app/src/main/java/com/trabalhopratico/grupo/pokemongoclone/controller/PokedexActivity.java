package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.view.PokedexAdapter;

public class PokedexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ControladoraFachadaSingleton cf;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);
        ListView lstPokemons = (ListView) findViewById(R.id.lstPokemons);
        //final PokedexAdapter adapter = new PokedexAdapter(getBaseContext(), R.layout.modelo_pokedex, cf.get);
    }
}
