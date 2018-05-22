package id.ac.pens.student.it.ahmadmundhofa.rmvts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.btn_bottom_sheet)
    ImageView btnBottomSheet;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    @BindView(R.id.btn_maps)
    LinearLayout goToMaps;

    @BindView(R.id.btn_alarm)
    RelativeLayout btnAlarm;

    @BindView(R.id.btn_ignition)
    RelativeLayout btnIgnition;

    @BindView(R.id.text_gps)
    TextView textGps;

    @BindView(R.id.text_vibration)
    TextView textVibration;

    @BindView(R.id.mode)
    TextView textMode;

    @BindView(R.id.text_alarm)
    TextView textAlarm;

    @BindView(R.id.ignition_status)
    TextView ignitionStatus;

    @BindView(R.id.img_alarm)
    ImageView imgAlarm;

    @BindView(R.id.img_ignition)
    ImageView imgIgnition;



    BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        btnBottomSheet.setImageResource(R.drawable.box_changed);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        btnBottomSheet.setImageResource(R.drawable.box);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @OnClick(R.id.btn_bottom_sheet)
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            btnBottomSheet.setImageResource(R.drawable.box_changed);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            btnBottomSheet.setImageResource(R.drawable.box);
        }
    }

    @OnClick(R.id.btn_maps)
    public void goToMaps() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_gps)
    public void setUpGps() {
        textGps.setText("Gps : OFF");
    }

    @OnClick(R.id.btn_parking_mode)
    public void setUpParking() {
        textVibration.setText("Vibration : OFF");
        textMode.setText("Mode Parkir OFF");
    }

    @OnClick(R.id.btn_alarm)
    public void setUpAlarm() {
        textAlarm.setText("Alarm : OFF");
        imgAlarm.setImageResource(R.drawable.alarm_on);
    }

    @OnClick(R.id.btn_ignition)
    public void setUpIgnition() {
        ignitionStatus.setText("IN ACTIVE");
        imgIgnition.setImageResource(R.drawable.ignition_on);
    }



}
