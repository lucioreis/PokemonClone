package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.model.Aparecimento;

import java.io.IOException;

public class CapturarActivity extends Activity implements SensorEventListener, View.OnTouchListener{
    private ImageView _pokeball, pokemon;
    private  Aparecimento aparecimento;
    private Intent it;
    private Sensor sensor;
    private SensorManager sensorManager;
    private float grausNovos[] = new float[3];
    private float grausTotais[] = new float[3];
    private float grausVelhos[] = new float[3];
    private Display dispaly;
    private Double coeficiente;
    private int larguraDaTela, alturaDaTela;
    private float dx, dy,centerX, centerY;
    FragmentTransaction fragmentTransaction;
    CameraSurfaceView cameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);

        cameraSurfaceView = new CameraSurfaceView();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.cameraFragment, cameraSurfaceView).commit();

        it = getIntent();
        aparecimento = (Aparecimento) it.getSerializableExtra("Apar");
        pokemon = (ImageView) findViewById(R.id.pokemon);
        pokemon.setImageResource(aparecimento.getPokemon().getFoto());

        _pokeball = (ImageView) findViewById(R.id.pokeball_image_view);
        _pokeball.setImageResource(R.drawable.pokeball);
        _pokeball.setOnTouchListener(this);

        Log.i("captura", getResources().getResourceEntryName( aparecimento.getPokemon().getFoto()));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor == null) Toast.makeText(this, "Nao tem girsocopiculos", Toast.LENGTH_SHORT).show();
        else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }

//        getCurrentFocus()..getViewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//                getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
               // // all views has been placed
                //// make your calculation now...
//            }
//        });
    }

    @Override
    protected void onResume(){
        super.onResume();
                pokemon.post(new Runnable() {
            @Override
            public void run() {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                alturaDaTela = size.y;
                larguraDaTela = size.x;
                coeficiente = larguraDaTela/72d;
                float larguraImgPkm = larguraDaTela/2;
                float proporcao = (pokemon.getMeasuredWidth())/larguraDaTela;
                float alturaImgPkm = pokemon.getMeasuredHeight()*proporcao/2;
                centerX = larguraDaTela/2 - (((int) larguraImgPkm)/2);
                centerY = alturaDaTela/2  - (((int) alturaImgPkm)/2);
                pokemon.getLayoutParams().height = (int) alturaImgPkm;
                pokemon.getLayoutParams().width = (int) larguraImgPkm;
                pokemon.setX(centerX);// - pokemon.getWidth()/2);
                pokemon.setY(centerY);
                _pokeball.getLayoutParams().height = alturaDaTela/10;
                _pokeball.getLayoutParams().width = alturaDaTela/10;
                Log.d("captura", pokemon.getX()+" "+ pokemon.getY() +  ' ' + pokemon.getLeft() + " " + pokemon.getRight());
                Log.d("captura", "X x Y = " + pokemon.getHeight()+ " + " + pokemon.getWidth());
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
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
            pokemon.setX((float) (grausTotais[1] * coeficiente));
            pokemon.setY((float) (grausTotais[0] * coeficiente));
            pokemon.setRotation(grausTotais[2]);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

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
                x0 = v.getX(); y0 = v.getY();
                timer_inicio = System.currentTimeMillis();
                dx = event.getRawX() - (v.getWidth() / 2);
                dy = event.getRawY() - (v.getHeight() / 2);
                v.setX(dx); v.setY(dy);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                timer_fim = System.currentTimeMillis();
                xf = v.getX(); yf = v.getY();
                float tempo = (timer_fim - timer_inicio);
                final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(_pokeball, "translationX", v.getX(), -larguraDaTela);
                objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                            if(checaColisao(pokemon, _pokeball)){
                                capituraPokemon();
                                objectAnimator.end();
                            }
                    }

                });
                objectAnimator.addListener( new ValueAnimator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                            _pokeball.setX(larguraDaTela/2 - _pokeball.getWidth()/2);
                            _pokeball.setY(alturaDaTela/2 - _pokeball.getHeight()/2);
                    }
                    @Override
                    public void onAnimationCancel(Animator animator){
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(_pokeball, "translationY", v.getY(), -alturaDaTela);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(objectAnimator).with(objectAnimator1);
                Double distanciaAPercorrer =  Math.sqrt(Math.pow(larguraDaTela, 2)+ Math.pow(alturaDaTela, 2));
                Double distanciaPercorrida =  Math.sqrt(Math.pow(xf, 2)+ Math.pow(yf, 2));
                animatorSet.setDuration((long)( distanciaAPercorrer/distanciaPercorrida/tempo ));
                animatorSet.start();
                v.setX(larguraDaTela/2 - v.getWidth()/2);
                v.setY(alturaDaTela - 200);
                Log.d("vxvy", ""+v.getY());
                Toast.makeText(getBaseContext(), "Errou a pokebola", Toast.LENGTH_SHORT);
                break;
            case MotionEvent.ACTION_MOVE:
                v.animate()
                        .x(event.getRawX() + dx)
                        .y(event.getRawY() + dy)
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }

    private void capituraPokemon() {
        Animation explosao = new ScaleAnimation(alturaDaTela/2,larguraDaTela/2, pokemon.getY(), pokemon.getX());
        pokemon.setImageResource(R.drawable.explosion);
        explosao.setDuration(350);
        pokemon.startAnimation(explosao);
        pokemon.setScaleType(ImageView.ScaleType.CENTER);
        pokemon.setAdjustViewBounds(true);
        float centerx = _pokeball.getX() - (_pokeball.getWidth() / 2);
        float centery = _pokeball.getY() - (_pokeball.getHeight() / 2);
        Animation rotacao = new RotateAnimation(-20, 20, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotacao.setDuration(800);
        rotacao.setRepeatCount(3);
        _pokeball.startAnimation(rotacao);

//        final ImageView pokeballTmp  = _pokeball;
//        Runnable runnable = new Runnable(){
//            @Override
//                public void run(){
//                    pokeballTmp.animate()
//                        .rotation( (float)20).setDuration(300)
//                        .rotation( (float) -40).setDuration(300)
//                        .rotation( (float) 40 ).setDuration(300)
//                        .rotation( (float) -20 ).setDuration(300).start();
//                }
//            };
//            new Thread(runnable);
//        try {
//            Thread.sleep(1300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

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
        Rect R2_pokebola = new Rect(pokebolaLeft, pokebolaRight, pokebolaRight,pokebolaBottom);
        return R1_pokemon.contains(R2_pokebola);
    }
};
