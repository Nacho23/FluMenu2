package marcelo.flumenu2;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Marcelo on 28/04/2016.
 */
public class Post {

    private InputStream is = null;
    private String respuesta = "";

    private void conectaPost(final ArrayList params, final String url){
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList nameValuePairs;
                try{
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost(url);
                    nameValuePairs = new ArrayList();

                    if(params != null){
                        for (int i = 0; i < params.size() - 1; i += 2){
                            nameValuePairs.add(new BasicNameValuePair((String) params.get(i), (String) params.get(i + 1)));
                        }
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    }
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                }catch (Exception e){
                    Log.e("log_tag", "Error in http connection " + e.toString());
                }finally{

                }
            }
        });
        tr.start();
    }

    private void getRespuestaPost(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            respuesta = sb.toString();
            Log.e("log_tag", "Cadena Json "+respuesta);
        }catch(Exception e){
            Log.e("log_tag", "Error convertir result "+e.toString());
        }
    }

    @SuppressWarnings("finally")
    private JSONArray getJsonArray(){
        JSONArray jArray = null;
        try{
            jArray = new JSONArray(respuesta);
        } catch(Exception e){

        }finally {
            return jArray;
        }
    }

    public JSONArray getServerData(ArrayList params, String url){
        conectaPost(params, url);
        if(is != null){
            getRespuestaPost();
        }
        if(respuesta != null && respuesta.trim() != ""){
            return getJsonArray();
        }else{
            return null;
        }
    }

}
