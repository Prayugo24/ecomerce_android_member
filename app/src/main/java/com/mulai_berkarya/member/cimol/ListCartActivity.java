package com.mulai_berkarya.member.cimol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.adapter.ListAdapterCart;
import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

public class ListCartActivity extends AppCompatActivity {
    static String id_transaksi;
    Button co1;
    String tag = "listbeli";
    ListView lvcart;
    Context context = this;
    SessionManager session;
    String urldel = URL_BASE_API + "/hapus_cart.php?id_member=";
    String url = URL_BASE_API + "/daftar_cart.php?id_member=";
    String urlDaftarCart = URL_BASE_API + "/daftar_cart.php";
    String urldelCart = URL_BASE_API + "/hapus_cart.php";
    int berat = 0, totalBarang = 0;
    ProgressDialog pd;
    private String[] idbarang;
    private String[] namabarang;
    private String[] jumlah;
    private String[] qty;
    private String[] gambar;
    private String listcart, idbrg, id_member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
//        alert
        alertDialogTrial();

        co1 = (Button) findViewById(R.id.bco);
        lvcart = (ListView) findViewById(R.id.lvcart);
        session = new SessionManager(getApplicationContext());

        pd = new ProgressDialog(ListCartActivity.this);
        pd.setMessage("Loading");

        //session.checkLogin();
        if (!session.isLoggedIn()) {
            Intent i = new Intent(this, LoginAct.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
            this.finish();
        }

        if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();
            id_member = user.get(SessionManager.KEY_ID);
            //new GetData().execute(url);
            getData(id_member);
        }

        co1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("berat_total", String.valueOf(berat));
                b.putString("total_barang", String.valueOf(totalBarang));
                Intent in = new Intent(getBaseContext(), OngkirAct.class);
                in.putExtras(b);
                startActivity(in);
                finish();
                MainActivity.setNotifCount(0);
            }
        });

        lvcart.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, final int position, long id) {
                AlertDialog.Builder adib = new AlertDialog.Builder(context);

                adib.setTitle("Konfirmasi");

                adib
                        .setMessage("Apakah yakin anda mau menghapusnya?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                idbrg = idbarang[position].toString();

                                hapusCart(id_member, idbrg);
                                getData(id_member);

                            }
                        })
                        .setNegativeButton("Tidak", null);
                AlertDialog aldi = adib.create();
                aldi.show();
            }
        })
        ;

    }

    private void hapusCart(final String idMember, String idbrg) {
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_member", idMember);
        params.add("id", idbrg);
        client.get(urldelCart, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pd.dismiss();
                try {
                    String status = response.getString("status");
                    if (status.equalsIgnoreCase("berhasil")) {
                        Toast.makeText(ListCartActivity.this, "Barang berhasil di hapus!", Toast.LENGTH_SHORT).show();
                        getData(idMember);
                    } else {
                        String error = response.getString("error");
                        Toast.makeText(ListCartActivity.this, "Barang gagal di hapus!" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
                throwable.printStackTrace();
                Log.w("TAG", "onFailure: " + responseString);
                Toast.makeText(ListCartActivity.this, "Terjadi kesalahan. Coba lagi nanti!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getData(String idMember) {
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_member", idMember);
        client.get(urlDaftarCart, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObj) {
                pd.dismiss();


                try {
                    berat = jsonObj.getInt("berat_total");
                    JSONArray response = jsonObj.getJSONArray("cart");


                    if(response.length() <=0)  {
                        Toast.makeText(ListCartActivity.this,"Tidak ada barang yang di beli!", Toast.LENGTH_SHORT).show();
                    }else {
                        idbarang = new String[response.length()];
                        namabarang = new String[response.length()];
                        jumlah = new String[response.length()];
                        qty = new String[response.length()];
                        //	berat = new String[jarray.length()];
                        gambar = new String[response.length()];

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            idbarang[i] = obj.getString("id").toString();
                            namabarang[i] = obj.getString("name").toString();
                            jumlah[i] = obj.getString("jumlah").toString();
                            qty[i] = obj.getString("qty").toString();
                            //		berat[i] = jarray.getJSONObject(i).getString("berat")toString();
                            gambar[i] = Config.URL_BASE + "/Gambar/" + obj.getString("gambar").toString();
                            id_transaksi = obj.getString("id_transaksi").toString();

                            //berat += obj.getInt("berat");
                            totalBarang += obj.getInt("harga_barang") * obj.getInt("qty");
                        }

                        ListAdapterCart adapter = new ListAdapterCart(getBaseContext(), namabarang,
                                jumlah, qty, gambar);
                        lvcart.setAdapter(adapter);
                    }

                    //set notif
                    MainActivity.setNotifCount(response.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
                throwable.printStackTrace();
                Log.w("TAG", "onFailure: " + responseString);
            }
        });

    }
    //    alert dialog trial
    public void alertDialogTrial(){
        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(ListCartActivity.this);
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
