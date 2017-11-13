package com.mulai_berkarya.member.cimol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.orhanobut.hawk.Hawk;

import java.util.HashMap;

@SuppressLint("CommitPrefEdits")
public class SessionManager {
    public static final String KEY_NAME = "nama";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id_member";
    public static final String KEY_KOTAID = "kota_id";
    public static final String KEY_KOTANAMA = "kota_nama";
    //status fcm jika sudah di upload bernilai true dan sebaliknya
    public static final String KEY_STATUS_FCM = "status_fcm";
    // nama sharepreference
    private static final String PREF_NAME = "Sesi";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        //pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        //editor = pref.edit();
        Hawk.init(context)
                .build();
    }

    /**
     * Create loginMethod session
     */
    public void createLoginSession(String name, String email, String id_member) {
        // Storing loginMethod value as TRUE
/*
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ID, id_member);
        editor.commit();
*/
        Hawk.put(IS_LOGIN, true);
        Hawk.put(KEY_NAME, name);
        Hawk.put(KEY_EMAIL, email);
        Hawk.put(KEY_ID, id_member);
    }

    public void setAsalKota(String idKota, String namaKota) {
        // Storing loginMethod value as TRUE
/*
        editor.putString(KEY_KOTAID, idKota);
        editor.putString(KEY_KOTANAMA, namaKota);
        editor.commit();
*/
        Hawk.put(KEY_KOTAID, idKota);
        Hawk.put(KEY_KOTANAMA, namaKota);
    }

    /**
     * Check loginMethod method wil check user loginMethod status
     * If false it will redirect user to loginMethod page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check loginMethod status
        if (!this.isLoggedIn()) {
            Intent i = new Intent(_context, LoginAct.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            //((Activity)_context).finish();
        }

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

/*
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
*/
        String nama = Hawk.get(KEY_NAME, null);
        String email = Hawk.get(KEY_EMAIL, null);
        String id = Hawk.get(KEY_ID, null);
        user.put(KEY_NAME, nama);
        user.put(KEY_EMAIL, email);
        user.put(KEY_ID, id);

        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        //editor.clear();
        //editor.commit();
        Hawk.deleteAll();

        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn() {
        //return pref.getBoolean(IS_LOGIN, false);
        boolean status = Hawk.get(IS_LOGIN, false);
        return status;
    }

    public String getIdKabAsal() {
        //return pref.getString(KEY_KOTAID, null);
        String idKota = Hawk.get(KEY_KOTAID, null);
        return idKota;
    }

    public String getIdUser() {
        //return pref.getString(KEY_ID, null);
        String idUser = Hawk.get(KEY_ID, null);
        return idUser;
    }

    public boolean getStatusFCM() {
        boolean status = Hawk.get(KEY_STATUS_FCM, false);
        return status;
    }

    //getter n setter untuk fcm status
    public void setStatusFCM(boolean status) {
        Hawk.put(KEY_STATUS_FCM, status);
    }


}
