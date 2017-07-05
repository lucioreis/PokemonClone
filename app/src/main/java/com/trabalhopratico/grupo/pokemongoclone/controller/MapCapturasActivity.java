package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.PokemomCapturado;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;
import com.trabalhopratico.grupo.pokemongoclone.model.Usuario;

import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapCapturasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_capturas);
        Usuario usuario = ControladoraFachadaSingleton.getOurInstance().getUser();
        Map<Pokemon, List<PokemomCapturado>> pokemonListMap = usuario.getPokemons();
        Set<Pokemon> list = pokemonListMap.keySet();
        List<String> tmp = new ArrayList<>();
        for(Pokemon i : list){
            tmp.add(i.getNome());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, tmp);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);
    }
}
