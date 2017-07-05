package com.trabalhopratico.grupo.pokemongoclone.model;

import android.content.ContentValues;

import com.trabalhopratico.grupo.pokemongoclone.util.BancoDadosSingleton;
import com.trabalhopratico.grupo.pokemongoclone.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by usuario on 18/04/2017.
 */

public class Usuario {
    private String login;
    private String senha;
    private String nome;
    private String sexo;
    private String foto;
    private String dtCadastro;
    private Map<Pokemon, List<PokemomCapturado>> pokemons;

    protected Usuario(String lg) {
        login = lg;
    }

    private void preencheCapturas(){

    }
    //TODO - Checar como será persistido a captura do poemon
    public boolean capturar(Aparecimento pkmn){
        if(pokemons == null) pokemons = new HashMap<>();
        TimeUtil timeUtil = new TimeUtil();
        Map<String, String> data = timeUtil.getHoraMinutoSegundoDiaMesAno();
        PokemomCapturado pokemomCapturado = new PokemomCapturado();
        pokemomCapturado.setDtCaptura(data.get("mes")+"/"+data.get("dia")+"/"+data.get("ano"));
        pokemomCapturado.setLatitude(pkmn.getLatitude());
        pokemomCapturado.setLongitude(pkmn.getLongitude());
        List<PokemomCapturado> tmp;
        tmp = pokemons.get(pkmn.getPokemon());
        if(tmp == null) tmp = new ArrayList<>();
        tmp.add(pokemomCapturado);
        pokemons.put(pkmn.getPokemon(), tmp);

        //Nao sei se isso fica aqui
//        BancoDadosSingleton bd = BancoDadosSingleton.getInstance();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("login", getLogin());
//        contentValues.put("idPokemon", pkmn.getPokemon().getNumero());
//        contentValues.put("latitue", pkmn.getLatitude());
//        contentValues.put("longitude", pkmn.getLongitude());
//        contentValues.put("dtCaptura", data.get("mes")+"/"+data.get("dia")+"/"+data.get("ano"));
//        bd.inserir("pokemonususario", contentValues);

        return false;
    }

    public int getQuantidadeCapturas(Pokemon pkmn){
        return 0;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(String idCadastro) {
        this.dtCadastro = idCadastro;
    }

    public Map<Pokemon, List<PokemomCapturado>> getPokemons() {
        return pokemons;
    }

}
