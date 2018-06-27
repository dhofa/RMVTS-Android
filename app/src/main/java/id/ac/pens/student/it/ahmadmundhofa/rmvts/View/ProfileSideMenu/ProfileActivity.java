package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.ProfileSideMenu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiModels;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.API.ApiService;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.ResponseModel;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils.SessionManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @BindView(R.id.btn_upload)
    TextView btnUpload;

    private Bitmap bitmap;
    private String selectedImagePath, token;
    private SessionManager sessionManager;
    private HashMap<String, String> dataSession;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private int STORAGE_PERMISSION_CODE = 20;
    private int CAMERA_PERMISSION_CODE  = 21;
    private File fotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);
        dataSession = sessionManager.getUserDetails();
        token = dataSession.get(SessionManager.token);
        settupProfile();
    }

    private void settupProfile() {
        String url_foto = dataSession.get(SessionManager.foto_profile);
        String owner = dataSession.get(SessionManager.owner);
        String email = dataSession.get(SessionManager.email);
        String plat = dataSession.get(SessionManager.plate_number);
        String address = dataSession.get(SessionManager.address);
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
    public void openCameraAndTakePicture() {
        //TODO: open camera and upload picture
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(isReadStorageAllowed()){
                            openGallery();
                        }else{
                            requestStoragePermission();
                        }
                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(isCameraAllowed()){
                            openCamera();
                        }else{
                            requestCameraPermission();
                        }
                    }
                });
        myAlertDialog.show();
    }



    private void openGallery() {
        Intent pictureActionIntent = null;
        pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
    }


    private void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File f = new File(android.os.Environment.getExternalStorageDirectory(), "profile.jpg");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//        startActivityForResult(intent, CAMERA_REQUEST);
        Intent intent = new Intent(ProfileActivity.this, CameraActivity.class);
        startActivityForResult(intent, CAMERA_REQUEST);

    }

    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    public boolean isCameraAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    //Requesting permission
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        selectedImagePath = null;
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            selectedImagePath = data.getStringExtra("PHOTO_PATH");
            fotoFile = new File(selectedImagePath);
            bitmap = BitmapFactory.decodeFile(fotoFile.getAbsolutePath());
            fotoProfile.setImageBitmap(bitmap);
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();
                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                fotoFile = new File(selectedImagePath);
                fotoProfile.setImageBitmap(bitmap);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
        btnUpload.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_upload)
    public void updateFotoProfile(){
        //TODO: upload api url foto profile
        if(fotoFile != null){
            Log.v("lokasi foto => ",fotoFile.toString());
//            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-file"), fotoFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file_foto", fotoFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), fotoFile));


            ApiModels apiService = ApiService.getHttp().create(ApiModels.class);
            Call<ResponseModel> call = apiService.updateFotoProfile(token, body);
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    if(response.body().getStatus().equals("success")){
                        settupProfile();
                        btnUpload.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
