package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogCamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.util.List;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.Driver;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;

public class LogCameraAdapter extends RecyclerView.Adapter<LogCameraAdapter.MyViewHolder>{
    List<Driver> data_images;
    Activity activity;

    public LogCameraAdapter(List<Driver> data_images, Activity activity) {
        this.data_images = data_images;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_log_camera, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Driver driver_images = data_images.get(position);
        String path = driver_images.getLinkImages();
        Picasso.get().load(path).into(holder.driverImage);
        holder.timeTaken.setText(driver_images.getImages());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,ViewImageActivity.class);
                intent.putExtra("id", driver_images.getId());
                intent.putExtra("image", driver_images.getImages());
                intent.putExtra("link_image", driver_images.getLinkImages());

                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data_images.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView driverImage;
        TextView timeTaken;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            driverImage = (ImageView) itemView.findViewById(R.id.image_driver);
            timeTaken   = (TextView)  itemView.findViewById(R.id.time_taken);
            cardView    = (CardView)  itemView.findViewById(R.id.cardView);
        }
    }
}
