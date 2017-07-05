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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.Aparecimento;
import com.trabalhopratico.grupo.pokemongoclone.model.ControladoraFachadaSingleton;
import com.trabalhopratico.grupo.pokemongoclone.model.Usuario;
import com.trabalhopratico.grupo.pokemongoclone.view.CameraPreview;

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
    private Double coeficiente;
    private int larguraDaTela, alturaDaTela;
    private float dx, dy,centerX, centerY, pokeballX, pokeballY;
    private float density;
    CameraPreview cameraSurfaceView;
    float alturaImgPkm;
    float larguraImgPkm;
    boolean capiturou = false;
    private final int DESCE = 1, SOBE = -1, DIREITA = 1, ESQUERDA = -1;
    private float POKEBALL_X_INICIAL, POKEBALL_Y_INICIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);
        cameraSurfaceView = new CameraPreview(this);

        it = getIntent();
        aparecimento = (Aparecimento) it.getSerializableExtra("Apar");

        _pokeball = (ImageView) findViewById(R.id.pokeball_image_view);
        _pokeball.setImageResource(R.drawable.pokeball);
        _pokeball.setOnTouchListener(this);

        pokemon = (ImageView) findViewById(R.id.pokemon);
        pokemon.setImageResource(aparecimento.getPokemon().getFoto());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor == null) Toast.makeText(this, "Nao tem girsocopiculos", Toast.LENGTH_SHORT).show();
        else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }

        density = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onResume(){

        super.onResume();

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        alturaDaTela = size.y;
        larguraDaTela = size.x;

        mp = MediaPlayer.create(this, R.raw.battle);
        mp.setLooping(true);
        mp.setVolume(0.1f, 0.1f);
        mp.start();

        cameraSurfaceView.safeCameraOpenInView(findViewById(R.id.camera_surface_view));
        pokemon.post(new Runnable() {
            @Override
            public void run() {

                Log.i("Dimensao", "A: " + alturaDaTela + " L: " + larguraDaTela);
                TextView nomeDoPokemon = (TextView) findViewById(R.id.nome_do_pokemon);
                nomeDoPokemon.setText(aparecimento.getPokemon().getNome());
                coeficiente = larguraDaTela/72d;
                larguraImgPkm = larguraDaTela/2;
                float proporcao = (pokemon.getMeasuredWidth())/larguraDaTela;
                alturaImgPkm = pokemon.getMeasuredHeight()*proporcao/2;

                pokemon.getLayoutParams().height = (int) (alturaImgPkm);
                pokemon.getLayoutParams().width = (int) (larguraImgPkm);

                centerX = larguraDaTela/2 - (((int) larguraImgPkm)/2);
                centerY = alturaDaTela/2  - (((int) alturaImgPkm)/2);

                pokemon.setX(centerX);
                pokemon.setY(centerY);

                Log.i("Dimensao", "Posicao pkmn - X: " + pokemon.getX() + " Y: " + pokemon.getY());
                Log.i("Dimensao", "Posicao pkbl - X: " + _pokeball.getX() + " Y: " + _pokeball.getY());

            }

        });

        _pokeball.post(new Runnable() {

            @Override
            public void run() {

                _pokeball.getLayoutParams().height = (int)(larguraDaTela*(float)0.15);
                _pokeball.getLayoutParams().width = (int)(larguraDaTela*(float)0.15);
                _pokeball.setX(larguraDaTela/2 - _pokeball.getWidth()/2);
                _pokeball.setY(alturaDaTela - _pokeball.getHeight() - 100);
                Log.i("Dimensao", "MW: " + _pokeball.getMeasuredWidth() + " MH: " + _pokeball.getMeasuredHeight());
                Log.d("captura", pokemon.getX()+" "+ pokemon.getY() +  ' ' + pokemon.getLeft() + " " + pokemon.getRight());
                Log.d("captura", "X x Y = " + _pokeball.getMeasuredHeight()+ " + " + _pokeball.getMeasuredWidth());
                Log.i("Dimensao", "Posicao pokebola - X: " + _pokeball.getX() + " Y: " + _pokeball.getY());

            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){

        super.onWindowFocusChanged(hasFocus);

        if(hasFocus){
            int posicao[] = new int[2];
            _pokeball.getLocationOnScreen(posicao);
            POKEBALL_X_INICIAL = posicao[0];
            POKEBALL_Y_INICIAL = posicao[1];
            Log.d("pkbl posicao ini", POKEBALL_X_INICIAL + "<-x | y->" + POKEBALL_Y_INICIAL);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mp.pause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
    private float x = 1, y = 1;
    @Override
    public void onSensorChanged(SensorEvent event) {

        //if(!(a && s)) return;
        //TODO - Testar o giroscópio melhor
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
        if(pokemon.hasWindowFocus()) {
//            pokemon.setX((float) (grausTotais[1] * coeficiente));
//            pokemon.setY((float) (grausTotais[0] * coeficiente));
            pokemon.setRotation(grausTotais[2]);
            float d = (float) (grausTotais[1]*coeficiente);
            float e = (float) (grausTotais[0]*coeficiente);
            pokemon.animate()
                    .x(d)
                    .y(e)
                    .setDuration(0)
                    .start();
        }
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
                        terminou[0] = true;
                        super.onAnimationEnd(animation);
                        _pokeball.setX(POKEBALL_X_INICIAL);
                        _pokeball.setY(POKEBALL_Y_INICIAL);
                        Toast.makeText(getBaseContext(), "Errou a Pokebola", Toast.LENGTH_LONG).show();
                    }

        };

        ValueAnimator.AnimatorUpdateListener updateListenerX =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if(terminou[0]) animation.end();

                        if(checaColisao(pokemon, _pokeball)){

                            Log.d("colisaoX", "colidiu");
                            animation.pause();
                            capiturou = capituraPokemon();

                        }else if (!capiturou){

                            Log.d("colisaoX", "nao colidiu");
                            float valueX = (float) animation.getAnimatedValue("TranslationX");
                            _pokeball.setTranslationX(valueX);

                        }

                    }
                };

        ValueAnimator.AnimatorUpdateListener updateListenerY =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if(terminou[0]) animation.end();
                        if(checaColisao(pokemon, _pokeball)){
                            Log.d("colisaoX", "colidiu");
                            animation.pause();
                            capiturou = capituraPokemon();

                        }else if (!capiturou){
                            Log.d("colisaoX", "nao colidiu");
                            float valueY = (float) animation.getAnimatedValue("TranslationY");
                            _pokeball.setTranslationY(valueY);

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

        _pokeball.setX(pokemon.getX() + larguraImgPkm/2);
        _pokeball.setY(pokemon.getY() - larguraImgPkm/2);
        _pokeball.invalidate();
        Log.d("pokemon coord","x = "+pokemonX+ "y= "+pokemonY + "altura= "+pokemonAltura+" largura="+pokemonLargura);

        pokemon.setImageResource(R.drawable.explosion);
        pokemon.invalidate();
        pokemon.setVisibility(View.VISIBLE);
        pokemon.postDelayed(new Runnable() {
            @Override
            public void run() {
                pokemon.setVisibility(View.INVISIBLE);
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
        _pokeball.postDelayed(sound, 851);
        _pokeball.postDelayed(sound, 1851);
        _pokeball.postDelayed(sound, 2851);
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

};
