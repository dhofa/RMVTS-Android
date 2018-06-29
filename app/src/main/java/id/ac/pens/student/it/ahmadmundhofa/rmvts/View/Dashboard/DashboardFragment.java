package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.Dashboard;


import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.RemoteMenu.RemoteActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    @BindView(R.id.text_gps)
    TextView textGps;
    @BindView(R.id.text_alarm)
    TextView textAlarm;
    @BindView(R.id.mode)
    TextView textMode;
    @BindView(R.id.title_alamat)
    TextView titleAlamat;
    @BindView(R.id.detail_alamat)
    TextView detailAlamat;
    @BindView(R.id.btn_remote)
    TextView btnRemote;
    @BindView(R.id.dynamicArcView)
    DecoView decoView;
    @BindView(R.id.textClock)
    TextClock textclock;
    @BindView(R.id.main_content)
    RelativeLayout mainContent;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private Unbinder unbinder;
    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        settupDashboardData();
        return view;
    }


    private void initializeCurrentChart(int jam, int total_jam) {
        String format = "%.0f";
        final SeriesItem seriesJamKerja = new SeriesItem.Builder(Color.parseColor("#FB7254"))
                .setRange(0, total_jam, jam)
                .build();
        int dataJamKerja = decoView.addSeries(seriesJamKerja);

        seriesJamKerja.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (currentPosition == total_jam) {
                    seriesJamKerja.setColor(Color.parseColor("#EAE6E5"));
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        //bagian menggambar chart tingkat kehadiran
        decoView.addEvent(new DecoEvent.Builder(jam)
                .setIndex(dataJamKerja)
                .setDelay(1000)
                .build());
    }

    private void settupDashboardData() {
        mainContent.setAlpha(0.0f);
        showProgress();
        sessionManager = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        dataSession = sessionManager.getUserDetails();
        token = dataSession.get(SessionManager.token);

        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.getDashboard(token);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (getActivity() != null && isAdded()) {
                    if (response.body().getStatus().equals("success")) {
                        DataResponse dataResponse = response.body().getData();
                        if (dataResponse != null) {
                            settupDataRelay(dataResponse);
                            if (dataResponse.getVehicleData().getLastLatitude() == 0 && dataResponse.getVehicleData().getLastLongitude() == 0) {
                                titleAlamat.setText(getResources().getString(R.string.not_found));
                                detailAlamat.setText(getResources().getString(R.string.detail_not_found));
                                Toast.makeText(getActivity(), "Anda belum memiliki data lokasi terakhir..", Toast.LENGTH_SHORT).show();
                                mainContent.animate().alpha(1.0f).setDuration(1000);
                                dismissProgress();
                            } else {
                                LatLng lokasi = new LatLng(dataResponse.getVehicleData().getLastLatitude(), dataResponse.getVehicleData().getLastLongitude());
                                sessionManager.updateLocation(String.valueOf(lokasi.latitude), String.valueOf(lokasi.longitude));
                                locationVehicle(lokasi);
                            }

                            String url_foto = dataResponse.getVehicleData().getUserPhotos();
                            sessionManager.saveFotoProfile(url_foto);
                        }
                    } else {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), "Fail to get data..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textclock.setFormat24Hour("kk:mm:ss");
        textclock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String data[] = charSequence.toString().split(":");
                initializeCurrentChart(Integer.parseInt(data[2]), 59);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void settupDataRelay(DataResponse dataResponse) {
        Boolean relay_gps = dataResponse.getRelay().getGps();
        Boolean relay_ignition = dataResponse.getRelay().getIgnition();
        Boolean relay_vibration = dataResponse.getRelay().getVibration();
        Boolean relay_buzzer = dataResponse.getRelay().getBuzzer();

        if (relay_buzzer) {
            resultAlarmTrue();
        } else {
            resultAlarmFalse();
        }

        if (relay_vibration) {
            resultParkingEventTrue();
        } else {
            resultParkingEventFalse();
        }

        if (relay_gps) {
            resultGpsTrue();
        } else {
            resultGpsFalse();
        }
    }

    private void resultAlarmFalse() {
        textAlarm.setText(getResources().getString(R.string.alarm_off));
    }

    private void resultAlarmTrue() {
        textAlarm.setText(getResources().getString(R.string.alarm_on));
    }

    private void resultParkingEventFalse() {
        textMode.setText(getResources().getString(R.string.parkir_off));
    }

    private void resultParkingEventTrue() {
        textMode.setText(getResources().getString(R.string.parkir_on));
    }

    private void resultGpsFalse() {
        textGps.setText(getResources().getString(R.string.gps_off));
    }

    private void resultGpsTrue() {
        textGps.setText(getResources().getString(R.string.gps_on));
    }

    private void locationVehicle(LatLng lokasi) {
        @Nullable
        String[] locationPinned = getLocationNameAndAddress(lokasi);

        assert locationPinned != null;
        if (locationPinned[0] != null && locationPinned[1] != null) {
            titleAlamat.setText(locationPinned[0]);
            detailAlamat.setText(locationPinned[1]);
        }
        mainContent.animate().alpha(1.0f).setDuration(1000);
        dismissProgress();
    }

    @Nullable
    public String[] getLocationNameAndAddress(LatLng posisiLatLong) {

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String[] locationPinned = {getResources().getString(R.string.not_found), getResources().getString(R.string.detail_not_found)};
        getActivity().runOnUiThread(() -> {
            try {
                int MAX_RESULTS = 1;
                if (posisiLatLong != null) {
                    List<Address> addresses = geocoder.getFromLocation(posisiLatLong.latitude, posisiLatLong.longitude, MAX_RESULTS);
                    for (int i = 0; i < MAX_RESULTS; i++) {
                        Log.v("List Alamat => ", "ke-" + String.valueOf(i));
                        if (addresses.get(i) != null && addresses.size() > 0) {
                            String address = addresses.get(i).getThoroughfare();
                            String city = addresses.get(i).getLocality();
                            String province = addresses.get(i).getAdminArea();
                            String country = addresses.get(i).getCountryName();
                            String name = addresses.get(i).getThoroughfare();
                            String completeAddress = "";
                            if (StringUtils.isNotBlank(address)) {
                                completeAddress += address + " ";
                            }
                            if (StringUtils.isNotBlank(city)) {
                                completeAddress += city + ", ";
                            }
                            if (StringUtils.isNotBlank(province)) {
                                completeAddress += province + " - ";
                            }
                            if (StringUtils.isNotBlank(country)) {
                                completeAddress += country;
                            }
                            if (StringUtils.isNotBlank(name)) {
                                locationPinned[0] = name;
                            }
                            locationPinned[1] = completeAddress;
                            i = MAX_RESULTS;
                        }
                    }
                }

            } catch (IOException e) {
                Log.e("GEO_", "Unable connect to Geocoder -> " + e.getLocalizedMessage());
            }
        });
        return locationPinned;
    }

    @OnClick(R.id.btn_remote)
    public void btn_remote_clicked() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), btnRemote, "transition");
        int revealX = (int) (btnRemote.getX() + btnRemote.getWidth() / 2);
        int revealY = (int) (btnRemote.getY() + btnRemote.getHeight() / 2);

        Intent intent = new Intent(getActivity(), RemoteActivity.class);
        intent.putExtra(RemoteActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(RemoteActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
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
