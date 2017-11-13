package com.mulai_berkarya.member.cimol;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mulai_berkarya.member.server.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.entypo_typeface_library.Entypo;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static int mNotifCount = 0;
    static MenuItem item;
    static ArrayList<String> arrNoHP = new ArrayList<>();
    static ArrayList<String> arrBBM = new ArrayList<>();
    static ArrayList<String> arrLine = new ArrayList<>();
    SessionManager session;
    String id_member;
    DatabaseHandler databaseHandler;
    ArrayList<String> arrKabId, arrKabNama;
    private NavigationView navigationView;

    static void setNotifCount(int count) {
        mNotifCount = count;
        if (mNotifCount > 0) {
            ActionItemBadge.update(item, mNotifCount);
        } else {
            ActionItemBadge.hide(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        alert
        alertDialogTrial();

        //notif
        session = new SessionManager(getApplicationContext());
        databaseHandler = new DatabaseHandler(this);
        databaseHandler.createDataBase();

        arrKabId = databaseHandler.getAllKabId();
        arrKabNama = databaseHandler.getAllKabNama();

        if (arrNoHP.size() <= 0) getNoHP();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Terbaru", TerbaruFragment.class)
                .add("Diskon", DiskonFragment.class)
                .add("Populer", PopulerFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagerTab);
        viewPagerTab.setViewPager(viewPager);

        HashMap<String, String> user = session.getUserDetails();

        id_member = user.get(SessionManager.KEY_ID);
        //over
        //jika bernilai  false maka akan mengirim ke server
        if (session.isLoggedIn()) {
            if (!session.getStatusFCM()) {
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                String token = FirebaseInstanceId.getInstance().getToken();
                if (null != token) sendRegistrationToServer(token);

            }
        }

        hideItem(session.isLoggedIn());

    }

    //over
    private void hideItem(boolean isLogin) {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        //jika login 'true' tampilkan semua
        if (isLogin) {
            nav_Menu.findItem(R.id.nav_category).setVisible(true);
            nav_Menu.findItem(R.id.nav_bayar).setVisible(true);
            //    nav_Menu.findItem(R.id.nav_resi).setVisible(true);
            nav_Menu.findItem(R.id.nav_konfirmasi).setVisible(true);
            nav_Menu.findItem(R.id.nav_setting).setVisible(true);
            nav_Menu.findItem(R.id.nav_about).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_registrasi).setVisible(false);
        } else {
            nav_Menu.findItem(R.id.nav_category).setVisible(true);
            nav_Menu.findItem(R.id.nav_bayar).setVisible(false);
            //      nav_Menu.findItem(R.id.nav_resi).setVisible(false);
            nav_Menu.findItem(R.id.nav_konfirmasi).setVisible(false);
            nav_Menu.findItem(R.id.nav_setting).setVisible(false);
            nav_Menu.findItem(R.id.nav_about).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_registrasi).setVisible(true);
        }
    }

    //notif
    private void sendRegistrationToServer(String token) {
        Log.e("token", token);
        // TODO: Implement this method to send token to your app server.
        AsyncHttpClient clients = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("fcm_id", token);
        params.add("id_member", id_member);
        clients.post(Config.URL_BASE_API + "/register_fcm.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String error = responseBody.toString();
                Log.e("TAG", error);
                Log.e("TAG", "Upload berhasil");
                //Toast.makeText(MainActivity.this, "Selamat Berbelanja", Toast.LENGTH_SHORT).show();
                session.setStatusFCM(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            keluar();

        }
    }

    long back_pressed;
    Toast toast;

    //notifikasi jika akan keluar
    public void keluar() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            toast.cancel();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            toast = Toast.makeText(getBaseContext(), "Tekan sekali lagi untuk keluar!", Toast.LENGTH_SHORT);
            toast.show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        item = menu.findItem(R.id.action_settings);

        ActionItemBadge.update(this, item,
                Entypo.Icon.ent_shopping_cart, ActionItemBadge.BadgeStyles.PURPLE, mNotifCount);

        if (mNotifCount > 0) {
            ActionItemBadge.update(item, mNotifCount);
        } else {
//            ActionItemBadge.hide(item);
            ActionItemBadge.update(item, mNotifCount);
        }
        if (!session.isLoggedIn()) {
            ActionItemBadge.hide(item);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent i = new Intent(MainActivity.this, ListCariAct.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, ListCartActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_category) {
            Intent in = new Intent(this, CategoryActivity.class);
            startActivity(in);

        } else if (id == R.id.nav_bayar) {
            Intent ui = new Intent(this, TransMember.class);
            startActivity(ui);

        } else if (id == R.id.nav_registrasi) {
            Intent iv = new Intent(this, RegistrasiMember.class);
            startActivity(iv);

        } else if (id == R.id.nav_about) {
            Intent ig = new Intent(this, TentangOwner.class);
            startActivity(ig);


        } else if (id == R.id.nav_contact) {
            Intent ig = new Intent(this, ContactOwner.class);
            startActivity(ig);

        } else if (id == R.id.nav_tentang) {
            Intent ig = new Intent(this, TentangAplikasi.class);
            startActivity(ig);

        } else if (id == R.id.nav_setting) {
            session.logoutUser();

        } else if (id == R.id.nav_konfirmasi) {
            Intent ig = new Intent(this, KonfirmasiBarang.class);
            startActivity(ig);

        } else if (id == R.id.nav_login) {
            Intent ig = new Intent(this, LoginAct.class);
            //startActivity(ig);
            startActivityForResult(ig, 0);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getNoHP() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Config.URL_BASE_API + "/daftar_owner.php", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("TAG", response.toString());
                try {
                    JSONArray arr = response.getJSONArray("owner");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String no = obj.getString("telp_owner");
                        arrNoHP.add(no);
                        String bbm = obj.getString("bbm");
                        arrBBM.add(bbm);
                        String line = obj.getString("line");
                        arrLine.add(line);
                    }
                    String idAsal = session.getIdKabAsal();

                    if (null == idAsal) {
                        JSONObject obj = arr.getJSONObject(0);
                        String asalKota = obj.getString("asal_kota");
                        int posisi = arrKabNama.indexOf(asalKota);
                        String kabAsalId = arrKabId.get(Integer.valueOf(posisi));
                        Log.e("TAG", "asal kota : " + asalKota + "\nid asal kota : " + kabAsalId);
                        session.setAsalKota(kabAsalId, asalKota);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    //untuk handling login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();

        }
    }


    //    alert dialog trial
    public void alertDialogTrial(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
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

