package com.mulai_berkarya.member.cimol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ClassTambahKomentar extends AppCompatActivity implements OnClickListener {

    //private static final String LINK_TAMBAH_KOMENTAR = "http://192.168.1.12/cimol-web/cimol-web/tambah_komentar.php";
    private static final String LINK_TAMBAH_KOMENTAR = Config.URL_BASE_API + "/tambah_komentar2.php";
    private static final String TAG_BERHASIL = "sukses";
    private static final String TAG_PESAN = "pesan";
    // utk tambah komentar perlu deklarasi class JSON
    String idBarang = "";
    String idUser = "";
    RatingBar ratingBar;
    String ratingnya;
    private EditText isiJudul, isiKomentar;
    private Button kirimKomentar;
    // utk progress bar_nya
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_komentar);
//        alert
        alertDialogTrial();

        isiJudul = (EditText) findViewById(R.id.judulKomentar);
        isiKomentar = (EditText) findViewById(R.id.isiKomentar);

        kirimKomentar = (Button) findViewById(R.id.kirimKomentar);
        kirimKomentar.setOnClickListener(this);

        idBarang = getIntent().getStringExtra("id");
        SessionManager session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        idUser = user.get(SessionManager.KEY_ID);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        pDialog = ProgressDialog.show(this, null, "Loading....", true);
        pDialog.setCancelable(true);
        pDialog.dismiss();

    }

    @Override
    public void onClick(View v) {
        //new TambahKomentar().execute();
        //idBarang
        //idUser
        String judul = isiJudul.getText().toString().trim();
        String isi = isiKomentar.getText().toString().trim();
        //Float rate = ratingBar.getRating();
        Float rate = 4.0f;

        if (TextUtils.isEmpty(judul) || TextUtils.isEmpty(isi) || rate <= 0) {
            Toast.makeText(this, "Masih ada yang kosong", Toast.LENGTH_SHORT).show();
        } else {
            tambahKomentar(idUser, idBarang, judul, isi, rate);
        }
    }

    public void tambahKomentar(String idUser, String idBarang, String judul, String isi, Float rate) {
        pDialog.show();
        Log.e("TAG", "idUser : " + idUser + "\nidBarang : " + idBarang + "\nJudul : " + judul + "\nIsi : " + isi + "\nRate:" + rate);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_member", idUser);
        params.add("id", idBarang);
        params.add("judul", judul);
        params.add("isi", isi);
        params.add("rate", String.valueOf(rate));
        client.post(LINK_TAMBAH_KOMENTAR, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pDialog.dismiss();
                try {
                    String status = response.getString("status");
                    if (status.equals("berhasil")) {
                        //finish();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                    Toast.makeText(ClassTambahKomentar.this, "Berhasil mengirim ulasan", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pDialog.dismiss();
                Log.e("TAG", responseString);
                throwable.printStackTrace();
                Toast.makeText(ClassTambahKomentar.this, "Cek koneksi internet anda", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ClassTambahKomentar.this);
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