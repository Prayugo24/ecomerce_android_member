package com.mulai_berkarya.member.cimol;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mulai_berkarya.member.server.Config;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

public class DetailbrgActivity2 extends AppCompatActivity {
    TextView txtNama, txtharga, txtBerat, txtDesk, txtStok, txtHargaDiskon, txtterjual, txtRating;
    ImageView imgBarang, btnSimpan;
    String URL_GAMBAR = Config.URL_BASE + "/Gambar";
    Bitmap bitmaps;
    EditText eqty, eket;
    Button bbeli;
    String url_detr = URL_BASE_API + "/tambah_trans.php";
    SessionManager session;
    String idBarang, id;
    File mediaStorageDir;
    int stok = 0;
    ProgressDialog loading;
    private TableRow tableRow;
    FloatingActionMenu fabKontak, fabUlasan;
    FloatingActionButton fabSMS, fabTelp, fabWA, fabBBM, fabLine;
    RatingBar rate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailbrg);
//        alert
        alertDialogTrial();

        Dexter.initialize(this);

        if (Build.VERSION.SDK_INT > 16) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        session = new SessionManager(getApplicationContext());
        //session.checkLogin();
        loading = new ProgressDialog(this);
        loading.setMessage("Loading....");


        txtNama = (TextView) findViewById(R.id.detail_nama);
        txtharga = (TextView) findViewById(R.id.detail_harga);
        txtBerat = (TextView) findViewById(R.id.detail_berat);
        txtStok = (TextView) findViewById(R.id.detail_stok);
        txtDesk = (TextView) findViewById(R.id.detail_desk);
        txtHargaDiskon = (TextView) findViewById(R.id.detail_hargaDiskon);
        txtterjual = (TextView) findViewById(R.id.jum_jual);
        txtRating = (TextView) findViewById(R.id.detail_rating);

        tableRow = (TableRow) findViewById(R.id.detailbrg_tableRow);

        imgBarang = (ImageView) findViewById(R.id.detail_gambar);
        btnSimpan = (ImageView) findViewById(R.id.detail_btnSimpan);
        eqty = (EditText) findViewById(R.id.editQty);
        eket = (EditText) findViewById(R.id.editKet);
        bbeli = (Button) findViewById(R.id.buttonBeli);
        //btnSms = (Button) findViewById(R.id.buttonSMS);
        //btnTelp = (Button) findViewById(R.id.buttonTelp);

        fabKontak = (FloatingActionMenu) findViewById(R.id.detail_fabKontak);
        fabUlasan = (FloatingActionMenu) findViewById(R.id.detail_fabUlasan);
        fabBBM = (FloatingActionButton) findViewById(R.id.detail_fabBBM);
        fabLine = (FloatingActionButton) findViewById(R.id.detail_fabLine);
        fabWA = (FloatingActionButton) findViewById(R.id.detail_fabWA);
        fabTelp = (FloatingActionButton) findViewById(R.id.detail_fabTelp);
        fabSMS = (FloatingActionButton) findViewById(R.id.detail_fabSMS);


        if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();
            id = user.get(SessionManager.KEY_ID);
        } else {
        }

        //   rate = (RatingBar)findViewById(R.id.ratingBar3);
        /*
        int posisi = getIntent().getIntExtra("posisi", 0);
        Barang barang = TerbaruFragment.listBarang.get(posisi);
        txtNama.setText(barang.getnama_barang());
        txtharga.setText("Rp. " + barang.getharga());
        txtBerat.setText(barang.getberat() + " gram");
        txtStok.setText(barang.getStok() + " ");
        txtDesk.setText(barang.getdeskripsi());
*/

        Bundle bundle = getIntent().getExtras();
        idBarang = bundle.getString("id");
        txtNama.setText(bundle.getString("nama"));
        txtDesk.setText(bundle.getString("deskripsi"));
        txtBerat.setText(bundle.getString("berat") + " gram");
        txtharga.setText("Rp. " + bundle.getString("harga"));
        txtterjual.setText(bundle.getString("terjual"));
        txtStok.setText(bundle.getString("stok"));
        stok = Integer.valueOf(bundle.getString("stok"));
        txtRating.setText(bundle.getString("rating"));


        //   rate.setNumStars(Integer.parseInt(bundle.getString("rating")));
        //  Toast.makeText(DetailbrgActivity2.this, "RATE:"+ bundle.getString("rating"), Toast.LENGTH_SHORT).show();


        //if (null != bundle.getString("harga_diskon") && "0" != bundle.getString("harga_diskon")) {
        if (!bundle.getString("harga_diskon").equalsIgnoreCase("0")) {
            tableRow.setVisibility(View.VISIBLE);
            txtHargaDiskon.setVisibility(View.VISIBLE);
            txtHargaDiskon.setText("Rp. " + bundle.getString("harga_diskon"));
            //txtharga.setPaintFlags(txtharga.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtharga.setPaintFlags(txtharga.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tableRow.setVisibility(View.GONE);
            txtHargaDiskon.setVisibility(View.GONE);
        }

        Glide
                .with(this)
                .load(URL_GAMBAR + "/" + bundle.getString("gambar"))
                .asBitmap()
                .animate(android.R.anim.fade_out)
                .fitCenter()
                .override(600, 400)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
//                .into(imgBarang);
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imgBarang.setImageBitmap(resource);
                        bitmaps = resource;
                    }
                });

        eqty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sInputan = eqty.getText().toString().trim();
                if (!TextUtils.isEmpty(sInputan)) {
                    if (Integer.valueOf(sInputan) > stok || Integer.valueOf(sInputan) <= 0) {
                        Toast.makeText(DetailbrgActivity2.this, "Jumlah produk salah!", Toast.LENGTH_SHORT).show();
                        bbeli.setEnabled(false);
                        bbeli.setClickable(false);
                    } else {
                        bbeli.setClickable(true);
                        bbeli.setEnabled(true);
                    }
                } else {
                    bbeli.setEnabled(false);
                    bbeli.setClickable(false);
                }
            }

        });

        bbeli.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String sQty = eqty.getText().toString().trim();
                if (TextUtils.isEmpty(sQty)) {
                    Toast.makeText(getBaseContext(), "Isikan jumlah barang yang akan dibeli!", Toast.LENGTH_SHORT).show();
                } else {
                    if (session.isLoggedIn()) {
                        beliBarang(id, idBarang, eket.getText().toString().trim(), eqty.getText().toString().trim());

                    } else {
                        Toast.makeText(DetailbrgActivity2.this, "Login dahulu", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(DetailbrgActivity2.this, LoginAct.class);
                        startActivity(i);
                        //DetailbrgActivity2.this.finish();
                    }
                }

            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.show();
                if (storeImage(bitmaps)) {
                    Toast.makeText(DetailbrgActivity2.this, "Gambar berhasil di simpan", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                } else {
                    Toast.makeText(DetailbrgActivity2.this, "Gambar gagal di simpan", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });

        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cimol");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        fabUlasan.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailbrgActivity2.this, FeedbackActivity.class);
                i.putExtra("id", idBarang);
                i.putExtra("id_user", id);
                startActivity(i);
            }
        });


        fabSMS.setOnClickListener(clickListener);
        fabTelp.setOnClickListener(clickListener);
        fabBBM.setOnClickListener(clickListener);
        fabLine.setOnClickListener(clickListener);
        fabWA.setOnClickListener(clickListener);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.detail_fabSMS:
                    if (MainActivity.arrNoHP.size() <= 0) getAllKontak();
                    else dialogBuilder("SMS", MainActivity.arrNoHP);
                    break;
                case R.id.detail_fabTelp:
                    if (MainActivity.arrNoHP.size() <= 0) getAllKontak();
                    else dialogBuilder("TELP", MainActivity.arrNoHP);
                    break;
                case R.id.detail_fabBBM:
                    if (MainActivity.arrBBM.size() <= 0) getAllKontak();
                    else dialogBuilder("BBM", MainActivity.arrBBM);
                    break;
                case R.id.detail_fabWA:
                    if (MainActivity.arrNoHP.size() <= 0) getAllKontak();
                    else dialogBuilder("WA", MainActivity.arrNoHP);
                    break;
                case R.id.detail_fabLine:
                    if (MainActivity.arrLine.size() <= 0) getAllKontak();
                    else dialogBuilder("LINE", MainActivity.arrLine);
                    break;
            }
        }
    };

    private void beliBarang(String id, String idBarang, String ket, String jumlah) {
        loading.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id_member", id);
        params.add("id", idBarang);
        params.add("qty", jumlah);
        params.add("ket", ket);
        client.post(url_detr, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                loading.dismiss();

                try {
                    String status = response.getString("status");
                    if (status.equalsIgnoreCase("berhasil")) {
                        Toast.makeText(getBaseContext(), "Anda membeli " +
                                        txtNama.getText().toString() + " " + eqty.getText().toString() + " buah",
                                Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(DetailbrgActivity2.this, ListCartActivity.class);
                        //	in.putExtra("id_barang", idBarang);
                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(DetailbrgActivity2.this, "Data tidak tersimpan!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(DetailbrgActivity2.this, "Data tidak tersimpan!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                loading.dismiss();
                Log.w("TAG", "onFailure: " + responseString);
                throwable.printStackTrace();
                Toast.makeText(DetailbrgActivity2.this, "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String[] noArr;

    private void dialogBuilder(final String kode, ArrayList<String> listPin) {
        noArr = new String[listPin.size()];
        noArr = listPin.toArray(noArr);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Customer Service");

        switch (kode.toUpperCase()) {
            case "SMS":
                builder.setItems(noArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        actionSMS(noArr[item]);
                    }
                });
                break;
            case "TELP":
                builder.setItems(noArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        actionTelp(noArr[item]);
                    }
                });
                break;
            case "BBM":
                builder.setItems(noArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        actionBBM(noArr[item]);
                    }
                });

                break;
            case "WA":
                builder.setItems(noArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        actionWA(noArr[item]);
                    }
                });
                break;
            case "LINE":
                builder.setItems(noArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        actionLine(noArr[item]);
                    }
                });
                break;
        }

        AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean storeImage(Bitmap image) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String namaFile = "IMG_" + timeStamp + ".PNG";

        FileOutputStream fos = null;
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + namaFile);
        try {
            fos = new FileOutputStream(mediaFile);
            image.compress(Bitmap.CompressFormat.PNG, 99, fos);
            fos.close();
            Log.e("TAG", "Lokasi : " + fos.toString());
            Log.e("TAG", "Lokasi : " + mediaFile.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void actionSMS(String no) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + no));
        startActivity(sendIntent);
    }

    public void actionWA(String no) {
        if (isAPPInstalled("com.whatsapp")) {
            Uri uri = Uri.parse("smsto:" + no);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(i);
        } else {
            Toast.makeText(this, "WA belum di install!", Toast.LENGTH_SHORT).show();
        }
    }

    public void actionLine(String id) {
        if (isAPPInstalled("jp.naver.line.android")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(id));
            Intent chooser = Intent.createChooser(intent, "Pilih aplikasi");
            startActivity(chooser);
        } else {
            Toast.makeText(this, "Line belum di install!", Toast.LENGTH_SHORT).show();
        }
    }

    public void actionTelp(final String no) {
        try {
            Dexter.checkPermission(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("tel:" + no));
                    startActivity(sendIntent);
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    Toast.makeText(getApplicationContext(), "Ijin di butuhkan!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }, Manifest.permission.CALL_PHONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionBBM(String id) {
        if (isAPPInstalled("com.bbm")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("bbmi://" + id));
            Intent chooser = Intent.createChooser(intent, "Pilih aplikasi");
            startActivity(chooser);
        } else {
            Toast.makeText(this, "BBM belum di install!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAPPInstalled(String namaPackage) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(namaPackage, PackageManager.GET_ACTIVITIES);
            //pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    public void getAllKontak() {
        final ProgressDialog loading = ProgressDialog.show(this, "", "Please Wait..", false, false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Config.URL_BASE_API + "/daftar_owner.php", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                loading.dismiss();
                //Log.e("TAG", response.toString());
                try {
                    JSONArray arr = response.getJSONArray("owner");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String no = obj.getString("telp_owner");
                        MainActivity.arrNoHP.add(no);
                        String bbm = obj.getString("bbm");
                        MainActivity.arrBBM.add(bbm);
                        String line = obj.getString("line");
                        MainActivity.arrLine.add(line);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loading.dismiss();
                throwable.printStackTrace();
            }
        });
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(DetailbrgActivity2.this);
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