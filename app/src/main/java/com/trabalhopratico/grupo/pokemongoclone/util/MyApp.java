package com.trabalhopratico.grupo.pokemongoclone.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by usuario on 24/04/2017.
 */

public class MyApp extends Application{
    private static Context context;
    public static Context getContext(){
        return MyApp.context;
    }
    public void onCreate(){
        super.onCreate();
        MyApp.context = getApplicationContext();
    }
}
