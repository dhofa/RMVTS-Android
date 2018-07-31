package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.RemoteMenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.LatLng;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTouch;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.DataResponse;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteActivity extends AppCompatActivity {
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private String URL_HOST = "https://rmvts.jagopesan.com/";
    private Socket mSocket;
    private int revealX;
    private int revealY;

    @BindView(R.id.btn_ignition_off)
    ToggleButton btnIgnitionOff;
    @BindView(R.id.button_ignition_on)
    ToggleButton btnIgnitionOn;
    @BindView(R.id.btn_parking_mode)
    ToggleButton btnParkingMode;
    @BindView(R.id.btn_gps)
    ToggleButton btnGps;
    @BindView(R.id.btn_alarm)
    ToggleButton btnAlarm;
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
    @BindView(R.id.dynamicArcView)
    DecoView decoView;
    @BindView(R.id.textClock)
    TextClock textclock;
    @BindView(R.id.main_content)
    ScrollView mainContent;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.button_started)
    Button button_started;

    private String token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        ButterKnife.bind(this);
        settupTransition(savedInstanceState);
        settupDashboardData();
        settupSocket();
    }

    private void settupDashboardData() {
        mainContent.setVisibility(View.INVISIBLE);
        progressbar.setVisibility(View.VISIBLE);
        sessionManager = new SessionManager(getApplicationContext());
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
                        settupDataRelay(dataResponse);
                        LatLng lokasi = new LatLng(dataResponse.getVehicleData().getLastLocation().getLastLatitude(), dataResponse.getVehicleData().getLastLocation().getLastLongitude());
                        locationVehicle(lokasi);
                    }
                }else{
                    Toast.makeText(RemoteActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(RemoteActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.INVISIBLE);
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

    private void locationVehicle(LatLng lokasi) {
        @Nullable
        String[] locationPinned = getLocationNameAndAddress(lokasi);

        assert locationPinned != null;
        if(locationPinned[0] != null && locationPinned[1]!=null){
            titleAlamat.setText(locationPinned[0]);
            detailAlamat.setText(locationPinned[1]);
        }
        mainContent.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.INVISIBLE);
    }

    @Nullable
    public String[] getLocationNameAndAddress(LatLng posisiLatLong) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String[] locationPinned = {"Location not found", "Detail location not found"};
        runOnUiThread(() -> {
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

    private Emitter.Listener buzzerEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status buzzer =>", msg);
                    if (msg.equals("true")) {
                        resultAlarmTrue();
                    } else {
                        resultAlarmFalse();
                    }
                }
            });
        }
    };

    private Emitter.Listener vibrationEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status vibration =>", msg);
                    if (msg.equals("true")) {
                        resultParkingEventTrue();
                    } else {
                        resultParkingEventFalse();
                    }
                }
            });
        }
    };

    private Emitter.Listener ignitionOffEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status ignition =>", msg);
                    if (msg.equals("true")) {
                        resultIgnitionTrue();
                    } else {
                        resultIgnitionFalse();
                    }
                }
            });
        }
    };

    private Emitter.Listener ignitionOnEmitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status ignition off =>", msg);
                    if (msg.equals("true")) {
                        resultControlIgnitionOnTrue();
                    } else {
                        resultControlIgnitionOnFalse();
                    }
                }
            });
        }
    };
    private void settupDataRelay(DataResponse dataResponse) {
        Boolean relay_gps = dataResponse.getRelay().getRealtimeGps();
        Boolean relay_ignition_off = dataResponse.getRelay().getIgnitionOff();
        Boolean relay_ignition_on  = dataResponse.getRelay().getIgnitionOn();
        Boolean relay_vibration = dataResponse.getRelay().getVibration();
        Boolean relay_buzzer = dataResponse.getRelay().getBuzzer();

        btnGps.setChecked(relay_gps);
        btnIgnitionOff.setChecked(relay_ignition_off);
        btnIgnitionOn.setChecked(relay_ignition_on);
        btnParkingMode.setChecked(relay_vibration);
        btnAlarm.setChecked(relay_buzzer);

        if(relay_buzzer){
            resultAlarmTrue();
        }else{
            resultAlarmFalse();
        }

        if(relay_ignition_on){
            resultControlIgnitionOnTrue();
        }else{
            resultControlIgnitionOnFalse();
        }

        if(relay_ignition_off){
            resultIgnitionTrue();
        }else{
            resultIgnitionFalse();
        }

        if(relay_vibration){
            resultParkingEventTrue();
        }else{
            resultParkingEventFalse();
        }

        if(relay_gps){
            resultGpsTrue();
        }else{
            resultGpsFalse();
        }
    }

    private void settupSocket() {
        try {
//            Jika menggunakan Room
//            IO.Options opts = new IO.Options();
//            opts.forceNew = true;
//            opts.query = "id_user=" + ID_USER;
//            mSocket = IO.socket(URL_HOST, opts);
            mSocket = IO.socket(URL_HOST);
        } catch (URISyntaxException e) {
            Log.v("Error karena => ", e.toString());
        }

        mSocket.on("relay1", buzzerEmitter);
        mSocket.on("relay2", ignitionOffEmitter);
        mSocket.on("relay3", ignitionOnEmitter);
        mSocket.on("vibration", vibrationEmitter);
        mSocket.connect();
    }

    private void settupTransition(Bundle savedInstanceState) {
        final Intent intent = getIntent();
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {

            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(500);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    @OnTouch(R.id.button_started)
    public boolean startingUpVehicle(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN){
            Log.d("start button", "pushed..");
            JSONObject content1 = new JSONObject();
            try {
                content1.put("msg", true);
            }catch (JSONException e){
                e.printStackTrace();
            }
            mSocket.emit("relay4", content1);
        } else if (action == event.ACTION_UP){
            Log.d("start button", "not pushed..");
            JSONObject content2 = new JSONObject();
            try {
                content2.put("msg", false);
            }catch (JSONException e){
                e.printStackTrace();
            }
            mSocket.emit("relay4", content2);
        }
        return false;
    }

    @OnCheckedChanged(R.id.button_ignition_on)
    public void setControlIgnitionOn(CompoundButton button, boolean checked) {
        JSONObject content = new JSONObject();
        if (checked) {
            try {
                content.put("msg", true);
            } catch (JSONException e) {
                return;
            }
            resultControlIgnitionOnTrue();
        } else {
            try {
                content.put("msg", false);
            } catch (JSONException e) {
                return;
            }
            resultControlIgnitionOnFalse();
        }
        mSocket.emit("relay3", content);
    }

    private void resultControlIgnitionOnFalse() {
        button_started.setVisibility(View.INVISIBLE);
        btnIgnitionOn.setBackground(getResources().getDrawable(R.drawable.background_box_orange));
        btnIgnitionOn.setText(R.string.turn_on_ignition_dissable);
    }

    private void resultControlIgnitionOnTrue() {
        button_started.setVisibility(View.VISIBLE);
        btnIgnitionOn.setBackground(getResources().getDrawable(R.drawable.background_box_green));
        btnIgnitionOn.setText(R.string.turn_on_ignition_enable);
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
        mSocket.emit("activate_python_gps", content);
    }

    private void resultGpsFalse() {
        btnGps.setBackground(getResources().getDrawable(R.drawable.togle_off_left_top));
        textGps.setText(R.string.gps_off);
    }

    private void resultGpsTrue() {
        btnGps.setBackground(getResources().getDrawable(R.drawable.togle_on_left_top));
        textGps.setText(R.string.gps_on);
    }

    @OnCheckedChanged(R.id.btn_parking_mode)
    public void setUpParking(CompoundButton button, boolean checked) {
        JSONObject content = new JSONObject();
        if (checked) {
            try {
                content.put("msg", true);
            } catch (JSONException e) {
                return;
            }
            resultParkingEventTrue();
        } else {
            try {
                content.put("msg", false);
            } catch (JSONException e) {
                return;
            }
            resultParkingEventFalse();
        }
        mSocket.emit("vibration", content);
    }

    private void resultParkingEventFalse() {
        btnParkingMode.setBackground(getResources().getDrawable(R.drawable.togle_off_right_top));
        textMode.setText(R.string.parkir_off);
    }

    private void resultParkingEventTrue() {
        btnParkingMode.setBackground(getResources().getDrawable(R.drawable.togle_on_right_top));
        textMode.setText(R.string.parkir_on);
    }

    @OnCheckedChanged(R.id.btn_alarm)
    public void setUpAlarm(CompoundButton button, boolean checked) {
        JSONObject content = new JSONObject();
        if (checked) {
            try {
                content.put("msg", true);
            } catch (JSONException e) {
                return;
            }
            resultAlarmTrue();
        } else {
            try {
                content.put("msg", false);
            } catch (JSONException e) {
                return;
            }
            resultAlarmFalse();
        }
        mSocket.emit("relay1", content);
    }

    private void resultAlarmFalse() {
        btnAlarm.setBackground(getResources().getDrawable(R.drawable.togle_off_right_bottom));
        textAlarm.setText(R.string.alarm_off);
    }

    private void resultAlarmTrue() {
        btnAlarm.setBackground(getResources().getDrawable(R.drawable.togle_on_right_bottom));
        textAlarm.setText(R.string.alarm_on);
    }

    @OnCheckedChanged(R.id.btn_ignition_off)
    public void setUpIgnition(CompoundButton button, boolean checked) {
        JSONObject content = new JSONObject();
        if (checked) {
            try {
                content.put("msg", true);
            } catch (JSONException e) {
                return;
            }
            resultIgnitionTrue();
        } else {
            try {
                content.put("msg", false);
            } catch (JSONException e) {
                return;
            }
            resultIgnitionFalse();
        }
        mSocket.emit("relay2", content);
    }

    private void resultIgnitionFalse() {
        btnIgnitionOff.setBackground(getResources().getDrawable(R.drawable.togle_off_left_bottom));
    }

    private void resultIgnitionTrue() {
        btnIgnitionOff.setBackground(getResources().getDrawable(R.drawable.togle_on_left_bottom));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        progressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, progressbar, "transition");
        int revealX = (int) (progressbar.getX() + progressbar.getWidth() / 2);
        int revealY = (int) (progressbar.getY() + progressbar.getHeight() / 2);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
        finish();
    }
}










//    private void settupBottomSheet() {
//        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
//
//        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setImageResource(R.drawable.box_changed);
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        btnBottomSheet.setImageResource(R.drawable.box);
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        break;
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
//    }

//    @OnClick(R.id.btn_bottom_sheet)
//    public void toggleBottomSheet() {
//        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            btnBottomSheet.setImageResource(R.drawable.box_changed);
//        } else {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            btnBottomSheet.setImageResource(R.drawable.box);
//        }
//    }
//
//    @OnClick(R.id.btn_maps)
//    public void goToMaps() {
//        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//        startActivity(intent);
//    }