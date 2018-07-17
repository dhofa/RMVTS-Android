package id.ac.pens.student.it.ahmadmundhofa.rmvts.View.LogActivityMenu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response.LogActivity;
import id.ac.pens.student.it.ahmadmundhofa.rmvts.R;

public class LogActivityAdapter extends RecyclerView.Adapter<LogActivityAdapter.MyViewHolder> {
    private List<LogActivity> logActivityList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_log_activity, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LogActivity logActivity = logActivityList.get(position);
        String title_text = logActivity.getTitle();
        String detail_text= logActivity.getDetail();
        holder.title.setText(title_text);
        holder.detail.setText(detail_text);

        if(title_text.equals("gps status")){
            holder.imageSymbol.setImageResource(R.drawable.ic_log_map);
        }else if(title_text.equals("vibration")){
            holder.imageSymbol.setImageResource(R.drawable.ic_vibration_orange);
        }else if(title_text.equals("Ignition Notification")){
            holder.imageSymbol.setImageResource(R.drawable.ic_ignition);
        }else if(title_text.equals("Turn Off Ignition")){
            holder.imageSymbol.setImageResource(R.drawable.ic_ignition_off);
        }else if(title_text.equals("Buzzer Notification")){
            holder.imageSymbol.setImageResource(R.drawable.ic_alarm);
        }else if(title_text.equals("Buzzer Turned Off")){
            holder.imageSymbol.setImageResource(R.drawable.ic_alarm_off);
        }
    }

    @Override
    public int getItemCount() {
        return logActivityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail;
        public ImageView imageSymbol;

        public MyViewHolder(View view) {
            super(view);
            title  = (TextView) view.findViewById(R.id.title);
            detail = (TextView) view.findViewById(R.id.detail);
            imageSymbol = (ImageView) view.findViewById(R.id.image_symbol);
        }
    }

    public LogActivityAdapter(List<LogActivity> logActivityList){
        this.logActivityList = logActivityList;
    }


}
