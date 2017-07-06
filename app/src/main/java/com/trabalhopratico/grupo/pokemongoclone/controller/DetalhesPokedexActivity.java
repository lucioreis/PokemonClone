package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;

public class DetalhesPokedexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_pokedex);
        Intent it = getIntent();
        Pokemon pokemon = (Pokemon) it.getSerializableExtra("pokemon");
        TextView txtNumero = (TextView) findViewById(R.id.txtNumeroDetalhes);
        TextView txtNome = (TextView) findViewById(R.id.txtNomeDetalhes);
        TextView txtQtd = (TextView) findViewById(R.id.txtQtdDetalhes);
        txtNome.setText(pokemon.getNome());
        txtNumero.setText(pokemon.getNumero()+"");


    }
}
