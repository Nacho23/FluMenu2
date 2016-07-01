package marcelo.flumenu2;


import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    SharedPreferences loginPref;
    SharedPreferences.Editor loginEditor;

    private EditText user, pass;
    private CheckBox cbRemember;
    private Button bLogin;
    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    //private static final String LOGIN_URL = "http://192.168.0.24/htmlAndroid/paginaWeb2/android/login.php";
    private static final String LOGIN_URL = "http://bafuach.esy.es/android/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private String username;
    private String password;

    private boolean checkFlag;

    String usuario;
    String contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        user = (EditText)findViewById(R.id.et_Usuario);
        pass = (EditText)findViewById(R.id.et_contrasenia);
        bLogin = (Button)findViewById(R.id.bt_Ingresar);
        cbRemember = (CheckBox)findViewById(R.id.cbRemember);
        cbRemember.setOnCheckedChangeListener(this);
        checkFlag = cbRemember.isChecked();

        bLogin.setOnClickListener(this);

        loginPref = getSharedPreferences("login.conf", Context.MODE_PRIVATE);
        loginEditor = loginPref.edit();

        usuario = loginPref.getString("username", "");
        contrasena = loginPref.getString("password","");

        if(!(usuario.equals("")) && !(contrasena.equals(""))){
            user.setText(usuario);
            pass.setText(contrasena);
            username = user.getText().toString();
            password = pass.getText().toString();
            new AttemptLogin().execute();
        }
    }

    private void saveData(){
        loginEditor.putString("username", user.getText().toString());
        loginEditor.putString("password", pass.getText().toString());
        loginEditor.apply();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_Ingresar:
                username = user.getText().toString();
                password = pass.getText().toString();
                new AttemptLogin().execute();
            default: break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkFlag = isChecked;
    }

    class AttemptLogin extends AsyncTask<String, String, String> {
        /** * Before starting background thread Show Progress Dialog * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Autenticando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // here Check for success tag
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest( LOGIN_URL, "POST", params);
                // checking log for json response
                Log.d("Login attempt", json.toString());
                // success tag for json
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    if(checkFlag){
                        saveData();
                    }

                    Log.d("Ingreso Exitoso!", json.toString());
                    Intent ii = new Intent(Login.this,Principal.class);
                    ii.putExtra("user", username);
                    // this finish() method is used to tell android os that we are done with current
                    //activity now! Moving to other activity
                    startActivity(ii);
                    finish();
                    return json.getString(TAG_MESSAGE);
                }
                else{
                    return json.getString(TAG_MESSAGE);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        /** * Once the background process is done we need to Dismiss the progress dialog asap * **/
        protected void onPostExecute(String message) {
            pDialog.dismiss();
            if (message != null){
                Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}