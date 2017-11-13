package com.mulai_berkarya.member.cimol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.adapter.AdapterOngkir;
import com.mulai_berkarya.member.entitas.Ongkir;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class OngkirBayarAct extends AppCompatActivity {

    //final String KEY_RAJAONGKIR = "89e21c58e447b2c15c12ea99356fc229";
    final String KEY_RAJAONGKIR = "644b2c9b5d04a0c3d543a9b01a462397";
    //final String URL_RAJAONGKIR = "http://api.rajaongkir.com/starter/cost";
    final String URL_RAJAONGKIR = "http://pro.rajaongkir.com/api/cost";
    ListView listView;
    RadioGroup radioGroup;
    RadioButton rbJne, rbTiki, rbPos;
    String atasNama, alamat, kodepos, telp, kabId, kecId, berat, totalBarang, kecamatan, kabupaten, provinsi;
    ProgressBar progressBar;
    ArrayList<Ongkir> ongkirs;
    SessionManager session;
    TextView agenKet;
    String agenPengirim;
    private String destinationType = "subdistrict"; //city --> subdistrict --> kecamatan ++++  city --> kabupaten
    boolean statusLoadKab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bayar_ongkir);
//        alert
        alertDialogTrial();

        ongkirs = new ArrayList<>();
        session = new SessionManager(this);

        listView = (ListView) findViewById(R.id.bayar_list);
        radioGroup = (RadioGroup) findViewById(R.id.bayar_rg);
        rbJne = (RadioButton) findViewById(R.id.bayar_rbJNE);
        rbPos = (RadioButton) findViewById(R.id.bayar_rbPOS);
        rbTiki = (RadioButton) findViewById(R.id.bayar_rbtiki);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        agenKet = (TextView) findViewById(R.id.bayar_agenKet);

        Bundle b = getIntent().getExtras();
        atasNama = b.getString("nama");
        alamat = b.getString("alamat");
        kodepos = b.getString("kodepos");
        telp = b.getString("telp");
        kabId = b.getString("id_kab");
        kecId = b.getString("id_kec");
        berat = b.getString("berat_total");
        kecamatan = b.getString("kecamatan");
        kabupaten = b.getString("kab_kot");
        provinsi = b.getString("provinsi");
        totalBarang = b.getString("total_barang");

        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(selectedId);
        agenPengirim = rb.getText().toString();
        biayaOngkir(agenPengirim, kecId, berat);
        Log.e("TAG", "kab id pengirim : " + session.getIdKabAsal());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedId);
                agenKet.setText(rb.getText().toString());

                listView.setAdapter(null);
                biayaOngkir(rb.getText().toString(), kecId, berat);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedId);
                Ongkir o = ongkirs.get(i);
                String desc = rb.getText().toString().toUpperCase() + " - " + o.getDesc();
                String hargaOngkir = o.getHarga();

                Bundle b = new Bundle();
                b.putString("nama", atasNama);
                b.putString("alamat", alamat);
                b.putString("kecamatan", kecamatan);
                b.putString("kab_kot", kabupaten);
                b.putString("provinsi", provinsi);
                b.putString("kodepos", kodepos);
                b.putString("telp", telp);
                b.putString("total_barang", totalBarang);
                b.putString("total_ongkir", hargaOngkir);
                b.putString("agen", desc);


                Intent in = new Intent(OngkirBayarAct.this, TotalAct.class);
                in.putExtras(b);
                startActivity(in);
            }
        });

    }

    private void biayaOngkir(String agen, String idTujuan, String berat) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i("TAG", "agen : " + agen + "\nidTujuan : " + idTujuan + "\nBerar : " + berat + "\nIdAsa: " + session.getIdKabAsal());

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("key", KEY_RAJAONGKIR);
        //params.add("origin", "501"); //yogyakarta
        params.add("origin", session.getIdKabAsal()); //yogyakarta
        params.add("originType", "city"); //kabupaten
        params.add("destination", idTujuan);
        params.add("destinationType", destinationType);
        params.add("weight", berat);
        params.add("courier", agen);

        client.post(URL_RAJAONGKIR, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.GONE);

                if (ongkirs.size() > 0)
                    ongkirs.clear();

                try {
                    JSONObject objRaja = response.getJSONObject("rajaongkir");
                    JSONArray arrResults = objRaja.getJSONArray("results");
                    JSONObject objResults = arrResults.getJSONObject(0);//cuma ada satu
                    //Toast.makeText(MainActivity.this, "Kurir : " + objResults.getString("name"), Toast.LENGTH_SHORT).show();  //bener
                    JSONArray arrCosts = objResults.getJSONArray("costs");

                    if (arrCosts.length() > 0) {
                        for (int i = 0; i < arrCosts.length(); i++) {
                            JSONObject objCosts = arrCosts.getJSONObject(i);
                            String nama = objCosts.getString("service");
                            String desk = objCosts.getString("description");
                            String harga = null, waktu = null;

                            if (!desk.contains("Trucking")) {
                                JSONArray arrCost2 = objCosts.getJSONArray("cost");
                                for (int j = 0; j < arrCost2.length(); j++) {
                                    JSONObject objCost2 = arrCost2.getJSONObject(j);
                                    harga = objCost2.getString("value");
                                    waktu = objCost2.getString("etd");
                                }

                                Ongkir o = new Ongkir();
                                o.setDesc(desk);
                                o.setHarga(harga);
                                o.setWaktu(waktu);
                                o.setNama(nama);
                                ongkirs.add(o);
                            }
                        }
                        AdapterOngkir adapter = new AdapterOngkir(OngkirBayarAct.this, R.layout.adapter_ongkir, ongkirs);
                        listView.setAdapter(adapter);
                        destinationType = "subdistrict";
                        statusLoadKab = false;
                    } else {
                        if (!statusLoadKab) {
                            destinationType = "city";
                            biayaOngkir(agenPengirim, kabId, OngkirBayarAct.this.berat);
                            statusLoadKab = true;
                            Log.i("TAG","status : kabupaten");
                        } else {
                            Toast.makeText(OngkirBayarAct.this, "Tidak ada agen pengiriman!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                throwable.printStackTrace();
                Toast.makeText(OngkirBayarAct.this, "Cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.GONE);
                throwable.printStackTrace();
/*
                try {
                    //JSONObject objRaja = errorResponse.getJSONObject("rajaongkir");
                    //JSONObject objStatus = objRaja.getJSONObject("status");
                    //Toast.makeText(OngkirBayarAct.this, objStatus.getString("description"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
*/
                Toast.makeText(OngkirBayarAct.this, "Terjadi kesalahan. Coba lagi nanti!", Toast.LENGTH_SHORT).show();

            }
        });

    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(OngkirBayarAct.this);
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