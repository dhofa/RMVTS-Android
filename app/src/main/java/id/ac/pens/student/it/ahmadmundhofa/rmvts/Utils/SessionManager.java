package id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LoginMenu.LoginActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.MainActivity;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int mode = 0;

    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String pref_name = "MyPref"; //just name of our preference
    public static final String token = "MyToken";
    public static final String owner = "Owner";
    public static final String email = "Email";
    public static final String password = "Password";
    public static final String fcm_token = "FcmToken";
    public static final String plate_number = "PlateNumber";
    public static final String foto_profile = "FotoProfile";
    public static final String address = "Address";
    public static final String vehicle_type = "VehicleType";
    public static final String id_user = "IdUser";
    public static final String last_latitude = "Latitude";
    public static final String last_longitude = "Longitude";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(pref_name, mode);
        editor = pref.edit();
        editor.apply();
    }

    public void createSession(String MyToken){
        editor.putString(token, "Bearer "+MyToken);
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public void updateLocation(String latitude, String longitude){
        editor.putString(last_latitude, latitude);
        editor.putString(last_longitude, longitude);
        editor.commit();
    }
    public void saveUserData(String Owner,String Email,String FCM, String Plat, String Address, String Type, String IdUser, String pass){
        editor.putString(owner, Owner);
        editor.putString(email, Email);
        editor.putString(fcm_token, FCM);
        editor.putString(plate_number, Plat);
        editor.putString(address, Address);
        editor.putString(vehicle_type, Type);
        editor.putString(id_user, IdUser);
        editor.putString(password, pass);
        editor.commit();
    }

    public void saveFotoProfile(String url){
        editor.putString(foto_profile,url);
        editor.commit();
    }

    public void checkLogin(){
        if (!this.is_login()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public boolean is_login() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logout(){
        editor.remove(token);
        editor.remove(owner);
        editor.remove(email);
        editor.remove(fcm_token);
        editor.remove(plate_number);
        editor.remove(address);
        editor.remove(vehicle_type);
        editor.remove(id_user);
        editor.remove(foto_profile);
        editor.clear();
        editor.commit();
        editor.apply();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(pref_name, pref.getString(pref_name, null));
        user.put(token, pref.getString(token, null));
        user.put(owner, pref.getString(owner, null));
        user.put(email, pref.getString(email, null));
        user.put(fcm_token, pref.getString(fcm_token, null));
        user.put(plate_number, pref.getString(plate_number, null));
        user.put(address, pref.getString(address, null));
        user.put(vehicle_type, pref.getString(vehicle_type, null));
        user.put(id_user, pref.getString(id_user, null));
        user.put(last_latitude, pref.getString(last_latitude, null));
        user.put(last_longitude, pref.getString(last_longitude, null));
        user.put(foto_profile, pref.getString(foto_profile, null));
        user.put(password, pref.getString(password, null));
        return user;
    }
}
