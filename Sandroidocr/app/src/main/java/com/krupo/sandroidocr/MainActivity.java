package com.krupo.sandroidocr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;


public class MainActivity extends Activity {
    public static final String PACKAGE_NAME = "com.datumdroid.android.ocr.simple";
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath().toString() + "/SimpleAndroidOCR/";

    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list
    public static final String lang = "eng";

    private static final String TAG = "SimpleAndroidOCR.java";

    protected Button _button;
    // protected ImageView _image;
    protected EditText _field,translatedet;
    protected String _path;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN = "photo_taken";
    private Button translate;
    private LinearLayout linHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };



        setContentView(R.layout.activity_main);

        // _image = (ImageView) findViewById(R.id.image);
        _field = (EditText) findViewById(R.id.scannedText);
        _button = (Button) findViewById(R.id.takePhoto);
        translate = (Button) findViewById(R.id.translate);
        _button.setOnClickListener(new ButtonClickHandler());
        translatedet=(EditText)findViewById(R.id.translatedText);
        _path = DATA_PATH + "/ocr.jpg";
        linHeader = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        translate.setOnClickListener(new ButtonClickHandler());
        boolean internet = isNetworkAvailable(getApplicationContext());
        boolean permissions = true;
        boolean tess_file = true;
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    permissions = false;
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                System.out.println("File exists***********");
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
                tess_file = false;
            }
        }
        if(!internet)
            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
        if(!permissions)
            Toast.makeText(getApplicationContext(), "No Permissions", Toast.LENGTH_SHORT).show();
        if(!tess_file)
            Toast.makeText(getApplicationContext(), "eng.traineddata file not found", Toast.LENGTH_SHORT).show();

    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            System.out.println(view.getId());
            switch (view.getId())
            {
                case R.id.takePhoto:
                    Log.v(TAG, "Starting Camera app");
                    startCameraActivity();
                    break;
                case R.id.translate:
                    translate();
            }

        }
    }
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }




    public void preexec()
    {
        linHeader.setVisibility(LinearLayout.VISIBLE);
    }
    public void postexec(String output)
    {
        translatedet.setText(output.trim());
        linHeader.setVisibility(LinearLayout.INVISIBLE);
    }
    public void translate()
    {
        System.out.println("translate");
        String inp = _field.getText().toString();
        System.out.println("*****"+inp);
        try {
            inp = URLEncoder.encode(inp,"UTF-8");
            System.out.println("******"+inp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String api = "https://translate.yandex.net/api/v1.5/tr.json/translate ?key=trnsl.1.1.20170222T131651Z.f2fc75b00b288266.56d1e3003dcf5b5d5104f0b80ff47bb13462d15d&lang=en-hi&text="+inp;
        System.out.println(api);
        // RequestServer rs = new RequestServer(api,this);
        //rs.execute();
    }
    // Simple android photo capture:
    // http://labs.makemachine.net/2010/03/simple-android-photo-capture/

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode: " + resultCode);

        if (resultCode == -1) {
            onPhotoTaken();
            Intent ca = new Intent(this,CropActivity.class);
            ca.putExtra("path",_path);
            startActivity(ca);
        } else {
            Log.v(TAG, "User cancelled");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    protected void onPhotoTaken() {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();

        if ( recognizedText.length() != 0 ) {
            _field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
            _field.setSelection(_field.getText().toString().length());
        }

        // Cycle done.
    }



}