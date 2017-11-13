package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

/**
 * Created by Mastah on 28/09/2016.
 */
public class KonfirmasiBayar extends AppCompatActivity {
    EditText enama, enomor, ejumlah;
    Button bkon;
    String id_trans, id_member;
    int total;
    String url = URL_BASE_API + "/konfirmasi_bayar.php?id_member=";
    SessionManager sessionManager;
    ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfimasi_bayar);
//        alert
        alertDialogTrial();


        Intent i = getIntent();
        id_trans = i.getStringExtra("id_transaksi");
        String sTotal = i.getStringExtra("total");
        Log.e("TAG ", id_trans);
        if (null != sTotal) total = Integer.valueOf(sTotal);

        sessionManager = new SessionManager(this);

        enama = (EditText) findViewById(R.id.namarek);
        enomor = (EditText) findViewById(R.id.nomorek);
        ejumlah = (EditText) findViewById(R.id.jumlahrek);
        bkon = (Button) findViewById(R.id.buttonkon);
        bkon.setEnabled(false);

        ejumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sInputan = ejumlah.getText().toString().trim();
                if (!android.text.TextUtils.isEmpty(sInputan)) {
                    Long input = Long.valueOf(sInputan);
                    if (input >= total)
                        bkon.setEnabled(true);
                    else
                        bkon.setEnabled(false);
                }
            }

        });

        bkon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sNama = enama.getText().toString().trim();
                String sNomor = enomor.getText().toString().trim();
                String sJumlah = ejumlah.getText().toString().trim();

                if (TextUtils.isEmpty(sNama) || TextUtils.isEmpty(sNomor) || TextUtils.isEmpty(sJumlah)) {
                    Toast.makeText(KonfirmasiBayar.this, "Masih ada yang kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    konfirmasi(sessionManager.getIdUser(), sNama, sNomor, sJumlah);
                }
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");

    }

    public void clear() {
        enama.setText(null);
        enomor.setText(null);
        ejumlah.setText(null);
    }

    public void konfirmasi(String iduser, String nama, String noRek, String jumlah) {
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_transaksi", id_trans);
        params.add("id_member", iduser);
        params.add("nama", nama);
        params.add("no_rek", noRek);
        params.add("jumlah", jumlah);
        params.add("judul_pesan", "Pemberitahuan");
        params.add("isi_pesan", nama + " mentransfer sebesar Rp." + jumlah);
        client.post(Config.URL_BASE_API + "/konfirmasi_bayar.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                try {
                    String status = response.getString("status");

                    if (status.equalsIgnoreCase("berhasil")) {
                        Toast.makeText(KonfirmasiBayar.this, "Anda sudah melakukan konfirmasi " +
                                "pembayaran. Pesanan anda akan segera kami proses", Toast.LENGTH_SHORT).show();
                        clear();
                    } else {
                        String errors = response.getString("error");
                        if (errors.contains("Duplicate")) {
                            Toast.makeText(KonfirmasiBayar.this, "Gagal! Anda sudah melakukan konfirmasi!", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                //super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("TAG", "error pembayaran : " + responseString);
                Toast.makeText(KonfirmasiBayar.this, "Cek koneksi internet!", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        });

    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(KonfirmasiBayar.this);
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
