package id.ac.pens.student.it.ahmadmundhofa.rmvts.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Activity.LoginMenu.LoginActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.checkLogin();
            finish();
        }, 2000);
    }
}
