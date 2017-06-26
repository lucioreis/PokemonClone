package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void login(View v){
        Log.i("LOGIN", "Acessou evento");
        EditText edtLogin = (EditText) findViewById(R.id.edtUserLogin);
        EditText edtSenha = (EditText) findViewById(R.id.edtSenhaLogin);
        if(ControladoraFachadaSingleton.getOurInstance().loginUser(edtLogin.getText().toString(), edtSenha.getText().toString())) {
            Intent it = new Intent(this, MapActivity.class);
            startActivity(it);
            finish();
        }else
            Toast.makeText(getBaseContext(), "Login ou senha inválido", Toast.LENGTH_SHORT).show();
    }

    public void cadastrar(View v){
        Intent it = new Intent(this, CadastrarActivity.class);
        startActivity(it);
        finish();
    }


}
