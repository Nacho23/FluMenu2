package marcelo.flumenu2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String user,token;
    TextView txt_user, logoff;
    ImageButton anuncio;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private static final String TAG = "Principal";
    private static final String SAVE_TOKEN_URL = "http://bafuach.esy.es/android/register_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d(TAG, "Subscribed to news topic");

        Log.d(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
        token = FirebaseInstanceId.getInstance().getToken();

        txt_user = (TextView) findViewById(R.id.textView_nombre);
        logoff = (TextView) findViewById(R.id.textView_logoff);
        anuncio = (ImageButton) findViewById(R.id.bt_anuncio);

        Bundle extras = getIntent().getExtras();

        //Obtenemos datos enviados desde el Login (usuario);
        if(extras != null){
            user = extras.getString("user");
        }else{
            user = "error";
        }

        this.sendRegistrationToServer(token);
        txt_user.setText("Bienvenido " + user);//Cmbiamos el texto al nombre del registrado

        logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cerrar sesion
                finish();

            }
        });

        anuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Principal.this, Anuncios.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void sendRegistrationToServer(String token) {

        if(user != null) {
            new AttemptRegister().execute();
        }
    }
    class AttemptRegister extends AsyncTask<String, String, String> {
        /** * Before starting background thread Show Progress Dialog * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Principal.this);
            pDialog.setMessage("Registrando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            try {
                List<NameValuePair> data = new ArrayList<>();
                data.add(new BasicNameValuePair("rut", user));
                data.add(new BasicNameValuePair("token", token));
                JSONObject response = jsonParser.makeHttpRequest( SAVE_TOKEN_URL, "POST", data);
                return response.getString("message");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        /** * Once the background process is done we need to Dismiss the progress dialog asap * **/
        protected void onPostExecute(String message) {
            pDialog.dismiss();
            if (message != null){
                //Toast.makeText(Principal.this, message, Toast.LENGTH_LONG).show();
            }
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
            // Handle the camera action
        } else if (id == R.id.nav_contacto) {
            Intent i = new Intent(Principal.this, Contacto.class);
            i.putExtra("user", user);
            startActivity(i);
        } else if (id == R.id.nav_info) {
            Dialog dialog = new Dialog(Principal.this);
            dialog.setContentView(R.layout.dialog_acercade);
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
