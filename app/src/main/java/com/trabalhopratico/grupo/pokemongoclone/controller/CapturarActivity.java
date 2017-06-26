package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.R.drawable;
import com.trabalhopratico.grupo.pokemongoclone.model.Aparecimento;

import java.io.IOException;

public class CapturarActivity extends Activity implements SensorEventListener, View.OnTouchListener{
    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView pokemonNaTela, _pokeball, pokemon;
    private  Aparecimento aparecimento;
    private Intent it;
    private Sensor sensor;
    private SensorManager sensorManager;
    private float grausNovos[] = new float[3];
    private float grausTotais[] = new float[3];
    private float grausVelhos[] = new float[3];
    private Display display;//= getWindowManager().getDefaultDisplay();
    private Double coeficiente_x, coeficiente_y;
    private int larguraDaTela, alturaDaTela;
    private float dx, dy, velx, vely;
    private View mCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);
        mCamera = getCameraInstance();
        safeCameraOpenInView(this.findViewById(android.R.id.content));

//        pokemonNaTela = (ImageView) findViewById(R.id.pokemon_img);
        it = getIntent();
        aparecimento = (Aparecimento) it.getSerializableExtra("Apar");
        /////////////////////////////////////////////////////////////////////
        //-----------------------------------------------------
        display = getWindowManager().getDefaultDisplay();
        larguraDaTela = display.getWidth();
        alturaDaTela = display.getHeight();
        // _pokeball = (ImageView) findViewById(R.id.pokebola);
        pokemon = (ImageView) findViewById(R.id.pokemon);

        pokemonNaTela = new ImageView(this);
        pokemonNaTela.setImageResource(aparecimento.getPokemon().getFoto());
     //   pokemon.setImageResource(aparecimento.getPokemon().getFoto());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pokemonNaTela.setLayoutParams(params);
        //pokemonNaTela.setBackgroundColor(Color.BLUE);
        pokemonNaTela.setAdjustViewBounds(true);
        FrameLayout fl = (FrameLayout) findViewById(R.id.overly_camera);
        fl.addView(pokemonNaTela);

        int tamanhoDaPokebola = alturaDaTela/10;
        _pokeball = new ImageView(this);
        _pokeball.setImageResource(drawable.pokeball);
        ViewGroup.LayoutParams _params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _pokeball.setLayoutParams(_params);
        _pokeball.setAdjustViewBounds(true);
        _pokeball.setMaxHeight(tamanhoDaPokebola);
        _pokeball.setMaxWidth(tamanhoDaPokebola);
        _pokeball.setScaleType(ImageView.ScaleType.FIT_XY);
        _pokeball.setX((larguraDaTela/2) - tamanhoDaPokebola/2);
        _pokeball.setY(alturaDaTela - ((int)(tamanhoDaPokebola*1.5)));
        _pokeball.setOnTouchListener(this);
        fl.addView(_pokeball);

        //------------------------------------------------------
        ///////////////////////////////////////////////////////
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        Log.i("captura", getResources().getResourceEntryName( aparecimento.getPokemon().getFoto()));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor == null) Toast.makeText(this, "Nao tem girsocopiculos", Toast.LENGTH_SHORT).show();
        else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        pokemonNaTela.post(new Runnable() {
            @Override
            public void run() {
                coeficiente_x = larguraDaTela/72d;
                coeficiente_y = alturaDaTela/72d;
                float proporcaoY = alturaDaTela/( ( (float)pokemonNaTela.getDrawable().getIntrinsicHeight())*2.0f);
                float proporcaoX = larguraDaTela/( ( (float)pokemonNaTela.getDrawable().getIntrinsicWidth())*2.0f);

                pokemonNaTela.getLayoutParams().height = (int) (pokemonNaTela.getDrawable().getIntrinsicHeight()*proporcaoY);
                pokemonNaTela.getLayoutParams().width = (int) (pokemonNaTela.getDrawable().getIntrinsicWidth()*proporcaoX);
                pokemonNaTela.setY(alturaDaTela/2 - (int)(pokemonNaTela.getDrawable().getIntrinsicHeight()*proporcaoY)/2);
                pokemonNaTela.setX(larguraDaTela/2 - (int)(pokemonNaTela.getDrawable().getIntrinsicWidth()*proporcaoX)/2);
                Log.d("captura", pokemonNaTela.getTop()+" "+pokemonNaTela.getBottom() +  ' ' + pokemonNaTela.getLeft() + " " + pokemonNaTela.getRight());
                Log.i("captura", "X x Y = " + pokemonNaTela.getDrawable().getIntrinsicHeight()+ " + " + pokemonNaTela.getDrawable().getIntrinsicWidth());
                Log.i("captura","alturaXlargura = " + pokemonNaTela.getHeight() +"x"+pokemonNaTela.getWidth());
                Log.i("captura","AlturaLarguraDaTela = " + alturaDaTela + " x " + larguraDaTela);
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
        releaseCameraAndPreview();
    }


    private Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        }catch(Exception e){}
        return c;
    }

    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            //mPreview.mCamera = null;
        }
    }

    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        mCameraView = view;
        qOpened = (mCamera != null);

        if(qOpened == true){
            mPreview = new CameraPreview(getBaseContext(), mCamera);
            SurfaceView preview = (SurfaceView) view.findViewById(R.id.camera);

            mPreview.setHolder(preview);
            mPreview.startCameraPreview();
        }
        return qOpened;
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
        pokemonNaTela.setX((float) (grausTotais[1]*coeficiente_x));
        pokemonNaTela.setY((float) (grausTotais[0]*coeficiente_y));
        pokemonNaTela.setRotation(grausTotais[2]);
        //Log.d("captura", pokemonNaTela.getTop()+" "+pokemonNaTela.getBottom() +  ' ' + pokemonNaTela.getLeft() + " " + pokemonNaTela.getRight());

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
                dx = v.getX() - event.getRawX() - (v.getWidth() / 2);
                dy = v.getY() - event.getRawY() - (v.getHeight() / 2);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                timer_fim = System.currentTimeMillis();
                xf = v.getX(); yf = v.getY();
                float vx = ((xf - x0)*10)/(timer_fim - timer_inicio);
                float vy = ((yf - y0)*10)/(timer_fim - timer_inicio);
                Log.d("vxvy", "vx= " + vx +"vy= "+vy);
                while ((v.getX() < larguraDaTela && v.getX() >= 0)
                        && (v.getY() < alturaDaTela  && v.getY() >= 0)){
                        v.setX(v.getX()+vx);
                        v.setY(v.getY()+vy);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(checaColisao(pokemonNaTela, v)){
                        Log.d("colli", "colidiu");
                        capituraPokemon();
                        finish();
                    }
                    v.animate()
                            .setDuration(100)
                            .x(v.getX()+vx)
                            .y(v.getY()+vy)
                            .start();
                    Log.d("cap", "vgety"+ v.getY());
                }
                Toast.makeText(getBaseContext(), "Errou a pokebola", Toast.LENGTH_SHORT);
                v.setX(larguraDaTela/2 - v.getWidth()/2);
                v.setY(alturaDaTela - 100);
                Log.d("vxvy", ""+v.getY());
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
        Toast.makeText(this, "Pokemon cApiturado", Toast.LENGTH_SHORT);
    }

    public boolean checaColisao(View pokemon,View pokebola) {
        //Retangulo:
        // a______b
        // |      |
        // c______d
        int a = (int)pokemon.getX();
        int b = a + pokemon.getWidth();
        int c = (int) pokemon.getY();
        int d = c + pokemon.getHeight();
        int A = (int) pokebola.getX();
        int B =  A + pokebola.getWidth();
        int C = (int) pokebola.getY();
        int D = C + pokebola.getHeight();
        Log.d("coli", a+ " "+ b+" "+c+" "+d);
        Log.d("coli", A+ " "+ B+" "+C+" "+D);
        Rect R1=new Rect(a, b, c, d);
        Rect R2=new Rect(A, B,C,D);
        return !(R1.intersect(R2));
    }

    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public void setHolder(SurfaceView surfaceView){
            mHolder = surfaceView.getHolder();
            mHolder.addCallback(this);

        }
        /**
         * This is called immediately after the surface is first created.
         * Implementations of this should start up whatever rendering code
         * they desire.  Note that only one thread can ever draw into
         * a {@link Surface}, so you should not draw into the Surface here
         * if your normal rendering will be in another thread.
         *
         * @param holder The SurfaceHolder whose surface is being created.
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try{
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }catch (IOException e) {
                Log.d("captura", "Error setting camera preview: " + e.getMessage() );
            }
        }

        /**
         * This is called immediately after any structural changes (format or
         * size) have been made to the surface.  You should at this point update
         * the imagery in the surface.  This method is always called at least
         * once, after {@link #surfaceCreated}.
         *
         * @param holder The SurfaceHolder whose surface has changed.
         * @param format The new PixelFormat of the surface.
         * @param width  The new width of the surface.
         * @param height The new height of the surface.
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here
            int degree = getDegreeCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            mCamera.setDisplayOrientation(90);
            Log.i("captura", "orientation=");
            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d("captura", "Error starting camera preview: " + e.getMessage());
            }

        }

        /**
         * This is called immediately before a surface is being destroyed. After
         * returning from this call, you should no longer try to access this
         * surface.  If you have a rendering thread that directly accesses
         * the surface, you must ensure that thread is no longer touching the
         * Surface before returning from this function.
         *
         * @param holder The SurfaceHolder whose surface is being destroyed.
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseCameraAndPreview();
        }
        public void startCameraPreview() {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private int getDegreeCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360; // compensate the mirror
            } else { // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            return result;
        }
    }
};
