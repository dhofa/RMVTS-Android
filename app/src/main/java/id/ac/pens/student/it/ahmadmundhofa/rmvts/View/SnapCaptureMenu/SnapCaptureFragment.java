package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.SnapCaptureMenu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.btn_take_foto)
    TextView btnTakeFoto;

    @BindView(R.id.image_capture)
    ImageView imageCapture;

    @BindView(R.id.keterangan)
    TextView keterangan;

    private Unbinder unbinder;
    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    private String URL_HOST = "https://rmvts.herokuapp.com/";
    private Socket mSocket;
    private Emitter.Listener snapCaptureEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
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
    };

    public SnapCaptureFragment() {
    }

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

        mSocket.on("refresh_foto", snapCaptureEmitter);
        mSocket.connect();
    }

    @OnClick(R.id.btn_take_foto)
    public void takeFoto() {
        JSONObject content = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", new Locale("in", "ID"));
        Date date = new Date();

        String current_time = String.valueOf(dateFormat.format(date));
//        Toast.makeText(getActivity(), current_time, Toast.LENGTH_SHORT).show();
        try {
            content.put("msg", current_time);
        } catch (JSONException e) {
            return;
        }

        mSocket.emit("ambilfoto", content);
        showProgress();
    }

    @OnClick(R.id.image_capture)
    public void settupDashboardData() {
        if (isAdded()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    sessionManager = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
                    dataSession = sessionManager.getUserDetails();
                    token = dataSession.get(SessionManager.token);
                    keterangan.setText(getResources().getString(R.string.getting_data));

                    ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
                    Call<ResponseModel> call = apiService.getDashboard(token);
                    call.enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            if (response.body().getStatus().equals("success")) {
                                DataResponse dataResponse = response.body().getData();
                                if (dataResponse != null) {
                                    //TODO: Get URL last image and set into image view
                                    String imgageUrl = dataResponse.getVehicleData().getLastDriverPhotos();
                                    Picasso.get().load(imgageUrl).into(imageCapture);
                                    keterangan.setText(getResources().getString(R.string.press_button_to_take_the_picture_from_your_iot_device));
                                }
                            } else {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            dismissProgress();
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            Toast.makeText(getActivity(), "Fail to get data..", Toast.LENGTH_SHORT).show();
                            dismissProgress();
                        }
                    });

                }
            });
        }
    }

    public void dismissProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressbar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
