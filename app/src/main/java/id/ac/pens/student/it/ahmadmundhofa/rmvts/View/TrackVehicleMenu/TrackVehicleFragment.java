package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.TrackVehicleMenu;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.Koordinat;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackVehicleFragment extends Fragment implements OnMapReadyCallback {
    @BindView(R.id.MyMap)
    MapView myMap;

    @BindView(R.id.input_date)
    TextView inputDate;

    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private Polyline line;
    private GoogleMap googleMap;
    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    private Unbinder unbinder;
    private boolean once = true;
    private int hari, bulan, tahun;

    public TrackVehicleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_vehicle, container, false);
        unbinder = ButterKnife.bind(this, view);
        myMap.onCreate(savedInstanceState);
        myMap.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        myMap.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            this.googleMap.setBuildingsEnabled(false);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-5, 120), 3.5f));
            if(once){
                settupCurentDate();
                once = false;
            }
        }
    }

    private void settupCurentDate() {
        showProgress();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatPeriode = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String periode = dateFormatPeriode.format(calendar.getTime());
        String dateView= dateFormatView.format(calendar.getTime());
        inputDate.setHint(dateView);
        settupGpsData(periode);
    }

    private void settupGpsData(String periode) {
        sessionManager = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        dataSession = sessionManager.getUserDetails();
        token = dataSession.get(SessionManager.token);

        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.getGpsData(token, periode);
        call.enqueue(new Callback<ResponseModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                googleMap.clear();

                if (response.body() != null) {
                    List<Koordinat> datakoordinat = response.body().getData().getKoordinat();
                    if(datakoordinat.size() >0){
                        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                        if (line != null) {
                            googleMap.clear();
                        }

                        final int[] index = {1};
                        datakoordinat.forEach((data) -> {
                            if (index[0] == 1) {
                                LatLng point = new LatLng(data.getLatitude(), data.getLongitude());
                                options.add(point);
                                googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location)).position(point).title("Your Vehicle"));
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                        .target(point)
                                        .bearing(5)
                                        .tilt(45)
                                        .zoom(14)
                                        .build()));
                                line = googleMap.addPolyline(options);

                            } else {
                                new Handler().postDelayed(() -> {
                                    LatLng point = new LatLng(data.getLatitude(), data.getLongitude());
                                    options.add(point);
                                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location)).position(point).title("Your Vehicle"));
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                            .target(point)
                                            .bearing(5)
                                            .tilt(45)
                                            .zoom(14)
                                            .build()));
                                    line = googleMap.addPolyline(options);
                                }, 500 * index[0]);
                            }
                            index[0]++;
                        });
                    }else {
                        Toast.makeText(getActivity(), "Sorry you dont have track data..", Toast.LENGTH_SHORT).show();
                    }
                }
                dismissProgress();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to get data..", Toast.LENGTH_SHORT).show();
                dismissProgress();
            }
        });
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
                        inputDate.setHint(String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year));
                    }
                }, tahun, bulan, hari);
        datePickerDialog.show();
    }

    @OnClick(R.id.search)
    public void searchDataPeriode(){
        showProgress();
        String tanggal[]= inputDate.getHint().toString().split("/");
        String periode  = tanggal[2]+"/"+tanggal[1]+"/"+tanggal[0];
        settupGpsData(periode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
