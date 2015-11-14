package com.maany.disabilityemaulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class ColorblindnessTestFilter extends AsyncTask<byte[],Integer,byte[]>{
    private ImageView imageView;
    private Camera camera;
    private Context context;

    public ColorblindnessTestFilter(Context context,ImageView imageView, Camera camera) {
        this.context = context;
        this.imageView = imageView;
        this.camera = camera;
    }

    @Override
    protected byte[] doInBackground(byte[]... params) {


        byte[] data = params[0];
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        YuvImage yuvimage=new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, 128, 96), 80, baos);
        byte[] jdata = baos.toByteArray();
//        int sizeOfData = jdata.length;

        return jdata;
    }

    @Override
    protected void onPostExecute(byte[] data) {
        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        imageView.setMinimumHeight(dm.heightPixels);
        imageView.setMinimumWidth(dm.widthPixels);
        imageView.setImageBitmap(bm);

    }

    private WindowManager getWindowManager(){
        return (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }
}