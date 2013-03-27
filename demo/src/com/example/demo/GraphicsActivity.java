package com.example.demo;

/* The static import line below allows us to 
(1) call any glXXX function without "gl." prefix or 
(2) use any OpenGL constant without "GL10." prefix

Without it, calling 
    glClear(GL_COLOR_BUFFER_BIT)
    
must be written as:
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    
API Level 7 or earlier supports only OpenGL ES 1.x. 
On a real-device, it's recommended to use OpenGL ES 2.0 
(level 8 or higher), but the Android emulator currently does not 
support OpenGL ES 2.0
*/
/* replace GLES11 with GLES20 for OpenGL ES 2.0 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class GraphicsActivity extends Activity {
// private static final String TAG = "Hans GLES"; /* Log tag */
 private GLView mView;
 private ToggleButton lighting, accel, anim;
 private RadioGroup group;
 private RadioButton moveLight, moveSphere;
 private SensorManager sm;
 private Display myDisplay;
 private TransformationParams par;
 private GLRenderer render;
 private boolean useSensor;
 
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     
     /* remove the title bar at the top of screen */
     requestWindowFeature(Window.FEATURE_NO_TITLE);

     /* remove the status bar, make it full screen */
     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
             WindowManager.LayoutParams.FLAG_FULLSCREEN);
 
     par = new TransformationParams();
     par.eyeX = 3f;
     par.eyeY = -4f;
     par.eyeZ = 3f;
     par.litePos[0] = 0f;
     par.litePos[1] = 0f;
     par.litePos[2] = 3f;
     par.droid_x = 2.0f;
     par.droid_y = 1.0f;
     /* no additional layout, GLSurface will be the only view */
     mView = new GLView(this);
     mView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

     sm = (SensorManager) getSystemService(SENSOR_SERVICE);
     myDisplay = getWindowManager().getDefaultDisplay();
     /* The following line compiles only under GL ES 2.0 */
//     mView.setEGLContextClientVersion(2);
     setContentView(R.layout.main);

     group = (RadioGroup) findViewById(R.id.radiogroup);
     moveLight = (RadioButton) findViewById(R.id.movelight);
     moveSphere = (RadioButton) findViewById(R.id.moveball);
     lighting = (ToggleButton) findViewById(R.id.tblight);
     lighting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				render.setLighting(isChecked);
				moveLight.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});
     accel = (ToggleButton) findViewById(R.id.tbsensor);
     accel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             moveSphere.setVisibility(isChecked ? View.GONE : View.VISIBLE);
             useSensor = isChecked;
         }
     });
     
     anim = (ToggleButton)findViewById(R.id.tbanim);
     anim.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             render.setAnimation(isChecked);
         }
     });
     /* we are going to replace the dummy view with our own GLView */
     View dummy = (View) findViewById(R.id.dummy);
     /* identify dummy's parent */
     ViewGroup top = (ViewGroup) dummy.getParent();
     
     /* copy over all layout parameters from dummy to your GLView */
     mView.setLayoutParams(dummy.getLayoutParams());
     
     /* replace by removing and adding */
     int idx = top.indexOfChild(dummy);
     top.removeViewAt(idx);
     top.addView (mView, idx);
 }

 
	@Override
 protected void onPause() {
     super.onPause();
     mView.onPause();
     sm.unregisterListener(sensory);
 }

 @Override
 protected void onResume() {
     super.onResume();
     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
     mView.onResume();
     for (Sensor s : sm.getSensorList(Sensor.TYPE_ACCELEROMETER))
         sm.registerListener(sensory, s, SensorManager.SENSOR_DELAY_UI);
 }

 /* save and restore the XY-translation, so after orientation switch,
  * the box stay at the same spot */ 
 @Override
 protected void onRestoreInstanceState(Bundle savedInstanceState) {
     super.onRestoreInstanceState(savedInstanceState);
     par = savedInstanceState.getParcelable("param");
 }

 @Override
 protected void onSaveInstanceState(Bundle outState) {
     super.onSaveInstanceState(outState);
     outState.putParcelable("param", par);
 }

 public class GLView extends GLSurfaceView {
     double prevDist, currDist;
     
     public GLView(Context context) {
         super(context);
         render = new GLRenderer(context, par);
         setRenderer(render);
         setRenderMode(RENDERMODE_CONTINUOUSLY);
     }

     @Override
     public boolean onTouchEvent(final MotionEvent event) {
         queueEvent(new Runnable() {

             @Override
             public void run() {
                 if (event.getPointerCount() == 1) {
                 	render.doSwipe(event, group.getCheckedRadioButtonId());
                 }
                 else if (event.getPointerCount() >= 2) {
                 	render.doPinch(event, group.getCheckedRadioButtonId());
                 }
                 /* when the current render mode is "WHEN_DIRTY", the
                  * following call to requestRender() is necessary */
                 requestRender();
             }
         });
         return true;
     }
     
 }

 private SensorEventListener sensory = new SensorEventListener() {
     private float[] grav = new float[3];
     final float ALPHA = 0.5f;
     
     @Override
     public void onSensorChanged(SensorEvent event) {
         if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
             return;
         if (useSensor)
             for (int k = 0; k < 3; k++)
                 grav[k] = ALPHA * grav[k] + (1 - ALPHA) * event.values[k];
         else
             for (int k = 0; k < 3; k++)
                 grav[k] = 0;
         render.doTilt(grav, event.timestamp, myDisplay.getOrientation());
     }
     
     @Override
     public void onAccuracyChanged(Sensor sensor, int accuracy) {
         // TODO Auto-generated method stub
         
     }
 };
}
