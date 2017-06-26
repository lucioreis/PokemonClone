package com.trabalhopratico.grupo.pokemongoclone.util;

import java.util.Random;

/**
 * Created by usuario on 20/04/2017.
 */

public class RandomUtil {

    public double randomDoubleInRange(double min, double max){
        Random rnd = new Random();
        return min + (rnd.nextDouble() * (max - min));
    }

    public int randomIntInRange(int min, int max){
        Random rnd = new Random();
        return rnd.nextInt(max - min) + min;
    }
}
