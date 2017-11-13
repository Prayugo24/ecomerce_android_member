package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.entitas.Category;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

public class CategoryActivity extends AppCompatActivity {
    ListView lvbrg;
    String que;
    String urlbycat = URL_BASE_API + "/barang_cat.php?category=";
    String URL_CAT = URL_BASE_API + "/barang_cat.php";
    String url = URL_BASE_API + "/daftar_barang.php";
    String scategory;
    ProgressDialog pd;
    JSONObject jsonobject;
    JSONArray jsonarray;
    ArrayList<String> catlist, idList;
    ArrayList<Category> category;
    ProgressBar progressBar;
    FloatingActionButton fabTambah;
    AlertDialog.Builder builder;
    private String[] idbarang;
    private String[] namabarang;
    private String[] hargabarang;
    private String[] desk;
    private String[] berat;
    private String[] gambar;
    private String listbar;
    private String URLCAT = URL_BASE_API + "/get_cat.php?id_category=";
    Context context = this;
    private SwipeRefreshLayout swipeRefreshLayout;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
//        alert
        alertDialogTrial();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // fabTambah = (FloatingActionButton) findViewById(R.id.kategori_fabTambah);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        catlist = new ArrayList<String>();
        idList = new ArrayList<String>();
        lvbrg = (ListView) findViewById(R.id.listviewnyaa);
        lvbrg.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, final int position, long id) {

                Intent i = new Intent(CategoryActivity.this, BarangPerKategori.class);
                i.putExtra("id_kategori", idList.get(position));
                startActivity(i);
                //Toast.makeText(CategoryActivity.this, "NAMA : " + catlist.get(position) + "\n ID : " + idList.get(position),Toast.LENGTH_SHORT).show();

            }
        });
        builder = new AlertDialog.Builder(CategoryActivity.this);

        getAllKategori();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //                loadData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAllKategori();
                    }
                }, 2500);
            }
        });

        lvbrg.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                boolean enable = false;
                if (lvbrg != null && lvbrg.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lvbrg.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lvbrg.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }

    private void getAllKategori() {
        if (idList.size() > 0 || catlist.size() > 0) {
            idList.clear();
            catlist.clear();
        }
        swipeRefreshLayout.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URLCAT, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    swipeRefreshLayout.setEnabled(true);
                    swipeRefreshLayout.setRefreshing(false);
                    JSONArray arr = response.getJSONArray("category");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String nama = obj.getString("category");
                        String idnya = obj.getString("id_category");
                        catlist.add(nama);
                        idList.add(idnya);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategoryActivity.this, android.R.layout.simple_list_item_1, catlist);
                lvbrg.setAdapter(adapter);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                super.onFailure(statusCode, headers, responseString, throwable);
                throwable.printStackTrace();
                Toast.makeText(CategoryActivity.this, "Cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.GONE);
                throwable.printStackTrace();
                Toast.makeText(CategoryActivity.this, "Cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(CategoryActivity.this);
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
