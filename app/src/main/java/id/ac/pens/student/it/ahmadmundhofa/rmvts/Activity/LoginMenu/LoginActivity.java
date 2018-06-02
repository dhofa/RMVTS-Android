package id.ac.pens.student.it.ahmadmundhofa.rmvts.Activity.LoginMenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Activity.MainActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
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

    private CircularProgressButton submit;
    private SessionManager sessionManager;
    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);

        submit = (CircularProgressButton) findViewById(R.id.submit);
        submit.setOnClickListener(v -> submitClicked(v));
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
                        saveDataUser(data,str_email,fcm_token);
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
                revertButtonAnimation("Failed Login, Try again..!");
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
    }

    private void saveDataUser(DataResponse data, String str_email, String fcm_token) {
        String owner  = data.getVehicleData().getOwner();
        String plat   = data.getVehicleData().getPlateNumber();
        String address= data.getVehicleData().getAddress();
        String type   = data.getVehicleData().getVehicleType();
        String token  = data.getToken();
        String id_user= data.getIdRaspberry();

        sessionManager.saveUserData(owner,str_email, fcm_token, plat, address, type, id_user);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        submit.dispose();
    }
}
