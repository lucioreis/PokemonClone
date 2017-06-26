package com.trabalhopratico.grupo.pokemongoclone.model;

import java.io.Serializable;

/**
 * Created by usuario on 18/04/2017.
 */

public class Tipo implements Serializable {
    private int idTipo;
    private String nome;

    public Tipo() {

    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
