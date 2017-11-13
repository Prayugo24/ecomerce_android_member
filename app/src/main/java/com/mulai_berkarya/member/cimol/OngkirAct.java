package com.mulai_berkarya.member.cimol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mulai_berkarya.member.server.Config.URL_BASE_API;

public class OngkirAct extends AppCompatActivity {
    EditText ekodepos, ealamat, etlp, enama;
    EditText sprov;
    AutoCompleteTextView skota, skec;
    ProgressDialog pd;
    String sprovinsi, skabkot, skecamatan, id;
    DatabaseHandler dbHelper;
    SessionManager session;
    ArrayList<String> arrKabId, arrKabNama, arrKecId, arrKecNama;
    String kabId = null;
    String kecId = null;
    DatabaseHandler databaseHandler;
    SQLiteDatabase db;
    private Button blanjut;
    //ArrayList<String> provlist;
    //ArrayList<Provinsi> provinsi;
    private String urlMember = URL_BASE_API + "/checkout.php";

    String berat, totalBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongkir);
//        alert
        alertDialogTrial();

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.createDataBase();

        Bundle b = getIntent().getExtras();
        berat = b.getString("berat_total");
        totalBarang = b.getString("total_barang");

        arrKabId = new ArrayList<>();
        arrKabNama = new ArrayList<>();

        arrKecId = new ArrayList<>();
        arrKecNama = new ArrayList<>();


        //arrKabId = databaseHandler.getAllKabId();
        //arrKabNama = databaseHandler.getAllKabNama();

        if (Build.VERSION.SDK_INT > 16) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        session = new SessionManager(getApplicationContext());
        //session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();

        id = user.get(SessionManager.KEY_ID);


        dbHelper = new DatabaseHandler(this);
        dbHelper.createDataBase();
        sprov = (EditText) findViewById(R.id.spinProv);
        skota = (AutoCompleteTextView) findViewById(R.id.spinKabkot);
        skec = (AutoCompleteTextView) findViewById(R.id.spinKec);
        blanjut = (Button) findViewById(R.id.btnlanjut);
        enama = (EditText) findViewById(R.id.editNama);
        ealamat = (EditText) findViewById(R.id.editAlamat);
        ekodepos = (EditText) findViewById(R.id.editKodepos);
        etlp = (EditText) findViewById(R.id.editTelp);
        //loadSpinnerData();

        getAllKab();
        skota.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String dipilih = adapterView.getItemAtPosition(i).toString();
                getAllKec(dipilih);

                int posisi = arrKabNama.indexOf(dipilih);
                kabId = arrKabId.get(Integer.valueOf(posisi));

                //int posisi = Arrays.asList(arrKab).indexOf(dipilih);
                //namaAsal = arrKab.get(Integer.valueOf(posisi));
            }
        });
        skec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String dipilih = adapterView.getItemAtPosition(i).toString();
                int posisi = arrKecNama.indexOf(dipilih);
                kecId = arrKecId.get(Integer.valueOf(posisi));
            }
        });

        blanjut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String atasNama = enama.getText().toString().trim();
                String alamat = ealamat.getText().toString().trim();
                String provinsi = sprov.getText().toString().trim();
                String kabupaten = skota.getText().toString().trim();
                String kecamatan = skec.getText().toString().trim();
                String kodepos = ekodepos.getText().toString().trim();
                String telp = etlp.getText().toString().trim();
                //kabId

                if (isIsi(atasNama) && isIsi(alamat) && isIsi(provinsi) && isIsi(kabupaten)
                        && isIsi(kecamatan) && isIsi(kodepos) && isIsi(telp)) {
                    if (null == kecId) {
                        Toast.makeText(OngkirAct.this, "Pilih kabupaten sesuai dengan rekomendasi", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle b = new Bundle();
                        b.putString("nama", atasNama);
                        b.putString("alamat", alamat);
                        //tambahan
                        b.putString("provinsi", provinsi);
                        b.putString("kab_kot", kabupaten);
                        b.putString("kecamatan", kecamatan);
                        //==============
                        b.putString("kodepos", kodepos);
                        b.putString("telp", telp);
                        b.putString("id_kab", kabId);
                        b.putString("id_kec", kecId);

                        b.putString("berat_total", berat);
                        b.putString("total_barang", totalBarang);

                        Intent in = new Intent(OngkirAct.this, OngkirBayarAct.class);
                        in.putExtras(b);

                        startActivity(in);

                    }

                } else {
                    Toast.makeText(OngkirAct.this, "Masih ada yang kosong", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public boolean isIsi(String nama) {
        if (TextUtils.isEmpty(nama)) return false;
        else return true;
    }

    public void getAllKab() {
        //ArrayList<String> arrayList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        //Cursor c = db.rawQuery("select * from domesticCities", null);
        Cursor c = db.rawQuery("select * from domesticCities where kabupaten not like '%(Kota)%'", null);
        c.moveToFirst();
        do {
            String kab = c.getString(c.getColumnIndex("kabupaten"));
            arrKabNama.add(kab);

            String kabId = c.getString(c.getColumnIndex("id"));
            arrKabId.add(kabId);

        } while (c.moveToNext());
        if (arrKabNama.size() > 0)
            skota.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrKabNama));
    }

    public void getAllKec(String kab) {
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from domesticSubDistricts where city like '" + kab.trim() + "'", null);
        c.moveToFirst();
        do {
            String id = c.getString(c.getColumnIndex("id"));
            arrKecId.add(id);
            String kec = c.getString(c.getColumnIndex("name"));
            arrKecNama.add(kec);
        } while (c.moveToNext());
        if (arrKecNama.size() > 0)
            skec.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrKecNama));

    }

    public boolean checkKec(String kec) {
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from domesticSubDistricts where name='" + kec.trim() + "'", null);
        c.moveToFirst();

        if (c.getCount() > 0) {
            return true;
        } else return false;

    }

    public boolean checkKab(String kab) {
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from domesticCities where kabupaten='" + kab.trim() + "'", null);
        c.moveToFirst();

        if (c.getCount() > 0) {
            return true;
        } else return false;

    }
    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(OngkirAct.this);
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