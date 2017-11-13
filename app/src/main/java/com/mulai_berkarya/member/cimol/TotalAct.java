package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.entitas.Ongkir;
import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

public class TotalAct extends AppCompatActivity {
    List<Ongkir> listongkir = new ArrayList<Ongkir>();
    Ongkir ongkir;
    JSONArray jarray;
    Button bok;
    TextView tjumlah, tongkir, ttotal, tNama, tAlamat, tTelp, tAgen, tkec, tkab, tprov;
    ProgressDialog progress;
    SessionManager session;
    String id;
    String url = URL_BASE_API + "/transaksi.php?id_transaksi=";
    String urlok = URL_BASE_API + "/konfirmasi.php?id_member=";

    String nama, alamat, kodepos, telp, totalBarang, agen, kecamatan, kabupaten, provinsi;
    String totalAll, biayaOngkir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total);
//        alert
        alertDialogTrial();

        progress = new ProgressDialog(this);
        progress.setMessage("Loading.....");
        progress.setCancelable(true);

        view();
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        Bundle b = getIntent().getExtras();
        nama = b.getString("nama");
        alamat = b.getString("alamat");
        kecamatan = b.getString("kecamatan");
        kabupaten = b.getString("kab_kot");
        provinsi = b.getString("provinsi");
        kodepos = b.getString("kodepos");
        telp = b.getString("telp");
        totalBarang = b.getString("total_barang");
        biayaOngkir = b.getString("total_ongkir");
        agen = b.getString("agen");
        //totalSemua = String.valueOf(Integer.valueOf(totalBarang) + Integer.valueOf(totalOngkir));
        totalAll = String.valueOf(Integer.valueOf(totalBarang) + Integer.valueOf(biayaOngkir));

        //TextView tjumlah, tongkir, ttotal, tNama, tAlamat, tTelp, tAgen;
        tjumlah.setText(totalBarang);
        tongkir.setText(String.valueOf(biayaOngkir));
        ttotal.setText(totalAll);

        tNama.setText(nama);
        tAlamat.setText(alamat);
        tkec.setText(kecamatan);
        tkab.setText(kabupaten);
        tprov.setText(provinsi);
        tTelp.setText(telp);
        tAgen.setText(agen);

        HashMap<String, String> user = session.getUserDetails();

        //id = user.get(SessionManager.KEY_ID);
        id = ListCartActivity.id_transaksi;
        bok = (Button) findViewById(R.id.btnOk);

        bok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                kirim(session.getIdUser(), nama, alamat, kodepos, telp, agen, biayaOngkir, totalAll, kecamatan, kabupaten, provinsi);
            }
        });
    }

    private void kirim(String idUser, String nama, String alamat, String kodepos, String telp,
                       String agen, String biayaOngkir, final String totalAll, String kecamatan, String kabupaten, String provinsi) {
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_member", idUser);
        params.add("atas_nama", nama);
        params.add("alamat", alamat);
        params.add("kecamatan", kecamatan);
        params.add("kab_kot", kabupaten);
        params.add("provinsi", provinsi);
        params.add("kode_pos", kodepos);
        params.add("telepon", telp);
        params.add("agen", agen);
        params.add("ongkir", biayaOngkir);
        params.add("total", totalAll);
        params.add("judul_pesan", "Pemberitahuan");
        params.add("isi_pesan", "Pesanan Baru...");

        client.post(Config.URL_BASE_API + "/checkout.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.dismiss();
                Toast.makeText(TotalAct.this, "Berhasil!!!", Toast.LENGTH_SHORT).show();
                try {
                    String status = response.getString("status");
                    if(status.equalsIgnoreCase("berhasil")){
                        Toast.makeText(TotalAct.this, "Berhasil!! Segera transfer uang dan lakukan " +
                                "konfirmasi pembayaran!!!", Toast.LENGTH_SHORT).show();
                        Bundle b = new Bundle();
                        b.putString("total", totalAll);
                        Intent i = new Intent(TotalAct.this , InfoRekening.class);
                        i.putExtras(b);
                        startActivity(i);
                    }else{
                        Toast.makeText(TotalAct.this, "Cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progress.dismiss();
                throwable.printStackTrace();
                Toast.makeText(TotalAct.this, "Cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                Log.e("TAG","Total : " + responseString);


            }
        });
    }

    private void view() {
        //tNama, tAlamat, tTelp, tAgen;
        tjumlah = (TextView) findViewById(R.id.textJumlah);
        tongkir = (TextView) findViewById(R.id.textOngkir);
        ttotal = (TextView) findViewById(R.id.textTotal);
        tkec = (TextView) findViewById(R.id.total_kecamatan);
        tkab = (TextView) findViewById(R.id.total_kabupaten);
        tprov = (TextView) findViewById(R.id.total_prov);
        tNama = (TextView) findViewById(R.id.total_atasNama);
        tAlamat = (TextView) findViewById(R.id.total_alamat);
        tTelp = (TextView) findViewById(R.id.total_telp);
        tAgen = (TextView) findViewById(R.id.total_agen);
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(TotalAct.this);
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
