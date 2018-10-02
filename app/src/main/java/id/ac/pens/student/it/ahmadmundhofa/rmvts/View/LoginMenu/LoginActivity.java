package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LoginMenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.MainActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.RegisterMenu.RegisterActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.layout_signup)
    LinearLayout layout_signup;

    @BindView(R.id.layout_logo)
    ImageView layout_logo;

    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;

    @BindView(R.id.go_register)
    TextView goRegister;

    private CircularProgressButton submit;
    private SessionManager sessionManager;
    private String TAG = LoginActivity.class.getSimpleName();
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        settupTransition(savedInstanceState);
        sessionManager = new SessionManager(this);

        submit = (CircularProgressButton) findViewById(R.id.submit);
        submit.setOnClickListener(v -> submitClicked(v));
    }

    private void settupTransition(Bundle savedInstanceState) {
        final Intent intent = getIntent();
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {

            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(500);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(400);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });


            circularReveal.start();
        }
    }

    private void submitClicked(View view) {
        startButtonAnimation();

        String str_email    = email.getText().toString();
        String str_password = password.getText().toString();
        String fcm_token    = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM TOKEN "+fcm_token);

        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.goLogin(str_email, str_password, fcm_token);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.body() != null){
                    if(response.body().getStatus().equals("success")){
                        DataResponse data = response.body().getData();
                        saveDataUser(data,str_password);
                        goToMain(view);
                    }else{
                        revertButtonAnimation(response.body().getMessage());
                    }
                }else{
                    revertButtonAnimation("Null data response body..");
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                revertButtonAnimation("Failed Login, Try again..! =>"+t.getMessage());
            }
        });
    }

    private void goToMain(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.success);
        submit.doneLoadingAnimation(383,bitmap);
        submit.animate().alpha(0.5f).setDuration(1000);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
        finish();
    }

    private void saveDataUser(DataResponse data, String password) {
        String owner  = data.getVehicleData().getOwner();
        String plat   = data.getVehicleData().getPlateNumber();
        String address= data.getVehicleData().getAddress();
        String type   = data.getVehicleData().getVehicleType();
        String token  = data.getUser().getToken();
        String id_user  = data.getId();
        String url_foto = data.getVehicleData().getUserPhotos();
        String str_email= data.getUser().getEmail();
        String fcm_token= data.getUser().getFcmToken();
        sessionManager.saveFotoProfile(url_foto);
        sessionManager.saveUserData(owner,str_email, fcm_token, plat, address, type, id_user, password);
        sessionManager.createSession(token);
    }

    private void startButtonAnimation() {
        submit.setBackgroundResource(R.color.indigo_100);
        submit.setSpinningBarColor(Color.parseColor("#6200EE"));
        submit.setSpinningBarWidth(10);
        layout_signup.animate().alpha(0.0f);
        email.animate().translationY(email.getHeight()).alpha(0.0f).setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        email.setVisibility(View.INVISIBLE);
                        submit.animate().translationY(-email.getHeight()).setDuration(200);
                    }
                });
        password.animate().translationY(password.getHeight()).alpha(0.0f).setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        password.setVisibility(View.INVISIBLE);
                    }
                });
        layout_logo.animate().translationY(layout_logo.getHeight()).alpha(0.0f).setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout_logo.setVisibility(View.GONE);
                    }
                });
        submit.startAnimation();
    }

    public void revertButtonAnimation(String message){
        submit.revertAnimation(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                submit.revertAnimation();
                submit.setBackgroundResource(R.color.button_login);
                layout_signup.animate().alpha(1.0f);
                layout_logo.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);

                submit.animate().translationY(0).setDuration(200);

                email.animate().translationY(0).alpha(1.0f).setDuration(400)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                email.setVisibility(View.VISIBLE);
                            }
                        });
                password.animate().translationY(0).alpha(1.0f).setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                password.setVisibility(View.VISIBLE);
                            }
                        });
                layout_logo.animate().translationY(0).alpha(1.0f).setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layout_logo.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        Snackbar alert_snack_bar = Snackbar.make(layout_logo, message, Snackbar.LENGTH_SHORT);
        alert_snack_bar.show();
    }

    @OnClick(R.id.go_register)
    public void goToRegister(){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, layout_logo, "transition");
        int revealX = (int) (layout_logo.getX() + layout_logo.getWidth() / 2);
        int revealY = (int) (layout_logo.getY() + layout_logo.getHeight() / 2);

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.putExtra(RegisterActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(RegisterActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        submit.dispose();
    }
}
