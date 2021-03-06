package com.maany.disabilityemaulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * 1st param is byte[] i.e raw data from camera.
 * Created by Mayank on 11/14/2015.
 */
public class ColorblindnessTestFilter extends AsyncTask<byte[], Integer, byte[]> {
    private static boolean toggle = false;
    private ImageView imageView;
    private ImageView imageViewRight;
    private Camera camera;
    private Context context;

    public ColorblindnessTestFilter(Context context, ImageView imageView, ImageView imageViewRight, Camera camera) {
        this.context = context;
        this.imageView = imageView;
        this.camera = camera;
        this.imageViewRight = imageViewRight;
    }

    @Override
    protected byte[] doInBackground(byte[]... params) {
        byte[] data = params[0];
        Camera.Size previewSize = camera.getParameters().getPreviewSize();

        //apply yuv image to that bytearray
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, 480, 340), 80, baos);

        byte[] jdata = baos.toByteArray();
//        int sizeOfData = jdata.length;

        return jdata;
    }

    @Override
    protected void onPostExecute(byte[] data) {
        Bitmap bmSrc = BitmapFactory.decodeByteArray(data, 0, data.length);

        Bitmap bm = bmSrc.copy(Bitmap.Config.ARGB_8888,true);
        Bitmap bm2 = bmSrc.copy(Bitmap.Config.ARGB_8888,true);
        // Process pixels
        int pixel,r,g;
        if(toggle==false){
            toggle = true;
            return;
        }
        toggle=false;
        for(int i=0;i<bm.getWidth();i++)
            for(int j=0;j<bm.getHeight();j++){
                pixel = bm.getPixel(i, j);

                try {
                    r = (int)(0.5667*Color.red(pixel) + 0.43333*Color.green(pixel));
                    g = (int)(0.55833*Color.red(pixel) + 0.44167*Color.green(pixel));
                    bm2.setPixel(i, j, Color.argb(100,(int)(0.5667*r + 0.43333*g),(int)(0.55833*r + 0.44167*g),(int)(0.24167*g+0.75833*Color.blue(pixel))));
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }


//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //  imageView.setMinimumHeight(dm.heightPixels);
        //  imageView.setMinimumWidth(dm.widthPixels);
        imageView.setImageBitmap(bm2);
        imageViewRight.setImageBitmap(bm2);

    }

    private WindowManager getWindowManager() {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
}