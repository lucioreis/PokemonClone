package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.view.PokedexAdapter;

public class PokedexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ControladoraFachadaSingleton cf = ControladoraFachadaSingleton.getOurInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);
        ListView lstPokemons = (ListView) findViewById(R.id.lstPokemons);
        Log.i("Teste", "Teste");
        final PokedexAdapter adapter = new PokedexAdapter(getBaseContext(), R.layout.modelo_pokedex, cf.getPokemon());
        //for(int i = 0; i < cf.getPokemon().size(); i++){
        Log.i("Teste", cf.getPokemon().size() + "");
        //}
        lstPokemons.setAdapter(adapter);
    }
}
