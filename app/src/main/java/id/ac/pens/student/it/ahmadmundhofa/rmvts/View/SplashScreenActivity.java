package id.ac.pens.student.it.ahmadmundhofa.rmvts.View;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LoginMenu.LoginActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {
    @BindView(R.id.icon)
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        new Handler().postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, icon, "transition");
            int revealX = (int) (icon.getX() + icon.getWidth() / 2);
            int revealY = (int) (icon.getY() + icon.getHeight() / 2);

            if(sessionManager.is_login()){
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
                intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

                ActivityCompat.startActivity(this, intent, options.toBundle());
            }else{
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
                intent.putExtra(LoginActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

                ActivityCompat.startActivity(this, intent, options.toBundle());
            }
            finish();
        }, 2000);
    }
}
