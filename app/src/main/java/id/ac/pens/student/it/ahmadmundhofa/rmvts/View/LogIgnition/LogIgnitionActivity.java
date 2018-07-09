package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogIgnition;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.Ignition;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.Vibration;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogVibration.LogVibrationActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogVibration.LogVibrationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogIgnitionActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_date)
    TextView inputDate;

    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private String token;
    private int hari, bulan, tahun;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    private List<Ignition> list = new ArrayList<>();
    private LogIgnitionAdapter logIgnitionAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_ignition);
        ButterKnife.bind(this);
        settupToolbar();
        settupDataSession();
    }

    @Override
    protected void onStart() {
        super.onStart();
        settupCurentDate();
    }

    @OnClick(R.id.input_date)
    public void setInputDate(){
        final Calendar c = Calendar.getInstance();
        tahun = c.get(Calendar.YEAR);
        bulan = c.get(Calendar.MONTH);
        hari = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        inputDate.setHint(String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year));
                    }
                }, tahun, bulan, hari);
        datePickerDialog.show();
    }

    @OnClick(R.id.search)
    public void searchDataPeriode(){
        progressbar.setVisibility(View.VISIBLE);
        String tanggal[]= inputDate.getHint().toString().split("/");
        String periode  = tanggal[2]+"/"+tanggal[1]+"/"+tanggal[0];
        getDataLogVibration(periode);
    }

    private void settupCurentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatPeriode = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String periode = dateFormatPeriode.format(calendar.getTime());
        String dateView= dateFormatView.format(calendar.getTime());
        inputDate.setHint(dateView);
        progressbar.setVisibility(View.VISIBLE);
        getDataLogVibration(periode);
    }

    private void settupDataSession() {
        sessionManager = new SessionManager(this);
        dataSession = sessionManager.getUserDetails();
        token = dataSession.get(SessionManager.token);
    }

    private void settupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());
    }

    public void getDataLogVibration(String periode) {
        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.getLogIgnition(token, periode);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DataResponse dataResponse = response.body().getData();
                list = dataResponse.getIgnition();
                recyclerView.setHasFixedSize(true);
                logIgnitionAdapter = new LogIgnitionAdapter(list);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LogIgnitionActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(logIgnitionAdapter);
                logIgnitionAdapter.notifyDataSetChanged();
                dismissProgress();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.v("Erorr =>", t.getMessage());
                dismissProgress();
            }
        });
    }

    public void dismissProgress(){
        if(progressbar.getVisibility()== View.VISIBLE){
            progressbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
