package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.ProfileSideMenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.foto_profile)
    CircleImageView fotoProfile;

    @BindView(R.id.profile_owner)
    TextView profileOwner;

    @BindView(R.id.profile_email)
    TextView profileEmail;

    @BindView(R.id.profile_plat)
    TextView profilePlat;

    @BindView(R.id.profile_address)
    TextView profileAddress;

    @BindView(R.id.profile_vehicle_type)
    TextView profileVehicleType;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);
        dataSession = sessionManager.getUserDetails();
        settupProfile();
    }

    private void settupProfile() {
        String url_foto = dataSession.get(SessionManager.foto_profile);
        String owner    = dataSession.get(SessionManager.owner);
        String email    = dataSession.get(SessionManager.email);
        String plat     = dataSession.get(SessionManager.plate_number);
        String address      = dataSession.get(SessionManager.address);
        String vehicle_type = dataSession.get(SessionManager.vehicle_type);

        Picasso.get().load(url_foto).into(fotoProfile);
        profileOwner.setText(owner);
        profileEmail.setText(email);
        profilePlat.setText(plat);
        profileAddress.setText(address);
        profileVehicleType.setText(vehicle_type);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());
    }

    @OnClick(R.id.foto_profile)
    public void openCameraAndTakePicture(){
        //TODO: open camera and upload picture
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
