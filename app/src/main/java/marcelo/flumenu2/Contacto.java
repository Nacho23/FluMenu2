package marcelo.flumenu2;

import android.app.Dialog;
import android.content.Entity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class Contacto extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String user;
    private Button bt_enviar;
    private EditText rut_p;
    private EditText mensaje_p;
    private TextView cont_Letras;

    String rut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacto);

        Bundle extras = getIntent().getExtras();
        rut = extras.getString("user");
        user = extras.getString("user");

        bt_enviar = (Button)findViewById(R.id.bt_enviar);
        rut_p = (EditText)findViewById(R.id.et_rut);
        mensaje_p = (EditText)findViewById(R.id.et_mensaje);
        cont_Letras = (TextView)findViewById(R.id.tv_contLetras);

        rut_p.setText(rut);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mensaje_p.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Codigo antes de cambiar el texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cont_Letras.setText("Caracteres restantes: " + (200 - Integer.valueOf(mensaje_p.length())));
                if(mensaje_p.length() >= 150){
                    cont_Letras.setTextColor(Color.RED);
                }else if(mensaje_p.length() == 200){
                    cont_Letras.setText("No puede escribir más caracteres");
                }else if(mensaje_p.length() < 150){
                    cont_Letras.setTextColor(Color.rgb(3,3,3));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*Despliega Menu Lateral*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_contact);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String rut = rut_p.getText().toString();
                final String msge = mensaje_p.getText().toString();
                ArrayList params = new ArrayList();
                params.add("rut");
                params.add(rut);
                params.add("mensaje");
                params.add(msge);
                try{
                    if(rut.trim().length() != 0 && msge.trim().length() != 0) {
                        Post post = new Post();
                        JSONArray datos = post.getServerData(params, "http://bafuach.esy.es/android/contactar.php");
                        if (datos != null && datos.length() > 0) {
                            JSONObject json_data = datos.getJSONObject(0);
                            int numRegistrados = json_data.getInt("rut");
                            if (numRegistrados == 0) {
                                Toast.makeText(getBaseContext(), "No se pudo enviar el mensaje.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Toast.makeText(getBaseContext(), "Mensaje Enviado Correctamente.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "Uno o ambos campos vacíos.", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(getBaseContext(), "No se pudo cnectar al servidor", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_contact);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /*@Override
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            finish();
            //Intent i = new Intent(Contacto.this, Principal.class);
            //startActivity(i);
        } else if (id == R.id.nav_contacto) {

        } else if (id == R.id.nav_info) {
            Dialog dialog = new Dialog(Contacto.this);
            dialog.setContentView(R.layout.dialog_acercade);
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_contact);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
