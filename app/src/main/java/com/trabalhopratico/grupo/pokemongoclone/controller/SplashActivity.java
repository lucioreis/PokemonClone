package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.MutableShort;
import android.webkit.WebView;
import android.widget.Toast;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.Usuario;
import com.trabalhopratico.grupo.pokemongoclone.util.BancoDadosSingleton;
import com.trabalhopratico.grupo.pokemongoclone.util.MyApp;

import java.io.IOException;

public class SplashActivity extends Activity {
    private MediaPlayer tocaMusica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("CICLO_DE_VIDA", "Splash Criado");
        setContentView(R.layout.activity_splash);
        WebView webView = (WebView) findViewById(R.id.splash_loader);
        webView.loadUrl("file:///android_asset/loading.gif");
        webView.setBackgroundColor(Color.TRANSPARENT);
        tocaMusica = MediaPlayer.create(this, R.raw.abertura2);
        tocaMusica.start();

        if (!VerificaConexao(getBaseContext())) {
            AlertDialog alerta;
            Log.i("PRE_VERIFICACOES", "SEM CONEXAO");
            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //define o titulo
            builder.setTitle("Sem conexão");
            //define a mensagem
            builder.setMessage("É necessário ter uma conexão com a internet");
            //define um botão como positivo
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            //cria o AlertDialog
            alerta = builder.create();
            //Exibe
            alerta.show();
        }
        Log.i("PRE_VERIFICACOES", "verificou conexao");
        tocaMusica.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!ControladoraFachadaSingleton.getOurInstance().temSessao()) {
                    Intent it = new Intent(getBaseContext(), LoginActivity.class);
                    tocaMusica.release();
                    startActivity(it);

                } else {
                    Intent it = new Intent(getBaseContext(), MapActivity.class);
                    tocaMusica.release();
                    startActivity(it);
                }
                finish();
            }
        });
        if (!ControladoraFachadaSingleton.getOurInstance().temSessao()) {
           // Intent it = new Intent(this, LoginActivity.class);
           // startActivity(it);

        } else {
           // Intent it = new Intent(this, MapActivity.class);
          //  startActivity(it);
        }
    }
    public static boolean VerificaConexao(Context _context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }
}
