package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogCamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.Driver;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogCameraActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    private List<Driver> list = new ArrayList<>();
    LogCameraAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_camera);
        ButterKnife.bind(this);
        settupToolbar();
        settupDataSession();
        getLogCamera();
    }

    private void settupDataSession() {
        sessionManager = new SessionManager(this);
        dataSession = sessionManager.getUserDetails();
        token = dataSession.get(SessionManager.token);
    }

    private void settupRecyclerView(List<Driver> list) {
        adapter = new LogCameraAdapter(list, LogCameraActivity.this);
        adapter.notifyDataSetChanged();
        layoutManager = new GridLayoutManager(LogCameraActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void getLogCamera() {
        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.getImages(token);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.body().getStatus().equals("success")){
                    DataResponse dataResponse = response.body().getData();
                    list = dataResponse.getDriver();
                    settupRecyclerView(list);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(LogCameraActivity.this, "Failed To get data..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void settupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
