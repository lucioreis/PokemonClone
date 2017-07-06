package com.trabalhopratico.grupo.pokemongoclone.model;

import java.io.Serializable;

/**
 * Created by usuario on 18/04/2017.
 */

public class PokemomCapturado implements Serializable{
    private double latitude;
    private double longitude;
    private String dtCaptura;

    public PokemomCapturado() {
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDtCaptura() {
        return dtCaptura;
    }

    public void setDtCaptura(String dtCaptura) {
        this.dtCaptura = dtCaptura;
    }
}
