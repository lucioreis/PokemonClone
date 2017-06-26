package com.trabalhopratico.grupo.pokemongoclone.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usuario on 20/04/2017.
 */

public class TimeUtil {
    public Map<String, String> getHoraMinutoSegundoDiaMesAno(){
        Map<String, String> m = new HashMap<String, String>();
        Calendar tempo = Calendar.getInstance();
        m.put("hora",tempo.get(Calendar.HOUR_OF_DAY)+"");
        m.put("minuto",tempo.get(Calendar.MINUTE)+"");
        m.put("segundo",tempo.get(Calendar.SECOND)+"");
        m.put("dia",tempo.get(Calendar.DAY_OF_MONTH)+"");
        m.put("mes",tempo.get(Calendar.MONTH)+"");
        m.put("ano",tempo.get(Calendar.YEAR)+"");
        return m;
    }
}
