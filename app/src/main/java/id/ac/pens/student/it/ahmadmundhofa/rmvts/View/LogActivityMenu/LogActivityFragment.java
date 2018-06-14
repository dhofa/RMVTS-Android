package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogActivityMenu;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.LogActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogActivityFragment extends Fragment {

    @BindView(R.id.recycle_layout)
    RecyclerView recyclerView;

    @BindView(R.id.input_date)
    TextView inputDate;

    private List<LogActivity> logActivityList = new ArrayList<>();
    private LogActivityAdapter logActivityAdapter;
    private int hari, bulan, tahun;
    private Unbinder unbinder = null;
    private String token, periode;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;

    public LogActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_activity, container, false);
        unbinder = ButterKnife.bind(this, view);
        periode = "2018/06/13";
        getDataLogActivity(periode);
        return view;
    }


    public void getDataLogActivity(String periode) {
        sessionManager = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        dataSession = sessionManager.getUserDetails();
        token   = dataSession.get(SessionManager.token);
        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.getLogActivity(token, periode);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DataResponse dataResponse = response.body().getData();
                logActivityList = dataResponse.getLogActivity();
                recyclerView.setHasFixedSize(true);
                logActivityAdapter = new LogActivityAdapter(logActivityList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(logActivityAdapter);
                logActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.v("Erorr =>", t.getMessage());
            }
        });
    }

    @OnClick(R.id.input_date)
    public void setInputDate(){
        final Calendar c = Calendar.getInstance();
        tahun = c.get(Calendar.YEAR);
        bulan = c.get(Calendar.MONTH);
        hari = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        inputDate.setHint(String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear)+"/"+String.valueOf(year));
                    }
                }, tahun, bulan, hari);
        datePickerDialog.show();
    }

    @OnClick(R.id.search)
    public void searchDataPeriode(){
        String tanggal[]= inputDate.getHint().toString().split("/");
        String periode  = tanggal[2]+"/"+tanggal[1]+"/"+tanggal[0];
        Toast.makeText(getActivity(), periode, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
