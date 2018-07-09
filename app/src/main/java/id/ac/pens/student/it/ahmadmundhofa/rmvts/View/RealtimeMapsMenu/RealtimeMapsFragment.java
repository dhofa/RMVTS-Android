package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.RealtimeMapsMenu;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RealtimeMapsFragment extends Fragment implements OnMapReadyCallback,LocationListener {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 99;
    @BindView(R.id.MyMap)
    MapView myMap;
    private GoogleMap googleMap;
    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    private Unbinder unbinder = null;
    private Location myLocation;
    private Marker marker_android;
    private Marker marker_vehicle;
    private String URL_HOST = "https://rmvts.herokuapp.com/";
    private Socket mSocket;
    private LatLng vehicle_last_location;

    public RealtimeMapsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_maps, container, false);
        unbinder = ButterKnife.bind(this, view);

        myMap.onCreate(savedInstanceState);
        myMap.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        myMap.getMapAsync(this);
        settupSocket();
        return view;
    }

    private void settupSocket() {
        try {
            mSocket = IO.socket(URL_HOST);
        } catch (URISyntaxException e) {
            Log.v("Error karena => ", e.toString());
        }

        mSocket.on("real-time-maps", realtimeMapsEmitter);
        mSocket.connect();
    }

    private Emitter.Listener realtimeMapsEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String vehicle_latitude, vehicle_longitude;
                    try {
                        vehicle_latitude = data.getString("latitude");
                        vehicle_longitude= data.getString("longitude");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("vehicle_latitude  =>", vehicle_latitude);
                    Log.e("vehicle_longitude =>", vehicle_longitude);
                    if(!vehicle_latitude.equals("null") && !vehicle_longitude.equals("null")){
                        LatLng lokasi_kendaraan = new LatLng(Double.parseDouble(vehicle_latitude), Double.parseDouble(vehicle_longitude));
                        updateLokasiVehicle(lokasi_kendaraan);
                    }
                }
            });
        }
    };

    private void updateLokasiVehicle(LatLng lokasi_kendaraan) {
        //TODO: remove marker vehicle dan perbaharui
        if(lokasi_kendaraan != null){
            marker_vehicle.remove();
            marker_vehicle = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location)).position(lokasi_kendaraan).title("Your Vehicle"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PermissionChecker.checkSelfPermission(Objects.requireNonNull(getActivity()), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            if (PermissionChecker.checkSelfPermission(Objects.requireNonNull(getActivity()), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.googleMap.setMyLocationEnabled(true);
                this.googleMap.getUiSettings().setAllGesturesEnabled(true);
                this.googleMap.setBuildingsEnabled(false);
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-5, 120), 3.5f));
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                assert locationManager != null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);
                myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                LatLng current_location = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                marker_android = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)).position(current_location).title("My Location"));
                getLastLocationApi();
            } else {
                requestPermissions();
            }
        }
    }

    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            showSnackbar(R.string.need_gps,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                myMap,
                getString(mainTextStringId),
                Snackbar.LENGTH_LONG)
                .setAction(getString(actionStringId), listener).show();
    }

    private void getLastLocationApi() {
        sessionManager = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        dataSession = sessionManager.getUserDetails();
        token = dataSession.get(SessionManager.token);

        ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
        Call<ResponseModel> call = apiService.getDashboard(token);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (Objects.requireNonNull(response.body()).getStatus().equals("success")) {
                    DataResponse dataResponse = response.body().getData();
                    if (dataResponse != null) {
                        if(dataResponse.getVehicleData().getLastLocation().getLastLatitude() == 0 && dataResponse.getVehicleData().getLastLocation().getLastLongitude() ==0){
                            vehicle_last_location = new LatLng(0,0);
                            Toast.makeText(getActivity(), "Anda belum memiliki data lokasi terakhir..", Toast.LENGTH_SHORT).show();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(marker_android.getPosition())
                                    .bearing(5)
                                    .tilt(45)
                                    .zoom(14)
                                    .build()));
                        }else{
                            vehicle_last_location = new LatLng(dataResponse.getVehicleData().getLastLocation().getLastLatitude(), dataResponse.getVehicleData().getLastLocation().getLastLongitude());
                            sessionManager.updateLocation(String.valueOf(vehicle_last_location.latitude), String.valueOf(vehicle_last_location.longitude));
                            marker_vehicle = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location)).position(vehicle_last_location).title("Your Vehicle"));

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(marker_android.getPosition());
                            builder.include(marker_vehicle.getPosition());


                            LatLngBounds bounds = builder.build();
                            int padding = 100; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                            .target(googleMap.getCameraPosition().target)
                                            .bearing(5)
                                            .tilt(45)
                                            .zoom(googleMap.getCameraPosition().zoom)
                                            .build()));
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(), "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onLocationChanged(Location location) {
        if(marker_android != null){
            marker_android.remove();
            LatLng current_location = new LatLng(location.getLatitude(), location.getLongitude());
            marker_android = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)).position(current_location).title("My Location"));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Nullable
    public String[] getLocationNameAndAddress(LatLng posisiLatLong) {

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String[] locationPinned = {"Location not found", "Detail location not found"};
        getActivity().runOnUiThread(() -> {
            try {
                int MAX_RESULTS = 1;
                if(posisiLatLong!=null){
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


}
