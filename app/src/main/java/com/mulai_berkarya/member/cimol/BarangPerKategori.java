package com.mulai_berkarya.member.cimol;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mulai_berkarya.member.adapter.AdapterBarang;
import com.mulai_berkarya.member.entitas.Barang;
import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BarangPerKategori extends AppCompatActivity {
    static ArrayList<Barang> listBarang;
    ProgressBar progressBar;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fabBarang;
    GridView gridView;
    boolean isGrid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftarbarang);
        listView = (ListView) findViewById(R.id.lvbarang);
//        alert
        alertDialogTrial();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        fabBarang = (FloatingActionButton) findViewById(R.id.barang_fab);
        gridView = (GridView) findViewById(R.id.gvbarang);

        listBarang = new ArrayList<>();
        final String idKategori = getIntent().getStringExtra("id_kategori");
        if (null != idKategori) loadData(idKategori);
        else Toast.makeText(this, "Gagal meload data", Toast.LENGTH_SHORT).show();

        Glide
                .with(this)
                .load(R.drawable.ic_grid)
                .fitCenter()
                .crossFade()
                .into(fabBarang);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Barang bb = listBarang.get(i);
                Barang b = new Barang();
                Bundle bundle = new Bundle();
                bundle.putString("id", bb.getid_barang());
                bundle.putString("nama", bb.getnama_barang());
                bundle.putString("deskripsi", bb.getdeskripsi());
                bundle.putString("berat", bb.getberat());
                bundle.putString("stok", bb.getStok());
                bundle.putString("harga", bb.getharga());
                bundle.putString("harga_diskon", bb.getHargaDiskon());
                bundle.putString("gambar", bb.getgambar());
                bundle.putString("terjual", bb.getTerjual());
                bundle.putString("rating", bb.getRate());

                Intent intent = new Intent();
                intent.setClass(BarangPerKategori.this, DetailbrgActivity2.class);
                intent.putExtra("posisi", i);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Barang bb = listBarang.get(i);
                Barang b = new Barang();
                Bundle bundle = new Bundle();
                bundle.putString("id", bb.getid_barang());
                bundle.putString("nama", bb.getnama_barang());
                bundle.putString("deskripsi", bb.getdeskripsi());
                bundle.putString("berat", bb.getberat());
                bundle.putString("stok", bb.getStok());
                bundle.putString("harga", bb.getharga());
                bundle.putString("harga_diskon", bb.getHargaDiskon());
                bundle.putString("gambar", bb.getgambar());
                bundle.putString("terjual", bb.getTerjual());
                bundle.putString("rating", bb.getRate());

                Intent intent = new Intent();
                intent.setClass(BarangPerKategori.this, DetailbrgActivity2.class);
                intent.putExtra("posisi", i);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        fabBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listBarang.isEmpty()) {
                    gantiTampilan(!isGrid);
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                loadData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData(idKategori);
//                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                boolean enable = false;
                if (gridView != null && gridView.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = gridView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = gridView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }


    private void loadData(String id_kategori) {
        swipeRefreshLayout.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        listBarang.clear();
        fabBarang.setEnabled(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_kategori", id_kategori);
        //Log.e("TAG", "id_kategori : " + id_kategori);
        //client.get(Config.URL_BASE_API + "/barang_cat.php?id_category=" + id_kategori, params, new JsonHttpResponseHandler() {
        client.get(Config.URL_BASE_API + "/barang_cat.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int wstatusCode, Header[] headers, JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
                fabBarang.setEnabled(true);
                //Log.e("TAG", response.toString());
                progressBar.setVisibility(View.GONE);
                JSONArray arrBarang = null;
                try {
                    arrBarang = response.getJSONArray("kategori");
                    for (int i = 0; i < arrBarang.length(); i++) {
                        JSONObject obj = arrBarang.getJSONObject(i);
                        Barang b = new Barang();
                        //          Toast.makeText(BarangPerKategori.this, "ID :  " + obj.getString("id"), Toast.LENGTH_SHORT).show();
                        b.setid_barang(obj.getString("id"));
                        b.setgambar(obj.getString("gambar"));
                        b.setberat(obj.getString("berat"));
                        b.setharga(obj.getString("harga_barang"));
                        b.setStok(obj.getString("stok"));
                        b.setdeskripsi(obj.getString("deskripsi"));
                        b.setnama_barang(obj.getString("name"));
                        b.setTerjual(obj.getString("terjual"));


                        //int hargaAsli = obj.getInt("harga_barang");
                        //int hargaDiskon = obj.getInt("harga_diskon");
                        //if (hargaAsli != hargaDiskon)
                        b.setHargaDiskon(obj.getString("harga_diskon"));
                        listBarang.add(b);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (listBarang.size() <= 0)
                    Toast.makeText(BarangPerKategori.this, "Tidak ada barang", Toast.LENGTH_SHORT).show();

                AdapterBarang adapter = new AdapterBarang(BarangPerKategori.this, R.layout.activity_listbarang_item, listBarang);
                listView.setAdapter(adapter);
                gridView.setAdapter(adapter);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
                fabBarang.setEnabled(true);

                throwable.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BarangPerKategori.this, "Cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
                fabBarang.setEnabled(true);

                throwable.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BarangPerKategori.this, "Cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void gantiTampilan(boolean apakahGrid) {
        if (apakahGrid) {
            AdapterBarang adapter = new AdapterBarang(this, R.layout.activity_listbarang_item_gv, listBarang);
            gridView.setAdapter(adapter);
            gridView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            Glide
                    .with(this)
                    .load(R.drawable.ic_list)
                    .fitCenter()
                    .crossFade()
                    .into(fabBarang);
            isGrid = true;
        } else {
            AdapterBarang adapter = new AdapterBarang(BarangPerKategori.this, R.layout.activity_listbarang_item, listBarang);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            Glide
                    .with(this)
                    .load(R.drawable.ic_grid)
                    .fitCenter()
                    .crossFade()
                    .into(fabBarang);
            isGrid = false;
        }
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(BarangPerKategori.this);
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
