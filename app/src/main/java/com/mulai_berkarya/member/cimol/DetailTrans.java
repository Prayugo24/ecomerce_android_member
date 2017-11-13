package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.adapter.AdapterDetail;
import com.mulai_berkarya.member.adapter.AdapterOrder;
import com.mulai_berkarya.member.entitas.Detail;
import com.mulai_berkarya.member.entitas.Order;
import com.mulai_berkarya.member.server.JSONParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

/**
 * Created by Mastah on 28/09/2016.
 */


public class DetailTrans extends AppCompatActivity{
    String tag = "listdet";
    Button bresi, bcek;
    ListView lvdetail;
    TextView tidtrans, ttanggal, tongkir, ttotal, tresi, tjumlah, talamat,
            tkec, tkab, tprov, tkode, ttelp, tnama, tkurir;
    Context context = this;
    SessionManager session;
    String url= URL_BASE_API + "/detail_admin.php?id_transaksi=";
    String url2= URL_BASE_API + "/transaksi_detail_member.php?id_transaksi=";
    ProgressDialog progress;
    JSONArray jarray;
    private String idtrans;
    ArrayList<Detail>details;
    ArrayList<Order> arrOrder;
    ProgressBar progressBar;
    String id, name, qty, jumlah, ket, gambar, total;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_trans);
//        alert
        alertDialogTrial();

        if (android.os.Build.VERSION.SDK_INT > 14) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        details = new ArrayList<>();
        arrOrder = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        idtrans = bundle.getString("id_transaksi");
        total = bundle.getString("total");

        lvdetail = (ListView) findViewById(R.id.listviewdetail22);
        lvdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lvdetail.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        progress = new ProgressDialog(this);
        view();
        loadetail();
        bresi = (Button) findViewById(R.id.buttokonb);
        bresi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(), KonfirmasiBayar.class);
                i.putExtra("id_transaksi", idtrans);
                i.putExtra("total", total);
                startActivity(i);

            }
        });
        bcek = (Button) findViewById(R.id.btncek);
        bcek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CekresiAct.class);
                i.putExtra("no_resi", tresi.getText().toString());
                startActivity(i);
            }
        });


    }

    private void view() {
        final ProgressDialog pDialog = ProgressDialog.show(this, null, "Please wait....", true);
        pDialog.setCancelable(true);

        tidtrans = (TextView)findViewById(R.id.textViewidtransaksi22);
        ttanggal = (TextView)findViewById(R.id.textviewtgl_trans22);
        tongkir = (TextView)findViewById(R.id.textViewongkir22);
        ttotal = (TextView)findViewById(R.id.textViewttl22);
        tresi = (TextView)findViewById(R.id.textviResi22);
        tjumlah = (TextView)findViewById(R.id.textViewsubt22);
        talamat = (TextView)findViewById(R.id.textViewAlamat2);
        tkec = (TextView)findViewById(R.id.textviewkec2);
        tkab = (TextView)findViewById(R.id.textViewkab2);
        tprov = (TextView)findViewById(R.id.textViewprof2);
        tkode = (TextView)findViewById(R.id.textViewkodepos2);
        ttelp = (TextView)findViewById(R.id.textViewtlp2);
        tnama = (TextView)findViewById(R.id.textatasnama2);
        tkurir = (TextView)findViewById(R.id.textViewkurir);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //params.add();
        client.get(url2+idtrans, null , new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                pDialog.dismiss();
                Log.e("TAG", response.toString());
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject obj = parser.getJSONFromUrl(url2+idtrans);
                    jarray = obj.getJSONArray("transaksi_detail");
                    for (int i = 0; i < jarray.length(); i++){
                        JSONObject c = jarray.getJSONObject(i);
                        Detail detail = new Detail();
                        tidtrans.setText(c.getString("id_transaksi"));
                        ttanggal.setText(c.getString("tgl_transaksi"));
                        tongkir.setText(c.getString("ongkir"));
                        ttotal.setText(c.getString("total"));
                        tresi.setText(c.getString("no_resi"));
                        tjumlah.setText(c.getString("jumlah"));
                        talamat.setText(c.getString("alamat"));
                        tkode.setText(c.getString("kode_pos"));
                        tkec.setText(c.getString("kecamatan"));
                        tprov.setText(c.getString("provinsi"));
                        tkab.setText(c.getString("kab_kot"));
                        ttelp.setText("Telp. "+c.getString("telepon"));
                        tnama.setText(c.getString("atas_nama"));
                        tkurir.setText(c.getString("agen_pengiriman"));
                        details.add(detail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AdapterDetail adapter = new AdapterDetail(DetailTrans.this ,
                        R.layout.activity_listbarang_item, details);
                //listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                throwable.printStackTrace();
                Toast.makeText(getBaseContext(), "Cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadetail(){
        final ProgressDialog pDialog = ProgressDialog.show(this, null, "Please wait....", true);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_transaksi", idtrans);
        client.get(url+idtrans, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                pDialog.dismiss();
                Log.e("TAG", response.toString());
                try{
                    JSONArray arr = response.getJSONArray("detail");
                    for(int i=0; i<arr.length(); i++){
                        JSONObject obj = arr.getJSONObject(i);
                        Order d = new Order();
                        d.setIdbrg(obj.getString("id"));
                        d.setNamabrg(obj.getString("name"));
                        d.setHargabrg(obj.getString("jumlah"));
                        d.setQty(obj.getString("qty"));
                        d.setKet(obj.getString("ket"));
                        d.setGambar(obj.getString("gambar"));
                        arrOrder.add(d);
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }
                AdapterOrder adapter = new AdapterOrder(DetailTrans.this, R.layout.detail_pemesanan, arrOrder);
                lvdetail.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                throwable.printStackTrace();

            }
        });
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(DetailTrans.this);
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
