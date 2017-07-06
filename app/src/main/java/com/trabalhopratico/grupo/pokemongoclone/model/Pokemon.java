package com.trabalhopratico.grupo.pokemongoclone.model;

import android.database.Cursor;
import android.util.Log;

import com.trabalhopratico.grupo.pokemongoclone.util.BancoDadosSingleton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by usuario on 18/04/2017.
 */
//DESCOMENTAR PARTE DE CONTROLADORAFACHADASINGLETON
public class Pokemon implements Serializable {
    private int numero;
    private String nome;
    private String categoria;
    private int foto;
    private int icone;
    private List<Tipo> tipos = new ArrayList<Tipo>();

    public Pokemon() {
    }

    protected Pokemon(int numero, String nome, String categoria, int foto, int icone, ControladoraFachadaSingleton cg) {
        this.numero = numero;
        this.nome = nome;
        this.categoria = categoria;
        this.foto = foto;
        this.icone = icone;
        preencherTipos(cg);
    }

    private void preencherTipos(ControladoraFachadaSingleton cg){
        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        String colunas[] = new String[]{"p.idPokemon pidP","pt.idPokemon ptidP"," pt.idTipo ptidT"};
        String where = "p.idPokemon == pt.idPokemon and p.idPokemon == " + numero;
        Cursor c = db.buscar("pokemon p, pokemontipo pt", colunas, where, "");
        while (c.moveToNext()) {
            for(int i = 0; i < cg.getTiposPokemons().size(); i++) {
                int idTipo = cg.getTiposPokemons().get(i).getIdTipo();
                if (idTipo == c.getInt(c.getColumnIndex("ptidT"))) {
                    tipos.add(cg.getTiposPokemons().get(i));
                }
            }
        }
    }

    public int hashCode(int i){
        if (categoria.equals("C")) return 0;
        else if (categoria.equals("I")) return 1;
        else if (categoria.equals("R")) return 2;
        else return 3;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    public List<Tipo> getTipos() {
        return tipos;
    }

    @Override
    public int hashCode(){
        return numero;
    }

    @Override
    public boolean equals(Object p){
        if(p instanceof Pokemon){
            return this.numero == ((Pokemon) p).numero;
        }
        return false;
    }
}
