package com.krupo.sandroidocr;

import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by harishkrupo on 13/3/16.
 */
public class RequestServer extends AsyncTask<Void,Void,Void> {
    HttpURLConnection urlConnection=null;
    String output=null;
    String api=null;
    TranslateActivity ma=null;

    @Override
    protected void onPostExecute(Void aVoid) {
        ma.postexec(output);
    }

    @Override
    protected void onPreExecute() {
        ma.preexec();
    }

    public RequestServer(String _api,TranslateActivity s)
    {
        this.api=_api;
        ma=s;
    }
    @Override
    protected Void doInBackground(Void... params1) {

        try {

            output = doInBackgroundHelper();
            System.out.println(output);
            JSONObject jobj = new JSONObject(output.toString());
            System.out.println(jobj);
            jobj=jobj.getJSONObject("data");
            System.out.println(jobj);
            JSONArray jarr=jobj.getJSONArray("translations");
            System.out.println(jarr);
            jobj=jarr.getJSONObject(0);
            System.out.println(jobj);
            output = jobj.getString("translatedText");
            System.out.println(output);


        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    protected String doInBackgroundHelper(){
        StringBuilder result = new StringBuilder();
        try{
            URL url = new URL(api);

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while((line = reader.readLine())!= null){
                result.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result.toString();
    }


}
