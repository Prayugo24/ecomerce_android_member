package com.mulai_berkarya.member.cimol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

public class LoginAct extends AppCompatActivity {
    Button daftar, login;
    Intent a;
    EditText email, password;
    String url, success, id_member;
    SessionManager session;

    String url_trans = URL_BASE_API + "/tambah_trans.php";
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        alert
        alertDialogTrial();

        session = new SessionManager(getApplicationContext());
        //Toast.makeText(getApplicationContext(),"User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


      /*  if (session.isLoggedIn()) {
            Intent a = new Intent(LoginAct.this, MainActivity.class);
            //a.getStringExtra(id_member);
            startActivity(a);
            finish();
        } else {
            Intent a = new Intent(LoginAct.this, MainActivity.class);
            //a.getStringExtra(id_member);
            startActivity(a);
            finish();
        }*/


        //buat db
        DatabaseHandler dbHelper = new DatabaseHandler(this);
        dbHelper.createDataBase();

        daftar = (Button) findViewById(R.id.daftar);
        login = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //url = URL_BASE_API + "/login.php?" + "e_mail="+ email.getText().toString() + "&pass="+ password.getText().toString();

                if (email.getText().toString().trim().length() > 0
                        && password.getText().toString().trim().length() > 0) {
                    //new Masuk().execute();
                    //transaksi();
                    if (isValidEmail(email.getText().toString().trim())) {
                        masuk(email.getText().toString().trim(), password.getText().toString().trim());
                    } else {
                        Toast.makeText(LoginAct.this, "Format email salah!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Username/password masih kosong gan.!!", Toast.LENGTH_LONG).show();
                }


            }
        });
        daftar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginAct.this, RegistrasiMember.class);
                startActivity(i);
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Login...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

    }

    //method baru untuk login
    public void masuk(String email, String pass) {
        pDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("e_mail", email);
        params.add("pass", pass);
        client.post(Config.URL_BASE_API + "/login_member.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pDialog.dismiss();
                throwable.printStackTrace();
                Log.d("TAG", "login : " + responseString);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pDialog.dismiss();
                try {
                    int success = response.getInt("success");
                    if (success == 1) {
                        JSONArray array = response.getJSONArray("login");
                        JSONObject obj = array.getJSONObject(0);
                        String idMember = obj.getString("id_member");
                        String namaMember = obj.getString("nama_member");
                        String eMail = obj.getString("e_mail");
                        //String pass = obj.getString("pass");

                        session.createLoginSession(namaMember, eMail, idMember);

                        if (getCallingActivity() != null) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            LoginAct.this.finish();
                        } else {
                            startActivity(new Intent(LoginAct.this, MainActivity.class));
                        }


                    } else if (success == 0) {
                        //gagal
                        Toast.makeText(LoginAct.this, "Cek email dan password!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginAct.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_alert_dialog,null);

        TextView title=(TextView)view.findViewById(R.id.tv_title);
        ImageButton imagebutton=(ImageButton) view.findViewById(R.id.image_pop);

        title.setText("Peringatan");

        imagebutton.setImageResource(R.drawable.trial);
        builder.setPositiveButton("Saya, mengerti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Terimakasih Banyak !!",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        builder.show();
    }
}
