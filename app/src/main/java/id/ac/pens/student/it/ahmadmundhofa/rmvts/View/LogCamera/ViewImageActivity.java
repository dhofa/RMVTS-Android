package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogCamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;

public class ViewImageActivity extends AppCompatActivity {
    @BindView(R.id.image_link)
    ImageView imageDriver;

    @BindView(R.id.image_title)
    TextView imageTitle;

    @BindView(R.id.created)
    TextView created;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String id, image, link_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.bind(this);
        settupToolbar();

        Intent extra = getIntent();
        if (extra != null) {
            id = extra.getExtras().getString("id");
            image = extra.getExtras().getString("image");
            link_image = extra.getExtras().getString("link_image");

            Picasso.get().load(link_image).into(imageDriver);
            imageTitle.setText(id);
            created.setText(image);
        }
    }

    private void settupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
