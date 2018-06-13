package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogActivityMenu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private List<LogActivity> logActivityList = new ArrayList<>();
    private LogActivityAdapter logActivityAdapter;

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
        settupLogActivity();
        return view;
    }

    private void settupLogActivity() {


        getDataLogActivity();
    }

    public void getDataLogActivity() {
        sessionManager = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        dataSession = sessionManager.getUserDetails();
        token   = dataSession.get(SessionManager.token);
        periode = "2018/06/13";
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

            }
        });
    }
}
