package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.server.Config;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TentangOwner extends AppCompatActivity {
    //private static final String url = Config.URL_BASE_API + "/edit_owner2.php";
    private static final String url = Config.URL_BASE_API + "/edit_owner2.php";
    ImageView imgTelp, imgSms, imgWa, imgBbm, imgLine, imgBrowser;
    private TextView txtNamaToko, txtEmail,
            txtTelp, txtNorek, txtKeterangan, txtAlamat, txtBbm, txtLine;
    private ProgressDialog progressBarNya;
    private String noHp = null, bbm = null, line = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_owner);
//        alert
        alertDialogTrial();
        Dexter.initialize(this);

        //txtNamaPemilik = (TextView) findViewById(R.id.editNM);
        txtNamaToko = (TextView) findViewById(R.id.editNamaToko);
        txtEmail = (TextView) findViewById(R.id.editMail);
        txtTelp = (TextView) findViewById(R.id.editTelp);
        //txtPassword1 = (TextView) findViewById(R.id.editPw);
        txtNorek = (TextView) findViewById(R.id.editNorek);
        txtKeterangan = (TextView) findViewById(R.id.editKet);
        txtAlamat = (TextView) findViewById(R.id.editAlamat);
        txtBbm = (TextView) findViewById(R.id.editpinbbm);
        txtLine = (TextView) findViewById(R.id.editLineID);

        imgTelp = (ImageView) findViewById(R.id.owner_telp);
        imgBbm = (ImageView) findViewById(R.id.owner_bbm);
        imgLine = (ImageView) findViewById(R.id.owner_line);
        imgSms = (ImageView) findViewById(R.id.owner_sms);
        imgWa = (ImageView) findViewById(R.id.owner_wa);
        imgBrowser = (ImageView) findViewById(R.id.owner_browser);

        imgTelp.setOnClickListener(clickListener);
        imgSms.setOnClickListener(clickListener);
        imgBbm.setOnClickListener(clickListener);
        imgLine.setOnClickListener(clickListener);
        imgWa.setOnClickListener(clickListener);
        imgBrowser.setOnClickListener(clickListener);

        //getDataOwner();
        getAllKontak();

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

                        if (MainActivity.arrBBM.isEmpty() || MainActivity.arrNoHP.isEmpty() || MainActivity.arrLine.isEmpty()) {
                            MainActivity.arrLine.clear();
                            MainActivity.arrBBM.clear();
                            MainActivity.arrNoHP.clear();

                            String no = obj.getString("telp_owner");
                            MainActivity.arrNoHP.add(no);
                            String bbm = obj.getString("bbm");
                            MainActivity.arrBBM.add(bbm);
                            String line = obj.getString("line");
                            MainActivity.arrLine.add(line);
                        }

                        if (i == 0) {
                            //txtNamaPemilik.setText(obj.getString("nama_owner"));
                            txtNamaToko.setText(obj.getString("nama_toko"));
                            txtAlamat.setText(obj.getString("alamat_owner"));
                            txtEmail.setText(obj.getString("email_owner"));
                            txtTelp.setText(obj.getString("telp_owner"));
                            txtBbm.setText(obj.getString("bbm"));
                            txtLine.setText(obj.getString("line"));
                            txtKeterangan.setText(obj.getString("keterangan"));
                            txtNorek.setText(obj.getString("rekening"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loading.dismiss();
                throwable.printStackTrace();
                Toast.makeText(TentangOwner.this, "Terjadi kesalahan.Coba lagi nanti!", Toast.LENGTH_SHORT).show();
            }
        });
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
            }, android.Manifest.permission.CALL_PHONE);
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


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.owner_sms:
                    if (MainActivity.arrNoHP.size() <= 0) getAllKontak();
                    else dialogBuilder("SMS", MainActivity.arrNoHP);
                    break;
                case R.id.owner_telp:
                    if (MainActivity.arrNoHP.size() <= 0) getAllKontak();
                    else dialogBuilder("TELP", MainActivity.arrNoHP);
                    break;
                case R.id.owner_bbm:
                    if (MainActivity.arrBBM.size() <= 0) getAllKontak();
                    else dialogBuilder("BBM", MainActivity.arrBBM);
                    break;
                case R.id.owner_wa:
                    if (MainActivity.arrNoHP.size() <= 0) getAllKontak();
                    else dialogBuilder("WA", MainActivity.arrNoHP);
                    break;
                case R.id.owner_line:
                    if (MainActivity.arrLine.size() <= 0) getAllKontak();
                    else dialogBuilder("LINE", MainActivity.arrLine);
                    break;
                case R.id.owner_browser:
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://cafeimers.com/"));
                    String title = "Pilih aplikasi ";
                    Intent chooser = Intent.createChooser(intent, title);
                    startActivity(chooser);
                    break;
            }
        }
    };
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(TentangOwner.this);
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
