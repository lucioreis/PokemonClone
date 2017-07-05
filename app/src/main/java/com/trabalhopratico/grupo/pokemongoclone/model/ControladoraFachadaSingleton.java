package com.trabalhopratico.grupo.pokemongoclone.model;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.trabalhopratico.grupo.pokemongoclone.util.BancoDadosSingleton;
import com.trabalhopratico.grupo.pokemongoclone.util.RandomUtil;
import com.trabalhopratico.grupo.pokemongoclone.util.TimeUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * Created by usuario on 18/04/2017.
 */

public final class ControladoraFachadaSingleton implements Serializable{
    private Usuario user;
    private Map<String, List<Pokemon> > pokemons = new HashMap<String, List<Pokemon> > ();
    private Aparecimento aparecimentos[] = new Aparecimento[10];
    private List<Tipo> tiposPokemons;
    private static final ControladoraFachadaSingleton ourInstance = new ControladoraFachadaSingleton();
    private boolean sorteouLendario = false;

    private ControladoraFachadaSingleton() {
        tiposPokemons = new ArrayList<Tipo>();
        pokemons.put("C",new ArrayList<Pokemon>());
        pokemons.put("I",new ArrayList<Pokemon>());
        pokemons.put("R",new ArrayList<Pokemon>());
        pokemons.put("L",new ArrayList<Pokemon>());
        Log.i("CFS", "Iniciou o construtor do CFS");
        daoTipos();
        Log.i("CFS", "Iniciou a lista de tipos");
        daoPokemons(this);
        Log.i("CFS", "Iniciou a tabela hash de pokemons");
    }

    private void daoTipos(){
        String colunas[] = new String []{"idTipo", "nome"};
        Cursor c = BancoDadosSingleton.getInstance().buscar("tipo", colunas, "", "");
        while(c.moveToNext()) {
            int idTipo = c.getColumnIndex("idTipo");
            Tipo tipo = new Tipo();
            tipo.setIdTipo(c.getInt(c.getColumnIndex("idTipo")));
            tipo.setNome(c.getString(c.getColumnIndex("nome")));
            tiposPokemons.add(tipo);
        }
    }

    private void daoPokemons(ControladoraFachadaSingleton controladorGeral){
        String colunas[] = new String []{"idPokemon", "nome","categoria","foto","icone"};
        Cursor c = BancoDadosSingleton.getInstance().buscar("pokemon", colunas, "", "");
        while(c.moveToNext()) {
            Pokemon pokemon = new Pokemon(c.getInt(c.getColumnIndex("idPokemon")),c.getString(c.getColumnIndex("nome")),c.getString(c.getColumnIndex("categoria")),c.getInt(c.getColumnIndex("foto")),c.getInt(c.getColumnIndex("icone")),controladorGeral);
            String cat = pokemon.getCategoria();
            pokemons.get(cat).add(pokemon);
            Log.i("POKEMON", pokemon.getIcone()+"");
        }
    }

    private void daoUsuario(){
        String colunas[] = new String []{"login", "senha", "nome", "sexo", "foto", "dtCadastro"};
        Cursor c = BancoDadosSingleton.getInstance().buscar("usuario", colunas, "", "");
        List<Pokemon> lC, lI, lR, lL;
        while(c.moveToNext()) {
            int login = c.getColumnIndex("login");
            user = new Usuario(c.getString(login));
            user.setNome(c.getString(c.getColumnIndex("nome")));
            user.setSenha(c.getString(c.getColumnIndex("senha")));
            user.setFoto(c.getString(c.getColumnIndex("foto")));
            user.setDtCadastro(c.getString(c.getColumnIndex("dtCadastro")));
            user.setSexo(c.getString(c.getColumnIndex("sexo")));
        }
    }

    private List<Pokemon> getPokemon() {
        List<Pokemon> list = pokemons.get("C");
        list.addAll(pokemons.get("I"));
        list.addAll(pokemons.get("R"));
        list.addAll(pokemons.get("L"));
        return list;
    }

    static ControladoraFachadaSingleton getInstance() {
        return ourInstance;
    }

    public Usuario getUser() {
        return user;
    }


    public Aparecimento[] getAparecimentos() {
        return aparecimentos;
    }

    public List<Tipo> getTiposPokemons() {
        return tiposPokemons;
    }


    public static ControladoraFachadaSingleton getOurInstance() {
        return ourInstance;
    }

    public boolean isSorteouLendario() {
        return sorteouLendario;
    }

    public void setSorteouLendario(boolean sorteouLendario) {
        this.sorteouLendario = sorteouLendario;
    }

    public boolean temSessao(){
        //select * from usuario where temSessao == "S"
        Cursor c = BancoDadosSingleton.getInstance().buscar("usuario", new String[]{"nome"}, "temSessao = 'S'", "");
        if(c.getCount() == 1) {
            daoUsuario();
            return true;
        }else
            return false;
    }

    public boolean loginUser(String login, String senha) {
        Log.i("LOGIN", "LOGINUSER");
        String colunas[] = new String[]{"*"};
        String where = "login = '" + login + "' AND senha = '" + senha +"'";
        Cursor c = BancoDadosSingleton.getInstance().buscar("usuario", colunas, where, "");
        Log.i("LOGIN", "Verificou se esta correto");
        if (c.getCount() == 1) {
            ContentValues values = new ContentValues();
            values.put("temSessao", "S");
            BancoDadosSingleton.getInstance().atualizar("usuario", values, where);
            Log.i("LOGIN", "Atualizou");
            daoUsuario();
            return true;
        }else
            return false;
    }

    public boolean cadastrarUser(String login, String senha, String nome, String sexo, String foto){
        ContentValues _values = new ContentValues();
        _values.put("nome", nome);
        _values.put("login", login);
        _values.put("senha", senha);
        _values.put("temSessao", "S");
        _values.put("sexo", sexo);
        _values.put("foto", foto);

        Date dt = new Date();
        SimpleDateFormat sdt = new SimpleDateFormat("dd/MM/yyyy");
        _values.put("dtCadastro", sdt.format(dt).toString());
        daoUsuario();
        if(getUser() != null){
            String where = "login = '" + getUser().getLogin() + "'";
            BancoDadosSingleton.getInstance().deletar("pokemonusuario", where);
            Log.i("BANCO_DADOS", "esvaziou bd");
            BancoDadosSingleton.getInstance().deletar("usuario", where);
        }

        BancoDadosSingleton.getInstance().inserir("usuario", _values);
        daoUsuario();
        return true;
    }

    public void sorteaAparecimentos(double latMin, double latMax, double longMin, double longMax) {
        int tamComum = pokemons.get("C").size();
        int tamIncomum = pokemons.get("I").size();
        int tamRaro = pokemons.get("R").size();
        int tamLendario = pokemons.get("L").size();
        TimeUtil timeUtil = new TimeUtil();
        RandomUtil randomUtil = new RandomUtil();
        Map<String, String> tempo = timeUtil.getHoraMinutoSegundoDiaMesAno();
        int numSorteado = randomUtil.randomIntInRange(1,100), numSorteado2 = randomUtil.randomIntInRange(1,100);
        int somaMinSegAtual = Integer.parseInt(tempo.get("minuto")) + Integer.parseInt(tempo.get("segundo"));
        int contAparecimentos = 0;

        // sorteio de lendarios
        if (!sorteouLendario && (numSorteado%2==0) && (numSorteado2%2==0) && (somaMinSegAtual%2!=0)) {
            sorteouLendario = true;
            int sorteio = randomUtil.randomIntInRange(0,tamLendario);
            Aparecimento ap = new Aparecimento();
            double latitude = randomUtil.randomDoubleInRange(latMin,latMax);
            double longitude = randomUtil.randomDoubleInRange(longMin,longMax);
            ap.setLatitude(latitude);
            ap.setLongitude(longitude);
            Pokemon pokemon = pokemons.get("L").get(sorteio);
            ap.setPokemon(pokemon);
            aparecimentos[contAparecimentos] = ap;
            contAparecimentos++;
        }   else if (sorteouLendario) {
            sorteouLendario = false;
        }

        // sorteio de pokemons comuns
        String nivelOcorrencia = "C";
        int numComuns, numIncomuns = 3, numRaros = 1;
        if (sorteouLendario) numComuns = 5;
        else numComuns = 6;
        for (int i = 0; i < numComuns; i++) {
            int sorteio = randomUtil.randomIntInRange(0,tamComum);
            Aparecimento ap = new Aparecimento();
            double latitude = randomUtil.randomDoubleInRange(latMin,latMax);
            double longitude = randomUtil.randomDoubleInRange(longMin,longMax);
            ap.setLatitude(latitude);
            ap.setLongitude(longitude);
            Pokemon pokemon = pokemons.get(nivelOcorrencia).get(sorteio);
            ap.setPokemon(pokemon);
            aparecimentos[contAparecimentos] = ap;
            contAparecimentos++;
        }

        // sorteio de pokemons incomuns
        nivelOcorrencia = "I";
        for (int i = 0; i < numIncomuns; i++) {
            int sorteio = randomUtil.randomIntInRange(0,tamIncomum);
            Aparecimento ap = new Aparecimento();
            double latitude = randomUtil.randomDoubleInRange(latMin, latMax);
            double longitude = randomUtil.randomDoubleInRange(longMin, longMax);
            ap.setLatitude(latitude);
            ap.setLongitude(longitude);
            Pokemon pokemon = pokemons.get(nivelOcorrencia).get(sorteio);
            ap.setPokemon(pokemon);
            aparecimentos[contAparecimentos] = ap;
            contAparecimentos++;
        }

        //sorteio de pokemons raros
        nivelOcorrencia = "R";
        for (int i = 0; i < numRaros; i++) {
            int sorteio = randomUtil.randomIntInRange(0,tamRaro);
            Aparecimento ap = new Aparecimento();
            double latitude = randomUtil.randomDoubleInRange(latMin, latMax);
            double longitude = randomUtil.randomDoubleInRange(longMin, longMax);
            ap.setLatitude(latitude);
            ap.setLongitude(longitude);
            Pokemon pokemon = pokemons.get(nivelOcorrencia).get(sorteio);
            ap.setPokemon(pokemon);
            aparecimentos[contAparecimentos] = ap;
            contAparecimentos++;
        }
    }

    public boolean logoutUser(){
        Log.i("BANCO_DADOS", user.getLogin() + " será alterado");
        String where = "login = '" + user.getLogin() + "'";
        ContentValues values = new ContentValues();
        values.put("temSessao", "N");
        BancoDadosSingleton.getInstance().atualizar("usuario", values, where);
        return true;
    }
}