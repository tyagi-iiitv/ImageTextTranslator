package com.krupo.sandroidocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TranslateActivity extends AppCompatActivity {

    protected Button _button;
    // protected ImageView _image;
    protected EditText _field,translatedet;
    protected String _path;
    protected boolean _taken;
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath().toString() + "/SimpleAndroidOCR/";
    private Button translate;
    private LinearLayout linHeader;
    private String TAG="lund";
    public static final String lang = "eng";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        _field = (EditText) findViewById(R.id.scannedText);
        translate = (Button) findViewById(R.id.translate);
        translatedet=(EditText)findViewById(R.id.translatedText);
        linHeader = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        translate.setOnClickListener(new ButtonClickHandler());

        Intent act = getIntent();
        _path =  act.getStringExtra("path");
        System.out.println(DATA_PATH);

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
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            System.out.println(view.getId());
            switch (view.getId())
            {
                case R.id.translate:
                    translate();
            }

        }
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
        try {
            inp = URLEncoder.encode(inp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String api = "https://www.googleapis.com/language/translate/v2?key=AIzaSyBgOzJaJZ-2vYfX5uA4dgsxkQstIBDBusQ&source=en&target=hi&q="+inp;
        RequestServer rs = new RequestServer(api,this);
        rs.execute();
    }

}
