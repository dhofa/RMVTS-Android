package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.ProfileSideMenu;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.FaceTracker.CameraSourcePreview;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.FaceTracker.Exif;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.FaceTracker.FaceGraphic;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.View.FaceTracker.GraphicOverlay;

public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.preview)
    CameraSourcePreview mPreview;
    @BindView(R.id.faceOverlay)
    GraphicOverlay mGraphicOverlay;
    @BindView(R.id.background_foto)
    ImageView background_foto;
    @BindView(R.id.count_down)
    TextView countDown;
    @BindView(R.id.take_picture)
    ImageView take_picture;

    private static final int RCP_CAMERA_AND_STORAGE = 456;
    private static final int RC_HANDLE_GMS = 9001;
    private CameraSource mCameraSource;
    private File userFile;
    private Boolean isFirstTaken = Boolean.FALSE;
    private boolean oneShotOnly = true;
    private boolean detectFace = false;

    private long lastCapture = 0;
    private CameraSource.PictureCallback mPicture = bytes -> {
        int orientation = Exif.getOrientation(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        bitmap = this.reScalePhotoBitmap(bitmap, 800, 800);
        switch (orientation) {
            case 90:
                bitmap = rotateImage(bitmap, 90);
                break;
            case 180:
                bitmap = rotateImage(bitmap, 180);
                break;
            case 270:
                bitmap = rotateImage(bitmap, 270);
                break;
            case 0:
                // if orientation is zero we don't need to rotate this
            default:
                break;
        }
        SavePhotoBitmapToDirectory task = new SavePhotoBitmapToDirectory();
        task.execute(bitmap);
    };

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Bitmap reScalePhotoBitmap(Bitmap bm, int maxWidth, int maxHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }
        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_left));
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.setupPermission();
        this.startCameraSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public void saveCurrentImage() {
        if (mCameraSource != null) {
            mCameraSource.takePicture(null, mPicture);
        }
    }

    public void setupPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                this.createCameraSource();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RCP_CAMERA_AND_STORAGE);
            }
            return;
        } else {
            this.createCameraSource();
        }
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMinFaceSize(0.8F)
                .build();

        detector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());

        if (!detector.isOperational()) {
        }
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(10.0f)
                .setAutoFocusEnabled(true)
                .build();
        take_picture.setVisibility(View.VISIBLE);
        take_picture.setOnClickListener(v -> saveCurrentImage());
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    class SavePhotoBitmapToDirectory extends AsyncTask<Bitmap, String, String> {
        @Override
        protected String doInBackground(Bitmap... image) {
            File folder = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File photo = new File(folder, "profile.jpg");
            try {
                Bitmap cameraBitmap = image[0];
                Matrix mtx = new Matrix();
                //this will prevent mirror effect
                mtx.preScale(-1.0f, 1.0f);
                // Setting post rotate to 90 because image will be possibly in landscape
                //   mtx.postRotate(90.f);
                // Rotating Bitmap , create real image that we want

                Bitmap bitmap =  Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(), cameraBitmap.getHeight(), mtx, true);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(photo);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                userFile = photo;
            } catch (IOException e) {
                Log.e("REGISTER_", "Exception in photoCallback", e);
            }
            return (null);
        }

        @Override
        protected void onPostExecute(String s) {
            mCameraSource.stop();
            mPreview.stop();
            // TODO : AFTER PHOTO SAVED
//            SHOW TO LAYOUT AND EXIT FROM THIS ACTIVITY
            if (userFile != null) {
                Intent intent = new Intent();
                intent.putExtra("PHOTO_PATH", userFile.getPath());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private final double OPEN_THRESHOLD = 0.50;
        private final double CLOSE_THRESHOLD = 0.15;
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        private int state = 0;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            // TODO : CAPTURE BY EYE BLINK
            /*
            float left = face.getIsLeftEyeOpenProbability();
            float right = face.getIsRightEyeOpenProbability();
            if ((left == Face.UNCOMPUTED_PROBABILITY) || (right == Face.UNCOMPUTED_PROBABILITY)) {
                // At least one of the eyes was not detected.
                return;
            }

            switch (state) {
                case 0:
                    if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
                        // Both eyes are initially open
                        state = 1;
                    }
                    break;
                case 1:
                    if ((left < CLOSE_THRESHOLD) && (right < CLOSE_THRESHOLD)) {
                        // Both eyes become closed
                        state = 2;
                    }
                    break;
                case 2:
                    if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
                        // Both eyes are open again
                        Log.i("BlinkTracker", "blink occurred!");
                        state = 0;
                        saveCurrentImage();
                    }
                    break;
            }
            */


            // TODO : CAPTURE BY FACE TRACKING
//            mOverlay.add(mFaceGraphic);
//            mFaceGraphic.updateFace(face);


            //TODO: PERUBAHAN UI HARUS DIDALAM METHOD runOnUiThread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v("LANDMARKS", String.valueOf(face.getLandmarks().size()));
                    //menggunakan landmark untuk mendeteksi adanya titik face, jika 0 maka tidak terdeteksi face
                    if (face.getLandmarks().size() <= 3) {
                        detectFace = false;
                        background_foto.setImageResource(R.drawable.wajah_tidak_terdeteksi);
                    } else {
                        detectFace = true;
                        background_foto.setImageResource(R.drawable.wajah_terdeteksi);
                        if(oneShotOnly){
                            settupCountDown();
                            oneShotOnly = false;
                        }
                    }
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    private void settupCountDown() {
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                countDown.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                countDown.setText("");
                if(detectFace){
                    saveCurrentImage();
                }else{
                    Toast.makeText(CameraActivity.this, "Your face should be on the circle..!", Toast.LENGTH_SHORT).show();
                    oneShotOnly = true;
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
