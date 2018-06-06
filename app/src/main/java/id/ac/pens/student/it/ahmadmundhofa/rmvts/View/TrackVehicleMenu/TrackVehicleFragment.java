package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.TrackVehicleMenu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackVehicleFragment extends Fragment implements OnMapReadyCallback {
    @BindView(R.id.MyMap)
    MapView myMap;
    private GoogleMap googleMap;
    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    private Unbinder unbinder = null;

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
            settupGpsData();
        }
    }

    private void settupGpsData() {
//        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
//        Call<ResponseModel> call = apiService.getGpsData(token);
//        call.enqueue(new Callback<ResponseModel>() {
//            @Override
//            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
//                List<Koordinat> datakoordinat = response.body().getData().getKoordinat();
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseModel> call, Throwable t) {
//
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
