package com.mulai_berkarya.member.cimol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by DELL on 25/10/2016.
 */

public class InfoRekening extends AppCompatActivity {
    TextView ttotal, trek;
    Button boke;
    String total;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_rekening);
//        alert
        alertDialogTrial();

        ttotal = (TextView) findViewById(R.id.textViewtotaltr);
        trek = (TextView) findViewById(R.id.textViewrek);
        boke = (Button) findViewById(R.id.btnSip);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Bundle b = getIntent().getExtras();
        total = b.getString("total");
        ttotal.setText("Rp. " + total);

        boke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InfoRekening.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        norekening();
    }

    private void norekening() {
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Config.URL_BASE_API + "/daftar_owner.php", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.e("TAG", response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray arr = response.getJSONArray("owner");
                    for (int i = 0; i < arr.length(); i++) {
                        //JSONObject obj = arr.getJSONObject(i);
                        JSONObject obj = arr.getJSONObject(0);
                        trek.setText(obj.getString("rekening"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InfoRekening.this, "Terjadi kesalahan! Coba lagi nanti", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(InfoRekening.this);
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
