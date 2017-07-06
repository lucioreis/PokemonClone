package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;

import static com.trabalhopratico.grupo.pokemongoclone.R.id.pokemon;

public class DetalhesPokedexActivity extends AppCompatActivity {
    Pokemon pokemon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_pokedex);
        Intent it = getIntent();
        pokemon = (Pokemon) it.getSerializableExtra("pokemon");
        TextView txtNumero = (TextView) findViewById(R.id.txtNumeroDetalhes);
        TextView txtNome = (TextView) findViewById(R.id.txtNomeDetalhes);
        TextView txtQtd = (TextView) findViewById(R.id.txtQtdDetalhes);
        ImageView imgPkm = (ImageView) findViewById(R.id.imgPokemonDetalhes);
        imgPkm.setImageResource(pokemon.getFoto());
        txtNome.setText(pokemon.getNome());
        txtNumero.setText(pokemon.getNumero()+"");
        txtQtd.setText(ControladoraFachadaSingleton.getOurInstance().getUser().getQuantidadeCapturas(pokemon)+"");


    }
    public void detalhes(View v){
        Intent it = new Intent(getBaseContext(), MapCapturasActivity.class);
        it.putExtra("pokemon", pokemon);
        startActivity(it);
    }
}
