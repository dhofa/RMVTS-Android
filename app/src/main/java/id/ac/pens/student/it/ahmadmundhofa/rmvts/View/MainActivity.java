package id.ac.pens.student.it.ahmadmundhofa.rmvts.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.Dashboard.DashboardFragment;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogActivityMenu.LogActivityFragment;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogCamera.LogCameraActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LoginMenu.LoginActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.ProfileSideMenu.ProfileActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.RealtimeMapsMenu.RealtimeMapsFragment;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.SnapCaptureMenu.SnapCaptureFragment;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.TrackVehicleMenu.TrackVehicleFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private TextView headerOwner, headerEmail;
    private ImageView fotoProfile;
    private Fragment fragment;
    private boolean twice = false;
    private BottomSheetBehavior sheetBehavior;
    private String URL_HOST = "https://rmvts.herokuapp.com/";
    private Socket mSocket;
    private int revealX;
    private int revealY;
    private ActionBarDrawerToggle toggle;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        dataSession = sessionManager.getUserDetails();

        settupTransition(savedInstanceState);
        settupSideNavigation();
        settupBottomNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment fragment = new DashboardFragment();
        loadFragment(fragment);
    }

    private void settupSideNavigation() {
        toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                showHeader();
            }
        };
        mDrawer.addDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //initialize data in navigation drawer, its diferent with activity or fragment
        View headerView = navigationView.getHeaderView(0);
        fotoProfile = (ImageView) headerView.findViewById(R.id.foto_profile);
        headerOwner = (TextView) headerView.findViewById(R.id.header_owner);
        headerEmail = (TextView) headerView.findViewById(R.id.header_email);

        toggle.syncState();
    }

    private void showHeader() {
        String nama = dataSession.get(SessionManager.owner);
        String email = dataSession.get(SessionManager.email);
        String imgageUrl = dataSession.get(SessionManager.foto_profile);

        headerOwner.setText(nama);
        headerEmail.setText(email);
        Picasso.get().load(imgageUrl).into(fotoProfile);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent goTo;
        if (id == R.id.side_profile) {
            goTo = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(goTo);
        } else if (id == R.id.side_log_ignition) {

        } else if (id == R.id.side_log_camera) {
            goTo = new Intent(MainActivity.this, LogCameraActivity.class);
            startActivity(goTo);
        } else if (id == R.id.side_log_vibration) {

        } else if (id == R.id.side_logout) {
            gotoLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void gotoLogout() {
        sessionManager.logout();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void settupBottomNavigation() {
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.dashboard:
                                toolbarTitle.setText(R.string.bottom_dashboard);
                                fragment = new DashboardFragment();
                                loadFragment(fragment);
                                break;
                            case R.id.realtime_maps:
                                toolbarTitle.setText(R.string.bottom_maps);
                                fragment = new RealtimeMapsFragment();
                                loadFragment(fragment);
                                break;
                            case R.id.capture_image:
                                toolbarTitle.setText(R.string.bottom_capture);
                                fragment = new SnapCaptureFragment();
                                loadFragment(fragment);
                                break;
                            case R.id.log_vehicle:
                                toolbarTitle.setText(R.string.bottom_your_location);
                                fragment = new TrackVehicleFragment();
                                loadFragment(fragment);
                                break;
                            case R.id.log_activity:
                                toolbarTitle.setText(R.string.bottom_log);
                                fragment = new LogActivityFragment();
                                loadFragment(fragment);
                                break;
                        }
                        return true;
                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(400);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });


            circularReveal.start();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragment instanceof DashboardFragment) {
                if (twice) {
                    finish();
                }
                twice = true;
                Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        twice = false;
                    }
                }, 3000);
            } else {
                navigation.setSelectedItemId(R.id.dashboard);
            }
        }
    }
}