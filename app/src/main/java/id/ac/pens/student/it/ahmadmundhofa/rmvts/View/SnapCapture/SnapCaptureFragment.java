package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.SnapCapture;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SnapCaptureFragment extends Fragment {

    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private Unbinder unbinder = null;
    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    private String URL_HOST = "https://rmvts.herokuapp.com/";
    private Socket mSocket;

    public SnapCaptureFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snap_capture, container, false);
        unbinder = ButterKnife.bind(this, view);
        settupSocket();
        return view;
    }

    private void settupSocket() {
        try {
            mSocket = IO.socket(URL_HOST);
        } catch (URISyntaxException e) {
            Log.v("Error karena => ", e.toString());
        }

        mSocket.on("snap-capture", snapCaptureEmitter);
        mSocket.connect();
    }

    private Emitter.Listener snapCaptureEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("Message response  =>", msg);
                    if (msg.equals("true")) {
                        //TODO: Get Api contain last image captured
                        settupDashboardData();
                    }
                }
            });
        }
    };


    private void settupDashboardData() {
        progressbar.setVisibility(View.VISIBLE);

        sessionManager = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        dataSession = sessionManager.getUserDetails();
        token = dataSession.get(SessionManager.token);

        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.getDashboard(token);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.body().getStatus().equals("success")){
                    DataResponse dataResponse = response.body().getData();
                    if(dataResponse!=null){
                        //TODO: Get URL last image and set into image view

                    }
                }else{
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(), "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
