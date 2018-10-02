package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.RealtimeMapsMenu;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.MarkerModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class RealtimeMapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 99;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    @BindView(R.id.MyMap)
    MapView myMap;
    @BindView(R.id.btn_gps)
    ToggleButton btnGps;
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
    private MarkerModel myLocationModel;
    private Marker lokasi_saya;
    private LatLng vehicle_last_location;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean mRequestingLocationUpdates;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private double latitude;
    private double longitude;
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
                        vehicle_longitude = data.getString("longitude");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("vehicle_latitude  =>", vehicle_latitude);
                    Log.e("vehicle_longitude =>", vehicle_longitude);
                    if (!vehicle_latitude.equals("null") && !vehicle_longitude.equals("null")) {
                        LatLng lokasi_kendaraan = new LatLng(Double.parseDouble(vehicle_latitude), Double.parseDouble(vehicle_longitude));
                        updateLokasiVehicle(lokasi_kendaraan);
                    }
                }
            });
        }
    };

    public RealtimeMapsFragment() {
    }

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

        mSocket.on("activate_realtime_gps", realtimeMapsEmitter);
        mSocket.connect();
    }

    @OnCheckedChanged(R.id.btn_gps)
    public void setUpGps(CompoundButton button, boolean checked) {
        JSONObject content = new JSONObject();
        if (checked) {
            try {
                content.put("msg", true);
            } catch (JSONException e) {
                return;
            }
            resultGpsTrue();
        } else {
            try {
                content.put("msg", false);
            } catch (JSONException e) {
                return;
            }
            resultGpsFalse();
        }
        mSocket.emit("activate_realtime_gps", content);
    }

    private void resultGpsFalse() {
        btnGps.setBackground(getResources().getDrawable(R.drawable.background_box_brown_toggle));

    }

    private void resultGpsTrue() {
        btnGps.setBackground(getResources().getDrawable(R.drawable.background_box_orange_toggle));
    }

    private void updateLokasiVehicle(LatLng lokasi_kendaraan) {
        //TODO: remove marker vehicle dan perbaharui
        if (lokasi_kendaraan != null) {
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
//                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
//                myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                LatLng current_location = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//                marker_android = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)).position(current_location).title("My Location"));

                mRequestingLocationUpdates = true;
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                mSettingsClient = LocationServices.getSettingsClient(getActivity());
                createLocationCallback();
                createLocationRequest();
                buildLocationSettingsRequest();
                startLocationUpdates();
            } else {
                requestPermissions();
            }
        }
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                myLocation = locationResult.getLastLocation();
                if (myLocation != null) {
                    updateLokasiKu(myLocation);
                }

            }
        };
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mRequestingLocationUpdates = true;
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                }
            }
        });

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
        } else {
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
                        if (dataResponse.getVehicleData().getLastLocation().getLastLatitude() == 0 && dataResponse.getVehicleData().getLastLocation().getLastLongitude() == 0) {
                            vehicle_last_location = new LatLng(0, 0);
                            Toast.makeText(getActivity(), "Anda belum memiliki data lokasi terakhir..", Toast.LENGTH_SHORT).show();
//                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//                                    .target(marker_android.getPosition())
//                                    .bearing(5)
//                                    .tilt(45)
//                                    .zoom(14)
//                                    .build()));
                        } else {
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


    private void updateLokasiKu(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        myLocationModel = new MarkerModel(latitude, longitude, "Lokasi Saya", 5, "MyLocation");
        LatLng myLocation = myLocationModel.getPosition();

        if (lokasi_saya != null) {
            lokasi_saya.remove();
        }
        lokasi_saya = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location)).position(myLocation).title(myLocationModel.getTitle()));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO: melakukan update lokasi dengan rentang 5 detik
            }
        }, 5000);

    }

    @Override
    public void onLocationChanged(Location location) {
        if (marker_android != null) {
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


}
