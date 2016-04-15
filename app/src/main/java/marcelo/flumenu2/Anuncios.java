package marcelo.flumenu2;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;

public class Anuncios extends Activity implements View.OnClickListener {

    int i = -1;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anuncios);
        TextView err = (TextView)findViewById(R.id.tvTexto);
        err.setText("INICIO");
        Button btnSig = (Button)findViewById(R.id.btSiguiente);
        Button btnAnt = (Button)findViewById(R.id.btAnterior);
        btnAnt.setOnClickListener(this);
        btnSig.setOnClickListener(this);
    }

    public void onClick(View v){
        final TextView err = (TextView)findViewById(R.id.tvTexto);
        int id = v.getId();
        if (id == R.id.btSiguiente) {
            i++;
            Thread nt = new Thread() {
                @Override
                public void run() {
                    final String datos = leer();
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        listaEntrada(obtDatosJSON(datos));
                                    }catch (Exception e){
                                        err.setText("No existen más mensajes");
                                        i--;
                                    }
                                }
                            });
                }
            };
            nt.start();
        }
        if (id == R.id.btAnterior) {
            i--;
            if(i == -1) i = 0;
            Thread nt = new Thread() {
                @Override
                public void run() {
                    final String datos = leer();
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    listaEntrada(obtDatosJSON(datos));
                                }
                            });
                }
            };
            nt.start();
        }
    }

    public void listaEntrada(ArrayList<String> datos) {
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos);
        JSONArray json = new JSONArray(datos);
        TextView err = (TextView)findViewById(R.id.tvTexto);
        int ultimoMsg = datos.size();
        err.setText(datos.get(i));
    }

    public String leer() {
        HttpClient cliente = new DefaultHttpClient();
        HttpContext contexto = new BasicHttpContext();
        //bafuach.esy.es
        HttpGet httpget = new HttpGet("http://bafuach.esy.es/GetData.php");
        String resultado = null;
        try{
            HttpResponse response = cliente.execute(httpget, contexto);
            HttpEntity entity = response.getEntity();
            resultado = EntityUtils.toString(entity, "UTF-8");
        }catch(Exception e){
            //TODO: handle exception
        }
        return resultado;
    }

    public ArrayList<String> obtDatosJSON(String datos) {
        TextView err = (TextView)findViewById(R.id.tvTexto);
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray json = new JSONArray(datos);
            String texto = "";
            for (int i = 0; i < json.length(); i++){
                texto = json.getJSONObject(i).getString("titulo") + "\n\n"
                        + json.getJSONObject(i).getString("mensaje");
                listado.add(texto);
            }
        }catch (Exception e){
            //TODO:handle Exception
        }
        return listado;
    }
}