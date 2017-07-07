package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;

import java.util.HashMap;
import java.util.Map;

import static com.trabalhopratico.grupo.pokemongoclone.R.id.pokemon;

public class DetalhesPokedexActivity extends AppCompatActivity {
    private Pokemon pokemon;
    private Map<String, Integer> tipoToCor = new HashMap<>();
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_pokedex);

        tipoToCor.put("Normal", R.color.Normal);
        tipoToCor.put("Fire", R.color.Fire);
        tipoToCor.put("Fighting", R.color.Fighting);
        tipoToCor.put("Water", R.color.Water);
        tipoToCor.put("Flying", R.color.Flying);
        tipoToCor.put("Grass", R.color.Grass);
        tipoToCor.put("Electric", R.color.Electric);
        tipoToCor.put("Poison", R.color.Poison);
        tipoToCor.put("Ground", R.color.Ground);
        tipoToCor.put("Psychic", R.color.Psychic);
        tipoToCor.put("Rock", R.color.Rock);
        tipoToCor.put("Ice", R.color.Ice);
        tipoToCor.put("Bug", R.color.Bug);
        tipoToCor.put("Dragon", R.color.Dragon);
        tipoToCor.put("Ghost", R.color.Ghost);
        tipoToCor.put("Dark", R.color.Dark);
        tipoToCor.put("Steel", R.color.Steel);
        tipoToCor.put("Fairy", R.color.Fairy);

        Intent it = getIntent();
        Display display = getWindowManager().getDefaultDisplay();

        pokemon = (Pokemon) it.getSerializableExtra("pokemon");

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
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

        TextView txtNumero = (TextView) findViewById(R.id.txtNumeroDetalhes);
        TextView txtNome = (TextView) findViewById(R.id.txtNomeDetalhes);
        TextView txtQtd = (TextView) findViewById(R.id.txtQtdDetalhes);
        TextView tipo1 = (TextView) findViewById(R.id.tipo1);
        TextView tipo2 = (TextView) findViewById(R.id.tipo2);

        ImageView imgPkm = (ImageView) findViewById(R.id.imgPokemonDetalhes);
        imgPkm.setImageResource(pokemon.getFoto());
        imgPkm.getLayoutParams().width = display.getWidth()/2;
        imgPkm.getLayoutParams().height = display.getWidth()/2;

        txtNome.setText(pokemon.getNome());
        getSupportActionBar().setTitle("Detalhes "+ pokemon.getNome());
        txtNumero.setText(pokemon.getNumero()+"");
        txtQtd.setText(ControladoraFachadaSingleton.getOurInstance().getUser().getQuantidadeCapturas(pokemon)+"");
        tipo1.setText(pokemon.getTipos().get(0).getNome());
        Log.i("tipos", pokemon.getTipos().get(0).getNome()+ " color->"+ tipoToCor.get(pokemon.getTipos().get(0).getNome()));
        tipo1.setBackgroundColor(getResources().getColor(tipoToCor.get(pokemon.getTipos().get(0).getNome())));

        if(pokemon.getTipos().size() > 1) {
            tipo2.setText(pokemon.getTipos().get(pokemon.getTipos().size()-1).getNome());
            tipo2.setBackgroundColor(getResources().getColor(tipoToCor.get(pokemon.getTipos().get(pokemon.getTipos().size()-1).getNome())));
        }

    }
    public void detalhes(View v){
        Intent it = new Intent(getBaseContext(), CapturasActivity.class);
        it.putExtra("pokemon", pokemon);
        startActivity(it);
    }

    public void voltar(View v){
        finish();
    }
}
