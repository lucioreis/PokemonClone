package com.trabalhopratico.grupo.pokemongoclone.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.PokemomCapturado;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;

import java.util.List;

/**
 * Created by lucio on 7/2/2017.
 */

public class PokedexAdapter extends ArrayAdapter<Pokemon> {
    private Pokemon[] items;

    ControladoraFachadaSingleton cf = ControladoraFachadaSingleton.getOurInstance();

    public PokedexAdapter(Context context, int textViewResourceId, Pokemon[] items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            Context ctx = getContext();
            LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.modelo_pokedex, null);
        }
        Pokemon pokemon = items[position];

        if (pokemon != null && cf.getUser().getQuantidadeCapturas(pokemon) != 0) {
                ((TextView) v.findViewById(R.id.txtNome)).setText(pokemon.getNome());
                ((TextView) v.findViewById(R.id.txtCodigo)).setText(String.format("#%03d", pokemon.getNumero()));
                ((ImageView) v.findViewById(R.id.imgPokemon)).setImageResource(pokemon.getIcone());
        }else {
            ((TextView) v.findViewById(R.id.txtNome)).setText("???");
            ((TextView) v.findViewById(R.id.txtCodigo)).setText(String.format("#%03d",position+1));
            ((ImageView) v.findViewById(R.id.imgPokemon)).setImageResource(R.drawable.help);
        }

        return v;
    }
}
