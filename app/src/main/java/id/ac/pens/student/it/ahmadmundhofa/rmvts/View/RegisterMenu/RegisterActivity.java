package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.RegisterMenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LoginMenu.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    
    @BindView(R.id.layout_logo)
    ImageView layoutLogo;
    
    @BindView(R.id.email)
    EditText email;
    
    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.owner)
    EditText owner;

    @BindView(R.id.plate_number)
    EditText plateNumber;

    @BindView(R.id.address)
    EditText address;

    @BindView(R.id.vehicle_type)
    EditText vehicleType;
    
    @BindView(R.id.layout_sigin)
    LinearLayout layoutSigin;

    @BindView(R.id.go_login)
    TextView goLogin;

    private CircularProgressButton submit;
    private String TAG = RegisterActivity.class.getSimpleName();
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        settupTransition(savedInstanceState);

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

    private void submitClicked(View view) {
        startButtonAnimation();

        String str_email    = email.getText().toString();
        String str_password = password.getText().toString();
        String str_owner    = owner.getText().toString();
        String str_address  = address.getText().toString();
        String str_plate_number = plateNumber.getText().toString();
        String str_vehicle_type = vehicleType.getText().toString();

        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.createUser(str_email, str_password, str_owner,str_plate_number,str_address,str_vehicle_type);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.body() != null){
                    if(response.body().getStatus().equals("success")){
                        revertButtonAnimation(response.body().getMessage());
                        Toast.makeText(RegisterActivity.this, "Resistration successfull..", Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegisterActivity.this, "Please contact admin to set raspberry PI Id_Device..", Toast.LENGTH_SHORT).show();
                        goBackToLogin();
                    }else{
                        revertButtonAnimation(response.body().getMessage());
                    }
                }else{
                    revertButtonAnimation("Null data response body..");
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                revertButtonAnimation("Failed Create Account, Try again..!");
            }
        });
    }

    @OnClick(R.id.go_login)
    public void goBackToLogin() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, layoutLogo, "transition");
        int revealX = (int) (layoutLogo.getX() + layoutLogo.getWidth() / 2);
        int revealY = (int) (layoutLogo.getY() + layoutLogo.getHeight() / 2);

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(LoginActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
        finish();
    }

    private void startButtonAnimation() {
        submit.setBackgroundResource(R.color.indigo_100);
        submit.setSpinningBarColor(Color.parseColor("#6200EE"));
        submit.setSpinningBarWidth(10);
        layoutSigin.animate().alpha(0.0f);
        email.animate().translationY(email.getHeight()).alpha(0.0f).setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        email.setVisibility(View.INVISIBLE);
                        submit.animate().translationY(-email.getHeight()).setDuration(200);
                    }
                });
        password.animate().translationY(password.getHeight()).alpha(0.0f).setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        password.setVisibility(View.INVISIBLE);
                    }
                });
        owner.animate().translationY(password.getHeight()).alpha(0.0f).setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        owner.setVisibility(View.INVISIBLE);
                    }
                });

        plateNumber.animate().translationY(password.getHeight()).alpha(0.0f).setDuration(800)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        plateNumber.setVisibility(View.INVISIBLE);
                    }
                });

        address.animate().translationY(password.getHeight()).alpha(0.0f).setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        address.setVisibility(View.INVISIBLE);
                    }
                });

        vehicleType.animate().translationY(password.getHeight()).alpha(0.0f).setDuration(1200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        vehicleType.setVisibility(View.INVISIBLE);
                    }
                });

        layoutLogo.animate().translationY(layoutLogo.getHeight()).alpha(0.0f).setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutLogo.setVisibility(View.GONE);
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
                layoutSigin.animate().alpha(1.0f);
                layoutLogo.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                owner.setVisibility(View.VISIBLE);
                plateNumber.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                vehicleType.setVisibility(View.VISIBLE);

                submit.animate().translationY(0).setDuration(200);

                email.animate().translationY(0).alpha(1.0f).setDuration(1200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                email.setVisibility(View.VISIBLE);
                            }
                        });
                password.animate().translationY(0).alpha(1.0f).setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                password.setVisibility(View.VISIBLE);
                            }
                        });

                owner.animate().translationY(0).alpha(1.0f).setDuration(800)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                password.setVisibility(View.VISIBLE);
                            }
                        });

                plateNumber.animate().translationY(0).alpha(1.0f).setDuration(600)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                password.setVisibility(View.VISIBLE);
                            }
                        });

                address.animate().translationY(0).alpha(1.0f).setDuration(400)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                password.setVisibility(View.VISIBLE);
                            }
                        });

                vehicleType.animate().translationY(0).alpha(1.0f).setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                password.setVisibility(View.VISIBLE);
                            }
                        });

                layoutLogo.animate().translationY(0).alpha(1.0f).setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutLogo.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        Snackbar alert_snack_bar = Snackbar.make(layoutLogo, message, Snackbar.LENGTH_SHORT);
        alert_snack_bar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        submit.dispose();
    }
}
