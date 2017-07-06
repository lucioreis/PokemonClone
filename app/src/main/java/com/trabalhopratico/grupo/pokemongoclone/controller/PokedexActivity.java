package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.PokemomCapturado;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;
import com.trabalhopratico.grupo.pokemongoclone.view.PokedexAdapter;

import java.util.List;
import java.util.Map;

import static com.trabalhopratico.grupo.pokemongoclone.R.id.pokemon;

public class PokedexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ControladoraFachadaSingleton cf = ControladoraFachadaSingleton.getOurInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);
        ListView lstPokemons = (ListView) findViewById(R.id.lstPokemons);
        Map<Pokemon, List<PokemomCapturado>> mapAux = cf.getUser().getPokemons();
        Pokemon pks[] = new Pokemon[152];
        for(Pokemon p : mapAux.keySet()){
            pks[p.getNumero()] = p;
        }
        final PokedexAdapter adapter = new PokedexAdapter(getBaseContext(), R.layout.modelo_pokedex, pks);
        lstPokemons.setAdapter(adapter);

        lstPokemons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ControladoraFachadaSingleton cf = ControladoraFachadaSingleton.getOurInstance();
                Pokemon item = adapter.getItem(position);
                if(cf.getUser().getQuantidadeCapturas(item) != 0) {
                    Intent it = new Intent(getBaseContext(), DetalhesPokedexActivity.class);
                    it.putExtra("pokemon", item);
                    startActivity(it);
                }else{
                    Toast.makeText(getBaseContext(), "Você ainda não capturou esse pokemon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
