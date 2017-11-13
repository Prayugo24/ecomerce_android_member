package com.mulai_berkarya.member.cimol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ContactOwner extends Activity /*implements OnClickListener*/ {
    private static final String url =  Config.URL_BASE_API + "/edit_owner2.php";
    private TextView txtNamaPemilik, txtNamaToko, txtEmail,
            txtTelp, txtPassword1, txtNorek, txtKeterangan, txtAlamat;
    private ProgressDialog progressBarNya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_owner);
//        alert
        alertDialogTrial();

        //txtNamaPemilik = (TextView) findViewById(R.id.editNM);
        //txtNamaToko = (TextView) findViewById(R.id.editNamaToko);
        //txtEmail = (TextView) findViewById(R.id.editMail);
        //txtTelp = (TextView) findViewById(R.id.editTelp);
        //txtPassword1 = (TextView) findViewById(R.id.editPw);
        //txtNorek = (TextView) findViewById(R.id.editNorek);
        txtKeterangan = (TextView) findViewById(R.id.editKet);
        //txtAlamat = (TextView) findViewById(R.id.editAlamat);

        getDataOwner();

    }

    private void getDataOwner() {
        progressBarNya = new ProgressDialog(ContactOwner.this);
        progressBarNya.setMessage("Loading data");
        progressBarNya.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progressBarNya.dismiss();
                try {
                    JSONObject obj = response.getJSONObject(0);
                    //idOwner = obj.getString("id_owner");
                    //txtNamaPemilik.setText(obj.getString("nama_owner"));
                    //txtNamaToko.setText(obj.getString("nama_toko"));
                    //txtEmail.setText(obj.getString("email_owner"));
                    //txtTelp.setText(obj.getString("telp_owner"));
                    //txtAlamat.setText(obj.getString("alamat_owner"));
                    txtKeterangan.setText(obj.getString("keterangan"));
                    //txtPassword1.setText(obj.getString("password"));
                    //txtNorek.setText(obj.getString("rekening"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(ContactOwner.this, "Cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
                finish();
                progressBarNya.dismiss();
            }
        });
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ContactOwner.this);
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
