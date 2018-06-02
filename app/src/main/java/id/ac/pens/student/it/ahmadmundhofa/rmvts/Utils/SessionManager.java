package id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Activity.MainActivity;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int mode = 0;

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String pref_name = "MyPref"; //just name of our preference
    private static final String token = "MyToken";
    private static final String owner = "Owner";
    private static final String email = "Email";
    private static final String fcm_token = "FcmToken";
    private static final String plate_number = "PlateNumber";
    private static final String address = "Address";
    private static final String vehicle_type = "VehicleType";

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

    public void saveUserData(String Owner,String Email,String FCM, String Plat, String Address, String Type){
        editor.putString(owner, Owner);
        editor.putString(email, Email);
        editor.putString(fcm_token, FCM);
        editor.putString(plate_number, Plat);
        editor.putString(address, Address);
        editor.putString(vehicle_type, Type);
        editor.commit();
    }

    public void checkLogin(){
        if (!this.is_login()){
//            Intent i = new Intent(context, LoginActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        }else {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    private boolean is_login() {
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
        editor.clear();
        editor.commit();
        editor.apply();
//        Intent i = new Intent(context, LoginActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(pref_name, pref.getString(pref_name, null));
        user.put(token, pref.getString(token, null));
        user.put(owner, pref.getString(owner, null));
        user.put(email, pref.getString(email, null));
        user.put(fcm_token, pref.getString(fcm_token, null));
        return user;
    }
}
