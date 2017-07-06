package com.trabalhopratico.grupo.pokemongoclone.controller;

import com.trabalhopratico.grupo.pokemongoclone.model.PokemomCapturado;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Fábio on 05/07/2017.
 */

public class ListaCapturas implements Serializable {
    private Map<Pokemon, List<PokemomCapturado>> pokemons;

    public ListaCapturas() {

    }

    public Map<Pokemon, List<PokemomCapturado>> getPokemons() {
        return pokemons;
    }

    public void setPokemons(Map<Pokemon, List<PokemomCapturado>> pokemons) {
        this.pokemons = pokemons;
    }
}
