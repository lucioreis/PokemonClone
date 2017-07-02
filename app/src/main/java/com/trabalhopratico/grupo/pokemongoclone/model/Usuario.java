package com.trabalhopratico.grupo.pokemongoclone.model;

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

    public boolean capturar(Aparecimento pkmn){
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
