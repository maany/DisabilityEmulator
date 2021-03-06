package com.maany.disabilityemaulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CameraActivity extends Activity {

    public static final String TAG = CameraActivity.class.getName();
    private Camera mCamera;
    private CameraPreview mPreview;
    private TextView debugText;
    private ImageView filteredImageView;
    private ImageView filteredImageViewRight;
    private Button deuteranopia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

//        debugText = (TextView)findViewById(R.id.debugText);
        debugText = new TextView(this);
        filteredImageView = (ImageView) findViewById(R.id.camera_preview_filtered);
        filteredImageViewRight = (ImageView) findViewById(R.id.camera_preview_filtered_right);
        deuteranopia = (Button) findViewById(R.id.deuteranopia);
        if(checkCameraHardware(this)){
            Toast.makeText(this, "Device has usable camera", Toast.LENGTH_LONG);
            Log.i(TAG, "Device has usable camera");
            debugText.setText("Cool");
        } else {
            Toast.makeText(this,"Device does not have usable camera", Toast.LENGTH_LONG);
            Log.e(TAG, "Device does not have usable camera");
            debugText.setText("Not Cool");
        }
        // Create an instance of Camera
        mCamera = getCameraInstance();
        if(mCamera!=null) {
            debugText.setText("Cool cool");
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera, filteredImageView, filteredImageViewRight);
            RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            //preview.setVisibility(View.INVISIBLE);
            //filteredImageView.setVisibility(View.INVISIBLE);
        }else{
            // camera not available
            debugText.setText("no Cool");
        }


        deuteranopia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CameraActivity.this,DeuteranopiaActivity.class);
                try {
                    killActivity();
                    startActivity(intent);

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
    //TODO specify image format
//            c.getParameters().setPreviewFormat(ImageFormat.);
        }
        catch (Exception e){
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    private void killActivity(){
        releaseCamera();
        finish();

    }

    private void releaseCamera(){

        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

    }
}
