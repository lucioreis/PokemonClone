package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.Usuario;
import com.trabalhopratico.grupo.pokemongoclone.util.BancoDadosSingleton;

public class PerfilActivity extends AppCompatActivity {
    private final ControladoraFachadaSingleton ctrl = ControladoraFachadaSingleton.getOurInstance();
    private final BancoDadosSingleton bd = BancoDadosSingleton.getInstance();
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Perfil");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
        getSupportActionBar().setTitle("Perfil do Usuário");
        Usuario usuario = ctrl.getUser();
        TextView nome = (TextView) findViewById(R.id.nome);
        TextView data_de_inicio = (TextView) findViewById(R.id.inicio_da_aventura);
        ImageView imageView = (ImageView) findViewById(R.id.imagem_de_perfil);
        String data = usuario.getDtCadastro();
        String nome_string = usuario.getNome();
        String sexo = usuario.getSexo();
        Log.i("PERFIL", sexo);
        if(sexo.equals("mulher")) imageView.setImageResource(R.drawable.female_grande);
        else imageView.setImageResource(R.drawable.male_grande);
        nome.setText(nome_string);
        Log.i("PerfilActivity", "data de cadastro setada");
        data_de_inicio.setText(data);
        Log.i("PerfilActivity", "Iniciando Curso 2" );
        //Talvez tenha que mudar isso aqui
        Cursor c2 = bd.buscar("pokemonusuario", new String[]{"idPokemon"}, "login='"+ctrl.getUser()+"'",null);
        TextView numero_de_capturas = (TextView) findViewById(R.id.numero_de_capturas);
        numero_de_capturas.setText(""+c2.getCount());
        Log.i("PerfilActivity", "Finalzando");

    }
    public void voltar(View v){
        Intent it = new Intent(this, MapActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(it);
        finish();
    }
    public void logout(View v){
        ControladoraFachadaSingleton.getOurInstance().logoutUser();
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
        finish();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem m){
        switch (m.getItemId()) {
            case R.id.back_to_map:
                Intent it = new Intent(this, LoginActivity.class);
                startActivity(it);
                return true;

            default:
                return super.onOptionsItemSelected(m);

        }
    }*/
}
