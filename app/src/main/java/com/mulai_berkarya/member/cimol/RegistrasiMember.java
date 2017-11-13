package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

public class RegistrasiMember extends AppCompatActivity implements OnClickListener {
    private static final String LINK_UTK_REGISTRASI = URL_BASE_API + "/registrasi_member.php";
    private static final String TAG_BERHASIL = "sukses";
    private static final String TAG_PESAN = "pesan";
    TextView tvError;
    // panggil ClassJsonParser ke sisni
    private EditText etNamaMember, etEmail, etTelp, etPassword1,
            etPassword2;
    private Button tombolRegistrasi;
    // utk progress bar
    private ProgressDialog progressBarNya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_member);
//        alert
        alertDialogTrial();

        etNamaMember = (EditText) findViewById(R.id.editNM);
        etEmail = (EditText) findViewById(R.id.editMail);
        etTelp = (EditText) findViewById(R.id.editTelp);
        etPassword1 = (EditText) findViewById(R.id.editPw);
        etPassword2 = (EditText) findViewById(R.id.editKPw);

        tombolRegistrasi = (Button) findViewById(R.id.bReg);
        tombolRegistrasi.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // klik tombol register untuk apa?
        progressBarNya = new ProgressDialog(RegistrasiMember.this);
        progressBarNya.setMessage("Loading...");
        progressBarNya.setIndeterminate(false);
        progressBarNya.setCancelable(true);
        progressBarNya.show();

        String nama = etNamaMember.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telp = etTelp.getText().toString().trim();
        String pass1 = etPassword1.getText().toString().trim();
        String pass2 = etPassword2.getText().toString().trim();


        if (isIsi(nama) && isIsi(email)
                && isIsi(telp) && isIsi(pass1) && isIsi(pass2)) {
            if (!pass1.equals(pass2)) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                progressBarNya.dismiss();
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Format email salah!", Toast.LENGTH_SHORT).show();
                progressBarNya.dismiss();
            } else {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("nama_member", nama);
                params.put("e_mail", email);
                params.put("Telp_member", telp);
                params.put("pass", pass1);

                client.post(LINK_UTK_REGISTRASI, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        progressBarNya.dismiss();


                        try {
                            String res = response.getString("status");
                            if (res.equalsIgnoreCase("berhasil")) {
                                Toast.makeText(RegistrasiMember.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                                etEmail.setText("");
                                etPassword1.setText("");
                                etPassword2.setText("");
                                etNamaMember.setText("");
                                etTelp.setText("");
                                Intent i = new Intent(RegistrasiMember.this, LoginAct.class);
                                startActivity(i);
                            } else {
                                String error = response.getString("error");
                                Toast.makeText(RegistrasiMember.this, "Gagal mendaftar." + error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistrasiMember.this, "Terjadi Kesalahan. Cek kembali koneksi internet anda", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(RegistrasiMember.this, "Terjadi Kesalahan. Cek kembali koneksi internet anda", Toast.LENGTH_SHORT).show();
                        progressBarNya.dismiss();
                    }

                });

            }
        }
    }


    public boolean isIsi(String nama) {
        if (!TextUtils.isEmpty(nama)) return true;
        else return false;
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
        AlertDialog.Builder builder=new AlertDialog.Builder(RegistrasiMember.this);
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
