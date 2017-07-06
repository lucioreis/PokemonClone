package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.Aparecimento;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.PokemomCapturado;
import com.trabalhopratico.grupo.pokemongoclone.model.Pokemon;
import com.trabalhopratico.grupo.pokemongoclone.model.Usuario;
import com.trabalhopratico.grupo.pokemongoclone.view.CameraPreview;

import java.util.List;
import java.util.Map;
import java.util.Set;

//TODO - acrescentar sons(Musica de batalha, quick da pokebola e pokemon capiturado)
//TODO - consulta ao panco de dados para saber se o pokemon é novo
//TODO - Verificar porque a posição da pokebola está errada
//TODO - Persistir o pokemon capiturado no banco de dados
public class CapturarActivity extends Activity implements SensorEventListener, View.OnTouchListener{
    private ImageView _pokeball, pokemon, explosion;
    private  Aparecimento aparecimento;
    private Intent it;
    private Sensor sensor;
    private MediaPlayer mp;
    private SensorManager sensorManager;
    private float grausNovos[] = new float[3];
    private float grausTotais[] = new float[3];
    private float grausVelhos[] = new float[3];
    private Display dispaly;
    private Double coeficiente, coeficienteX, coeficienteY;
    private int larguraDaTela, alturaDaTela;
    private float dx, dy,centerX, centerY, pokeballX, pokeballY;
    private float proporcao;
    CameraPreview cameraSurfaceView;
    float alturaImgPkm;
    float larguraImgPkm;
    boolean capiturou = false;
    boolean pokemonPreparado = false, pokeballPreparado = false;
    private final int DESCE = 1, SOBE = -1, DIREITA = 1, ESQUERDA = -1;
    private float POKEBALL_X_INICIAL, POKEBALL_Y_INICIAL;
    private int pokemonHeight;
    private int pokemonWidth;
    private int musicPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        cameraSurfaceView = new CameraPreview(this);

        it = getIntent();
        aparecimento = (Aparecimento) it.getSerializableExtra("Apar");

        Log.i("rsrc", "id_de_foto = " + aparecimento.getPokemon().getFoto());
        pokemon = (ImageView) findViewById(R.id.pokemon);

        TextView nomeDoPokemon = (TextView) findViewById(R.id.nome_do_pokemon);
        nomeDoPokemon.setText(aparecimento.getPokemon().getNome());

        TextView textView = (TextView) findViewById(R.id.pokemon_novo);

        if( ! pokemonNovo(aparecimento.getPokemon()) ){

            textView.setVisibility(View.INVISIBLE);
        }

        _pokeball = (ImageView) findViewById(R.id.pokeball_image_view);
        _pokeball.setImageResource(R.drawable.pokeball);
        _pokeball.setOnTouchListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor == null) Toast.makeText(this, "Nao tem girsocopiculos", Toast.LENGTH_SHORT).show();
        else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }

        pokemon.getViewTreeObserver().addOnPreDrawListener(new   ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                pokemon.getViewTreeObserver().removeOnPreDrawListener(this);

                pokemonHeight = pokemon.getHeight();
                pokemonWidth = pokemon.getWidth();

                return false;
            }
        });


        mp = MediaPlayer.create(this, R.raw.battle);
        mp.setLooping(true);
        mp.setVolume(0.8f, 0.8f);
    }

    @Override
    protected void onResume(){

        super.onResume();
        mp.seekTo(musicPosition);
        mp.start();

        pokemon.setImageResource(aparecimento.getPokemon().getFoto());

        cameraSurfaceView.safeCameraOpenInView(findViewById(R.id.camera_surface_view));

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        alturaDaTela = size.y;
        larguraDaTela = size.x;
        coeficiente = larguraDaTela/72d;
        coeficienteX = larguraDaTela/72d;
        larguraImgPkm = larguraDaTela/2;
        proporcao = larguraImgPkm/(float)larguraDaTela;

        pokemon.post(new Runnable() {
            @Override
            public void run() {
                Log.i("Dimensao", "A: " + alturaDaTela + " L: " + larguraDaTela);
                pokemon.getLayoutParams().height = (int) (pokemonHeight *proporcao);
                pokemon.getLayoutParams().width = (int) (larguraImgPkm);

                centerX = larguraDaTela/2 - (((int)  larguraImgPkm)/2);
                centerY = alturaDaTela/2  - ( (pokemon.getLayoutParams().height)/2 );

                Log.i("Dimensao", "pokemonHeight = "+ pokemon.getHeight() + " pokemonWidth = "+ pokemon.getWidth());

                pokemonPreparado = true;

                Log.i("Dimensao", "Posicao pkmn - X: " + pokemon.getX() + " Y: " + pokemon.getY());
                Log.i("Dimensao", "Posicao pkbl - X: " + _pokeball.getX() + " Y: " + _pokeball.getY());

                grausTotais[1] += pokemon.getX()/coeficiente;
                grausTotais[0] += pokemon.getY()/coeficiente;
            } });
///////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
        _pokeball.post(new Runnable() {

            @Override
            public void run() {

                _pokeball.getLayoutParams().height = (int)(larguraDaTela*(float)0.15);
                _pokeball.getLayoutParams().width = (int)(larguraDaTela*(float)0.15);
                _pokeball.setX(larguraDaTela/2 - _pokeball.getWidth()/2);
                _pokeball.setY(alturaDaTela - _pokeball.getHeight() - 75);

                Log.i("Dimensao", "MW: " + _pokeball.getMeasuredWidth() + " MH: " + _pokeball.getMeasuredHeight());
                Log.d("captura", pokemon.getX()+" "+ pokemon.getY() +  ' ' + pokemon.getLeft() + " " + pokemon.getRight());
                Log.d("Dimensao", "getHeight = " + _pokeball.getHeight()+ "  getWidth = " + _pokeball.getWidth());
                Log.i("Dimensao", "Posicao pokebola - X: " + _pokeball.getX() + " Y: " + _pokeball.getY());

                POKEBALL_X_INICIAL = larguraDaTela/2 - _pokeball.getMeasuredWidth()/2;
                POKEBALL_Y_INICIAL = alturaDaTela - _pokeball.getMeasuredHeight() - 75;

                pokeballPreparado = true;

            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        mp.pause();
        musicPosition = mp.getCurrentPosition();
        sensorManager.unregisterListener(this);
    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     * <p>
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(!(pokeballPreparado || pokemonPreparado)) return;

        float xyz[] = new float[3];
        for(int i = 0; i < 3; i++){
            xyz[i] = (float) (event.values[i]*59.2958);
            grausNovos[i] = (float) (xyz[i]*0.02);
            grausTotais[i] += grausNovos[i];
            if( !(grausTotais[i] > grausVelhos[i] + 0.08 || grausTotais[i] < grausVelhos[i] - 0.08) )
                grausTotais[i] = grausVelhos[i];
            grausVelhos[i] = grausTotais[i];
        }
        if(grausTotais[1] > 180){
            grausTotais[1] = grausTotais[1] - 360;
        }
        if(grausTotais[1] < -180){
            grausTotais[1] = 360 + grausTotais[1];
        }
        if(grausTotais[0] > 180){
            grausTotais[0] = grausTotais[0] - 360;
        }
        if(grausTotais[0] < -180){
            grausTotais[0] = 360 + grausTotais[0];
        }

        //Log.i("captura", "gtx = "+ grausTotais[1] + " gty= "+grausTotais[0]);
        float d = (float) (grausTotais[1]*coeficiente);
        float e = (float) (grausTotais[0]*coeficiente);
        pokemon.setX(d);
        pokemon.setY(e);
        pokemon.setRotation(grausTotais[2]);
        //Log.d("captura", pokemon.getTop()+" "+pokemon.getBottom() +  ' ' + pokemon.getLeft() + " " + pokemon.getRight());

    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     * <p>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    long timer_inicio, timer_fim;
    float x0, y0, xf, yf;
    @Override
    public boolean onTouch(View v, final MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:

                timer_inicio = System.currentTimeMillis();
                x0 = event.getRawX() - (v.getWidth() / 2);
                y0 = event.getRawY() - (v.getHeight() / 2);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                timer_fim = System.currentTimeMillis();
                final long tempo = (timer_fim - timer_inicio);
                xf = v.getX(); yf = v.getY();
                MediaPlayer.create(this, R.raw.arremesso).start();
                animacaoDeLancamento(tempo);
                break;

            case MotionEvent.ACTION_MOVE:

                v.animate()
                        .x(event.getRawX() - v.getWidth()/2)
                        .y(event.getRawY() - v.getHeight()/2)
                        .setDuration(0)
                        .start();
                break;

            default:

                return false;

        }

        return true;

    }

    public void animacaoDeLancamento(Long tempo){

        final Float velocidadeX = (xf - x0)/(tempo);
        final Float velocidadeY = (yf - y0)/(tempo);
        final boolean[] terminou = {false};
        int direcaoX = (velocidadeX <= 0)? ESQUERDA:DIREITA;
        int direcaoY = (velocidadeY <= 0)? SOBE:DESCE;
        Long tempoX = (long) Math.abs(larguraDaTela/velocidadeX);
        Long tempoY = (long) Math.abs(alturaDaTela/velocidadeY);

        AnimatorListenerAdapter listenerAdapter =
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationPause(Animator animation){

                        Log.d("onPause", "Animação chamou o onPause()");
                        super.onAnimationPause(animation);

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        _pokeball.post(new Runnable() {
                            @Override
                            public void run() {
//                                _pokeball.setX(POKEBALL_X_INICIAL);
                                _pokeball.setX(larguraDaTela/2 - _pokeball.getWidth()/2);
//                                _pokeball.setY(POKEBALL_Y_INICIAL);
                                _pokeball.setY(alturaDaTela - _pokeball.getHeight() - 75);
                                _pokeball.bringToFront();
                            }
                        });
                        Log.d("LocalPokebola", _pokeball.getY() + " <- y x->" + _pokeball.getX());
                        Toast.makeText(getBaseContext(), "Errrrrrrrou a Pokebola", Toast.LENGTH_LONG).show();
                    }

        };

        ValueAnimator.AnimatorUpdateListener updateListenerX =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        if(terminou[0]) animation.cancel();

                        if(checaColisao(pokemon, _pokeball)){

                            Log.d("colisaoX", "colidiu");
                            animation.pause();
                            capiturou = capituraPokemon();

                        }else if (!capiturou){

                            Log.d("colisaoX", "nao colidiu");
                            float valueX = (float) animation.getAnimatedValue("TranslationX");
                            _pokeball.setTranslationX(valueX);

                            if( _pokeball.getX() > larguraDaTela + 100 || _pokeball.getX() < -100) terminou[0] = true;

                        }

                    }
                };

        ValueAnimator.AnimatorUpdateListener updateListenerY =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        if(terminou[0]) animation.cancel();

                        if(checaColisao(pokemon, _pokeball)){
                            Log.d("colisaoX", "colidiu");
                            animation.pause();
                            capiturou = capituraPokemon();

                        }else if (!capiturou){


                            Log.d("colisaoX", "nao colidiu");
                            float valueY = (float) animation.getAnimatedValue("TranslationY");
                            _pokeball.setTranslationY(valueY);

                            if( _pokeball.getY() > alturaDaTela +100 || _pokeball.getY() < -100) terminou[0] = true;

                        }

                    }
                };

        ValueAnimator objectAnimatorX = ObjectAnimator.ofFloat(_pokeball, "TranslationX", direcaoX*larguraDaTela);
        objectAnimatorX.addUpdateListener(updateListenerX);
        objectAnimatorX.setDuration(tempoX);

        ValueAnimator objectAnimatorY = ObjectAnimator.ofFloat(_pokeball, "TranslationY", direcaoY*alturaDaTela);
        objectAnimatorY.addUpdateListener(updateListenerY);
        objectAnimatorY.setDuration(tempoY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimatorX).with(objectAnimatorY);
        animatorSet.addListener(listenerAdapter);
        animatorSet.start();

    }

    private boolean capituraPokemon() {
        final float pokemonX = pokemon.getX();
        final float pokemonY = pokemon.getY();
        final int pokemonAltura = pokemon.getHeight();
        final int pokemonLargura = pokemon.getWidth();


        pokemon.setImageResource(R.drawable.explosion);
        _pokeball.setVisibility(View.INVISIBLE);

        pokemon.postDelayed(new Runnable() {
            @Override
            public void run() {
                pokemon.setVisibility(View.INVISIBLE);
                _pokeball.setVisibility(View.VISIBLE);
                _pokeball.setX(pokemonX + pokemonAltura/2);
                _pokeball.setY(pokemonY + pokemonLargura/2);
                Log.i("pokemon coord","x = "+pokemonX+ "y= "+pokemonY + "altura= "+pokemonAltura+" largura="+pokemonLargura);
            }
        }, 350);

        Runnable sound = new Runnable() {
            @Override
            public void run() {
                MediaPlayer.create(getBaseContext(), R.raw.quicando).start();
            }
        };
        ValueAnimator rotate = ObjectAnimator.ofFloat(_pokeball, "Rotation", 0, -20, 0, 20, 0, -20, 0, 0);
        rotate.setDuration(3500).setStartDelay(351);
        rotate.start();
        _pokeball.postDelayed(sound, 0);
        _pokeball.postDelayed(sound, 350);
        _pokeball.postDelayed(sound, 700);
        pokemon.postDelayed(new Runnable() {
            @Override
            public void run() {

                ControladoraFachadaSingleton controladora = ControladoraFachadaSingleton.getOurInstance();
                Usuario usuario = controladora.getUser();
                usuario.capturar(aparecimento);
                Toast.makeText(getBaseContext(), "Pokemon Capturado!", Toast.LENGTH_LONG).show();
                finish();

            }
        },6000);
        pokemon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp.pause();
                MediaPlayer.create(getBaseContext(), R.raw.sucesso).start();
            }
        }, 3000);
        return true;
    }


    public boolean checaColisao(View pokemon,View pokebola) {

        int pokemonTop = (int) pokemon.getY();
        int pokemonLeft = (int) pokemon.getX();
        int pokemonBottom = pokemonTop + pokemon.getHeight();
        int pokemonRight = pokemonLeft + pokemon.getWidth();

        int pokebolaTop = (int) pokebola.getY();
        int pokebolaLeft = (int) pokebola.getX();
        int pokebolaBottom = pokebolaTop + pokebola.getHeight();
        int pokebolaRight = pokebolaLeft + pokebola.getWidth();

        Log.d("coli_pokemon",pokemon.getLeft()+" "+ pokemon.getTop()+" "+ pokemon.getRight()+" "+ pokemon.getBottom());

        Rect R1_pokemon = new Rect(pokemonLeft, pokemonTop, pokemonRight, pokemonBottom);
        Rect R2_pokebola = new Rect(pokebolaLeft, pokebolaTop, pokebolaRight,pokebolaBottom);

        return R1_pokemon.intersect(R2_pokebola);

    }

    public boolean pokemonNovo(final Pokemon pokemon){
        ControladoraFachadaSingleton controladoraFachadaSingleton = ControladoraFachadaSingleton.getOurInstance();
        Usuario usuario = controladoraFachadaSingleton.getUser();
        Map<Pokemon, List<PokemomCapturado>> mapPokemon = usuario.getPokemons();
        boolean pokemonNovo = true;
        if(mapPokemon == null) return pokemonNovo;
        Set<Pokemon> pokemons = mapPokemon.keySet();
        for(Pokemon p : pokemons){
            if(pokemon.getNome().equals( p.getNome() )) pokemonNovo = false;
        }
        return pokemonNovo;
    }

};
