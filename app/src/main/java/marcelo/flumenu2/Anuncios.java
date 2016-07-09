package marcelo.flumenu2;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Anuncios extends Activity implements View.OnClickListener {

    int i = 0;
    int id_evento = 0;
    String datos;
    JSONParser jsonParser = new JSONParser();
    String rut;

    Spinner spinner;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anuncios);

        new AttemptReadData().execute();

        Bundle extras = getIntent().getExtras();
        rut = extras.getString("user");

        spinner = (Spinner)findViewById(R.id.spinner_request);
        ArrayAdapter adapterSpinner = ArrayAdapter.createFromResource(this, R.array.spinner_request,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        spinner.setEnabled(false);

        TextView err = (TextView)findViewById(R.id.tvTexto);
        btnEnviar = (Button)findViewById(R.id.btn_SendRequest);
        Button btnSig = (Button)findViewById(R.id.btSiguiente);
        Button btnAnt = (Button)findViewById(R.id.btAnterior);
        btnSig.setOnClickListener(this);
        btnAnt.setOnClickListener(this);


        btnEnviar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String respuesta = spinner.getSelectedItem().toString();
                final String id_evento_string = String.valueOf(id_evento);
                ArrayList params = new ArrayList();
                params.add("id_evento");
                params.add(id_evento_string);
                params.add("rut");
                params.add(rut);
                params.add("respuesta");
                params.add(respuesta);

                try{
                    if(id_evento != 0){
                        Post post = new Post();
                        //JSONArray datos = post.getServerData(params,"http://bafuach.esy.es/android/enviarRespuesta.php");
                        JSONArray datos = post.getServerData(params,"http://localhost/webpage-BAFUACh/android/enviarRespuesta.php");
                        if(datos != null && datos.length() > 0){
                            Toast.makeText(getBaseContext(), "No se pudo enviar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getBaseContext(), "Se envió Correctamente", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "No existe el evento", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(getBaseContext(), "No se pudo enviar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClick(View v){
        final TextView err = (TextView)findViewById(R.id.tvTexto);
        int id = v.getId();
        if (id == R.id.btSiguiente) {
            i++;
            //Log.e("json Parsering", "" + datos);
            try {
                listaEntrada(obtDatosJSON(datos));
            }catch (Exception e){
                err.setText("No existen más mensajes");
            }
        }
        if (id == R.id.btAnterior) {
            i--;
            if (i == -1) i = 0;
            try {
                listaEntrada(obtDatosJSON(datos));
            }catch (Exception e){
                err.setText("No existen más mensajes");
            }
        }
    }

    public void listaEntrada(ArrayList<String> datos) {
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos);
        JSONArray json = new JSONArray(datos);
        TextView err = (TextView)findViewById(R.id.tvTexto);
        err.setText(datos.get(i));
        if (datos.get(i).contains("(Evento)")) {
            spinner.setEnabled(true);
            btnEnviar.setEnabled(true);
        } else {
            btnEnviar.setEnabled(false);
            spinner.setEnabled(false);
        }
    }

    public ArrayList<String> obtDatosJSON(String datos) {
        TextView err = (TextView)findViewById(R.id.tvTexto);
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray json = new JSONArray(datos);
            String texto = "";
            for (int i = 0; i < json.length(); i++){
                id_evento = json.getJSONObject(i).getInt("id");
                texto = json.getJSONObject(i).getString("titulo") + "\n\n"
                        + json.getJSONObject(i).getString("mensaje");
                listado.add(texto);
            }
        }catch (Exception e){
            Log.e("json Parsering", "" + e.toString());
        }
        return listado;
    }

    class AttemptReadData extends AsyncTask<String, String, String> {
        /** * Before starting background thread Show Progress Dialog * */
        boolean failure = false;
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Anuncios.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            datos = jsonParser.getStringFromUrl("http://bafuach.esy.es/android/GetData.php");
            return datos;
        }
        /** * Once the background process is done we need to Dismiss the progress dialog asap * **/
        protected void onPostExecute(String message) {
            //Toast.makeText(Anuncios.this, datos, Toast.LENGTH_LONG).show();
            pDialog.dismiss();
            listaEntrada(obtDatosJSON(datos));
        }
    }
}