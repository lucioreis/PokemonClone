package com.trabalhopratico.grupo.pokemongoclone.model;

import android.content.ContentValues;
import android.database.Cursor;

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
        //preencheCapturas();
    }

    private void preencheCapturas(){
        BancoDadosSingleton bd = BancoDadosSingleton.getInstance();
        String[] colunas = new String[]{"p.idPokemon idPkmn","pu.dtCaptura dtCaptura","pu.latitude latitude","pu.longitude longitude"};
        String where = "pu.login = u.login AND pu.idPokemon = p.idPokemon AND u.login = '" + login + "'";

        Cursor c = bd.buscar("pokemon p, usuario u, pokemonusuario pu", colunas, where, "");
        ControladoraFachadaSingleton instance = ControladoraFachadaSingleton.getInstance();
        List<Pokemon> listpkmn  = instance.getPokemon();
        while (c.moveToNext()) {
            for(Pokemon p : listpkmn) {
                int num = p.getNumero();
                pokemons.put(p,new ArrayList<PokemomCapturado>());
                if (num == c.getInt(c.getColumnIndex("idPkmn"))) {
                    PokemomCapturado pc = new PokemomCapturado();
                    pc.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
                    pc.setLongitude(c.getDouble(c.getColumnIndex("longitude")));
                    pc.setDtCaptura(c.getString(c.getColumnIndex("dtCaptura")));
                    pokemons.get(p).add(pc);
                }

            }
        }
    }

    public boolean capturar (Aparecimento aparecimento) {
        Pokemon pkmnAux = aparecimento.getPokemon();
        TimeUtil timeUtil = new TimeUtil();
        Map<String,String> ts = timeUtil.getHoraMinutoSegundoDiaMesAno();
        BancoDadosSingleton bd = BancoDadosSingleton.getInstance();
        ContentValues _values = new ContentValues();
        _values.put("login",getLogin());
        _values.put("idPokemon",aparecimento.getPokemon().getNumero());
        _values.put("latitude", aparecimento.getLatitude());
        _values.put("longitude", aparecimento.getLongitude());
        _values.put("dtCaptura", ts.get("dia")+"/"+ts.get("mes")+"/"+ts.get("ano")+" "+ts.get("hora")+":"+ts.get("minuto")+":"+ts.get("segundo"));
        bd.inserir("pokemonusuario",_values);
        PokemomCapturado pc = new PokemomCapturado();
        double latitude = aparecimento.getLatitude();
        double longitude = aparecimento.getLongitude();
        String dtCaptura = ts.get("dia")+"/"+ts.get("mes")+"/"+ts.get("ano")+" "+ts.get("hora")+":"+ts.get("minuto")+":"+ts.get("segundo");
        pc.setLatitude(latitude);
        pc.setLongitude(longitude);
        pc.setDtCaptura(dtCaptura);
        pokemons.get(pkmnAux).add(pc);
        return true;
    }

    //TODO - Checar como será persistido a captura do poemon
//    public boolean capturar(Aparecimento pkmn){
//        if(pokemons == null) pokemons = new HashMap<>();
//        TimeUtil timeUtil = new TimeUtil();
//        Map<String, String> data = timeUtil.getHoraMinutoSegundoDiaMesAno();
//        PokemomCapturado pokemomCapturado = new PokemomCapturado();
//        pokemomCapturado.setDtCaptura(data.get("mes")+"/"+data.get("dia")+"/"+data.get("ano"));
//        pokemomCapturado.setLatitude(pkmn.getLatitude());
//        pokemomCapturado.setLongitude(pkmn.getLongitude());
//        List<PokemomCapturado> tmp;
//        tmp = pokemons.get(pkmn.getPokemon());
//        if(tmp == null) tmp = new ArrayList<>();
//        tmp.add(pokemomCapturado);
//        pokemons.put(pkmn.getPokemon(), tmp);
//
//        //Nao sei se isso fica aqui
////        BancoDadosSingleton bd = BancoDadosSingleton.getInstance();
////        ContentValues contentValues = new ContentValues();
////        contentValues.put("login", getLogin());
////        contentValues.put("idPokemon", pkmn.getPokemon().getNumero());
////        contentValues.put("latitue", pkmn.getLatitude());
////        contentValues.put("longitude", pkmn.getLongitude());
////        contentValues.put("dtCaptura", data.get("mes")+"/"+data.get("dia")+"/"+data.get("ano"));
////        bd.inserir("pokemonususario", contentValues);
//
//        return false;
//    }

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
