package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class KonfirmasiBarang extends AppCompatActivity {

    TextView tvTanggal, tvAtasNama, tvTotal, tvTujuan, tvStatus;
    Button btnSudah, btnBelum;
    SessionManager sessionManager;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.konfirmasi_barang);
//        alert
        alertDialogTrial();

        tvTanggal = (TextView) findViewById(R.id.konf_brg_tanggal);
        tvAtasNama = (TextView) findViewById(R.id.konf_brg_atasNama);
        tvTotal = (TextView) findViewById(R.id.konf_brg_total);
        tvTujuan = (TextView) findViewById(R.id.konf_brg_tujuanKirim);
        tvStatus = (TextView) findViewById(R.id.konf_brg_status);
        btnSudah = (Button) findViewById(R.id.konf_brg_btnSudah);

        sessionManager = new SessionManager(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

        loadData(sessionManager.getIdUser());

        btnSudah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                konfirmasiPenerimaan("sudah", sessionManager.getIdUser());
            }
        });
    }

    public void konfirmasiPenerimaan(String ket, String idUser) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id", idUser);
        params.add("ket", ket);
        client.get(Config.URL_BASE_API + "/konfirmasi_barang.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pDialog.dismiss();
                try {
                    String status = response.getString("status");
                    if (status.equalsIgnoreCase("berhasil")) {
                        Toast.makeText(KonfirmasiBarang.this, "Terimah kasih sudah mengkonfirmasi", Toast.LENGTH_SHORT).show();
                        loadData(sessionManager.getIdUser());
                    } else {
                        Toast.makeText(KonfirmasiBarang.this, "Akan segera kami proses", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pDialog.dismiss();
                Toast.makeText(KonfirmasiBarang.this, "Cek koneksi internet!", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        });

    }


    private void loadData(String idUser) {
        pDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_user", idUser);
        client.get(Config.URL_BASE_API + "/trans_detail.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                pDialog.dismiss();
                if(response.length() >0) {
                    try {
                        JSONObject obj = response.getJSONObject(0);
                        String idTransaksi = obj.getString("id_transaksi");
                        String tanggalTransaksi = obj.getString("tgl_transaksi");
                        String atasNama = obj.getString("atas_nama");
                        String total = obj.getString("total");
                        String tujuan = obj.getString("alamat");
                        String konfirmasiBarang = obj.getString("konfirmasi_barang");

                        tvTujuan.setText(tujuan);
                        tvTotal.setText("Rp. " + total);
                        tvAtasNama.setText(atasNama);
                        tvTanggal.setText(dateFormatter(tanggalTransaksi));

                        if (konfirmasiBarang.equalsIgnoreCase("sudah")) {
                            tvStatus.setText("Sudah di terima");
                      //      btnBelum.setEnabled(false);
                            btnSudah.setEnabled(false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                     //   btnBelum.setEnabled(false);
                        btnSudah.setEnabled(false);
                    }
                }else {
                    //btnBelum.setEnabled(false);
                    btnSudah.setEnabled(false);
                    Toast.makeText(KonfirmasiBarang.this, "Tidak ada transaksi!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pDialog.dismiss();
                Toast.makeText(KonfirmasiBarang.this, "Cek koneksi internet!", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            //    btnBelum.setEnabled(false);
                btnSudah.setEnabled(false);
            }
        });

    }

    public String dateFormatter(String tanggal) {
        String dates = "";
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = format1.parse(tanggal);

            //SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy");
            dates = format2.format(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dates;
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(KonfirmasiBarang.this);
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
