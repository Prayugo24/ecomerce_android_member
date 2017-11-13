package com.mulai_berkarya.member.cimol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mulai_berkarya.member.adapter.AdapterBarang;
import com.mulai_berkarya.member.entitas.Barang;
import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PopulerFragment extends Fragment {
    //static ArrayList<Barang> listBarang = new ArrayList<Barang>();
    static ArrayList<Barang> listBarang;
    ProgressBar progressBar;
    ListView listView;
    GridView gridView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fabBarang;
    boolean isGrid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_daftarbarang, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        listView = (ListView) rootView.findViewById(R.id.lvbarang);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        gridView = (GridView) rootView.findViewById(R.id.gvbarang);
        fabBarang = (FloatingActionButton) rootView.findViewById(R.id.barang_fab);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listBarang = new ArrayList<Barang>();
        Glide
                .with(getActivity())
                .load(R.drawable.ic_grid)
                .fitCenter()
                .crossFade()
                .into(fabBarang);

        loadData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Barang bb = listBarang.get(i);
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
                //b.setid_barang(bb.getid_barang());
                //b.setnama_barang(bb.getnama_barang());
                //b.setdeskripsi(bb.getdeskripsi());
                //b.setharga(bb.getharga());
                //b.setgambar(bb.getgambar());
                //b.setberat(bb.getberat());

                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailbrgActivity2.class);
                intent.putExtra("posisi", i);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Barang bb = listBarang.get(i);
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

                //b.setid_barang(bb.getid_barang());
                //b.setnama_barang(bb.getnama_barang());
                //b.setdeskripsi(bb.getdeskripsi());
                //b.setharga(bb.getharga());
                //b.setgambar(bb.getgambar());
                //b.setberat(bb.getberat());

                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailbrgActivity2.class);
                intent.putExtra("posisi", i);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
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
                        loadData();
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
                    boolean firstItemVisible = gridView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = gridView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });

    }

    private void gantiTampilan(boolean apakahGrid) {
        if (apakahGrid) {
            if (getActivity() != null) {
                AdapterBarang adapter = new AdapterBarang(getActivity(), R.layout.activity_listbarang_item_gv, listBarang);
                gridView.setAdapter(adapter);
                gridView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                Glide
                        .with(getActivity())
                        .load(R.drawable.ic_list)
                        .fitCenter()
                        .crossFade()
                        .into(fabBarang);
                isGrid = true;
            } else {
                PopulerFragment.super.onAttach(getContext());
            }
        } else {
            if (getActivity() != null) {
                AdapterBarang adapter = new AdapterBarang(getActivity(), R.layout.activity_listbarang_item, listBarang);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
                Glide
                        .with(getActivity())
                        .load(R.drawable.ic_grid)
                        .fitCenter()
                        .crossFade()
                        .into(fabBarang);
                isGrid = false;
            } else {
                PopulerFragment.super.onAttach(getContext());
            }
        }

    }

    private void loadData() {
        fabBarang.setEnabled(false);
        swipeRefreshLayout.setEnabled(false);
        listBarang.clear();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Config.URL_BASE_API + "/daftar_barang_populer.php", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //                super.onSuccess(statusCode, headers, response);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
                fabBarang.setEnabled(true);

                Log.e("TAG", response.toString());
                progressBar.setVisibility(View.GONE);
                JSONArray arrBarang = null;
                try {
                    arrBarang = response.getJSONArray("barang");
                    for (int i = 0; i < arrBarang.length(); i++) {
                        JSONObject obj = arrBarang.getJSONObject(i);
                        Barang b = new Barang();
                        b.setid_barang(obj.getString("id"));
                        b.setgambar(obj.getString("gambar"));
                        b.setberat(obj.getString("berat"));
                        b.setStok(obj.getString("stok"));
                        b.setharga(obj.getString("harga_barang"));
                        b.setdeskripsi(obj.getString("deskripsi"));
                        b.setnama_barang(obj.getString("name"));
                        b.setDiskonPersen(obj.getString("diskon"));
                        //b.setHargaDiskon(obj.getString("harga_diskon"));
                        b.setHargaDiskon(String.valueOf(obj.getInt("harga_diskon")));
                        b.setTerjual(obj.getString("terjual"));
                        b.setRate(obj.getString("rating"));
                        listBarang.add(b);
                    }
                    if (getActivity() != null) {
                        AdapterBarang adapter = new AdapterBarang(getActivity(), R.layout.activity_listbarang_item, listBarang);
                        listView.setAdapter(adapter);
                        gridView.setAdapter(adapter);
                    } else {
                        PopulerFragment.super.onAttach(getContext());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
                fabBarang.setEnabled(true);

                throwable.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
                fabBarang.setEnabled(true);

                throwable.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Cek koneksi internet anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}